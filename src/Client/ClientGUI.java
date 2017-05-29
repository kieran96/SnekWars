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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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

	//TODO Add Login code to login GUI

	public static void main(String[] args) {

		LoginServer loginServer1 = new LoginServer();
		BoundedBuffer<LoginPacket> bb = new BoundedBuffer<LoginPacket>(10);
        BoundedBuffer<LoggingPacket> loggingPacketBoundedBuffer = new BoundedBuffer<LoggingPacket>(10000);
		String inputUsername = "Dicko";
		String inputPassword = "password123";
		Login clientLogin = new Login();
		clientLogin.setUsername(inputUsername);
		clientLogin.setPassword(inputPassword);
		try {
                bb.put(clientLogin.send());
                loggingPacketBoundedBuffer.put(new LoggingPacket("Put into LoginPacketBuffer: Username: " + clientLogin.getUsername()));

            LoginPacket received = null;
            received = bb.get();
            loggingPacketBoundedBuffer.put(new LoggingPacket("Got this packet from the LoginPacketBuffer: " + received.toString()));

            //If password is correct run rest of game.
            if (loginServer1.login(received)) {
                try {
                    loggingPacketBoundedBuffer.put(new LoggingPacket("Username/Password match."));
                    List<Snake> snakes = new CopyOnWriteArrayList<Snake>();
                    snakes.add(new Snake(received.getUsername(), new MoveSet(new MoveKey(KeyEvent.VK_UP), new MoveKey(KeyEvent.VK_LEFT), new MoveKey(KeyEvent.VK_DOWN), new MoveKey(KeyEvent.VK_RIGHT))));
                    snakes.add(new Snake("PlaceHolder", new MoveSet(new MoveKey(KeyEvent.VK_W), new MoveKey(KeyEvent.VK_A), new MoveKey(KeyEvent.VK_S), new MoveKey(KeyEvent.VK_D))));

                    for(Snake snake : snakes) {
                        loggingPacketBoundedBuffer.put(new LoggingPacket("Added " + snake.name + " to HumanPlayersList"));
                    }

                    Thread mainTester = new Thread(new Server(Server.Type.PRODUCTION, snakes, loggingPacketBoundedBuffer));

                    loggingPacketBoundedBuffer.put(new LoggingPacket("About to start mainThread with these HumanPlayers: " + snakes.toString()));

                    mainTester.start();
                } catch(Exception e) { e.printStackTrace(); }
            } else {
                loggingPacketBoundedBuffer.put(new LoggingPacket("Wrong Username/Password match"));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}
}
