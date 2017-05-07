package Server;

public class MainThread extends Thread {
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
	private MoveHandler context;
	
	public MainThread() {
		super();
		System.out.println("MainThread constructed with no context.");
	}
	public MainThread(String name) {
		super(name);
		System.out.println("MainThread constructed with the name: " + name + " and no context.");
	}
	public MainThread(MoveHandler context) {
		super();
		this.context = context;
		System.out.println("MainThread constructed with no name and the context: " + this.context.toString());
	}
	public MainThread(MoveHandler context, String name) {
		super(name);
		this.context = context;
		System.out.println("MainThread constructed with the name: " + name + " and the context: " + this.context.toString());
	}
	/*
	 * Copy constructor for MainThread, if this runs - there may be an issue.
	 */
	public MainThread(MainThread mt) {
		System.out.println("Copy constructor on MainThread called!");
		this.context = mt.context;
	}
	@Override
	public void start() {
		super.start();
	}
//	public static void main(String[] args) {
//		Thread mainServer = new Thread(new Server());
//		mainServer.start();
//		
//	Thread mainTester = new Thread(new Server(Server.Type.TESTING));
//	mainTester.start();
//        
//	}

}
