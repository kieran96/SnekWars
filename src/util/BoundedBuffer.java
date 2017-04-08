package util;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

/**
 * Working from the sudo code found here: 
 * https://en.wikipedia.org/wiki/Producer%E2%80%93consumer_problem#Using_monitors
 * @author Mitchell
 *
 */
public class BoundedBuffer {
	private int maxSize;
	private int currentPackets = 0;
	
	private List<MovePacket> dataStorage;
	
	public BoundedBuffer(int initSize) {
		this.maxSize = initSize;
		dataStorage = Collections.synchronizedList(new ArrayList<MovePacket>(initSize));
	}
	/**
	 * Get a MovePacket.java from the BoundedBuffer.
	 * @return a move packet from the BoundedBuffer as a MovePacket.
	 * @throws InterruptedException 
	 * @see MovePacket
	 */
	public synchronized MovePacket get() throws InterruptedException {
		while(isEmpty()) {
			this.wait();
		}
		//They now have access to the array: We can actually do work.
		
		
		this.currentPackets--;
		System.out.println("A packet is about to be removed from the buffer by: " + Thread.currentThread().getName() + " current size: " + this.currentPackets);
		this.notifyAll();
		return this.dataStorage.remove(0);
	}
	public synchronized void put(MovePacket mp) throws InterruptedException {
		while(this.currentPackets == this.maxSize) {
			this.wait();
		}
		//They now have access to the array: We can actually do work.
		
		
		//Add the packet to the List (Should be adding to the end of the list).
		this.currentPackets++;
		System.out.println("A packet is about to be put into the buffer by: " + Thread.currentThread().getName() + " current size: " + this.currentPackets);
		this.dataStorage.add(mp);
		
		//Notify everyone that is waiting
		this.notifyAll();
	}
	private boolean isEmpty() {
		if(this.currentPackets == 0) return true;
		return false;
	}
	public int getCurrentPacketsSize() {
		return this.currentPackets;
	}
	public int getMaxSize() {
		return this.maxSize;
	}
	public void setMaxSize(int nMaxSize) {
		this.maxSize = nMaxSize;
		List<MovePacket> nList = Collections.synchronizedList(new ArrayList<MovePacket>(nMaxSize));

		for(int i = 0; i < dataStorage.size(); i++) {
			nList.add(dataStorage.remove(i));
		}
		
		dataStorage = nList;
	}
}
