package Server;


import util.LoginPacket;

public class Login {
	/*
	 * Currently i want the gui to set the username and password for its login thread and
	 * then add the login thread to a queue to get called upon in the server to check if login is valid
	 */
	String username = null;
	String password = null;
	
	public Login() {		
	}
	
	//Login packet shit
	public LoginPacket send() {
		// I want this to call upon the instance of LoginServer created within server or main thread and
		// have it added to BoundedBuffer so that one one login request can be processed by the LoginServer at a time.
		
		return new LoginPacket(username, password);
		//return false;
	}
	public String getUsername() { return this.username; }

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
