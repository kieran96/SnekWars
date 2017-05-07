package Server;

public class Snake {
	private int[][] move = new int[0][0];
	private int[][] me;
	private boolean AISTATUS;
	public Snake() {
		//Base snake created;
		System.out.println("Base Sanke created with no relevance");
	}
	public Snake(int[][] snake) {
		this.me = snake;
		System.out.println("Base snake created with a snake location and no brain sense.");
	}
	public Snake(int[][] snake, boolean isAIPlayer) {
		this.me = snake;
		this.AISTATUS = isAIPlayer;
		//System.out.println("Base snake created with a snake location. Is AI? : " + isAIPlayer);
	}
	public void makeMove() {
		//generate a random Move if AI, if not wait for player!
		double random = Math.random();
		
		if(AISTATUS) {
			if(random <=0.25) {
				move = new int[0][0];
			} else if(random <= 0.50) {
				move = new int[1][0];
				
			} else if(random <= 0.75) {
				move = new int[0][1];
			} else if(random <= 1) {
				move = new int[1][1];
			}			
		} else {
			//handle move
		}

	}
	public int[][] getMove() {
		return this.move;
	}
	public boolean getAIStatus() {
		return this.AISTATUS;
	}
	public int[][] getSnakeLocale() {
		return this.me;
	}
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("The expected Move this Snake will make next tick: " + move + "\n");
		b.append("The AI Status of this Snake: " + this.AISTATUS + "\n");
		b.append("The Snake's location" + this.me);
		return b.toString();
	}
}
