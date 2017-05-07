package Client;

import Server.Server;

public class ClientGUI {
	//TODO Separate given Snake code which handles the gui
	
	public static void main(String[] args) {
		Thread mainTester = new Thread(new Server(Server.Type.TESTING));
		mainTester.start();
	}
}
