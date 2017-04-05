
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
	 * 
	 */
	public static final char TYPE_SEPARATOR = '$';
	
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
	
	private void sendTo(String username, String str) {
		for(int i=0; i<clientSockets.size(); i++) {
			if(clientSockets.get(i).username == username) {
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
				System.out.println();
				System.out.println("Client detected");
				System.out.println("Socket accepted - " + socket);
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
			this.thread = new Thread(this, "Client Socket on Server Side");
			this.thread.start();
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
					username = message.substring(0, message.indexOf(' '));
					for(int i=0; i<clientSockets.size(); i++) {
						if(this != clientSockets.get(i))
							sendTo(this.username, 
									MessageType.USER_CONNECT.toString() + TYPE_SEPARATOR + 
									clientSockets.get(i).username + " is in the chat room");						
					}
					sendToAll(this, str);
					break;
				case CHANGE_USERNAME:
					username = message.substring(0, message.indexOf(' '));
					sendToAll(this, str);
					break;
				case PRIVATE_CHAT_MESSAGE:
					sendTo(message.substring(0, message.indexOf(' ')), 
							message.substring(message.indexOf(' ')+1));
					break;
				default:
					sendToAll(this, str);
					break;
			}
			
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
