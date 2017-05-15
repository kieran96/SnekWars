package util;

import java.util.List;
import java.util.ArrayList;
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
		dataStorage = new ArrayList<MovePacket>(initSize);
	}
	/**
	 * Get a MovePacket.java from the BoundedBuffer.
	 * @return a move packet from the BoundedBuffer as a MovePacket.
	 * @throws InterruptedException 
	 * @see MovePacket
	 */
	public synchronized MovePacket get() throws InterruptedException {
		while(isEmpty()) {
			//System.out.println(Thread.currentThread().toString() + " is trying to access the BoundedBuffer, to get; but is empty . . .");
			this.wait();
		}
		//They now have access to the array: We can actually do work.
		
		
		this.currentPackets--;
		//System.out.println("A packet is about to be removed from the buffer by: " + Thread.currentThread().getName() + " current size: " + this.currentPackets);
		
		//Note - This notifyAll wakes threads but returns first before any other thread can interrupt the return.
		this.notifyAll();
		return this.dataStorage.remove(0);
	}
	public synchronized void put(MovePacket mp) throws InterruptedException {
		while(this.currentPackets == this.maxSize) {
			System.out.println(Thread.currentThread().toString() + " is trying to access the BoundedBuffer, to put; but is full. . .");
			this.wait();
		}
		//They now have access to the array: We can actually do work.
		
		
		//Add the packet to the List (Should be adding to the end of the list).
		this.currentPackets++;
		//System.out.println("A packet is about to be put into the buffer by: " + Thread.currentThread().getName() + " current size: " + this.currentPackets);
		this.dataStorage.add(mp);
		
		//Notify everyone that is waiting
		this.notifyAll();
	}
	public boolean isEmpty() {
		return ((this.currentPackets > 0) ? false : true);
	}
	public int getCurrentPacketsSize() {
		return this.currentPackets;
	}
	public int getMaxSize() {
		return this.maxSize;
	}
	public void setMaxSize(int newMaxSize) {
		this.maxSize = newMaxSize;
	}
	public String toString() {
		return dataStorage.toString();
	}
}
