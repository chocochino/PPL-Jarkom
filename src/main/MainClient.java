package main;

import chat.Client;
import chat.gui.ClientGUI;

public class MainClient {

	public static void main(String[] args) {
		String IpAddress = "localhost";
		int portNumber = 9090;
		String username = "A";
		new ClientGUI(new Client(IpAddress, portNumber, username, null));
	}	
}
