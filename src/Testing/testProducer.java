package Testing;

import util.BoundedBuffer;
import util.MovePacket;

public class testProducer implements Runnable {
	BoundedBuffer myB;
	static int[][] a = {{0, 1, 2}, {2, 1, 0}};
	static int[][] b = {{2, 1, 0}, {0, 1, 2}};
	static int amount = 0;
	public testProducer(BoundedBuffer b) {
		myB = b;
	}
	@Override
	public synchronized void run() {
		// TODO Auto-generated method stub
		
		try {
			while(true) { 
				myB.put(new MovePacket());
				amount++;
				if((amount % 10000000) == 0) {
					System.out.println("Amount of times we have 'put' a packet: " + amount);
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
