package Server;

import java.awt.Canvas;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import util.BoundedBuffer;
import util.MovePacket;

public class Snake {
	
	public final static int EMPTY = 0;
	public final static int FOOD_BONUS = 1;
	public final static int FOOD_MALUS = 2;
	public final static int BIG_FOOD_BONUS = 3;
	public final static int SNAKE = 4;
	public final static int SNAKE_HEAD=5;
	//private int[][] grid = null;
	public int[][] enemysnake = null;
	public boolean alive = false;
	public int counter = 0;
	public int direction = 1; //FORCE LEFT
	public int grow = 0;
	public int headposX = -1;
	public int headposY = -1;
	public int bonusTime = 0;
	public BoundedBuffer bb;
	public String name = null;
	public boolean AI = true;
	private int[][] move;

	
	public void newDirection() {
		Random random = new Random();
		int randomTick = random.nextInt(2);
		if (randomTick == 1) {
			int getFacePos = random.nextInt(3);
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
	public Snake(String name, boolean AIStatus) {
		this.name = name;
		enemysnake = new int[Game.getGameSize() * Game.getGameSize()][2];
		/*for (int i = 0; i < Game.getGameSize(); i++) {
			for (int j = 0; j < Game.getGameSize(); j++) {
				Game.grid[i][j] = EMPTY;
			}
		}*/
		for (int i = 0; i < Game.getGameSize() * Game.getGameSize(); i++) {
			enemysnake[i][0] = -1;
			enemysnake[i][1] = -1;
		}
		//enemysnake[0][0] = Game.getGameSize()/2;
		//enemysnake[0][1] = Game.getGameSize()/2;
		//Game.grid[Game.getGameSize()/2][Game.getGameSize()/2] = SNAKE_HEAD;
		Game.placeBonus(FOOD_BONUS);
		createSnake();
		this.AI = AIStatus;
	}
	public Snake(int[][] snake) {
		this.enemysnake = snake;
	}
	//actually appends to the board a new snake.
	private void createSnake() {
		Random rand = new Random();
		int randomX = rand.nextInt(Game.getGameSize());
		int randomY = rand.nextInt(Game.getGameSize());
		//enemysnake[0][0] = Game.getGameSize()/2;
		//enemysnake[0][1] = Game.getGameSize()/2;
		headposX = randomX;
		headposY = randomY;
		enemysnake[0][0] = randomX;
		enemysnake[0][1] = randomY;
		Game.grid[randomX][randomY] = SNAKE_HEAD;
		Game.placeBonus(FOOD_BONUS);
		System.out.println("A snake spawned at: x:"+randomX+" & y:"+randomY);
		alive = true;
	}
	public int getScore() {
		int score = 0;
		for (int i = 0; i < Game.getGameSize() * Game.getGameSize(); i++) {
			if ((enemysnake[i][0] < 0) || (enemysnake[i][1] < 0)) {
				break;
			}
			score++;
		}
		return score;
	}

	@Override 
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(this.name + " " + move);
		return b.toString();
	}

}