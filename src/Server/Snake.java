package Server;

import java.awt.Canvas;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import util.BoundedBuffer;
import util.MovePacket;

public class Snake implements Runnable {
	
	public final static int EMPTY = 0;
	public final static int FOOD_BONUS = 1;
	public final static int FOOD_MALUS = 2;
	public final static int BIG_FOOD_BONUS = 3;
	public final static int SNAKE = 4;
	public final static int SNAKE_HEAD=5;
	//private int[][] grid = null;
	private int[][] enemysnake = null;
	private boolean alive = false;
	public int counter = 0;
	private int direction = 2; //FORCE LEFT
	private int grow = 0;
	private int headposX = -1;
	private int headposY = -1;
	private int bonusTime = 0;
	private BoundedBuffer bb;
	private String name = null;

	@Override
	public void run() {
		while (true) {
			System.out.println("thread:"+Thread.currentThread());
			System.out.println("snake:"+this.name);
			System.out.println("snakeposX:"+headposX);
			System.out.println("snakeposY:"+headposY);
			/*try {
				moveenemySnake(enemysnake);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null,
						ex);
			}
		}
	}
	
	public Snake(String name) {
		this.name = name;
		enemysnake = new int[Game.getGameSize() * Game.getGameSize()][2];
		createSnake();
	}
	
	private void createSnake() {
		Random rand = new Random();
		int randomX = rand.nextInt(20);
		int randomY = rand.nextInt(20);
		enemysnake[0][0] = Game.getGameSize()/2;
		enemysnake[0][1] = Game.getGameSize()/2;
		headposX = randomX;
		headposY = randomY;
		Game.grid[randomX][randomY] = SNAKE_HEAD;
		Game.placeBonus(FOOD_BONUS);
		alive = true;
	}
	
	
	private void moveenemySnake(int[][] snake) throws InterruptedException {
		if (direction < 0) {
			return;
		}
		int ymove = 0;
		int xmove = 0;
			xmove = 0;
			ymove = -1;
			//MovePacket up = new MovePacket(new int[snake[0][0]][snake[0][1]], xmove, ymove);
			//bb.put(up);
		int tempx = snake[0][0];
		int tempy = snake[0][1];
		int fut_x = snake[0][0] + xmove;
		int fut_y = snake[0][1] + ymove;
		/*
		 * Commented code here states that if the snake leaves the screen; game over for that snake.
		 */
		//		 if ((fut_x < 0) || (fut_y < 0) || (fut_x >= gameSize)
		//		 || (fut_y >= gameSize)) {
		//		 gameOver();
		//		 return;
		//		 }
		/*
		 * Code that handles when the snake reaches the side of the screen - move to other side.
		 */
		if(fut_x < 0)
			fut_x = Game.getGameSize() - 1;
		if(fut_y < 0)
			fut_y = Game.getGameSize() - 1;
		if(fut_x >= Game.getGameSize())
			fut_x = 0;
		if(fut_y >= Game.getGameSize())
			fut_y = 0;


		if (Game.grid[fut_x][fut_y] == FOOD_BONUS)
		{
			grow ++;
			Game.placeBonus(FOOD_BONUS);
		}
		else if(Game.grid[fut_x][fut_y] == BIG_FOOD_BONUS)
			grow += 3;
		snake[0][0] = fut_x;
		snake[0][1] = fut_y;
		if ((Game.grid[snake[0][0]][snake[0][1]] == SNAKE)) {
			Game.gameOver();
			return;
		}
		Game.grid[tempx][tempy] = EMPTY;
		int snakex, snakey, i;
		for (i = 1; i < Game.getGameSize() * Game.getGameSize(); i++) {
			if ((snake[i][0] < 0) || (snake[i][1] < 0)) {
				break;
			}
			Game.grid[snake[i][0]][snake[i][1]] = EMPTY;
			snakex = snake[i][0];
			snakey = snake[i][1];
			snake[i][0] = tempx;
			snake[i][1] = tempy;
			tempx = snakex;
			tempy = snakey;
		}
		Game.grid[snake[0][0]][snake[0][1]] = SNAKE_HEAD;
		for (i = 1; i < Game.getGameSize() * Game.getGameSize(); i++) {
			if ((snake[i][0] < 0) || (snake[i][1] < 0)) {
				break;
			}
			Game.grid[snake[i][0]][snake[i][1]] = SNAKE;
		}
		bonusTime --;
		if (bonusTime == 0)
		{
			for (i = 0; i < Game.getGameSize(); i++)
			{
				for (int j = 0; j < Game.getGameSize(); j++)
				{
					if(Game.grid[i][j]==BIG_FOOD_BONUS)
						Game.grid[i][j]=EMPTY;
				}
			}
		}
		if (grow > 0) {
			snake[i][0] = tempx;
			snake[i][1] = tempy;
			Game.grid[snake[i][0]][snake[i][1]] = SNAKE;
			if(Game.getScore()%10 == 0)
			{
				Game.placeBonus(BIG_FOOD_BONUS);
				bonusTime = 100;
			}
			grow --;
		}
	}

}
