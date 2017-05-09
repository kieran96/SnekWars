package Server;

import java.io.File;
import java.util.concurrent.ConcurrentNavigableMap;

import org.mapdb.DB;
import org.mapdb.DBMaker;

public class LoginServer {
	//TODO Use the code given to us to create player database
	DB db = DBMaker.newFileDB(new File("testdb"))
            .closeOnJvmShutdown()
            .make();
    ConcurrentNavigableMap<String, String> users = db.getTreeMap("UserCollection");
	//TODO: From Mitchell: MainThread wants an amount of fake players to load; 
	//TODO: From Mitchell: as-well as a list of real players sent back to us.
	/**
	 * 
	 */
	public LoginServer() {
		//users<Username, Password>
		users.put("Dicko", "password123");
		db.commit();
		
	}
	
	/**
	 * What the server will probably run when we create an instance of LoginServer from Server.java
	 * @return True if login is OK and we should continue with loading server. False is not OK to start.
	 * @see Server.java
	 */
	public boolean login(String username, String password) {
		if (users.get(username).equals(password)) {
			return true;
		}
		return false;
	}
}
