package Server;

import Testing.IllegalBoundedBufferException;
import Testing.IllegalRoleException;
import util.BoundedBuffer;
import util.MovePacket;
import java.util.ArrayList;

public class MoveHandler implements Runnable {
	private BoundedBuffer bb;
	static int amountProduced = 0;
	static double amountConsumed = 0;
	long runningTime = System.currentTimeMillis();
	
	enum Role {CONSUMER, PRODUCER};
	private Role role;
	ArrayList<Snake> playerList;
	

	public MoveHandler(BoundedBuffer bb, Role role, ArrayList<Snake> playerList) {
		this.bb = bb;
		this.role = role;
		this.playerList = playerList;
		System.out.println("Constructing MoveHandlerThread with a BoundedBuffer and a " 
				+ this.role.toString() 
				+ " role and a PlayerList of the size " 
				+ this.playerList.size());

	}
	public synchronized BoundedBuffer getBb() {
		return bb;
	}
	public synchronized void setBb(BoundedBuffer bb) {
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
		if(this.role == Role.CONSUMER) {
			//should contain the code to "update the board"
			//attempt to consume
			while(true) {
				try {
					if(bb == null) {
						throw new IllegalBoundedBufferException("Illegal BoundedBuffer state");
					} else {
						bb.get();
						//MOVE
						//move code from moveenemysnake and rewrite moveenemysnake to work with ArrayList.
						Thread.sleep(300);
						for(Snake snake : this.getSnakeList()) {	
							//System.out.println("snake:"+snake.toString()+ "direction:"+snake.direction);
							if (snake.direction < 0) {
								return;
							}
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
								//MovePacket up = new MovePacket(new int[snake[0][0]][snake[0][1]], xmove, ymove);
								//bb.put(up);
							int tempx = snake.enemysnake[0][0];
							int tempy = snake.enemysnake[0][1];
							//int tempx = 10;
							//int tempy = 10;
							//System.out.println("tempx"+tempx);
							//System.out.println("tempy:"+tempy);
							int fut_x = snake.enemysnake[0][0] + xmove;
							int fut_y = snake.enemysnake[0][1] + ymove;
							/*System.out.println("snake:"+snake.name);
							System.out.println("direction:"+snake.direction);
							System.out.println("fut_x:"+fut_x);
							System.out.println("fut_y:"+fut_y);*/
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
	
	
							if (Game.grid[fut_x][fut_y] == snake.FOOD_BONUS)
							{
								snake.grow ++;
								Game.placeBonus(snake.FOOD_BONUS);
							}
							else if(Game.grid[fut_x][fut_y] == snake.BIG_FOOD_BONUS)
								snake.grow += 3;
								snake.enemysnake[0][0] = fut_x;
								snake.enemysnake[0][1] = fut_y;
							if ((Game.grid[snake.enemysnake[0][0]][snake.enemysnake[0][1]] == snake.SNAKE) || (Game.grid[snake.enemysnake[0][0]][snake.enemysnake[0][1]] == snake.SNAKE_HEAD)) {
								//Game.gameOver();
								Game.placeBonus(snake.FOOD_BONUS);
								System.out.println("A enemy snake has been killed...");
								//Game.placeBonusAtLoc(Game.BIG_FOOD_BONUS, snake.enemysnake[0][0], snake.enemysnake[0][1]);
								//Game.grid[gameSize/2][gameSize/2];
								Game.grid[tempx][tempy] = snake.EMPTY;
								Game.placeBonusAtLoc(Game.BIG_FOOD_BONUS, tempx, tempy);
								playerList.remove(snake);
								snake.alive = false;
								
								break;
							}
							Game.grid[tempx][tempy] = snake.EMPTY;
							int snakex, snakey, i;
							for (i = 1; i < Game.getGameSize() * Game.getGameSize(); i++) {
								if ((snake.enemysnake[i][0] < 0) || (snake.enemysnake[i][1] < 0)) {
									break;
								}
								Game.grid[snake.enemysnake[i][0]][snake.enemysnake[i][1]] = snake.EMPTY;
								snakex = snake.enemysnake[i][0];
								snakey = snake.enemysnake[i][1];
								snake.enemysnake[i][0] = tempx;
								snake.enemysnake[i][1] = tempy;
								tempx = snakex;
								tempy = snakey;
							}
							Game.grid[snake.enemysnake[0][0]][snake.enemysnake[0][1]] = snake.SNAKE_HEAD;
							for (i = 1; i < Game.getGameSize() * Game.getGameSize(); i++) {
								if ((snake.enemysnake[i][0] < 0) || (snake.enemysnake[i][1] < 0)) {
									break;
								}
								Game.grid[snake.enemysnake[i][0]][snake.enemysnake[i][1]] = snake.SNAKE;
							}
							snake.bonusTime --;
							if (snake.bonusTime == 0)
							{
								for (i = 0; i < Game.getGameSize(); i++)
								{
									for (int j = 0; j < Game.getGameSize(); j++)
									{
										if(Game.grid[i][j]==snake.BIG_FOOD_BONUS)
											Game.grid[i][j]=snake.EMPTY;
									}
								}
							}
							if (snake.grow > 0) {
								snake.enemysnake[i][0] = tempx;
								snake.enemysnake[i][1] = tempy;
								Game.grid[snake.enemysnake[i][0]][snake.enemysnake[i][1]] = snake.SNAKE;
								if(Game.getScore()%10 == 0)
								{
									Game.placeBonus(snake.BIG_FOOD_BONUS);
									snake.bonusTime = 100;
								}
								snake.grow --;
							}
						
						}
						
						amountConsumed++;
						if(amountConsumed % 10000 == 0) {
							System.out.println("The current amount of Packets processed: " 
									+ amountConsumed + " Current run-time: " 
									+ ((System.currentTimeMillis() - runningTime) / 1000) 
									+ " seconds. Average packets per second: "
									+ (amountConsumed / ((System.currentTimeMillis() - runningTime) / 1000)));
						}
					}
					//do work here
//					this.delay(0.05);
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
					//MovePacket should come to us from all n players and 
					//then placed into a new MovePacket and sent to the BB.
					while(true) {
						this.delay(0.300);
						//Thread.sleep(250);
						for(int i = 0; i<playerList.size(); i++) {
							if(playerList.get(i) != null) {
								//we need a move from the list of snakes.
								bb.put(new MovePacket(playerList.get(i).getMove(), playerList.get(i)));
								
								amountProduced++;							
								
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
			//means we have no role!
			try {
				throw new IllegalRoleException("Invalid attempt to run a MoveHandler without a designated role.");
			} catch (IllegalRoleException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	public void delay(double seconds) {
		double actualDelay = (seconds * 1000);
		try {
			System.out.println("The actual delay: " + actualDelay);
			Thread.sleep((long) actualDelay);
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
