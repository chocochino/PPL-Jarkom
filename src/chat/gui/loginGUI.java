package chat.gui;

import javax.swing.*;
import java.awt.event.*;
import chat.gui.ClientGUI;

/**
 * @author Cika Desela
 *
 */

public class loginGUI extends JFrame implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5065842314852120870L;
	
	JButton loginButton= new JButton("Login");
	JPanel panel= new JPanel();
	JTextField usernameTx= new JTextField();
	JTextField IpAddressTx= new JTextField();
	JTextField portTx= new JTextField();
	JLabel usernameL= new JLabel("Username");
	JLabel IpAddL= new JLabel("IP Address");
	JLabel portL= new JLabel ("Port");
	ClientGUI gui;
	String username;
	String IpAddress;
	int portNumber;
	boolean isLogin;
	
	public loginGUI()
	{
		super("Simple Messenger");
		setSize(300,200);
		setLocation(500,300);
		panel.setLayout(null);
		
		//JLabel
		usernameL.setBounds(30,30,150,20);
		IpAddL.setBounds(30,60,150,20);
		portL.setBounds(30,90,150,20);
				
		panel.add(usernameL);
		panel.add(IpAddL);
		panel.add(portL);
		
		//TextField
		usernameTx.setBounds(100,30,150,20);
		IpAddressTx.setBounds(100, 60, 150, 20);
		portTx.setBounds(100, 90, 150, 20);
		loginButton.setBounds(110,130,80,20);
		
		loginButton.addActionListener(this);
		
		panel.add(usernameTx);
		panel.add(IpAddressTx);
		panel.add(portTx);
		panel.add(loginButton);
		
		
		getContentPane().add(panel);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		
		isLogin = false;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		username= usernameTx.getText();
		IpAddress=IpAddressTx.getText();
		String port= portTx.getText();
		portNumber = Integer.parseInt(port);
		isLogin = true;
	}
	
	public boolean isLogin() {
		return isLogin;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getIpAddress() {
		return IpAddress;
	}
	
	public int getPortNumber() {
		return portNumber;
	}
}
