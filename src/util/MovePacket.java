package util;

public class MovePacket {
	private int[][] moveLocation;
	private int[][] theSnake;
	public MovePacket() {
		
	}
	public MovePacket(int[][] mL, int[][] s) {
		this.moveLocation = mL;
		this.theSnake = s;
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
