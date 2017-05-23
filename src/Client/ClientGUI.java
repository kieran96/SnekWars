package Client;

import Server.Login;
import Server.LoginServer;
import Server.Server;
import util.*;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class ClientGUI implements ActionListener{
	//TODO Separate given Snake code which handles the gui
	//TODO main(String[] args) should be moved to it's own class
	
	JFrame frame;
	JPanel panel;
	JLabel label1, label2;
	JTextField username;
	JPasswordField password;
	JButton submit;
	JLabel feedback;
	
	static String inputUsername = "Dicko";
	static String inputPassword = "password123";
	
	public ClientGUI() {
		frame = new JFrame();
		frame.setSize(300, 100);
		
		panel = new JPanel();
		panel.setLayout(new GridLayout(3,1));
		label1 = new JLabel("Username");
		label2 = new JLabel("Password");
		username = new JTextField(15);
		password = new JPasswordField(20);
		submit = new JButton("Submit");
		submit.addActionListener(this);
		
		panel.add(label1);
		panel.add(username);
		panel.add(label2);
		panel.add(password);
		panel.add(submit);
		
		frame.add(panel);
		frame.setTitle("User Login");
		frame.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (username.getText() == inputUsername && password.getPassword().toString() == inputPassword) {
			
		}
			
	}
	
	
	
	public static void main(String[] args) {
		LoginServer loginServer1 = new LoginServer();
		BoundedBuffer<LoginPacket> bb = new BoundedBuffer(10);
		
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
