package util;

import Server.Snake;

public class MovePacket {
	private int[][] moveLocation;
	private Snake theSnake;
	
	int xmove;
	int ymove;
	
	public MovePacket() {
		
	}
	public MovePacket(int[][] mL, Snake s) {
		this.moveLocation = mL;
		this.theSnake = s;
	}
	public MovePacket(Snake snake, int xmove, int ymove) {
		this.theSnake = snake;
		this.xmove = xmove;
		this.ymove = ymove;
	}
	public MovePacket(MovePacket mp) {
		this.moveLocation = mp.getMoveLocation();
		this.theSnake = mp.getTheSnake();
	}
	
	public int[][] getMoveLocation() {
		return moveLocation;
	}
	public Snake getTheSnake() {
		return theSnake;
	}
	public int getXMove() {
		return xmove;
	}
	public int getYMove() {
		return ymove;
	}
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		if(moveLocation != null) b.append("MoveLocation: " + moveLocation.length + " ");
		if(theSnake != null) b.append("The Snake: " + theSnake.toString() + " ");
		b.append("xmove: " + xmove + " ");
		b.append("ymove: " + ymove);
		return b.toString();
	}
	@Override
    public boolean equals(Object obj) {
        if(this == null || obj == null) {
        	return false;
        }
        if(!(obj instanceof MovePacket)) {
        	return false;
        }
		MovePacket move = (MovePacket) obj;
		if(move.getXMove() == this.getXMove() 
				&& (move.getYMove() == this.getYMove())
				&& (move.getMoveLocation() == this.getMoveLocation())
				&& (move.getTheSnake() == this.getTheSnake())) {
			return true;
		}
		return false;
    }
}
