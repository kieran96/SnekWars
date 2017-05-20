package Server;


import util.BoundedBuffer;
import util.Snake;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The worker class of the entire project. We expect to load this file first then
 * fire all relevant threads to start the snake game. 
 * @author Mitchell & Kieran 
 *
 */
public class Server implements Runnable{
	Thread p1;
	Thread p2;
	Thread c1;
	Thread c2;
	BoundedBuffer bb;
	ArrayList<Snake> players;
	public enum Type {
			TESTING, 
			PRODUCTION
	};
	Type type;
	//TODO Create main loop from given pseudo code
	//TODO Create thread handler and monitors for server threads.
	/**
	 * Constructor for Server; 
	 */
	public Server(Type type, ArrayList<Snake> playerList) {
		this.type = type;
		System.out.println("Constructing server based on the logic of a " + this.type.toString() + " server");
		bb = new BoundedBuffer(500);
		this.players = playerList;
	}
	
	public static Thread[] getPlayers() {
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
		return threadArray;
	}
	
	public static int playerCount() {
		return Thread.activeCount();
	}
	

	
	@Override
	public void run() {
		//while (true) {
		System.out.println("thread:"+Thread.activeCount());
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
		//for (int i=0; i<Thread.activeCount(); i++) {

		/*
		 * 	while not done
		 * 	for each player in world
		 * 		if input exists
		 * 		get player command
		 * 		execute player command
		 * 		tell player of the results
		 * 	simulate the world
		 * 	broadcast to all players
		 */
		//TODO: Need to start two threads here; One for the Player Fetching - One for GUI Updates.
		int processors = Runtime.getRuntime().availableProcessors() * 2;
		ExecutorService e = Executors.newFixedThreadPool(processors);	
/*
		e.submit(new Thread(new testProducer(bb), "p1"));
		e.submit(new Thread(new testProducer(bb), "p2"));
		e.submit(new Thread(new testProducer(bb), "p3"));
		e.submit(new Thread(new testProducer(bb), "p4"));
		e.submit(new Thread(new testProducer(bb), "p5"));
		e.submit(new Thread(new testProducer(bb), "p6"));

		e.submit(new Thread(new testConsumer(bb), "c1"));
		e.submit(new Thread(new testConsumer(bb), "c2"));
		e.submit(new Thread(new testConsumer(bb), "c3"));
		e.submit(new Thread(new testConsumer(bb), "c4"));
        */
		if(this.type == Type.TESTING) {
//			Snake[] nSnake = new Snake[100];
//			for(int i = 0; i<nSnake.length; i++) {
//				nSnake[i] = new Snake(new int[0][0], true);
//			} //fake array of snakes for debugging.
			/*
			 * Note:
			 * 
			 * Producers must be allocated to the Thread Pool first, if 8 Consumers are loaded into the pool before a 
			 * producer is, none of them will complete before the re-allocation of threads done by the pool can be fired. 
			 * Therefore don't use more than availableProcessors() threads.
			 * 
			 */
//			for(int i = 1; i<=2; i++) {
//
//				//create i Threads to producer moves from the players (the more of these, the more MovesPerSecond).
//				e.submit(new Thread(new MoveHandler(bb, MoveHandler.Role.PRODUCER, nSnake), ("Producer_"+i)));	
//				
//			}
//			for(int i = 1; i<=(4); i++){ 
//				//create i Threads to handle load of Producer(s)
//				e.submit(new Thread(new MoveHandler(bb, MoveHandler.Role.CONSUMER, nSnake), ("Consumer_"+i)));				
//			}

			
			//Change to a Future task, the future of which returning as an int (int as "Length of Snakes") which will dictate the
			//amount of thread re-allocation to be done - to allow for more Snakes running concurrently.
			System.out.println(e.toString());
			
//			for (Thread t : getPlayers()) {
//				if (t.isAlive()) {
//					System.out.println("current thread:"+ t + " name: ");
//					//might need to encapsulate MoveHandler within it's own thread, therefore we can access the MoveHandler from our custom
//					//thread.
//
//				}
//				if(t instanceof MainThread) {
//					MainThread newT = (MainThread) t;
//					newT.toString();
//				}
//			}
			
		} else if(this.type == Type.PRODUCTION) {
	        Game theGame = new Game(bb, players);
	        theGame.init();
	        theGame.mainLoop();			
		}

	}

}
