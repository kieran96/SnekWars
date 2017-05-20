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
import util.MoveKey;
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
	private ArrayList<Snake> humanPlayers;

	Thread p1;
	Thread c1;
	ExecutorService e;
	public Game(BoundedBuffer BB, ArrayList<Snake> players) {
		//super();
		this.bb = BB;
		int processors = Runtime.getRuntime().availableProcessors() * 2;

		e = Executors.newFixedThreadPool(processors);	
		frame = new JFrame();
		GridBagLayout g = new GridBagLayout();
		frame.setLayout(g);
		canvas = new Canvas();
		grid = new int[gameSize][gameSize];

		this.humanPlayers = players;

		this.snakeList = new ArrayList<Snake>();
		this.createPlayersHuman();

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
	public void createPlayersHuman() {
		for(Snake snake : humanPlayers) {
			snake.createSnake();
			snakeList.add(snake);
		}
	}
	public void createPlayersAI(int amount) {
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
//			counter++;
//			if (counter == 100) {
//				this.createPlayersAI(100);
//			}
			renderGame();
			for(int i = 0;i<humanPlayers.size(); i++) {
				if(!humanPlayers.get(i).alive) {
					humanPlayers.remove(i);
				}
			}
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
//		for (int i = 0; i < gameSize * gameSize; i++) {
//			if ((snake.boardLocation[i][0] < 0) || (snake.boardLocation[i][1] < 0)) {
//				break;
//			}
//			score++;
//		}
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
		MoveKey move = new MoveKey(ke.getKeyCode());
		if(move.getKey() == KeyEvent.VK_ESCAPE) {
			running = false;
			System.exit(0);
		}
		if(move.getKey() == KeyEvent.VK_SPACE) {
			paused = !paused;
		}
		Dimension dim;
		//Send the KeyEvent to the snake, so it may handle.
		for(Snake snake : humanPlayers) {
			for(MoveKey key : snake.moveSet.getMoveSet()) {
				if(key.equals(move)) {
					snake.updateMove(move);
				}
			}
		}
//		case KeyEvent.VK_F11:
//			dim = Toolkit.getDefaultToolkit().getScreenSize();
//			if ((height != dim.height - 50) || (width != dim.height - 50)) {
//				height = dim.height - 50;
//				width = dim.height - 50;
//			} else {
//				height = 600;
//				width = 600;
//			}
//			frame.setSize(width + 7, height + 27);
//			canvas.setSize(width + 7, height + 27);
//			canvas.validate();
//			frame.validate();
//			break;
//			/*
//			 * case KeyEvent.VK_F12: dim =
//			 * Toolkit.getDefaultToolkit().getScreenSize(); if((height !=
//			 * dim.height) || (width != dim.width)) {
//			 *
//			 * frame.setVisible(false); //frame.setUndecorated(true);
//			 * frame.setVisible(true);
//			 * GraphicsEnvironment.getLocalGraphicsEnvironment
//			 * ().getDefaultScreenDevice().setFullScreenWindow(frame);
//			 *
//			 * } break;
//			 */
//		case KeyEvent.VK_ESCAPE:
//			running = false;
//			System.exit(0);
//			break;
////		case KeyEvent.VK_SPACE:
////			if(!game_over) {
////
////			}
////			break;
//		default:
//			// Unsupported key
//			break;
//		}
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
