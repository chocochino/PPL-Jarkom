package chat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
	 */
	private boolean isReceivingFile;
	
	/**
	 * 
	 */
	private String fileSender;
	
	/**
	 * 
	 */
	private FileOutputStream fos;
	private PrintWriter pw;
	
	/**
	 * 
	 * @param host
	 * @param portNumber
	 * @param username
	 * @param intendedOutput
	 */
	public Client(String host, int portNumber, String username, PrintStream intendedOutput) {
		//Create socket for client
		try {
			this.clientSocket = new Socket(host, portNumber);
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
		this.isReceivingFile = false;
		addUser(this.username);
	}
	
	/**
	 * 
	 */
	public void connect() {
		if(isConnected) return;
		this.isConnected = true;
		this.thread = new Thread(this, "Client Socket Java");
		sendMessage("", MessageType.USER_CONNECT, null);
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
		sendMessage("", MessageType.USER_DISCONNECT, null);
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
	 * @param targetUser TODO
	 */
	public void sendMessage(String str, MessageType type, String targetUser) {
		StringBuilder message = new StringBuilder(type.toString() + MessageType.TYPE_SEPARATOR);
		
		if(targetUser != null) {
			message.insert(0, targetUser + MessageType.PRIVATE);
		}
		
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
			case FILE:
				File file = new File(str);
				message.append(username + MessageType.SENDER);
				sendFile(file, message.toString());
				sendMessage(' ' + file.getName(), MessageType.END_FILE, targetUser);
				output.println("File has been sent");
				break;
			case END_FILE:
				message.append(str);
				break;
			default: break;
		}
		output.flush();
		sendToServer.println(message.toString());
		sendToServer.flush();
	}
	
	/**
	 * 
	 * @param file
	 * @return a String with format "<filename> <filecontent>"
	 */
	private void sendFile(File file, String str) {
		if(file == null || file.isDirectory()) {
			output.println("Choosen file is invalid");
			output.flush();
			return;
		}

		StringBuilder message = new StringBuilder(str + file.getName() + MessageType.FILENAME_SEPARATOR);
		FileInputStream fis;
		byte[] buffer = new byte[4096];
		
		try {
			fis = new FileInputStream(file);
			while(fis.read(buffer) > 0) {
				for(int i=0; i<buffer.length; i++) {	
					message.append(Byte.toString(buffer[i]));
					message.append(' ');
				}
			}
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		sendToServer.println(message.toString());
		sendToServer.flush();
	}
	
	/**
	 * 
	 * @param str
	 */
	public void receiveMessage(String str) {
		if(str == null || str.indexOf(MessageType.TYPE_SEPARATOR) < 0) {
			return;
		}
		MessageType[] types = MessageType.values();
		MessageType type = null;
		
		//Finding Correct MessageType
		for(int i=0; i<types.length; i++) {
			if(types[i].toString().equals(str.substring(0, str.indexOf(MessageType.TYPE_SEPARATOR)))) {
				type = types[i];
				break;
			}
		}
		
		String message = str.substring(str.indexOf(MessageType.TYPE_SEPARATOR)+1);
		switch(type) {
			case USER_CONNECT:
				addUser(message.substring(0, message.indexOf(' ')));
				output.println(message);
				output.flush();
				break;
			case USER_DISCONNECT:
				removeUser(message.substring(0, message.indexOf(' ')));
				output.println(message);
				output.flush();
				break;
			case CHANGE_USERNAME:
				changeUser(message.substring(0, message.indexOf(' ')), message.substring(message.lastIndexOf(' ')+1));
				output.println(message);
				output.flush();
				break;
			case CHAT_MESSAGE:
				output.println(message);
				output.flush();
				break;
			case FILE:
				if(!isReceivingFile) {
					isReceivingFile = true;
					fileSender = message.substring(0, message.indexOf(MessageType.SENDER));
					message = message.substring(message.indexOf(MessageType.SENDER) + 1);
					File newFile = new File(username + '_' + message.substring(0, message.indexOf(MessageType.FILENAME_SEPARATOR)));
					message = message.substring(message.indexOf(MessageType.FILENAME_SEPARATOR)+1);
					try {
						fos = new FileOutputStream(newFile);
						pw = new PrintWriter(fos);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					output.println(fileSender + " is sending " + newFile.getName() + " to you.");
				}
				
				try {
					StringBuilder temp = new StringBuilder();
					for(int i=0; i<message.length(); i++) {
						if(message.charAt(i) == ' ') {
							fos.write(Byte.parseByte(temp.toString()));
							fos.flush();
							temp.delete(0, temp.length());
						}
						else temp.append(message.charAt(i));
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case END_FILE:
				pw.close();
				isReceivingFile = false;
				fileSender = null;
				output.println("File has received.");
				output.flush();
				break;
			default: break;
		}
		
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
