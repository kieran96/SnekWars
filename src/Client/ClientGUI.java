package Client;

import Server.Login;
import Server.LoginServer;
import Server.Server;
import util.BoundedBuffer;
import util.LoginPacket;

public class ClientGUI {
	//TODO Separate given Snake code which handles the gui
	//TODO main(String[] args) should be moved to it's own class
	public static void main(String[] args) {
		LoginServer loginServer1 = new LoginServer();
		BoundedBuffer<LoginPacket> bb = new BoundedBuffer(10);
		
		
		String inputUsername = "Dicko";
		String inputPassword = "password123";
		Login clientLogin = new Login();
		clientLogin.setUsername(inputUsername);
		clientLogin.setPassword(inputPassword);
		try {
			bb.put(clientLogin.send());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		LoginPacket received = null;
		try {
			received = bb.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (loginServer1.login(received)) {
			Thread mainTester = new Thread(new Server(Server.Type.PRODUCTION));
			mainTester.start();
		} else {
			System.out.println("Wrong Username and/or Password");
		}
		
		
		
	}
}
