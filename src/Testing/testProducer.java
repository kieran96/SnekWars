package Testing;

import util.BoundedBuffer;
import util.MovePacket;

public class testProducer implements Runnable {
	BoundedBuffer myB;
	static int a = 0;
	public testProducer(BoundedBuffer b) {
		myB = b;
	}
	@Override
	public synchronized void run() {
		// TODO Auto-generated method stub
		try {
			while(true) { 
				myB.put(new MovePacket(new int[a++][a++], new int[a++][a++]));
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
