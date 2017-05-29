package util;

public class LoginPacket {
	
	String username;
	String password;
	
	public LoginPacket(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String toString() {
		if(username != null) {
			return username;
		} else {
			return "No Username Found.";
		}
	}

}
