import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;

public class Cell 
{
	private double posX, posY;
	private int health;
	private BufferedImage sprites[];
	private int currentSpriteIndex;
	private GameState gameState;
	private double angle;
	private AffineTransform trans;
	private double speed;
	private int spriteDelay = 6;
	private int spriteCount = 0;
	
	public Cell(GameState gameState, String fileName, int numFrames)
	{
		BufferedImage image;
		try
		{
			image = ImageIO.read(new File(System.getProperty("user.dir") + "\\" + fileName));
		}catch(IOException e)
		{
			e.printStackTrace();

			return;
		}
		sprites = new BufferedImage[numFrames];
		for(int i = 0; i < numFrames; i++)
		{
			sprites[i] = image.getSubimage(0, i*image.getHeight()/numFrames, image.getWidth(), image.getHeight()/numFrames);
		}
		
		trans = new AffineTransform();
		this.gameState = gameState;
	}
	
	public void draw(Graphics2D g)
	{
		
		trans.setToIdentity();
		trans.setToRotation(angle, posX, posY);
		trans.translate(posX - sprites[currentSpriteIndex].getWidth(null)/2, posY - sprites[currentSpriteIndex].getHeight(null)/2);
		g.drawImage(sprites[currentSpriteIndex], trans, null);
	}
	public void move()
	{
		posX = posX + speed*Math.cos(angle);
		posY = posY + speed*Math.sin(angle);
	}
	
	public void reverse()
	{
		posX = posX - speed*Math.cos(angle);
		posY = posY - speed*Math.sin(angle);
	}
	public void move(double distance)
	{
		posX = posX + distance*Math.cos(angle);
		posY = posY + distance*Math.sin(angle);
	}
	public void move(double moveAngle, double distance)
	{
		moveAngle = toNiceAngle(moveAngle);
		posX = posX + distance*Math.cos(moveAngle);
		posY = posY + distance*Math.sin(moveAngle);
	}
	public void turn(double angleDiff)
	{
		angle += angleDiff;
	}
	public void turn(double angleDiff, double focalX, double focalY)
	{
		//find angle to cell from focal point
		if(focalX != posX && focalY != posY)
		{
			double angleToCell = Mathbot.getAngle(focalX, focalY, posX, posY);
			double distanceToCell = Mathbot.getDistance(focalX, focalY, posX, posY);
			double newAngleToCell = angleToCell + angleDiff;
			posX = focalX + distanceToCell * Math.cos(newAngleToCell);
			posY = focalY + distanceToCell * Math.sin(newAngleToCell);
		}
		angle += angleDiff;
	}
	public void adjustHealth(int change)
	{
		health = health + change;
	}
	public boolean isColliding(Cell otherCell)
	{
		if(Math.pow(posX - otherCell.posX, 2) + Math.pow(posY - otherCell.posY,2) <= Math.pow(getWidth()/2 + otherCell.getWidth()/2, 2))
		{
			return true;
		}
		return false;
	}
	public boolean isColliding(Cell otherCell, int offset)
	{
		if(Math.pow(posX - otherCell.posX, 2) + Math.pow(posY - otherCell.posY,2) <= Math.pow(getWidth()/2 + otherCell.getWidth()/2 + offset, 2))
		{
			return true;
		}
		return false;
	}

	public void update()
	{
		//advance currentSpriteIndex
		spriteCount++;
		if(spriteCount > spriteDelay)
		{
			spriteCount = 0;
			if(currentSpriteIndex + 1 < sprites.length)
			{
				currentSpriteIndex ++;
			}
			else
			{
				currentSpriteIndex = 0;
			}
		}
	}
	
	private double toNiceAngle(double angle)
	{
		if(angle < -Math.PI)
		{
			return toNiceAngle(angle + 2*Math.PI);
		}
		if(angle > Math.PI)
		{
			return toNiceAngle(angle - 2*Math.PI);
		}
		return angle;
	}
	//getters and setters
	public int getWidth(){return sprites[0].getWidth();}
	
	public int getHeight(){return sprites[0].getHeight();}
	
	public void setPosX(double newPosX) {posX = newPosX;}
	
	public void setPosY(double newPosY) {posY = newPosY;}
	
	public double getPosX() {return posX;}
	
	public double getPosY() {return posY;}

	public Point getPosition() {return new Point((int)posX, (int)posY);}
	
	public void setSpeed(double newSpeed){speed = newSpeed;}
	public double getSpeed(){return speed;}
	
	public void setAngle(double newAngle){angle = toNiceAngle(newAngle);}
	public double getAngle() {return angle;}
	
	public GameState getGameState(){return gameState;}
	
	public void setHealth(int newHealth) {health = newHealth;}
	public int getHealth() {return health;}
}
