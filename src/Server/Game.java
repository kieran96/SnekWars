package Server;


import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import util.BoundedBuffer;
import util.MovePacket;
import util.Snake;

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
	public final static int PLAYER_SNAKE_HEAD=6;
	public static int[][] grid = null;
	private static Snake snake;
	public int counter = 0;
	private int direction = 1;
	private int height = 600;
	private int width = 600;
	//Code to make sizeable game based on screensize
	Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	int screenSize = (int) (dim.height * 0.6);
	private static int gameSize = 81;
	public static long speed = 10;
	private JFrame frame = null;
	private Canvas canvas = null;
	private Graphics graph = null;
	private BufferStrategy strategy = null;
	public static boolean game_over = false;
	public static boolean paused = false;
	public static boolean running = true;
	private BoundedBuffer bb;
	private ArrayList<Snake> snakeList;

	Thread p1;
	Thread c1;
	ExecutorService e;
	/**
	 * @param args
	 * the command line arguments
	 */

	public Game(BoundedBuffer BB) {
		//super();
		this.bb = BB;
		int processors = Runtime.getRuntime().availableProcessors() * 2;

		e = Executors.newFixedThreadPool(processors);	
		frame = new JFrame();
		GridBagLayout g = new GridBagLayout();
		frame.setLayout(g);
		canvas = new Canvas();
		grid = new int[gameSize][gameSize];
		snake = new Snake("The Player", false);
		snake.enemysnake = new int[gameSize * gameSize][2];
		this.snakeList = new ArrayList<Snake>();
		this.createPlayer(100);
		this.createPlayer();

		this.startServers();

	}
	public void startServers() {
		if(p1 == null) {
			p1 = new Thread(new MoveHandler(bb, MoveHandler.Role.PRODUCER, this.snakeList), "p1");
			e.submit(p1);
		}
		if(c1 == null) {
			c1 = new Thread(new MoveHandler(bb, MoveHandler.Role.CONSUMER, this.snakeList), "c1");
			e.submit(c1);
		}

	}
	public void createPlayer() {
		
		snakeList.add(this.snake);

		for (int i = 0; i < gameSize * gameSize; i++) {
			snake.enemysnake[i][0] = -1;
			snake.enemysnake[i][1] = -1;
		}
		snake.enemysnake[0][0] = gameSize/2;
		snake.enemysnake[0][1] = gameSize/2;
		grid[gameSize/2][gameSize/2] = PLAYER_SNAKE_HEAD;

		
		snake.alive = true;
	}
	public void createPlayer(int amount) {
		for(int i = 0; i<=amount; i++) {
			this.snakeList.add(new Snake("AI Snake "+(snakeList.size()+1), true));
		}

	}
	public void init() {
		frame.setSize(screenSize, screenSize);
		frame.setResizable(true);
		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
		frame.setBackground(Color.blue);
		canvas.setSize(screenSize-50, screenSize-50);
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.CENTER;
		frame.add(canvas, c);
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
			try {
				Thread.sleep(speed);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//work here
			/*for (int i=0; i<snakeList.size(); i++) {
				System.out.println("Array index("+i+")"+snakeList.get(i));
			}*/

//			if(this.snakeList.contains(snake)) {
//				if(!this.snakeList.get(0).alive) {
//					if(!snake.IsAI) {
//						for(int i = 0; i<snakeList.size(); i++) {
//							System.out.println(snakeList.get(i).toString());
//						}
//					}
//				}
//				if(this.snakeList.get(snakeList.indexOf(snake)) == null) {
//					System.out.println("Attempting to add back the player.");
//					th
//					this.createPlayer();
//				}	
//			} else {
//				System.out.println("Attempting to add back the player.");
//				this.createPlayer();
//			}
			int x = this.snakeList.indexOf(snake);
			if(x == -1) {				
				System.out.println("right . . . . . .");
				this.createPlayer();
			} else {
				if(!this.snakeList.get(x).alive) {
					System.out.println("huh...");
					this.snakeList.remove(x);
				}
			}
			counter += 1;
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
				this.createPlayer();
			}*/
//			for (int h = 0; h<grid.length; h++) {
//				for (int k=0; k<grid.length; k++) {
//					System.out.print(grid[h][k]+" ");
//					System.out.print(grid[k][h]+" ");
//				}
//				System.out.println();
//			}
			//At 10sec mark spawn 20 snakes
			//System.out.println("thread:"+Thread.activeCount());
//			Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
//			Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
//			//for (int i=0; i<Thread.activeCount(); i++) {
//			for (Thread t:Server.getPlayers()) {
//				if (t.isAlive() && t.toString().contains("thread")) {
//					System.out.println("current thread:"+t.getName());
//				}
//			}
//			

			renderGame();

		}
	}

	private void initGame() {
		// Initialise tabs
		for (int i = 0; i < gameSize; i++) {
			for (int j = 0; j < gameSize; j++) {
				grid[i][j] = EMPTY;
			}
		}
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
						case PLAYER_SNAKE_HEAD:
							graph.setColor(new Color(255,0,0));
							graph.fillOval(i * gridUnit, j * gridUnit,
									gridUnit, gridUnit);
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
			if ((snake.enemysnake[i][0] < 0) || (snake.enemysnake[i][1] < 0)) {
				break;
			}
			score++;
		}
		return score;
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
		switch (code) {
		case KeyEvent.VK_UP:
			if (snake.direction != DOWN) {
				snake.direction = UP;
				//System.out.println("Player's direction changed to UP");
			}
			break;
		case KeyEvent.VK_DOWN:
			if (snake.direction != UP) {
				snake.direction = DOWN;
				//System.out.println("Player's direction changed to DOWN");

			}
			break;
		case KeyEvent.VK_LEFT:
			if (snake.direction != RIGHT) {
				snake.direction = LEFT;
				//System.out.println("Player's direction changed to LEFT");

			}
			break;
		case KeyEvent.VK_RIGHT:
			if (snake.direction != LEFT) {
				snake.direction = RIGHT;
				//System.out.println("Player's direction changed to RIGHT");

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
//		case KeyEvent.VK_SPACE:
//			if(!game_over) {
//
//			}
//			break;
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
