package main;

import chat.Client;
import chat.gui.ClientGUI;
import chat.gui.loginGUI;

public class MainClient {

	private static loginGUI login;
	
	public static void main(String[] args) {
		login = new loginGUI();
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(!login.isLogin()) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							Thread.sleep(300);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					
						login.dispose();
					}
				}).start();
				
				new ClientGUI(new Client(login.getIpAddress(), login.getPortNumber(), login.getUsername(), null));
				
			}
		});
		
		t.start();
		
	}
	
}
