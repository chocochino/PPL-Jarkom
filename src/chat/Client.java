package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 
 * @author Steven Albert
 *
 */
public class Client implements Runnable {
	
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
	private String username;
	
	/**
	 * List of all users in a room
	 */
	private ArrayList<String> onlineUsers;
	
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
	 */
	private int numberOfUserChange;
	
	/**
	 * 
	 * @param hostName
	 * @param portNumber
	 * @param username
	 * @param intendedOutput
	 */
	public Client(String hostName, int portNumber, String username, PrintStream intendedOutput) {
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
		this.username = username;
		this.setPrintStreamOfChatBox(intendedOutput);
		this.onlineUsers = new ArrayList<String>();
		this.numberOfUserChange = 0;
		addUser(this.username);
	}
	
	/**
	 * 
	 */
	public void connect() {
		if(isConnected) return;
		this.isConnected = true;
		this.thread = new Thread(this, "Client Socket Java");
		sendMessage("", MessageType.USER_CONNECT);
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
	 * @return
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * 
	 */
	public void disconnect() {
		if(!isConnected) return;
		this.isConnected = false;
		sendMessage("", MessageType.USER_DISCONNECT);
		try {
			this.clientSocket.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * 
	 * @param username
	 * @return
	 */
	private boolean addUser(String username) {
		if(onlineUsers.add(username)) {
			System.out.println("ADD");
			userChanged();
			return true;
		}
		else return false;
	}
	
	/**
	 * 
	 * @param username
	 * @return
	 */
	private boolean removeUser(String username) {
		if(onlineUsers.remove(username)) {
			System.out.println("REMOVE");
			userChanged();
			return true;
		}
		else return false;
	}
	
	/**
	 * 
	 * @param username
	 * @param newUsername
	 * @return
	 */
	private void changeUser(String username, String newUsername) {
		removeUser(username);
		addUser(newUsername);
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<String> getUsersName() {
		return onlineUsers;
	}
	
	/**
	 * 
	 */
	public void userChanged() {
		Collections.sort(onlineUsers);
		this.numberOfUserChange++;
	}
	
	/**
	 * 
	 */
	public void userChangeKnown() {
		this.numberOfUserChange--;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getNumberOfUserChange() {
		return numberOfUserChange;
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
		StringBuilder message = new StringBuilder(type.toString() + Server.TYPE_SEPARATOR);
		switch(type) {
			case USER_CONNECT:
				if(!isConnected) connect();
				message.append(username + " has join the chat room");
				output.println("You have join the chat room");
				break;
			case USER_DISCONNECT:
				if(isConnected) disconnect();
				message.append(username + " has left the chat room");
				output.println("You have left the chat room");
				break;
			case CHAT_MESSAGE:
				message.append(username + ": " + str);
				output.println("You : " + str);
				break;
			case CHANGE_USERNAME:
				str = str.trim();
				message.append(username + " change username to " + str);
				output.println("Username changed to " + str);
				changeUser(this.getUsername(), str);
				this.username = str;
				break;
			default: break;
		}
		output.flush();
		sendToServer.println(message.toString());
		sendToServer.flush();
	}
	
	/**
	 * 
	 * @param str
	 */
	public void receiveMessage(String str) {
		if(str == null || str.indexOf(Server.TYPE_SEPARATOR) < 0) return;
		MessageType[] types = MessageType.values();
		MessageType type = null;
		
		for(int i=0; i<types.length; i++) {
			if(types[i].toString().equals(str.substring(0, str.indexOf(Server.TYPE_SEPARATOR)))) {
				type = types[i];
				break;
			}
		}
		
		String message = str.substring(str.indexOf(Server.TYPE_SEPARATOR)+1);
		switch(type) {
			case USER_CONNECT:
				addUser(message.substring(0, message.indexOf(' ')));
				break;
			case USER_DISCONNECT:
				removeUser(message.substring(0, message.indexOf(' ')));
				break;
			case CHANGE_USERNAME:
				changeUser(message.substring(0, message.indexOf(' ')), message.substring(message.lastIndexOf(' ')+1));
				break;
			default: 
				break;
		}
		output.println(message);
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
