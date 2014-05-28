import java.util.*;
public class Player extends Cell{

	
	double focalOffsetAngleFromPlayer, focalOffsetDistance;

	public Player(GameState gamestate, String fileName, int numFrames)
	{
		super(gamestate, fileName, numFrames);
		setSpeed(3);
		gamestate.setPlayer(this);
	}
	public Player(GameState gamestate, String fileName, int numFrames, int posX, int posY)
	{
		super(gamestate, fileName, numFrames);
		setSpeed(3);
		gamestate.setPlayer(this);
		setPosX(posX);
		setPosY(posY);
	}
	
	public void update()
	{
		super.update();
		checkStickingBacteria();
		//Handles movement based on keypresses
		if(getGameState().isKeyDown(GameState.KEY_UP))
		{
			move();
		}
		if(getGameState().isKeyDown(GameState.KEY_LEFT))
		{
			turn(-0.05);
		}
		if(getGameState().isKeyDown(GameState.KEY_RIGHT))
		{
			turn(0.05);
		}
		if(getGameState().isKeyDown(GameState.KEY_DOWN))
		{
			reverse();
		}
		
		for(Bacteria b : getGameState().getStickingBacteria())
		{
			if(b.getPosX() - b.getWidth()/2 < 0 || b.getPosX() + b.getWidth()/2 > getGameState().getScreenWidth() || b.getPosY() - b.getHeight()/2 < 0 || b.getPosY() + b.getHeight()/2 > getGameState().getScreenHeight())
			{
				getGameState().gameOver();
			}
		}
		if(getPosX() - getWidth()/2 < 0 || getPosX() + getWidth()/2 > getGameState().getScreenWidth() || getPosY() - getHeight()/2 < 0 || getPosY() + getHeight()/2 > getGameState().getScreenHeight())
		{
			getGameState().gameOver();
		}
	}
	
	//Method checks if player is colliding with any bacteria, and sticks them if so
	private void checkStickingBacteria()
	{
		for(int i = 0; i < getGameState().getBouncingBacteria().size(); i++)
		{

			if(this.isColliding(getGameState().getBouncingBacteria().get(i)))
			{
				attachBacteria(getGameState().getBouncingBacteria().get(i));
				i--;
			}		
		}
		for(int i = 0; i < getGameState().getBouncingBacteria().size(); i++)
		{
			for(int j = 0; j < getGameState().getStickingBacteria().size(); j++)
			{
				if(getGameState().getStickingBacteria().get(j).isColliding(getGameState().getBouncingBacteria().get(i)))
				{
					attachBacteria(getGameState().getBouncingBacteria().get(i));
					i--;
					break;
				}
			}
		}                                                                                                                                                                                                                                                            
	}
	
	private void attachBacteria(Bacteria b)
	{
		b.attach();
		getGameState().stick(b);
		getGameState().playSound(getGameState().getSlurps());
		eliminateBacteria(b);
		/*calculate focal point based on angle, distance mean*/
		
		//calculate average angle
		int countLeftOfCenter = 0;
		int countRightOfCenter = 0;
		double sum = 0;
		for(Bacteria i:getGameState().getStickingBacteria())
		{
			sum += i.getAngle();
			if(i.getPosX() > this.getPosX())
			{
				countRightOfCenter++;
			}
			else if(i.getPosX() < this.getPosX())
			{
				countLeftOfCenter++;
			}
			//if both x coordinates are equal, it is neither left or right
		}
		double averageAngle = sum/getGameState().getStickingBacteria().size();
		/*if(countLeftOfCenter > countRightOfCenter)
		{
			averageAngle += Math.PI;
		}*/
		
		//calculate average distance, weighing the Player as ten cells
		double distanceSum = 0;
		for(Bacteria i:getGameState().getStickingBacteria())
		{
			distanceSum += Mathbot.getDistance(getPosX(), getPosY(), i.getPosX(), i.getPosY());
		}
		double averageDistance = distanceSum/(getGameState().getStickingBacteria().size()+10);
		focalOffsetAngleFromPlayer = this.getAngle() - averageAngle;
		focalOffsetDistance = averageDistance;

	}
	private void eliminateBacteria(Bacteria b)
	{
		ArrayList<Bacteria> list = new ArrayList<Bacteria>();
		list.add(b);
		ArrayList<Bacteria> newList;
		newList = eliminateBacteria(b, list);
		if(newList.size() >= 3)
		{
			getGameState().playSound(getGameState().getPows());
			for(Bacteria newB: newList)
			{
				getGameState().getStickingBacteria().remove(newB);
				getGameState().increaseScore(1);
			}
		}
		checkStuckBacteria();
	}
	
	private ArrayList<Bacteria> eliminateBacteria(Bacteria b, ArrayList<Bacteria> alreadyChecked)
	{
		ArrayList<Bacteria> list = new ArrayList<Bacteria>();
		for(int i = 0; i < getGameState().getStickingBacteria().size(); i++)
		{
			if(!alreadyChecked.contains(getGameState().getStickingBacteria().get(i)) && getGameState().getStickingBacteria().get(i).getColour() == b.getColour() && b.isColliding(getGameState().getStickingBacteria().get(i), 5))
			{
				alreadyChecked.add(getGameState().getStickingBacteria().get(i));
				list.add(getGameState().getStickingBacteria().get(i));
			}
		}
		
		for(Bacteria newB : list)
		{
			eliminateBacteria(newB, alreadyChecked);
		}
		return alreadyChecked;
	}
	
	//Method is used after something is eliminated, checks if all connected bacteria are still connected
	private void checkStuckBacteria()
	{
		ArrayList<Bacteria> temp = new ArrayList<Bacteria>();
		for(Bacteria b : getGameState().getStickingBacteria())
		{
			if(b.isColliding(this))
			{
				//All bacteria that are directly touching
				temp.add(b);
			}
		}
		
		for(Bacteria b: getGameState().getStickingBacteria())
		{
			if(!temp.contains(b))
			{
				for(int i = 0; i < temp.size(); i++)
				{
					if(b.isColliding(temp.get(i)))
					{
						temp.add(b);
						break;
					}
				}
			}
		}
		for(int i = 0; i < getGameState().getStickingBacteria().size(); i++)
		{
			if(!temp.contains(getGameState().getStickingBacteria().get(i)))
			{
				getGameState().getStickingBacteria().get(i).kill();
				getGameState().getDyingBacteria().add(getGameState().getStickingBacteria().get(i));
				getGameState().getStickingBacteria().remove(i);
				i--;
			}
		}
	}
}
