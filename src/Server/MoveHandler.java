package Server;

import Testing.IllegalBoundedBufferException;
import Testing.IllegalRoleException;
import util.BoundedBuffer;
import util.MovePacket;

public class MoveHandler implements Runnable {
	private BoundedBuffer bb;
	static int amountProduced = 0;
	static double amountConsumed = 0;
	long runningTime = System.currentTimeMillis();
	
	enum Role {CONSUMER, PRODUCER};
	private Role role;
	Snake[] playerList;
	
	public MoveHandler() {
		System.out.println("Constructing MoveHandlerThread with no BoundedBuffer, no role and no PlayerList");
	}
	public MoveHandler(BoundedBuffer bb) {
		this.bb = bb;
		System.out.println("Constructing MoveHandlerThread with a BoundedBuffer and with no role.");
	}
	public MoveHandler(Role role) {
		this.role = role;
		System.out.println("Constructing MoveHandlerThread with no BoundedBuffer and a " 
				+ this.role.toString() 
				+ " role and no PlayerList");
	}
	public MoveHandler(Snake[] playerList) {
		this.playerList = playerList;
		System.out.println("Constructing MoveHandlerThread with no BoundedBuffer, no Role and a PlayerList of the size: "
				+ this.playerList.length);
	}
	public MoveHandler(BoundedBuffer bb, Role role) {
		this.bb = bb;
		this.role = role;
		System.out.println("Constructing MoveHandlerThread with a BoundedBuffer, a " 
				+ this.role.toString() 
				+ " role and no PlayerList");
	}	
	public MoveHandler(Role role, Snake[] playerList) {
		this.role = role;
		this.playerList = playerList;
		System.out.println("Constructing MoveHandlerThread with no BoundedBuffer,  a " 
				+ this.role.toString() 
				+ " role and a PlayerList of the size"
				+ this.playerList.length);
	}
	public MoveHandler(BoundedBuffer bb, Snake[] playerList) {
		this.bb = bb;
		this.playerList = playerList;
		System.out.println("Constructing MoveHandlerThread with a BoundedBuffer, no role and a PlayerList of the size: " 
				+ this.playerList.length);
	}
	public MoveHandler(BoundedBuffer bb, Role role, Snake[] playerList) {
		this.bb = bb;
		this.role = role;
		this.playerList = playerList;
		System.out.println("Constructing MoveHandlerThread with a BoundedBuffer and a " 
				+ this.role.toString() 
				+ " role and a PlayerList of the size " 
				+ this.playerList.length);

	}
	public synchronized BoundedBuffer getBb() {
		return bb;
	}
	public synchronized void setBb(BoundedBuffer bb) {
		this.bb = bb;
	}
	public synchronized Role getRole() {
		return role;
	}
	public synchronized void setRole(Role role) {
		this.role = role;
	}
	public synchronized Snake[] getSnakeList() {
		return this.playerList;
	}
	public synchronized void setPlayerList(Snake[] playerList) {
		this.playerList = playerList;
		
	}
	
	@Override
	public void run() {
		if(this.role == Role.CONSUMER) {
			//should contain the code to "update the board"
			//attempt to consume
			while(true) {
				try {
					if(bb == null) {
						throw new IllegalBoundedBufferException("Illegal BoundedBuffer state");
					} else {
						bb.get();
						amountConsumed++;
						if(amountConsumed % 10000 == 0) {
							System.out.println("The current amount of Packets processed: " 
									+ amountConsumed + " Current run-time: " 
									+ ((System.currentTimeMillis() - runningTime) / 1000) 
									+ " seconds. Average packets per second: "
									+ (amountConsumed / ((System.currentTimeMillis() - runningTime) / 1000)));
						}
					}
					//do work here
//					this.delay(0.05);
				} catch(InterruptedException | IllegalBoundedBufferException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}				
			}
		} 
		else if(this.role == Role.PRODUCER) {
			try {
				if(bb == null) {
					throw new IllegalBoundedBufferException("Illegal BoundedBuffer state");
				} else {
					//MovePacket should come to us from all n players and 
					//then placed into a new MovePacket and sent to the BB.
					while(true) {
						this.delay(1);
						for(int i = 0; i<playerList.length; i++) {
							if(playerList[i] != null) {
								bb.put(new MovePacket(playerList[i].getMove(), playerList[i]));
								amountProduced++;							
							}
						}						
					}
				}
			} catch(IllegalBoundedBufferException | InterruptedException e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		} 
		else {
			//means we have no role!
			try {
				throw new IllegalRoleException("Invalid attempt to run a MoveHandler without a designated role.");
			} catch (IllegalRoleException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	public void delay(double seconds) {
		double actualDelay = (seconds * 1000);
		try {
			Thread.sleep((long) actualDelay);
		} catch(Exception e) {
			System.out.println(e.getStackTrace());
		}
	}
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("MoveHandler \n BoundedBuffer size: " + this.getBb().getMaxSize());
		b.append("\n Role: " + this.getRole().toString());
		b.append("\n Player list size: " + this.getSnakeList().length);
		
		return b.toString();
	}
}
