package main;

import chat.Server;

public class MainServer {

	public static void main(String[] args) {
		Server server = new Server(9090);
		server.runServer();
	}

}
