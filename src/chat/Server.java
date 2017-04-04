
package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
		
		private Socket socket;
		
		/**
		 * 
		 */
		private InputStream input;
		
		/**
		 * 
		 */
		private OutputStream output;
		
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
				this.output = clientSocket.getOutputStream();
				this.input = clientSocket.getInputStream();
				this.reader = new BufferedReader(new InputStreamReader(input));
				this.writer = new PrintWriter(new OutputStreamWriter(output));
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
		public void sendMessage(String str) {
			writer.println(str);
			writer.flush();
		}
		
		@Override
		public void run() {
			String str;
			
			try {
				while((str = reader.readLine()) != null) {
					sendToAll(this, str);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
