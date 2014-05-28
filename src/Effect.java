import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;

public class Effect {
	private int posX, posY;
	private boolean isDone = false;
	private BufferedImage[] sprites;
	private int currentSpriteIndex;
	private int numFrames;
	private int frameDelay;
	private int delayCount;
	
	public Effect(String filePath, int frames, int delay)
	{
		BufferedImage image;
		try
		{
			image = ImageIO.read(new File(System.getProperty("user.dir") + "\\" + filePath));
		}catch(IOException e)
		{
			e.printStackTrace();
			for(int i = 0; i < Integer.MAX_VALUE; i++)
			{
				e.printStackTrace();
			}
			return;
		}
		numFrames = frames;
		sprites = new BufferedImage[numFrames];
		for(int i = 0; i < numFrames; i++)
		{
			sprites[i] = image.getSubimage(0, i*image.getHeight()/numFrames, image.getWidth(), image.getHeight()/numFrames);
		}
		//Ensure that images start from first frame
		currentSpriteIndex = 0;
		frameDelay = delay;
		//Delay will be used to make the animation look more smooth
		delayCount = 0;
	}
	
	public void draw(Graphics2D g)
	{
		g.drawImage(sprites[currentSpriteIndex], posX, posY, null);
	}
	
	public void update()
	{
		delayCount++;
		if(delayCount == frameDelay)
		{
			delayCount = 0;
			currentSpriteIndex++;
			if(currentSpriteIndex == numFrames)
			{
				isDone = true;
				currentSpriteIndex = 0;
			}
		}
	}
	
	public void setPosX(int x){posX = x;}
	public void setPosY(int y){posY = y;}
	public boolean checkIfDone(){return isDone;}
}
