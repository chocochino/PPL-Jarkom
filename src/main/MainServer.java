package main;

import javax.swing.JOptionPane;

import chat.Server;
import chat.gui.serverGUI;

public class MainServer {

	public static void main(String[] args) {
		int portNumber = Integer.parseInt(JOptionPane.showInputDialog("Port to be actived"));
		Server server = new Server(portNumber);
		new serverGUI(server);
	}

}
