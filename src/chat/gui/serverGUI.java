package chat.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;

import chat.Server;
import gui.swing.MessageBox;

/**
 * @author Cika Desela
 *
 */
public class serverGUI extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2681651979090902463L;

	private MessageBox mssgBox;
	
	private MessageBox OLUserBox;
	
	private JButton startServerB;
	
	private JButton stopServerB;
	
	private PrintWriter onlineUsersUpdateWriter;
	
	private Thread onlineUsersUpdaterThread;
	
	private Server server;
	
	private ArrayList<String> connectedClients;
	
	
	public serverGUI(Server server){
		super("Server");
		
		this.setBounds(100,100,600,420);
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
				server.closeServer();
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				server.closeServer();
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
		
		
		//Main Box
		mssgBox= new MessageBox(10,50,430,300);
		mssgBox.setEditable(false);
		mssgBox.setBackground(Color.BLACK);
		mssgBox.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
		mssgBox.setForeground(Color.WHITE);
		this.add(mssgBox.getScrollPane());
		
		//Online Box
		OLUserBox= new MessageBox(450,50,120,300);
		OLUserBox.setEditable(false);
		this.add(OLUserBox.getScrollPane());
		
		//Start
		startServerB= new JButton("Start Server");
		startServerB.setBounds (10,15,200,30);
		startServerB.setEnabled(true);
		startServerB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				server.runServer();
				onlineUsersUpdaterThread.start();
				startServerB.setEnabled(false);
				stopServerB.setEnabled(true);
			}
		});
		startServerB.setVisible(true);
		this.add(startServerB);
		
		//Stop
		stopServerB= new JButton("Stop Server");
		stopServerB.setBounds(220,15,200,30);
		stopServerB.setEnabled(true);
		stopServerB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				server.closeServer();
				stopServerB.setEnabled(false);
			}
		});
		stopServerB.setVisible(true);
		stopServerB.setEnabled(false);
		this.add(stopServerB);
		
		this.setVisible(true);
		
		//Online clients print thread
		onlineUsersUpdateWriter = new PrintWriter(OLUserBox.getOutputStream());
		onlineUsersUpdaterThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				String[] clients;
				while(!server.isConnected()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				while(server.isConnected()) {
					clients = server.getConnectedClients();
					//Check if the same
					if(connectedClients.size() != clients.length) {
						connectedClients.clear();
						OLUserBox.setText("");
						for(int i=0; i<clients.length; i++) {
							connectedClients.add(clients[i]);
							onlineUsersUpdateWriter.println(clients[i]);
							onlineUsersUpdateWriter.flush();
						}
					}
					else {
						int i;
						for(i=0; i<clients.length; i++) {
							if(!connectedClients.get(i).equals(clients[i])) {
								break;
							}
						}
						if(i < clients.length) {
							connectedClients.clear();
							OLUserBox.setText("");
							for(i=0; i<clients.length; i++) {
								connectedClients.add(clients[i]);
								onlineUsersUpdateWriter.println(clients[i]);
								onlineUsersUpdateWriter.flush();
							}
						}
					}
					
					//Sleep
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		//OTHERS
		this.server = server;
		this.server.setOutputPrintStream(new PrintStream(mssgBox.getOutputStream()));
		connectedClients = new ArrayList<String>();
		mssgBox.append("Port number of this server socket is " + server.getPortNumber() + "\n");
	}
		
}
