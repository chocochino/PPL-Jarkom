package main;

import java.util.Scanner;

import chat.Client;
import chat.gui.ClientGUI;

public class MainClient {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String IpAddress = "localhost";
		int portNumber = 9090;
		String username = scanner.nextLine();
		scanner.close();
		new ClientGUI(new Client(IpAddress, portNumber, username, null));
	}	
}
