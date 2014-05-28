import java.awt.Graphics2D;
import java.util.Random;
import java.io.*;

public class Bacteria extends Cell
{
	public static final int YELLOW = 0, BLUE = 1, GREEN = 2, RED = 3, PURPLE = 4, ORANGE = 5;
	private final int EXPLOSION_DELAY = 2;
	private boolean isAttachedToPlayer, isDead, isExploding, isExploded;
	private double distanceFromPlayer;
	private double angleFromPlayer;
	private int colour;
	private Effect explosion;
	
	public Bacteria(GameState gameState, int colour, int posX, int posY, double angle) {
		super(gameState, getColorFilename(colour), 1);
		explosion = new Effect(getColorGooFilename(colour), 6, EXPLOSION_DELAY);
		this.colour = colour;
		setSpeed(5);
		this.setPosX(posX);
		this.setPosY(posY);
		this.setAngle(angle);
	}
	
	public void update()
	{
		super.update();
		if(!isDead)
		{
			if(!isAttachedToPlayer)
			{
				//check if touching other bacteria or walls, and bounce
				checkBounce();
				move();
			}
			else //it is attached to player
			{
				double newPosX = Mathbot.getPointX(getGameState().getPlayer().getPosX(),
						getGameState().getPlayer().getPosY(), 
						distanceFromPlayer, 
						angleFromPlayer + getGameState().getPlayer().getAngle());
				double newPosY = Mathbot.getPointY(getGameState().getPlayer().getPosX(),
						getGameState().getPlayer().getPosY(), 
						distanceFromPlayer, 
						angleFromPlayer + getGameState().getPlayer().getAngle());
				setPosX(newPosX);
				setPosY(newPosY);
				//TODO: Game over if it is touching the edge of the screen
			}
		}
		else
		{
			if(isExploding)
			{
				explosion.update();
				if(explosion.checkIfDone())
				{
					isExploded = true;
				}
			}
			else if(getPosition().x + getWidth()/2 > getGameState().getScreenWidth() || getPosition().x - getWidth()/2 < 0 || getPosition().y + getHeight()/2> getGameState().getScreenHeight() || getPosition().y - getHeight()/2 < 0)
			{
				isExploding = true;
				getGameState().playSound(getGameState().getPows());
				explosion.setPosX((int)getPosX() - getWidth()/2);
				explosion.setPosY((int)getPosY() - getHeight()/2);
			}
			else
			{
				move();
			}
		}
	}
	
	public void draw(Graphics2D g)
	{
		if(isExploding)
		{
			explosion.draw(g);
		}
		else
		{
			super.draw(g);
		}
	}
	private void checkBounce()
	{
		/* check if touching wall */
		
		if((getPosition().x + getWidth()/2 > getGameState().getScreenWidth() && getAngle() < Math.PI/2 && getAngle() > -Math.PI/2) || (getPosition().x - getWidth()/2 < 0 && (getAngle() > Math.PI/2 || getAngle() < -Math.PI/2)))
		{
			setAngle(Math.PI - getAngle());
		}
		else if(getPosition().y + getHeight()/2> getGameState().getScreenHeight() && getAngle() > 0 || getPosition().y - getHeight()/2 < 0 && getAngle() < 0)
		{
			setAngle(-getAngle());
		}
		
		/* check if center is off screen*/
		
		if(getPosition().x > getGameState().getScreenWidth() || getPosition().x < 0 || getPosition().y < 0 || getPosition().y > getGameState().getScreenHeight())
		{
			double minAngle, maxAngle;
			if(getPosition().x < getGameState().getScreenWidth()/2)
			{
				if(getPosition().y < getGameState().getScreenHeight()/2)
				{
					minAngle = Mathbot.getAngle(getPosition().x, getPosition().y, getGameState().getScreenWidth(), 0);
					maxAngle = Mathbot.getAngle(getPosition().x, getPosition().y, 0, getGameState().getScreenHeight());
				}
				else
				{
					minAngle = Mathbot.getAngle(getPosition().x, getPosition().y, getGameState().getScreenWidth(), getGameState().getScreenHeight());
					maxAngle = Mathbot.getAngle(getPosition().x, getPosition().y, 0, 0);
				}
				
			}
			else
			{
				if(getPosition().y < getGameState().getScreenHeight()/2)
				{
					minAngle = Mathbot.getAngle(getPosition().x, getPosition().y, getGameState().getScreenWidth(), getGameState().getScreenHeight());
					maxAngle = Mathbot.getAngle(getPosition().x, getPosition().y, 0, 0);
				}
				else
				{
					minAngle = Mathbot.getAngle(getPosition().x, getPosition().y, getGameState().getScreenWidth(), 0);
					maxAngle = Mathbot.getAngle(getPosition().x, getPosition().y, 0, getGameState().getScreenHeight());
				}
				
				if(getPosition().y > 0 && getPosition().y < getGameState().getScreenHeight())
				{
					maxAngle += 2*Math.PI;
				}
			}
			if(this.getAngle() < minAngle || this.getAngle() > maxAngle)
			{
				Random random = new Random();
				this.setAngle(minAngle + random.nextDouble() *(maxAngle - minAngle)); 
			}
		}
		
		/*check if touching other bacteria*/
		for(Bacteria b:getGameState().getBouncingBacteria())
		{
			if(b != this && this.isColliding(b))
			{
                double angleBetweenCells = Mathbot.getAngle(b.getPosX(),b.getPosY(),this.getPosX(),this.getPosY());
				double angleLineOfReflection = Mathbot.toNiceAngle(Math.PI/2 + angleBetweenCells);
				
				this.setAngle(angleLineOfReflection + (angleLineOfReflection - this.getAngle()));
				b.setAngle(angleLineOfReflection + (angleLineOfReflection - b.getAngle()));
				/*
				this.setAngle(angleBetweenCells + (angleBetweenCells - this.getAngle()));
				b.setAngle(angleBetweenCells + (angleBetweenCells - b.getAngle()));
				*/
				
				//move the cells apart so they are no longer touching
				while(this.isColliding(b))
				{
					this.move(angleBetweenCells + Math.PI, 0.5);
					b.move(angleBetweenCells, 0.5);
				}
			}
		}
	}
	public void attach()
	{
		isAttachedToPlayer = true;
		distanceFromPlayer = Mathbot.getDistance(getPosX(), getPosY(), getGameState().getPlayer().getPosX(), getGameState().getPlayer().getPosY());
		 angleFromPlayer = Mathbot.getAngle(getGameState().getPlayer().getPosX(), getGameState().getPlayer().getPosY(), getPosX(), getPosY()) - getGameState().getPlayer().getAngle();
	}
	public void detach()
	{
		isAttachedToPlayer = false;
	}
	private static String getColorFilename(int color)
	{
		switch(color)
		{
		case Bacteria.BLUE:
			return "Images" + File.separator + "Blue.png";
		case Bacteria.GREEN:
			return "Images" + File.separator + "Green.png";
		case Bacteria.ORANGE:
			return "Images" + File.separator + "Orange.png";
		case Bacteria.PURPLE:
			return "Images" + File.separator + "Purple.png";
		case Bacteria.RED:
			return "Images" + File.separator + "Red.png";
		case Bacteria.YELLOW:
			return "Images" + File.separator + "Yellow.png";
		default:
				return null;
		}
	}
	
	private static String getColorGooFilename(int color)
	{
		switch(color)
		{
		case Bacteria.BLUE:
			return "Images" + File.separator + "BlueGoo.png";
		case Bacteria.GREEN:
			return "Images" + File.separator + "GreenGoo.png";
		case Bacteria.ORANGE:
			return "Images" + File.separator + "OrangeGoo.png";
		case Bacteria.PURPLE:
			return "Images" + File.separator + "PurpleGoo.png";
		case Bacteria.RED:
			return "Images" + File.separator + "RedGoo.png";
		case Bacteria.YELLOW:
			return "Images" + File.separator + "YellowGoo.png";
		default:
				return null;
		}
	}
	public double getDistanceFromPlayer() {return distanceFromPlayer;}
	public double getAngleFromPlayer() {return angleFromPlayer;}
	public int getColour() {return colour;}
	public void kill() {isDead = true;}
	public boolean isExploded() {return isExploded;}
}
