package Server;

import java.util.concurrent.Callable;

public class Login implements Callable {
	/*
	 * Currently i want the gui to set the username and password for its login thread and
	 * then add the login thread to a queue to get called upon in the server to check if login is valid
	 */
	String username = null;
	String password = null;
	
	public Login() {
		
	}

	@Override
	public Boolean call() throws Exception {
		// I want this to call upon the instance of LoginServer created within server or main thread and
		// have it be semaphored so that one one login request can be processed by the LoginServer at a time.
		//return LoginServer.login(username, password);
		return false;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
