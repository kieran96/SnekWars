package util;

import java.util.Random;

import Server.Game;

public class Snake {
	
	//private int[][] grid = null;
	public int[][] boardLocation = null;
	public boolean alive = false;
	public int direction = 1; //FORCE LEFT
	public int lastDirection = -1;
	public int grow = 0;
	private int headposX = -1;
	private int headposY = -1;
	private int playerHead;
	public int bonusTime = 0;
	public MoveSet moveSet;
	public BoundedBuffer bb;
	public String name = null;
	public boolean IsAI;
	private int[][] move;
	public int deaths = 0;
	public Snake(String name, boolean AIStatus) {
		this.name = name;
		this.IsAI = AIStatus;
		System.out.println("Snake: " + name + " has been generated, is it AI: " + this.IsAI);
		boardLocation = new int[Game.getGameSize() * Game.getGameSize()][2];
		for (int i = 0; i < Game.getGameSize() * Game.getGameSize(); i++) {
			boardLocation[i][0] = -1;
			boardLocation[i][1] = -1;
		}
		Game.placeBonus(Game.FOOD_BONUS);
		createSnake();
	}
	public Snake(String name, MoveSet moveSet) {
		this.name = name;
		this.IsAI = false;
		this.moveSet = moveSet;

		System.out.println("Snake: " + name + " who is not an AI has the MoveSet:" + moveSet.toString());
	}
	public Snake(Snake sn) {
		this(sn.name, sn.IsAI);
	}

	public void updateMove(MoveKey key) {
		if(key.getKey() == moveSet.getMoveSet()[0].getKey()) {
			if (this.lastDirection != Game.DOWN) {
					this.direction = Game.UP;
			}
		}
		if(key.getKey() == moveSet.getMoveSet()[1].getKey()) {
			if (this.lastDirection != Game.RIGHT) {
				this.direction = Game.LEFT;
			}
		}
		if(key.getKey() == moveSet.getMoveSet()[2].getKey()) {
			if (this.lastDirection != Game.UP) {
				this.direction = Game.DOWN;
			}
		}
		if(key.getKey() == moveSet.getMoveSet()[3].getKey()) {
			if (this.lastDirection != Game.LEFT) {
				this.direction = Game.RIGHT;
			}
		}
	}
	public void newDirection() {
		Random random = new Random();
		int randomTick = random.nextInt(2);
		if (randomTick == 1) {
			int getFacePos = random.nextInt(4);

			switch (getFacePos) {
			case 0:
				if (direction != Game.DOWN) {
					direction = Game.UP;
				}
				break;
			case 1:
				if (direction != Game.UP) {
					direction = Game.DOWN;
				}
				break;
			case 2:
				if (direction != Game.RIGHT) {
					direction = Game.LEFT;
				}
				break;
			case 3:
				if (direction != Game.LEFT) {
					direction = Game.RIGHT;
				}
				break;
			}
		}
	}

	public void createSnake() {
		Random rand = new Random();
		int randomX = rand.nextInt(Game.getGameSize());
		int randomY = rand.nextInt(Game.getGameSize());
		boardLocation = new int[Game.getGameSize() * Game.getGameSize()][2];
		for (int i = 0; i < Game.getGameSize() * Game.getGameSize(); i++) {
			boardLocation[i][0] = -1;
			boardLocation[i][1] = -1;
		}
		if (name.equalsIgnoreCase("placeholder")) {
			headposY = Game.getGameSize()/4;
			headposX = Game.getGameSize()/2;
			boardLocation[0][0] = Game.getGameSize()/4;
			boardLocation[0][1] = Game.getGameSize()/2;
			direction = 3;
		} else if (name.equalsIgnoreCase("dicko")) {
			headposY = Game.getGameSize() - Game.getGameSize()/4;
			headposX = Game.getGameSize()/2;
			boardLocation[0][0] = Game.getGameSize() - Game.getGameSize()/4;
			boardLocation[0][1] = Game.getGameSize()/2;
			direction = 2;
		} else {
			headposX = randomX;
			headposY = randomY;
			boardLocation[0][0] = randomX;
			boardLocation[0][1] = randomY;
		}

//		if(!IsAI) {
//			Game.grid[randomX][randomY] = Game.PLAYER_SNAKE_HEAD;
//		} else {
//			Game.grid[randomX][randomY] = Game.SNAKE_HEAD;
//		}
		Game.placeBonus(Game.FOOD_BONUS);
		System.out.println("The snake: "+name+" spawned at: x:"+headposX+" & y:"+headposY);
		alive = true;
	}
	/*
		Utilities:
	 */
	public void setHead(int index) {
		this.playerHead = index;
	}
	public int getHead() {
		return this.playerHead;
	}
	@Override
	public String toString() {
		String b;
		if(this.moveSet != null) {
			b = (this.name + " AI?: " + this.IsAI + " MoveSet: " + this.moveSet.toString());
		} else {
			b = (this.name + " AI?: " + this.IsAI + " MoveSet: null");

		}
		return b;
	}
	@Override 
	public int hashCode() {
		int prime = 31;
		int result = 0;
		if(this.name != null) {
			result += this.name.hashCode();
		}
		if(this.direction != 0) {
			result += this.direction * 31 * ((this.direction + 30) * 31);
		}
		return result;
	}
	@Override 
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		} 
		if(!(obj instanceof Snake)) {
			return false;
		}
		Snake nsnake = (Snake) obj;
		return (this.hashCode() == nsnake.hashCode());
	}
}