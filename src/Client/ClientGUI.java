package Client;

import Server.Login;
import Server.LoginServer;
import Server.Server;
import util.*;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

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
		//If password is correct run rest of game.
		if (loginServer1.login(received)) {
			System.out.println("Username/Password match.");
			ArrayList<Snake> snakes = new ArrayList<Snake>();
			snakes.add(new Snake(received.getUsername(), new MoveSet(new MoveKey(KeyEvent.VK_UP), new MoveKey(KeyEvent.VK_LEFT), new MoveKey(KeyEvent.VK_DOWN), new MoveKey(KeyEvent.VK_RIGHT))));
			snakes.add(new Snake("PlaceHolder", new MoveSet(new MoveKey(KeyEvent.VK_W), new MoveKey(KeyEvent.VK_A), new MoveKey(KeyEvent.VK_S), new MoveKey(KeyEvent.VK_D))));
			Thread mainTester = new Thread(new Server(Server.Type.PRODUCTION, snakes));
			mainTester.start();
		} else {
			System.out.println("Wrong Username and/or Password");
		}
		
		
		
	}
}
