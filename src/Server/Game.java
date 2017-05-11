package Server;


import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.MovePacket;
import util.BoundedBuffer;

public class Game implements KeyListener, WindowListener {
	// KEYS MAP
	public final static int UP = 0;
	public final static int DOWN = 1;
	public final static int LEFT = 2;
	public final static int RIGHT = 3;
	// GRID CONTENT
	public final static int EMPTY = 0;
	public final static int FOOD_BONUS = 1;
	public final static int FOOD_MALUS = 2;
	public final static int BIG_FOOD_BONUS = 3;
	public final static int SNAKE = 4;
	public final static int SNAKE_HEAD=5;
	public static int[][] grid = null;
	private static int[][] snake = null;
	//private int[][] enemysnake = null;
	private ArrayList<Snake> snakeList;
	private boolean enemysnakeAlive = false;
	public int counter = 0;
	private int direction = -1;
	private int next_direction = -1;
	private int height = 600;
	private int width = 600;
	//Code to make sizeable game based on screensize
	Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	int screenSize = (int) (dim.height * 0.5);
	private static int gameSize = 40;
	private long speed = 300;
	private Frame frame = null;
	private Canvas canvas = null;
	private Graphics graph = null;
	private BufferStrategy strategy = null;
	private static boolean game_over = false;
	private boolean paused = false;
	private int grow = 0;
	private long cycleTime = 0;
	private long sleepTime = 0;
	private int bonusTime = 0;
	private boolean running = true;
	private BoundedBuffer bb;
	/**
	 * @param args
	 * the command line arguments
	 */

	public Game(BoundedBuffer BB) {
		super();
		this.bb = BB;
		frame = new Frame();
		canvas = new Canvas();
		grid = new int[gameSize][gameSize];
		snake = new int[gameSize * gameSize][2];
		this.snakeList = new ArrayList<Snake>();
		Thread p1 = new Thread(new MoveHandler(bb, MoveHandler.Role.PRODUCER, this.snakeList), "p1");
		Thread c1 = new Thread(new MoveHandler(bb, MoveHandler.Role.CONSUMER, this.snakeList), "c1");
		p1.start();
		c1.start();
	}
	public void init() {
		frame.setSize(screenSize, screenSize);
		frame.setResizable(false);
		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
		canvas.setSize(screenSize, screenSize);
		frame.add(canvas);
		canvas.addKeyListener(this);
		frame.addWindowListener(this);
		frame.dispose();
		frame.validate();
		frame.setTitle("Snake");
		frame.setVisible(true);
		canvas.setIgnoreRepaint(true);
		canvas.setBackground(Color.WHITE);
		canvas.createBufferStrategy(2);
		strategy = canvas.getBufferStrategy();
		graph = strategy.getDrawGraphics();
		initGame();
		renderGame();
	}
	public void mainLoop() {
		while (running) {
			//work here
			/*for (int i=0; i<snakeList.size(); i++) {
				System.out.println("Array index("+i+")"+snakeList.get(i));
			}*/
			counter += 1;
			cycleTime = System.currentTimeMillis();
			//System.out.println(counter);
			/*System.out.println(counter);
			System.out.println("Thread:"+Thread.currentThread().getName());
			System.out.println("Thread:"+Thread.activeCount());
			if (counter == 10) {
				Server.createPlayer();
			}*/
			//System.out.println(game_over);
			//Spawn a new enemy snake every 10sec
			/*if (counter%10 == 0) {
				this.createPlayer();x=
			}*/
			
			//At 10sec mark spawn 20 snakes
			if (counter == 10) {
				for (int i=0; i<2; i++) {		
					this.createPlayer();
				}
			}
			
			if (enemysnakeAlive == true) {
				//System.out.println("moving left");
			}
			//System.out.println("thread:"+Thread.activeCount());
			Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
			Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
			//for (int i=0; i<Thread.activeCount(); i++) {
			for (Thread t:Server.getPlayers()) {
				if (t.isAlive() && t.toString().contains("thread")) {
					//System.out.println("current thread:"+t.getName());
				}
			}
			
			if(!paused && !game_over)
			{
				direction = next_direction;
				try {
					moveSnake();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			renderGame();
			cycleTime = System.currentTimeMillis() - cycleTime;
			sleepTime = speed - cycleTime;
			if (sleepTime < 0)
				sleepTime = 0;
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException ex) {
				Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null,
						ex);
			}
		}
	}
	public void createPlayer() {
//		Thread thread = new Thread(new Snake("Thread-"+playerCount()+""));
//		thread.setName("Thread-"+playerCount()+"");
//		thread.start();

		this.snakeList.add(new Snake(""+(snakeList.size()+1)));
		
		/*Thread thread = new Thread("Thread-"+playerCount()+"");
		System.out.println("Created player:"+thread.getName());
		thread.run();*/
		/*e.submit(new Thread(new testProducer(bb), "p-"+playerCount()+""));
		e.submit(new Thread(new testConsumer(bb), "c-"+playerCount()+""));
		try {
			System.out.println("Move loc:"+bb.get().getXMove());
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		
		//e.submit(new Thread(new testProducer(bb), "p1"));
		//e.submit(new Thread(new testConsumer(bb), "c1"));
	}
	private void initGame() {
		// Initialise tabs
		for (int i = 0; i < gameSize; i++) {
			for (int j = 0; j < gameSize; j++) {
				grid[i][j] = EMPTY;
			}
		}
		for (int i = 0; i < gameSize * gameSize; i++) {
			snake[i][0] = -1;
			snake[i][1] = -1;
		}
		snake[0][0] = gameSize/2;
		snake[0][1] = gameSize/2;
		grid[gameSize/2][gameSize/2] = SNAKE_HEAD;
		for (int i=0; i<100;i++) {
			placeBonus(FOOD_BONUS);
		}
		/*for(Snake snake : this.snakeList) {
			for (int i = 0; i < gameSize * gameSize; i++) {
				snake.enemysnake[i][0] = -1;
				snake.enemysnake[i][1] = -1;
			}
			snake.enemysnake[0][0] = gameSize/2;
			snake.enemysnake[0][1] = gameSize/2;
			grid[gameSize/2][gameSize/2] = SNAKE_HEAD;
			//placeBonus(FOOD_BONUS);
		}*/
	}
	private void renderGame() {
		int gridUnit = screenSize / gameSize;
		canvas.paint(graph);
		do {
			do {
				graph = strategy.getDrawGraphics();
				((Graphics2D)graph).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				// Draw Background
				graph.setColor(Color.WHITE);
				graph.fillRect(0, 0, screenSize, screenSize);
				// Draw snake, bonus ...
				int gridCase = EMPTY;
				for (int i = 0; i < gameSize; i++) {
					for (int j = 0; j < gameSize; j++) {
						gridCase = grid[i][j];
						switch (gridCase) {
						case SNAKE:
							graph.setColor(new Color(17,14,218));
							graph.fillOval(i * gridUnit, j * gridUnit,
									gridUnit, gridUnit);
							break;
						case SNAKE_HEAD:
							graph.setColor(new Color(0,0,0));
							graph.fillOval(i * gridUnit, j * gridUnit,
									gridUnit, gridUnit);
							break;
						case FOOD_BONUS:
							graph.setColor(new Color(3,171,14));
							graph.fillOval(i * gridUnit + gridUnit / 4, j
									* gridUnit + gridUnit / 4, gridUnit / 2,
									gridUnit / 2);
							break;
						case FOOD_MALUS:
							graph.setColor(new Color(244,2,31));
							graph.fillOval(i * gridUnit + gridUnit / 4, j
									* gridUnit + gridUnit / 4, gridUnit / 2,
									gridUnit / 2);
							break;
						case BIG_FOOD_BONUS:
							graph.setColor(new Color(198,39,203));
							graph.fillOval(i * gridUnit + gridUnit / 4, j
									* gridUnit + gridUnit / 4, gridUnit / 2,
									gridUnit / 2);
							break;
						default:
							break;
						}
					}
				}
				graph.setFont(new Font(Font.SANS_SERIF, Font.BOLD, screenSize / 40));
				if (game_over) {
					graph.setColor(Color.RED);
					graph.drawString("GAME OVER", screenSize / 2 - 30, screenSize / 2);
				}
				else if (paused) {
					graph.setColor(Color.RED);
					graph.drawString("PAUSED", screenSize / 2 - 30, screenSize / 2);
				}
				graph.setColor(Color.BLACK);
				graph.drawString("SCORE = " + getScore(), 10, 20);
				graph.dispose();
			} while (strategy.contentsRestored());
			// Draw image from buffer
			strategy.show();
			Toolkit.getDefaultToolkit().sync();
		} while (strategy.contentsLost());
	}
	public static int getScore() {
		int score = 0;
		for (int i = 0; i < gameSize * gameSize; i++) {
			if ((snake[i][0] < 0) || (snake[i][1] < 0)) {
				break;
			}
			score++;
		}
		return score;
	}

	private void moveSnake() throws InterruptedException {
		if (direction < 0) {
			return;
		}
		int ymove = 0;
		int xmove = 0;
		switch (direction) {
		case UP:
			xmove = 0;
			ymove = -1;
			//MovePacket up = new MovePacket(new int[snake[0][0]][snake[0][1]], xmove, ymove);
			//bb.put(up);
			break;
		case DOWN:
			xmove = 0;
			ymove = 1;
            //MovePacket down = new MovePacket(new int[snake[0][0]][snake[0][1]], xmove, ymove);
            //bb.put(down);
			break;
		case RIGHT:
			xmove = 1;
			ymove = 0;
            //MovePacket right = new MovePacket(new int[snake[0][0]][snake[0][1]], xmove, ymove);
            //bb.put(right);
			break;
		case LEFT:
			xmove = -1;
			ymove = 0;
            //MovePacket left = new MovePacket(new int[snake[0][0]][snake[0][1]], xmove, ymove);
            //bb.put(left);
			break;
		default:
			xmove = 0;
			ymove = 0;
			break;
		}
		int tempx = snake[0][0];
		int tempy = snake[0][1];
		for (int h = 0; h<grid.length; h++) {
			for (int k=0; k<grid.length; k++) {
				System.out.print(grid[k][h]+" ");
			}
			System.out.println();
		}
		System.out.println("--------------------------");

	    //System.out.println("grid:"+Arrays.deepToString(grid));
		//System.out.println("temp_x:"+tempx);
		//System.out.println("temp_y:"+tempy);
		int fut_x = snake[0][0] + xmove;
		int fut_y = snake[0][1] + ymove;
		//System.out.println("direction:"+direction);
		//System.out.println("fut_x:"+fut_x);
		//System.out.println("fut_y:"+fut_y);
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
			fut_x = gameSize - 1;
		if(fut_y < 0)
			fut_y = gameSize - 1;
		if(fut_x >= gameSize)
			fut_x = 0;
		if(fut_y >= gameSize)
			fut_y = 0;


		if (grid[fut_x][fut_y] == FOOD_BONUS)
		{
			grow ++;
			placeBonus(FOOD_BONUS);
		}
		else if(grid[fut_x][fut_y] == BIG_FOOD_BONUS)
			grow += 3;
		snake[0][0] = fut_x;
		snake[0][1] = fut_y;
		if ((grid[snake[0][0]][snake[0][1]] == SNAKE) || (grid[snake[0][0]][snake[0][1]] == SNAKE_HEAD)) {
			gameOver();
			return;
		}
		grid[tempx][tempy] = EMPTY;
		int snakex, snakey, i;
		for (i = 1; i < gameSize * gameSize; i++) {
			if ((snake[i][0] < 0) || (snake[i][1] < 0)) {
				break;
			}
			grid[snake[i][0]][snake[i][1]] = EMPTY;
			snakex = snake[i][0];
			snakey = snake[i][1];
			snake[i][0] = tempx;
			snake[i][1] = tempy;
			tempx = snakex;
			tempy = snakey;
		}
		grid[snake[0][0]][snake[0][1]] = SNAKE_HEAD;
		for (i = 1; i < gameSize * gameSize; i++) {
			if ((snake[i][0] < 0) || (snake[i][1] < 0)) {
				break;
			}
			grid[snake[i][0]][snake[i][1]] = SNAKE;
		}
		bonusTime --;
		if (bonusTime == 0)
		{
			for (i = 0; i < gameSize; i++)
			{
				for (int j = 0; j < gameSize; j++)
				{
					if(grid[i][j]==BIG_FOOD_BONUS)
						grid[i][j]=EMPTY;
				}
			}
		}
		if (grow > 0) {
			snake[i][0] = tempx;
			snake[i][1] = tempy;
			grid[snake[i][0]][snake[i][1]] = SNAKE;
			if(getScore()%10 == 0)
			{
				placeBonus(BIG_FOOD_BONUS);
				bonusTime = 100;
			}
			grow --;
		}
	}
	
	public static int getGameSize() {
		return gameSize;
	}
	public static void placeBonus(int bonus_type) {
		int x = (int) (Math.random() * 1000) % gameSize;
		int y = (int) (Math.random() * 1000) % gameSize;
		if (grid[x][y] == EMPTY) {
			grid[x][y] = bonus_type;
			//grid[x+1][y] = bonus_type;
			//grid[x+2][y] = bonus_type;
		} else {
			placeBonus(bonus_type);
		}
	}
	public static void placeBonusAtLoc(int bonus_type, int x, int y) {
		if (grid[x][y] == EMPTY) {
			grid[x][y] = bonus_type;
		} else {
			placeBonus(bonus_type);
		}
	}
	public static void gameOver() {
		game_over = true;
	}
	// / IMPLEMENTED FUNCTIONS
	public void keyPressed(KeyEvent ke) {
		int code = ke.getKeyCode();
		Dimension dim;
		// System.out.println("Key pressed" + ke.toString());
		paused = false;
		switch (code) {
		case KeyEvent.VK_UP:
			if (direction != DOWN) {
				next_direction = UP;
			}
			break;
		case KeyEvent.VK_DOWN:
			if (direction != UP) {
				next_direction = DOWN;
			}
			break;
		case KeyEvent.VK_LEFT:
			if (direction != RIGHT) {
				next_direction = LEFT;
			}
			break;
		case KeyEvent.VK_RIGHT:
			if (direction != LEFT) {
				next_direction = RIGHT;
			}
			break;
		case KeyEvent.VK_F11:
			dim = Toolkit.getDefaultToolkit().getScreenSize();
			if ((height != dim.height - 50) || (width != dim.height - 50)) {
				height = dim.height - 50;
				width = dim.height - 50;
			} else {
				height = 600;
				width = 600;
			}
			frame.setSize(width + 7, height + 27);
			canvas.setSize(width + 7, height + 27);
			canvas.validate();
			frame.validate();
			break;
			/*
			 * case KeyEvent.VK_F12: dim =
			 * Toolkit.getDefaultToolkit().getScreenSize(); if((height !=
			 * dim.height) || (width != dim.width)) {
			 *
			 * frame.setVisible(false); //frame.setUndecorated(true);
			 * frame.setVisible(true);
			 * GraphicsEnvironment.getLocalGraphicsEnvironment
			 * ().getDefaultScreenDevice().setFullScreenWindow(frame);
			 *
			 * } break;
			 */
		case KeyEvent.VK_ESCAPE:
			running = false;
			System.exit(0);
			break;
		case KeyEvent.VK_SPACE:
			if(!game_over)
				paused = true;
			break;
		default:
			// Unsupported key
			break;
		}
	}
	public void windowClosing(WindowEvent we) {
		running = false;
		System.exit(0);
	}
	// UNNUSED IMPLEMENTED FUNCTIONS
	public void keyTyped(KeyEvent ke) {}
	public void keyReleased(KeyEvent ke) {}
	public void windowOpened(WindowEvent we) {}
	public void windowClosed(WindowEvent we) {}
	public void windowIconified(WindowEvent we) {}
	public void windowDeiconified(WindowEvent we) {}
	public void windowActivated(WindowEvent we) {}
	public void windowDeactivated(WindowEvent we) {}
}
