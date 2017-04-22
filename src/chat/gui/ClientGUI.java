package chat.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

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
	 * JTextField Object for entering username of a client
	 */
	private JTextField usernameField;
	
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
	private JButton setUsernameButton;
	
	/**
	 * 
	 */
	private JButton disconnectButton;
	
	/**
	 * 
	 */
	private JComboBox<String> receiverChooserComboBox;
	
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
		
		//Username Field
		usernameField = new JTextField(20);
		usernameField.setBounds(70, 15, 80, 30);
		usernameField.setText(client.getUsername());
		usernameField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		usernameField.setVisible(true);
		this.add(usernameField);		
		
		//Message Box
		messageBox = new MessageBox(10, 50, 430, 255);
		messageBox.setEditable(false);
		this.add(messageBox.getScrollPane());
		
		//Connected User Box
		onlineUsersBox = new MessageBox(450, 50, 120, 255);
		onlineUsersBox.setEditable(false);
		this.add(onlineUsersBox.getScrollPane());
		
		//Enter Message Box
		enterMessageBox = new MessageBox(10, 330, 430, 50);
		this.add(enterMessageBox.getScrollPane());
		
		Action send_key = new AbstractAction() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 4110177217844790717L;

			@Override
			public void actionPerformed(ActionEvent e) {
				String message = enterMessageBox.getText();
				String user = receiverChooserComboBox.getItemAt(receiverChooserComboBox.getSelectedIndex());
				if(user.equals("ALL")) user = null;
				
				if(!message.trim().equals("")) {
					if(message.charAt(message.length()-1) == '\n') message = message.substring(0, message.length()-1);
					client.sendMessage(message, MessageType.CHAT_MESSAGE, user);
				}
				enterMessageBox.setText("");
			}
		};
		
		enterMessageBox.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), send_key);
		
		//Set Username Button
		setUsernameButton = new JButton("Change");
		setUsernameButton.setBounds(155, 15, 80, 30);
		setUsernameButton.setEnabled(true);
		setUsernameButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				client.sendMessage(usernameField.getText(), MessageType.CHANGE_USERNAME, null);
				usernameField.setText(client.getUsername());
			}
		});
		setUsernameButton.setVisible(true);
		this.add(setUsernameButton);
		
		//Send Button
		sendButton = new JButton("Send");
		sendButton.setBounds(450, 310, 120, 30);
		sendButton.setEnabled(true);
		sendButton.addActionListener(send_key);
		sendButton.setVisible(true);
		this.add(sendButton);
		
		//Choose File Button
		chooseFileButton = new JButton("Choose File");
		chooseFileButton.setBounds(450, 350, 120, 30);
		chooseFileButton.setEnabled(true);
		chooseFileButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setDialogTitle("Choose File");
				fileChooser.setApproveButtonText("Send File");
				fileChooser.setMultiSelectionEnabled(false);
				int option = fileChooser.showOpenDialog(null);
				if(option == JFileChooser.APPROVE_OPTION) {
					System.out.println(fileChooser.getSelectedFile().getAbsolutePath());
					String user = receiverChooserComboBox.getItemAt(receiverChooserComboBox.getSelectedIndex());
					client.sendMessage(fileChooser.getSelectedFile().getAbsolutePath(), MessageType.FILE, user);
				}
			}
		});
		chooseFileButton.setVisible(true);
		this.add(chooseFileButton);
		
		//Disconnect button
		disconnectButton = new JButton("Disconnect");
		disconnectButton.setBounds(450, 15, 120, 30);
		disconnectButton.setVisible(true);
		disconnectButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				client.disconnect();
				disconnectButton.setEnabled(false);
			}
		});
		this.add(disconnectButton);
		
		//Receiver ComboBox
		receiverChooserComboBox = new JComboBox<String>();
		receiverChooserComboBox.setBounds(10, 308, 150, 20);
		receiverChooserComboBox.setVisible(true);
		this.add(receiverChooserComboBox);
		
		//Online User Print
		onlineUsersUpdateWriter = new PrintWriter(onlineUsersBox.getOutputStream());
		onlineUsersUpdaterThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(client.isConnected()) {
					if(client.getNumberOfUserChange() > 0) {
						//Update user
						client.userChangeKnown();
						
						//Clear the String in connectedUserBox and ComboBox
						onlineUsersBox.setText("");
						receiverChooserComboBox.removeAllItems();
						receiverChooserComboBox.addItem("ALL");
						
						//Add connected users' username
						ArrayList<String> users = client.getUsersName();
						for(int i=0; i<users.size(); i++) {
							onlineUsersUpdateWriter.print(users.get(i));
							receiverChooserComboBox.addItem(users.get(i));
							if(users.get(i).equals(client.getUsername())) {
								onlineUsersUpdateWriter.print(" (You)");
								receiverChooserComboBox.removeItemAt(receiverChooserComboBox.getItemCount()-1);
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
				
				enterMessageBox.setEnabled(false);
				sendButton.setEnabled(false);
				chooseFileButton.setEnabled(false);
				usernameField.setEnabled(false);
			}
		});
		
		this.setVisible(true);
		enterMessageBox.requestFocusInWindow();
		//End of initialize frame
		
		//Other setting
		this.client.setPrintStreamOfChatBox(new PrintStream(this.messageBox.getOutputStream()));
		this.onlineUsersUpdaterThread.start();
		this.client.connect();
	}
}
