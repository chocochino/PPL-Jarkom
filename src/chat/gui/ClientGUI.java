package chat.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JFrame;

import chat.Client;
import chat.MessageType;
import gui.swing.MessageBox;

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
	private MessageBox connectedUserBox;
	
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
	 * 
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
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				client.disconnect();
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				client.disconnect();
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		//Message Box
		messageBox = new MessageBox(10, 30, 430, 255);
		messageBox.setEditable(false);
		this.add(messageBox.getScrollPane());
		
		//Connected User Box
		connectedUserBox = new MessageBox(450, 30, 120, 255);
		connectedUserBox.setEditable(false);
		this.add(connectedUserBox.getScrollPane());
		
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
				// TODO Auto-generated method stub
				String message = enterMessageBox.getText();
				if(!message.trim().equals("")) {
					client.sendMessage(enterMessageBox.getText(), MessageType.MESSAGE);
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
				// TODO Auto-generated method stub
				
			}
		});
		chooseFileButton.setVisible(true);
		this.add(chooseFileButton);
		
		//---
		this.setVisible(true);
		
		this.client.setPrintStreamOfChatBox(new PrintStream(this.messageBox.getOutputStream()));
		this.client.connect();
	}
}
