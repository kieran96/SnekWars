package Testing;

import util.BoundedBuffer;

public class testConsumer implements Runnable {
	BoundedBuffer myB;
	static int amount = 0;
	public testConsumer(BoundedBuffer b) {
		myB = b;
	}
	@Override
	public synchronized void run() {
		// TODO Auto-generated method stub
		
		try {
			while(true) {
				myB.get();
				amount++;
				if((amount % 10000000) == 0) {
					System.out.println("Amount of times we have 'got' a packet: " + amount); 
				}
				//System.out.println("Got this packet from the Buffer: " + myB.get().toString());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
