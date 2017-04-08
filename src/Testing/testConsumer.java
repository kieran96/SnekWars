package Testing;

import util.BoundedBuffer;

public class testConsumer implements Runnable {
	BoundedBuffer myB;
	public testConsumer(BoundedBuffer b) {
		myB = b;
	}
	@Override
	public synchronized void run() {
		// TODO Auto-generated method stub
		
		try {
			while(true) {
				myB.get();
				//System.out.println("Got this packet from the Buffer: " + myB.get().toString());
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
