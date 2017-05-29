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

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import util.*;

public class Game implements KeyListener {
	// KEYS MAP
	public final static int UP = 0;
	public final static int DOWN = 1;
	public final static int LEFT = 2;
	public final static int RIGHT = 3;
	// GRID CONTENT
	final static int EMPTY = 0;
	public final static int FOOD_BONUS = 1;
	public final static int BIG_FOOD_BONUS = 3;
	final static int SNAKE = 4;
	public final static int SNAKE_HEAD=5;
	final static int PLAYER_ONE = 7;
	final static int PLAYER_TWO = 8;
	final static int PLAYER_THREE = 9;
	final static int PLAYER_FOUR = 10;
	private static int score;
	public static int[][] grid = null;
	public int counter = 0;
	private int direction = 1;
	private int height = 600;
	private int width = 600;
	//Code to make sizeable game based on screensize
	Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	int screenSize = (int) (dim.height * 0.6);
	private static int gameSize = 80;
    static long speed = 10;
	private JFrame frame = null;
	private Canvas canvas = null;
	private Graphics graph = null;
	private BufferStrategy strategy = null;
	static boolean game_over = false;
	public static boolean paused = false;
	public static boolean running = true;
	private BoundedBuffer<MovePacket> bb;
	private BoundedBuffer<LoggingPacket> loggingPacketBoundedBuffer;
	private List<Snake> snakeList;
	private List<Snake> humanPlayers;

    private Thread p1;
    private Thread c1;
    private Thread c2;
    private Thread logger;
    private ExecutorService e;

	Game(BoundedBuffer<MovePacket> BB, BoundedBuffer<LoggingPacket> loggingPacketBoundedBuffer, List<Snake> players) {
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
		this.loggingPacketBoundedBuffer = loggingPacketBoundedBuffer;

		this.snakeList = new CopyOnWriteArrayList<Snake>();
		this.createPlayersHuman();
		//this.createPlayersAI(100, false);
		this.startServers(false, 1, 7);

	}
	private void startServers(boolean usePool, int producerSize, int consumerSize) {
		if(usePool) {
			for(int i = 0; i<producerSize; i++) {
				e.submit(new Thread(new MoveHandler(bb, loggingPacketBoundedBuffer, MoveHandler.Role.PRODUCER, this.snakeList), "p"+i));
			}
			for(int i = 0; i<consumerSize; i++) {
				e.submit(new Thread(new MoveHandler(bb, loggingPacketBoundedBuffer, MoveHandler.Role.CONSUMER, this.snakeList), "c"+i));
			}
		} else {
			if(p1 == null) {
				p1 = new Thread(new MoveHandler(bb, loggingPacketBoundedBuffer, MoveHandler.Role.PRODUCER, this.snakeList), "p1");
				p1.start();
			}
			if(c1 == null) {
				c1 = new Thread(new MoveHandler(bb, loggingPacketBoundedBuffer, MoveHandler.Role.CONSUMER, this.snakeList), "c1");
				c1.start();
			}
			if(c2 == null) {
				c2 = new Thread(new MoveHandler(bb, loggingPacketBoundedBuffer, MoveHandler.Role.CONSUMER, this.snakeList), "c2");
				c2.start();
			}
		}
		if(logger == null) {
			e.submit(new Thread(new util.Logger(loggingPacketBoundedBuffer), "Logger_1"));
//			e.submit(new Thread(new util.Logger(loggingPacketBoundedBuffer), "Logger_2"));
		}

	}
	private void createPlayersHuman() {
		for(int i = 7; i<7+humanPlayers.size(); i++) {
			humanPlayers.get(i-7).createSnake();
			humanPlayers.get(i-7).setHead(i);
			snakeList.add(humanPlayers.get(i-7));
		}
	}
    private void createPlayersAI(int amount, boolean useThreadedCreation) {
		if(useThreadedCreation) {
			Thread sc1 = new Thread(new MoveHandler(this.bb, loggingPacketBoundedBuffer, MoveHandler.Role.SNAKE_PRODUCER, amount, this.snakeList), "sc1");
			Thread sc2 = new Thread(new MoveHandler(this.bb, loggingPacketBoundedBuffer, MoveHandler.Role.SNAKE_PRODUCER, amount, this.snakeList), "sc2");
			Thread sc3 = new Thread(new MoveHandler(this.bb, loggingPacketBoundedBuffer, MoveHandler.Role.SNAKE_PRODUCER, amount, this.snakeList), "sc3");
			Thread sc4 = new Thread(new MoveHandler(this.bb, loggingPacketBoundedBuffer, MoveHandler.Role.SNAKE_PRODUCER, amount, this.snakeList), "sc4");

			sc1.start();
			sc2.start();
			sc3.start();
			sc4.start();
//			try {
//				sc1.join();
//				sc2.join();
//				sc3.join();
//				sc4.join();
//			} catch (InterruptedException e1) {
//				e1.printStackTrace();
//			}

		} else {
			for(int i = 0; i<=amount; i++) {
				this.snakeList.add(new Snake("AI Snake "+(snakeList.size()+1), true));
			}
		}
	}
    void init() {
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
		frame.dispose();
		frame.validate();
		frame.setTitle("Snake");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		canvas.setIgnoreRepaint(true);
		canvas.setBackground(Color.WHITE);
		canvas.createBufferStrategy(2);
		strategy = canvas.getBufferStrategy();
		graph = strategy.getDrawGraphics();

		renderGame();

	}
    void mainLoop() {
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
			for(Snake snake : this.humanPlayers) {
				if(!snake.alive) {
					try{
						loggingPacketBoundedBuffer.put(new LoggingPacket("[Game] [Event] \t Added back the snake: " + snake.toString()));
					} catch(Exception e) {
						e.printStackTrace();
					}
					snakeList.add(snake);
                    snake.createSnake();
                }
			}
			renderGame();
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
				int gridCase;
				for (int i = 0; i < gameSize; i++) {
					for (int j = 0; j < gameSize; j++) {
						gridCase = grid[i][j];
						switch (gridCase) {
							case SNAKE:
								graph.setColor(new Color(17, 14, 218));
								graph.fillOval(i * gridUnit, j * gridUnit,
										gridUnit, gridUnit);
								break;
							case SNAKE_HEAD:
								graph.setColor(new Color(0, 0, 0));
								graph.fillOval(i * gridUnit, j * gridUnit,
										gridUnit, gridUnit);
								break;
							case FOOD_BONUS:
								graph.setColor(new Color(3, 171, 14));
								graph.fillOval(i * gridUnit + gridUnit / 4, j
												* gridUnit + gridUnit / 4, gridUnit / 2,
										gridUnit / 2);
								break;
							case BIG_FOOD_BONUS:
								graph.setColor(new Color(198, 39, 203));
								graph.fillOval(i * gridUnit + gridUnit / 4, j
												* gridUnit + gridUnit / 4, gridUnit / 2,
										gridUnit / 2);
								break;
							case PLAYER_ONE:
								graph.setColor(new Color(26, 241, 193));
								graph.fillOval(i * gridUnit, j * gridUnit,
										gridUnit, gridUnit);
								break;
							case PLAYER_TWO:
								graph.setColor(new Color(230, 195, 97));
								graph.fillOval(i * gridUnit, j * gridUnit,
										gridUnit, gridUnit);
								break;
							case PLAYER_THREE:
								graph.setColor(new Color(170, 30, 36));
								graph.fillOval(i * gridUnit, j * gridUnit,
										gridUnit, gridUnit);
								break;
							case PLAYER_FOUR:
								graph.setColor(new Color(103, 66, 34));
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
			e.shutdown();
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
	// UNNUSED IMPLEMENTED FUNCTIONS
	public void keyTyped(KeyEvent ke) {}
	public void keyReleased(KeyEvent ke) {}
}
