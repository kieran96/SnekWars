package util;

public class MovePacket {
	private int[][] moveLocation;
	private int[][] theSnake;
	
	int xmove;
	int ymove;
	
	public MovePacket() {
		
	}
	public MovePacket(int[][] mL, int[][] s) {
		this.moveLocation = mL;
		this.theSnake = s;
	}
	public MovePacket(int[][] snake, int xmove, int ymove) {
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
	public int[][] getTheSnake() {
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
		if(moveLocation != null) b.append(moveLocation.length);
		if(theSnake != null) b.append(theSnake.length);
		return b.toString();
	}
	@Override
    public boolean equals(Object obj) {
        
		
		return true;
    }
}
