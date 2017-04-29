package Server;

public class MainThread {
	/**
	 * The main body of the project that will run when the overall project runs
	 * 
	 * Requirements:
	 * 
	 * Needs to first load the login screen that allows the 'real players' to login 
	 * Then send us an amount of players to generate.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
//		Thread mainServer = new Thread(new Server());
//		mainServer.start();
		
		Thread mainTester = new Thread(new Server());
		mainTester.start();
        
	}

}
