package Client;

import Server.LoginServer;
import Server.Server;

public class ClientGUI {
	//TODO Separate given Snake code which handles the gui
	//TODO main(String[] args) should be moved to it's own class
	public static void main(String[] args) {
		Thread mainTester = new Thread(new Server(Server.Type.PRODUCTION));
		Thread loginTester = new Thread(new LoginServer());
		
		mainTester.start();
	}
}
