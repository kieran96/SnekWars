package Server;

import Testing.testConsumer;
import Testing.testProducer;
import util.BoundedBuffer;
import java.util.concurrent.Executor;
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
	//TODO Create main loop from given pseudo code
	//TODO Create thread handler and monitors for server threads.
	/**
	 * Constructor for Server; 
	 */
	public Server() {
		//TODO: Get back to me @Richard on how this login is going to run. Do I need to call .run()?
		//TODO: How am I getting the information back from the LoginServer?				
		//login
		
//		LoginServer login = new LoginServer();
//		if(login.login()) {
//			//allow for starting of the server.
//			
//		}
		bb = new BoundedBuffer(5);

		
	}
	
	@Override
	public void run() {
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
		
		ExecutorService e = Executors.newFixedThreadPool(8);	
		e.submit(new Thread(new testProducer(bb), "p1"));
		e.submit(new Thread(new testProducer(bb), "p2"));
		e.submit(new Thread(new testProducer(bb), "p3"));
		e.submit(new Thread(new testProducer(bb), "p4"));

		e.submit(new Thread(new testConsumer(bb), "c1"));
		e.submit(new Thread(new testConsumer(bb), "c2"));
		
		//		p1 = new Thread(new testProducer(bb));
//		p1.start();
//		p2 = new Thread(new testProducer(bb));
//		
//		c1 = new Thread(new testConsumer(bb));
//		c1.start();

	}

}
