package util;

import util.Snake;

public class MovePacket {
	private Snake theSnake;

	public MovePacket() {
		
	}
	/*
	 * The MovePacket currently in use by the MoveHandler: 11-May-2017.
	 */
	public MovePacket(Snake snake) {
		this.theSnake = snake;
 	}
	public MovePacket(MovePacket mp) {
		this.theSnake = mp.getTheSnake();
	}

	public Snake getTheSnake() {
		return theSnake;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		if(theSnake != null) b.append("The Snake: ").append(theSnake.toString()).append(" ");
		return b.toString();
	}
	@Override
    public boolean equals(Object obj) {
        if(obj == null) {
        	return false;
        }
        if(!(obj instanceof MovePacket)) {
        	return false;
        }
		return (this.theSnake.equals(((MovePacket) obj).getTheSnake()));
    }
}
