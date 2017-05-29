package Server;

import Testing.IllegalBoundedBufferException;
import Testing.IllegalRoleException;
import util.BoundedBuffer;
import util.LoggingPacket;
import util.MovePacket;
import util.Snake;

import java.util.ArrayList;
import java.util.List;

public class MoveHandler implements Runnable {
	//Required communication Buffer and playerList:
	private BoundedBuffer<MovePacket> bb;
	private List<Snake> playerList;
	private BoundedBuffer<LoggingPacket> loggerBuffer;

	//Statistics counters:
	private static int amountProduced = 0;
	private static double amountConsumed = 0;
	long runningTime = System.currentTimeMillis();

	//How many snakes to generate:
	private static int MAX_SNAKE_LIMIT;
	private static int CURRENT_SNAKE_COUNT;
	//Our Role.
	enum Role {CONSUMER, PRODUCER, SNAKE_PRODUCER}
	private Role role;

	/*
	Typically expects List<Snake> to be a Thread-safe collection, if not - undefined issues may occur.
	 */
	MoveHandler(BoundedBuffer<MovePacket> bb, BoundedBuffer<LoggingPacket> loggingPacketBoundedBuffer, Role role, List<Snake> playerList) {
		this.bb = bb;
		this.loggerBuffer = loggingPacketBoundedBuffer;
		this.role = role;
		this.playerList = playerList;
		System.out.println("Constructing MoveHandlerThread with a BoundedBuffer and a " 
				+ this.role.toString() 
				+ " role and a PlayerList of the size " 
				+ this.playerList.size());

	}
	MoveHandler(BoundedBuffer<MovePacket> bb, BoundedBuffer<LoggingPacket> loggingPacketBoundedBuffer, Role role, int amountOfSnakesToGenerate, List<Snake> playerList) {
		this.bb = bb;
		this.loggerBuffer = loggingPacketBoundedBuffer;
		this.role = role;
		MAX_SNAKE_LIMIT = amountOfSnakesToGenerate;
		this.playerList = playerList;
		System.out.println("Constructing MoveHandlerThread with a BoundedBuffer and a "
				+ this.role.toString()
				+ " role and a PlayerList of the size "
				+ this.playerList.size());
	}
	@Override
	public void run() {
		System.out.println("Starting MoveHandlerThread with a BoundedBuffer and a "
				+ this.role.toString() 
				+ " role and a PlayerList of the size " 
				+ this.playerList.size());
		if(this.role == Role.CONSUMER) {
			//should contain the code to "update the board"
			//attempt to consume
			while(Game.running) {
				try {
					if(bb == null) {
						throw new IllegalBoundedBufferException("Illegal BoundedBuffer state");
					} else if (Game.running){
						if(Game.paused) {
							this.delay(10);
						} else {
							//Get from bb and set-up:
							MovePacket context = bb.get();
							Snake snake = context.getTheSnake();
							loggerBuffer.put(new LoggingPacket("[MoveHandler] [Utility]  \t" + Thread.currentThread().toString() + " Got MovePacket from Buffer " + context.toString()));
							if (snake.IsAI) {
								snake.newDirection();
							}
							//TODO: Move to MovePacket. DONE
							int ymove = 0;
							int xmove = 0;
							if (snake.direction == Game.UP) {
								ymove = -1;
								xmove = 0;
								snake.lastDirection = Game.UP;
							} else if (snake.direction == Game.DOWN) {
								ymove = 1;
								xmove = 0;
								snake.lastDirection = Game.DOWN;

							} else if (snake.direction == Game.LEFT) {
								ymove = 0;
								xmove = -1;
								snake.lastDirection = Game.LEFT;

							} else if (snake.direction == Game.RIGHT) {
								ymove = 0;
								xmove = 1;
								snake.lastDirection = Game.RIGHT;

							}
							//where the snake currently is:
							int tempx = snake.boardLocation[0][0];
							int tempy = snake.boardLocation[0][1];


							//Where the snake will be going next:
							int fut_x = snake.boardLocation[0][0] + xmove;
							int fut_y = snake.boardLocation[0][1] + ymove;
							loggerBuffer.put(new LoggingPacket("[MoveHandler] [Move]  \t[" + snake.name + "] Current location X-Y: " + tempx + "-" + tempy));
							loggerBuffer.put(new LoggingPacket("[MoveHandler] [Move]  \t[" + snake.name + "] Future location X-Y: " + fut_x + "-" + fut_y));
						/*
						 * Code that handles when the snake reaches the side of the screen - move to other side.
						 */
							if (fut_x < 0)
								fut_x = Game.getGameSize() - 1;
							if (fut_y < 0)
								fut_y = Game.getGameSize() - 1;
							if (fut_x >= Game.getGameSize())
								fut_x = 0;
							if (fut_y >= Game.getGameSize())
								fut_y = 0;

							//If the snake just ran into some food:
							if (Game.grid[fut_x][fut_y] == Game.FOOD_BONUS) {
								loggerBuffer.put(new LoggingPacket("[MoveHandler] [Event]  \t[" + snake.name + "] has ran into FOOD_BONUS"));
								snake.grow++;
							} else if (Game.grid[fut_x][fut_y] == Game.BIG_FOOD_BONUS) {
								loggerBuffer.put(new LoggingPacket("[MoveHandler] [Event]  \t[" + snake.name + "] has ran into BIG_FOOD_BONUS"));
								snake.grow += 3;
							}

							/*
							 * Actually update the move.
							 */
							snake.boardLocation[0][0] = fut_x;
							snake.boardLocation[0][1] = fut_y;
						
							/*
							 * Should the snake die?
							 */
							if ((Game.grid[snake.boardLocation[0][0]][snake.boardLocation[0][1]] >= 4)) {
								Game.placeBonus(Game.FOOD_BONUS);
								//TODO: Move this to the in game console (Should be easy enough to do).
								System.out.println(snake.name + " has been killed...");

								loggerBuffer.put(new LoggingPacket("[MoveHandler] [Event] \t[" + snake.name + "] has been killed . . . "));

								for (int i = 1; i < Game.getGameSize() * Game.getGameSize(); i++) {
									if ((snake.boardLocation[i][0] < 0) || (snake.boardLocation[i][1] < 0)) {
										break;
									}
									Game.grid[snake.boardLocation[i][0]][snake.boardLocation[i][1]] = Game.FOOD_BONUS;
								}

								Game.grid[tempx][tempy] = Game.BIG_FOOD_BONUS;
								if (!playerList.remove(snake)) {
									loggerBuffer.put(new LoggingPacket("[MoveHandler] [CAUTION] \t Unsuccessfully removed [" + snake.name + "] from list"));
								}
								snake.alive = false;
								snake.deaths++;
							}
							/*
							If the snake survived the above code, move it to where it should go.
							 */
							if (snake.alive) {
								Game.grid[tempx][tempy] = Game.EMPTY;
								int snakex, snakey, i;
								for (i = 1; i < Game.getGameSize() * Game.getGameSize(); i++) {
									if ((snake.boardLocation[i][0] < 0) || (snake.boardLocation[i][1] < 0)) {
										break;
									}
									Game.grid[snake.boardLocation[i][0]][snake.boardLocation[i][1]] = Game.EMPTY;
									snakex = snake.boardLocation[i][0];
									snakey = snake.boardLocation[i][1];
									snake.boardLocation[i][0] = tempx;
									snake.boardLocation[i][1] = tempy;
									tempx = snakex;
									tempy = snakey;
								}
								if (!snake.IsAI) {
									Game.grid[snake.boardLocation[0][0]][snake.boardLocation[0][1]] = snake.getHead();
								} else {
									Game.grid[snake.boardLocation[0][0]][snake.boardLocation[0][1]] = Game.SNAKE_HEAD;
								}
								for (i = 1; i < Game.getGameSize() * Game.getGameSize(); i++) {
									if ((snake.boardLocation[i][0] < 0) || (snake.boardLocation[i][1] < 0)) {
										break;
									}
									Game.grid[snake.boardLocation[i][0]][snake.boardLocation[i][1]] = Game.SNAKE;
								}
								/*
								If it has recently eaten something, let the snake grow a tick.
								 */
								if (snake.grow > 0) {
									snake.boardLocation[i][0] = tempx;
									snake.boardLocation[i][1] = tempy;
									Game.grid[snake.boardLocation[i][0]][snake.boardLocation[i][1]] = Game.SNAKE;
									snake.grow--;
								}
							}

							//Statistics:
						//amountConsumed++;

							//if (amountConsumed % 100 == 0) {
//						System.out.println("The current amount of Packets processed: "
//								+ amountConsumed + " Current run-time: "
//								+ ((System.currentTimeMillis() - runningTime) / 1000)
//								+ " seconds. Average packets per second: "
//								+ (amountConsumed / ((System.currentTimeMillis() - runningTime) / 1000))
//								+ " amount more Produced: "
//								+ (amountProduced - amountConsumed));
//							//}
							loggerBuffer.put(new LoggingPacket("[MoveHandler] [Event] \t" + context.toString() + " has finished being worked on. . . "));
						}
					}
				} catch(InterruptedException | IllegalBoundedBufferException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}				
			}
		} 
		else if(this.role == Role.PRODUCER) {
			try {
				if(bb == null) {
					throw new IllegalBoundedBufferException("Illegal BoundedBuffer state");
				} else {
					while(Game.running) {
						if(Game.paused) {
							this.delay(10);
						} else {
							this.delay(200);
							if(playerList.size() > 0) {
								for(int i = 0; i<playerList.size(); i++) {
									bb.put(new MovePacket(playerList.get(i)));
									amountProduced++;
								}
							}
						}

					}
				}
			} catch(IllegalBoundedBufferException | InterruptedException e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		} else if(this.role == Role.SNAKE_PRODUCER) {
			while(CURRENT_SNAKE_COUNT <= MAX_SNAKE_LIMIT) {
				this.playerList.add(new Snake("AI Snake "+(CURRENT_SNAKE_COUNT), true));
				CURRENT_SNAKE_COUNT++;
			}
		}
		else {
			try {
				throw new IllegalRoleException("Invalid attempt to run a MoveHandler without a designated role.");
			} catch (IllegalRoleException e) {
				e.printStackTrace();
			}
		}
		System.out.println(Thread.currentThread().getName() + " " + this.role.toString() + " is exitting. . . ");
	}
	private void delay(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("MoveHandler \n BoundedBuffer current MovePackets: ").append(this.bb.toString());
		b.append("\n Role: " + this.role.toString());
		b.append("\n Player list size: ").append(this.playerList.size());
		return b.toString();
	}
}
