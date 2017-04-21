
package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * 
 * @author Steven Albert
 *
 */
public class Server implements Runnable {
	
	/**
	 * Port number of server socket
	 */
	private int portNumber;
	
	/**
	 * Server socket status - connected / disconnected
	 */
	private boolean isConnected;
	
	/**
	 * Object of ServerSocket
	 */
	private ServerSocket serverSocket;
	
	/**
	 * List of all connected clients on server side
	 */
	private ArrayList<ClientSocket> clientSockets;
	
	/**
	 * Thread for running a server socket in waiting for request
	 */
	private Thread thread;
	
	
	/**
	 * 
	 * @param portNumber
	 */
	public Server(int portNumber) {
		this.portNumber = portNumber;
		this.isConnected = false;
		this.thread = new Thread(this, "Server Socket Java");
		this.clientSockets = new ArrayList<ClientSocket>();
	}
	
	/**
	 * 
	 */
	public void runServer() {
		this.isConnected = true;
		this.thread.start();
	}
	
	/**
	 * 
	 */
	public void closeServer() {
		this.isConnected = false;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getPortNumber() {
		return this.portNumber;
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
	 * @param username
	 * @param str
	 */
	private void sendTo(String username, String str) {
		for(int i=0; i<clientSockets.size(); i++) {
			if(clientSockets.get(i).username.equals(username)) {
				clientSockets.get(i).sendMessage(str);
				return;
			}
		}
	}

	/**
	 * 
	 * @param sender
	 * @param str
	 */
	private void sendToAll(ClientSocket sender, String str) {
		for(int i=0; i<clientSockets.size(); i++) {
			if(clientSockets.get(i).socket == sender.socket) continue;
			clientSockets.get(i).sendMessage(str);
		}
	}

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(portNumber);
			
			while(true) {
				Socket socket = serverSocket.accept();
				ClientSocket clientSocket = new ClientSocket(socket);
				clientSockets.add(clientSocket);
			}
		} catch (IOException e) {
			System.out.println("Server Socket Exception");
			System.out.println(e.getMessage());
		}
	}
		
	/**
	 * 
	 * @author Steven Albert
	 *
	 */
	private class ClientSocket implements Runnable {
		
		/**
		 * 
		 */
		private Socket socket;
		
		/**
		 * 
		 */
		private String username;
		
		/**
		 * 
		 */
		private BufferedReader reader;
		
		/**
		 * 
		 */
		private PrintWriter writer;
		
		/**
		 * 
		 */
		private Thread thread;
		
		/**
		 * 
		 */
		private boolean isSendingFile;		
		
		
		/**
		 * 
		 * @param clientSocket
		 */
		public ClientSocket(Socket clientSocket) {
			try {
				this.socket = clientSocket;
				this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				this.writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
				writer.println("Client has connected to server");
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.isSendingFile = false;
			this.thread = new Thread(this, "Client Socket on Server Side");
			this.thread.start();
		}
		
		private void shareInChat(String username, String str) {
			if(username.trim().equals("")) {
				sendToAll(this, str);
			}
			else {
				sendTo(username, str.substring(0, str.indexOf(':')) + " <private> " + str.substring(str.indexOf(':')));
			}
		}
		
		/**
		 * 
		 * @param str
		 */
		private void sendMessage(String str) {
			writer.println(str);
			writer.flush();
		}
		
		/**
		 * 
		 * @param str
		 */
		private void receiveMessage(String str) {
			if(str == null) return;
			String usr = new String();
			int index;
			
			if((index = str.indexOf(MessageType.PRIVATE)) > -1) {
				usr = str.substring(0, index);
				str = str.substring(index + MessageType.PRIVATE.length());
			}
			
			if(isSendingFile) {
				if(str.indexOf(MessageType.TYPE_SEPARATOR) >= 0) {
					if(str.substring(0, str.indexOf(MessageType.TYPE_SEPARATOR)).equals(MessageType.END_FILE.toString())) {
						shareInChat(usr, str);
						isSendingFile = false;
					}
					else {
						shareInChat(usr, MessageType.FILE.toString() + MessageType.TYPE_SEPARATOR + str);
					}
				}
				else {
					shareInChat(usr, MessageType.FILE.toString() + MessageType.TYPE_SEPARATOR + str);
				}
				return;
			}
			
			if(str.indexOf(MessageType.TYPE_SEPARATOR) < 0) {	
				return;
			}
		
			MessageType[] types = MessageType.values();
			MessageType type = null;
			
			for(int i=0; i<types.length; i++) {
				if(types[i].toString().equals(str.substring(0, str.indexOf(MessageType.TYPE_SEPARATOR)))) {
					type = types[i];
					break;
				}
			}
						
			String message = str.substring(str.indexOf(MessageType.TYPE_SEPARATOR)+1);
			switch(type) {
				case USER_CONNECT:
					username = message.substring(0, message.indexOf(' '));
					for(int i=0; i<clientSockets.size(); i++) {
						if(this != clientSockets.get(i))
							sendTo(this.username, 
									MessageType.USER_CONNECT.toString() + MessageType.TYPE_SEPARATOR + 
									clientSockets.get(i).username + " is in the chat room");						
					}
					shareInChat(usr, str);
					break;
				case USER_DISCONNECT:
					shareInChat(usr, str);
					clientSockets.remove(this);
					break;
				case CHAT_MESSAGE:
					shareInChat(usr, str);
					break;
				case CHANGE_USERNAME:
					username = message.substring(message.lastIndexOf(' ')+1);
					shareInChat(usr, str);
					break;
				case FILE:
					shareInChat(usr, str);
					isSendingFile = true;
					break;
				case END_FILE:
					shareInChat(usr, str);
					isSendingFile = false;
					break;
				default:
					shareInChat(usr, str);
					break;
			}
			
			System.out.println("Server has received a " + type.toString() + " from " + username);
			if(usr.trim().equals("")) {
				usr = "ALL";
			}
			System.out.println("username is sending " + type.toString() + " to " + usr);

		}
		
		@Override
		public void run() {
			String str;
			
			try {
				while((str = reader.readLine()) != null) {
					receiveMessage(str);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
