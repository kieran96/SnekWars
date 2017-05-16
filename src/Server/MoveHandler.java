package Server;

import Testing.IllegalBoundedBufferException;
import Testing.IllegalRoleException;
import util.BoundedBuffer;
import util.MovePacket;
import util.Snake;

import java.util.ArrayList;

public class MoveHandler implements Runnable {
	private BoundedBuffer<MovePacket> bb;
	static int amountProduced = 0;
	static double amountConsumed = 0;
	long runningTime = System.currentTimeMillis();
	
	enum Role {CONSUMER, PRODUCER};
	private Role role;
	ArrayList<Snake> playerList;


	public MoveHandler(BoundedBuffer<MovePacket> bb, Role role, ArrayList<Snake> playerList) {
		this.bb = bb;
		this.role = role;
		this.playerList = playerList;
		System.out.println("Constructing MoveHandlerThread with a BoundedBuffer and a " 
				+ this.role.toString() 
				+ " role and a PlayerList of the size " 
				+ this.playerList.size());

	}
	public synchronized BoundedBuffer<MovePacket> getBb() {
		return bb;
	}
	public synchronized void setBb(BoundedBuffer<MovePacket> bb) {
		this.bb = bb;
	}
	public synchronized Role getRole() {
		return role;
	}
	public synchronized void setRole(Role role) {
		this.role = role;
	}
	public synchronized ArrayList<Snake> getSnakeList() {
		return this.playerList;
	}
	public synchronized void setPlayerList(ArrayList<Snake> playerList) {
		this.playerList = playerList;
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
						//this.delay(16);

						//Get from bb and set-up:
						MovePacket context = bb.get();
						Snake snake = context.getTheSnake();
						if(snake.IsAI == true) {
							snake.newDirection();
						}
						//decide where we will be going next:
						//TODO: Move to MovePacket.
						int ymove = 0;
						int xmove = 0;
						if (snake.direction == Game.UP) {
							ymove = -1;
							xmove = 0;
						} else if (snake.direction == Game.DOWN) {
							ymove = 1;
							xmove = 0;
						} else if (snake.direction == Game.LEFT) {
							ymove = 0;
							xmove = -1;
						} else if (snake.direction == Game.RIGHT) {
							ymove = 0;
							xmove = 1;
						}
						//where the snake currently is:
						int tempx = snake.enemysnake[0][0];
						int tempy = snake.enemysnake[0][1];

						
						//Where the snake will be going next:
						int fut_x = snake.enemysnake[0][0] + xmove;
						int fut_y = snake.enemysnake[0][1] + ymove;

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

						//If the snake just ran into some food:
						if (Game.grid[fut_x][fut_y] == Game.FOOD_BONUS)
						{
							snake.grow ++;
						}
						else if(Game.grid[fut_x][fut_y] == Game.BIG_FOOD_BONUS) 
							snake.grow += 3;
							snake.enemysnake[0][0] = fut_x;
							snake.enemysnake[0][1] = fut_y;
							
						
							/*
							 * Should the snake die?
							 */
						if ((Game.grid[snake.enemysnake[0][0]][snake.enemysnake[0][1]] == Game.SNAKE) 
								|| (Game.grid[snake.enemysnake[0][0]][snake.enemysnake[0][1]] == Game.SNAKE_HEAD
								|| (Game.grid[snake.enemysnake[0][0]][snake.enemysnake[0][1]] == Game.PLAYER_SNAKE_HEAD))) {
							
							//Game.gameOver();
							Game.placeBonus(Game.FOOD_BONUS);
							System.out.println(snake.name + " has been killed...");
							//Game.placeBonusAtLoc(Game.BIG_FOOD_BONUS, snake.enemysnake[0][0], snake.enemysnake[0][1]);
							//Game.grid[gameSize/2][gameSize/2];
							//
							//for(int i = 0; i<snake.grow; i++) {
							//	for(int b = 0; b<snake.grow; b++) {
							/*for (int i=0; i<snake.getScore(); i++) {
									Game.grid[tempx+i][tempy] = snake.EMPTY;
									Game.placeBonusAtLoc(Game.BIG_FOOD_BONUS, tempx+i, tempy);
							}*/
							for (int i = 1; i < Game.getGameSize() * Game.getGameSize(); i++) {
								if ((snake.enemysnake[i][0] < 0) || (snake.enemysnake[i][1] < 0)) {
									break;
								}
								Game.grid[snake.enemysnake[i][0] ][snake.enemysnake[i][1]]= Game.FOOD_BONUS;
								//Game.placeBonusAtLoc(Game.BIG_FOOD_BONUS, tempx+i, tempy);
								//snake.enemysnake[i][0] = snake.EMPTY;
								//snake.enemysnake[i][1] = snake.EMPTY;
								//Game.placeBonusAtLoc(Game.BIG_FOOD_BONUS, tempx+i, tempy);
								//System.out.println("x:"+snake.enemysnake[i][0]+"| y:"+snake.enemysnake[i][1]);
							}
							/*for(int a = 0; a<snake.enemysnake.length; a++) {
								for(int b = 0; b<snake.enemysnake.length; b++) {
									System.out.print(snake.enemysnake[a][b] + " ");
//									if(snake.enemysnake[a][b] != 0) {
//										
//									}
								}
							} */
							//	}
							//}
							Game.grid[tempx][tempy] = Game.BIG_FOOD_BONUS;
							if(playerList.remove(snake)) {
								System.out.println("Successfully removed " + snake.name + " from list");
 							} else {
								System.out.println("Unsuccessfully removed " + snake.name + " from list");

								System.out.println(snake.toString());
							}
							snake.alive = false;
							snake.deaths++;
						}
						
						if(snake.alive != false) {
							Game.grid[tempx][tempy] = Game.EMPTY;
							int snakex, snakey, i;
							for (i = 1; i < Game.getGameSize() * Game.getGameSize(); i++) {
								if ((snake.enemysnake[i][0] < 0) || (snake.enemysnake[i][1] < 0)) {
									break;
								}
								Game.grid[snake.enemysnake[i][0]][snake.enemysnake[i][1]] = Game.EMPTY;
								snakex = snake.enemysnake[i][0];
								snakey = snake.enemysnake[i][1];
								snake.enemysnake[i][0] = tempx;
								snake.enemysnake[i][1] = tempy;
								tempx = snakex;
								tempy = snakey;
							}
							if(snake.IsAI == false) {
								Game.grid[snake.enemysnake[0][0]][snake.enemysnake[0][1]] = Game.PLAYER_SNAKE_HEAD;

							} else {
								Game.grid[snake.enemysnake[0][0]][snake.enemysnake[0][1]] = Game.SNAKE_HEAD;								
							}
							for (i = 1; i < Game.getGameSize() * Game.getGameSize(); i++) {
								if ((snake.enemysnake[i][0] < 0) || (snake.enemysnake[i][1] < 0)) {
									break;
								}
								Game.grid[snake.enemysnake[i][0]][snake.enemysnake[i][1]] = Game.SNAKE;
								//System.out.println("x:"+snake.enemysnake[i][0]+"| y:"+snake.enemysnake[i][1]);
							}
							snake.bonusTime --;
							if (snake.bonusTime == 0) {
								for (i = 0; i < Game.getGameSize(); i++) {
									for (int j = 0; j < Game.getGameSize(); j++){
										if(Game.grid[i][j]==Game.BIG_FOOD_BONUS) {
											Game.grid[i][j]=Game.EMPTY;
										}
									}
								}
							}
							if (snake.grow > 0) {
								snake.enemysnake[i][0] = tempx;
								snake.enemysnake[i][1] = tempy;
								Game.grid[snake.enemysnake[i][0]][snake.enemysnake[i][1]] = Game.SNAKE;
								if(Game.getScore()%10 == 0)
								{
									Game.placeBonus(Game.BIG_FOOD_BONUS);
									snake.bonusTime = 100;
								}
								snake.grow --;
							}
						}
						
						//Statistics:
//						amountConsumed++;

						if(amountConsumed % 100 == 0) {
//							System.out.println("The current amount of Packets processed: " 
//									+ amountConsumed + " Current run-time: " 
//									+ ((System.currentTimeMillis() - runningTime) / 1000) 
//									+ " seconds. Average packets per second: "
//									+ (amountConsumed / ((System.currentTimeMillis() - runningTime) / 1000)));
							//System.out.println("AIPACKETS: " + AIPackets + " verse " + "PLAYERPACKETS: " + PLAYERPackets + " DIFFERENCE: " + (AIPackets - PLAYERPackets));
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
						this.delay(200);
						if(playerList.size() > 0) {
							for(int i = 0; i<playerList.size(); i++) {
								if(playerList.get(i) != null) {
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
		} 
		else {
			try {
				throw new IllegalRoleException("Invalid attempt to run a MoveHandler without a designated role.");
			} catch (IllegalRoleException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
		System.out.println(Thread.currentThread().getName() + " " + this.role.toString() + " is exitting. . . ");
	}
	public void delay(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch(Exception e) {
			System.out.println(e.getStackTrace());
		}
	}
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("MoveHandler \n BoundedBuffer size: " + this.getBb().getMaxSize());
		b.append("\n Role: " + this.getRole().toString());
		b.append("\n Player list size: " + this.getSnakeList().size());
		
		return b.toString();
	}
}
