package chat.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;

import chat.Client;
import chat.MessageType;
import gui.swing.MessageBox;

/**
 * 
 * @author Steven Albert
 *
 */
public class ClientGUI extends JFrame {

	private static final long serialVersionUID = -6130481756585137635L;
	
	/**
	 * Client that has been connected to server
	 */
	private Client client;
	
	/**
	 * MessageBox Object to display messages
	 */
	private MessageBox messageBox;
	
	/**
	 * MessageBox Object to display users that are connected
	 */
	private MessageBox onlineUsersBox;
	
	/**
	 * MessageBox Object for entering the message that will be sent
	 */
	private MessageBox enterMessageBox;
	
	/**
	 * Button for sending a message
	 */
	private JButton sendButton;
	
	/**
	 * Button for choose a file to sent
	 */
	private JButton chooseFileButton;
	
	/**
	 * PrintWriter Object for printing online user
	 */
	private PrintWriter onlineUsersUpdateWriter;
	
	/**
	 * Thread for update the connected users in the chat room
	 */
	private Thread onlineUsersUpdaterThread;
	
	
	/**
	 * Initialize the Graphical User Interface for Client
	 * @param client
	 */
	public ClientGUI(Client client) {
		super("Client");
		
		//Client
		this.client = client;
		
		//Frame setting
		this.setBounds(100, 100, 600, 420);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(null);
		this.setResizable(false);
		this.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
			}
			
			@Override
			public void windowIconified(WindowEvent e) {	
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				client.disconnect();
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				client.disconnect();
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
		
		//Message Box
		messageBox = new MessageBox(10, 30, 430, 255);
		messageBox.setEditable(false);
		this.add(messageBox.getScrollPane());
		
		//Connected User Box
		onlineUsersBox = new MessageBox(450, 30, 120, 255);
		onlineUsersBox.setEditable(false);
		this.add(onlineUsersBox.getScrollPane());
		
		//Enter Message Box
		enterMessageBox = new MessageBox(10, 290, 430, 70);
		this.add(enterMessageBox.getScrollPane());
		
		//Send Button
		sendButton = new JButton("Send");
		sendButton.setBounds(450, 295, 120, 30);
		sendButton.setEnabled(true);
		sendButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = enterMessageBox.getText();
				if(!message.trim().equals("")) {
					client.sendMessage(enterMessageBox.getText(), MessageType.CHAT_MESSAGE);
				}
				enterMessageBox.setText("");
			}
		});
		sendButton.setVisible(true);
		this.add(sendButton);
		
		//Choose File Button
		chooseFileButton = new JButton("Choose File");
		chooseFileButton.setBounds(450, 330, 120, 30);
		chooseFileButton.setEnabled(true);
		chooseFileButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//Unimplemented
			}
		});
		chooseFileButton.setVisible(true);
		this.add(chooseFileButton);
		
		//Online User Print
		onlineUsersUpdateWriter = new PrintWriter(onlineUsersBox.getOutputStream());
		onlineUsersUpdaterThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(client.isConnected()) {
					if(client.getNumberOfUserChange() > 0) {
						//Update user
						client.userChangeKnown();
						
						//Clear the String in connectedUserBox
						onlineUsersBox.setText("");
						
						//Add connected users' username
						ArrayList<String> users = client.getUsersName();
						for(int i=0; i<users.size(); i++) {
							onlineUsersUpdateWriter.print(users.get(i));
							if(users.get(i).equals(client.getUsername())) {
								onlineUsersUpdateWriter.print(" (You)");
							}
							onlineUsersUpdateWriter.println();
							onlineUsersUpdateWriter.flush();
						}
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		
		this.setVisible(true);
		//End of initialize frame
		
		//Other setting
		this.client.setPrintStreamOfChatBox(new PrintStream(this.messageBox.getOutputStream()));
		this.onlineUsersUpdaterThread.start();
		this.client.connect();
	}
}
