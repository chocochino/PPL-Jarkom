package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * 
 * @author Steven Albert
 *
 */
public class Client implements Runnable {

	private static final char typeSeparator = '$';
	
	/**
	 * Object of Socket for client
	 */
	private Socket clientSocket;
	
	/**
	 * Client's socket status - connected / disconnected
	 */
	private boolean isConnected;
	
	/**
	 * The name of the client
	 */
	private String clientName;
	
	/**
	 * List of all users in a room
	 */
	private ArrayList<String> users;
	
	/**
	 * Thread for running a receiver in waiting of incoming message
	 */
	private Thread thread;
	
	/**
	 * BufferedReader Object for the messages that sent to this client
	 */
	private BufferedReader acceptedMessage;
	
	/**
	 * PrintWriter Object to send messages to server socket
	 */
	private PrintWriter sendToServer;
	
	/**
	 * PrintStream Object to display the accepted message
	 */
	private PrintStream output;
	
	
	/**
	 * 
	 * @param hostName
	 * @param portNumber
	 * @param clientName
	 * @param intendedOutput
	 */
	public Client(String hostName, int portNumber, String clientName, PrintStream intendedOutput) {
		//Create socket for client
		try {
			this.clientSocket = new Socket(hostName, portNumber);
		} catch (UnknownHostException e) {
			System.out.println("Host not found");
		} catch (IOException e) {
			System.out.println("Can't create socket");
			System.exit(1);
		}
		//Create BufferedReader
		try {
			this.acceptedMessage = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e) {
			System.out.println("Can't create BufferedReader");
			System.exit(1);
		}
		//Create PrintWriter
		try {
			this.sendToServer = new PrintWriter(clientSocket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Can't create PrintWriter");
			System.exit(1);
		}
		
		//Assign another information
		this.clientName = clientName;
		this.setPrintStreamOfChatBox(intendedOutput);
		this.users = new ArrayList<String>();
		this.users.add(this.clientName);
	}
	
	/**
	 * 
	 */
	public void connect() {
		if(isConnected) return;
		this.isConnected = true;
		this.thread = new Thread(this, "Client Socket Java");
		sendMessage("", MessageType.CONNECT);
		this.thread.run();
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isConnected() {
		return this.isConnected;
	}
	
	/**
	 * 
	 */
	public void disconnect() {
		if(!isConnected) return;
		this.isConnected = false;
		sendMessage("", MessageType.DISCONNECT);
		try {
			this.clientSocket.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<String> getUsersName() {
		return users;
	}
	
	/**
	 * 
	 * @param printStream
	 */
	public void setPrintStreamOfChatBox(PrintStream printStream) {
		this.output = printStream;
	}
	
	/**
	 * 
	 * @param str
	 * @param type
	 */
	public void sendMessage(String str, MessageType type) {
		StringBuilder message = new StringBuilder(type.toString() + "$");
		switch(type) {
			case CONNECT:
				if(!isConnected) connect();
				message.append(clientName + " has join the chat room");
				output.println("You have join the chat room");
				break;
			case DISCONNECT:
				if(isConnected) disconnect();
				message.append(clientName + " has left the chat room");
				output.println("You have left the chat room");
				break;
			case MESSAGE:
				message.append(clientName + ": " + str);
				output.println("You : " + str);
				output.flush();
				break;
			default: break;
		}
		sendToServer.println(message.toString());
		sendToServer.flush();
	}
	
	/**
	 * 
	 * @param str
	 */
	public void receiveMessage(String str) {
		if(str == null || str.indexOf(typeSeparator) < 0) return;
		MessageType[] types = MessageType.values();
		MessageType type = null;
		
		for(int i=0; i<types.length; i++) {
			if(types[i].toString().equals(str.substring(0, str.indexOf(typeSeparator)))) {
				type = types[i];
				break;
			}
		}
		
		String message = str.substring(str.indexOf(typeSeparator)+1);
		switch(type) {
			case CONNECT:
				users.add(message.substring(0, message.indexOf(' ')));
				output.println("SYSTEM: " + message);
				break;
			case DISCONNECT:
				users.remove(message.substring(0, message.indexOf(' ')));
				output.println("SYSTEM: " + message);
				break;
			case MESSAGE:
				output.println(message);
				break;
			default: break;
		}
		output.flush();
	}

	@Override
	public void run() {
		String str;
		try {
			while(isConnected && (str = acceptedMessage.readLine()) != null) {
				receiveMessage(str);
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
