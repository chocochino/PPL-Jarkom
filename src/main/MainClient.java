package main;

import javax.swing.JOptionPane;

import chat.Client;
import chat.gui.ClientGUI;

public class MainClient {

	public static void main(String[] args) {
		String username = JOptionPane.showInputDialog("Input username");
		String IpAddress = JOptionPane.showInputDialog("IP Address");
		String port = JOptionPane.showInputDialog("Port number");
		int portNumber = Integer.parseInt(port);		
		
		new ClientGUI(new Client(IpAddress, portNumber, username, null));
	}
	
}
