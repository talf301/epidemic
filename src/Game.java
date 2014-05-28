import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.sound.sampled.*;

public class Game extends JPanel{
	private JFrame frame;
	private GameState gameState;
	private Player player;
	private boolean isInit;
	private BufferedImage background, win, lose;
	private ArrayList<String> slurps;
	private ArrayList<String> pows;
	private ArrayList<Bacteria> stickingBacteria, bouncingBacteria, dyingBacteria;
	private ArrayList<Level> levels;
	private int currentLevel = 0;
	public Game (JFrame f)
	{
		super();
		
		setBackground(Color.WHITE);
		frame = f;
		this.setDoubleBuffered(true);
		this.setIgnoreRepaint(true);
	}
	public void paintComponent(Graphics g)
	{	
		super.paintComponent(g);
		Graphics2D g2D = (Graphics2D)g;
		RenderingHints r = new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		r.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2D.setRenderingHints(r);
		g.setColor(Color.WHITE);
		
		//Try catch statement stops drawing during first run through.
		try{
			//Only draw if the game is neither lost nor won
			if(!gameState.isGameOver() && !gameState.isGameWon())
			{	
				
					g2D.drawImage(background, 0, 0, null);
					player.draw(g2D);
					for(Bacteria b: bouncingBacteria)
					{
						b.draw(g2D);
					}
					
					for(Bacteria b: stickingBacteria)
					{
						b.draw(g2D);
					}
					for(Bacteria b: dyingBacteria)
					{
						b.draw(g2D);
					}
			}
			else if(gameState.isGameWon())
			{
				//Draw game winning image
				g2D.drawImage(win,0,0,null);
			}
			else if(gameState.isGameOver())
			{
				//Draw game over image
				g2D.drawImage(lose,0,0,null);
			}
		}
		catch(NullPointerException e)
		{
			System.out.println("Initializing...");
		}

	}
	
	public void update()
	{
		//Calls initialize method the first time the game updates
		if(!isInit)
		{
			init();
			isInit = true;
		}
		try
		{
		//Game logic only happens if game is active
			if(!gameState.isGameOver() && !gameState.isGameWon())
			{
				//Spawn bacteria
				Random random = new Random();
				int totalBacteria = bouncingBacteria.size() + stickingBacteria.size(); 
				for(int i = 0; i < totalBacteria ; i++)
				{
					if(random.nextDouble() < (1/(48 * levels.get(currentLevel).getDoubleRate())))
					{
						generateRandomNewBacteria();
					}
				}
				player.update();
				for(Bacteria b: bouncingBacteria)
				{
					b.update();
				}
				for(Bacteria b: stickingBacteria)
				{
					b.update();
				}
				for(int i = 0; i < dyingBacteria.size(); i++)
				{
					dyingBacteria.get(i).update();
					if(dyingBacteria.get(i).isExploded())
					{
						dyingBacteria.remove(i);
						i--;
					}
				}
				//Checks if level has been beaten, if so move on to next level
				if(gameState.getScore() >= levels.get(currentLevel).getMaxScore())
				{
					nextLevel();
				}
				if(gameState.getBouncingBacteria().size() == 0 && gameState.getStickingBacteria().size() == 0)
				{
					nextLevel();
				}
				
				
			}
			this.repaint();
		}
		catch(NullPointerException e)
		{
			e.printStackTrace();
		}
	}
	
	private void init()
	{
		levels = new ArrayList<Level>();
		for(int i = 1; ;i++)
		{
			File eff = new File(System.getProperty("user.dir") + "\\Data\\Level" + i + ".txt");
			
			if(eff.exists())
			{
				levels.add(new Level(System.getProperty("user.dir") + "\\Data\\Level" + i + ".txt"));
			}
			else
			{
				break;
			}
		}
		reloadBackdrop();
		pows = new ArrayList<String>();
		slurps = new ArrayList<String>();
		bouncingBacteria = new ArrayList<Bacteria>();
		stickingBacteria = new ArrayList<Bacteria>();
		dyingBacteria = new ArrayList<Bacteria> ();
		gameState = new GameState(frame,super.getWidth(), super.getHeight(),bouncingBacteria, stickingBacteria, dyingBacteria, slurps, pows);
		player = new Player(gameState, "Images\\LukeV2_1.png", 4, 500, 500);
		/* create initial bacteria at random locations */
		for(int i = 0; i < levels.get(currentLevel).getNumInitialBacteria(); i++)
		{
			generateRandomNewBacteria();
		}
		try
		{
		Clip clip = AudioSystem.getClip();
		AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(System.getProperty("user.dir") + "\\Sound\\Music1.wav"));  
		clip.open(inputStream); 
		clip.loop(Clip.LOOP_CONTINUOUSLY);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		//slurps

		for(int i = 1; ;i++)
		{
			File eff = new File(System.getProperty("user.dir") + "\\Sound\\Slurp" + i + ".wav");
			if(eff.exists())
			{
				slurps.add(System.getProperty("user.dir") + "\\Sound\\Slurp" + i + ".wav");
			}
			else
			{
				break;
			}
		}
		
		//pows
		for(int i = 1; ;i++)
		{
			File eff = new File(System.getProperty("user.dir") + "\\Sound\\Pow" + i + ".wav");
			if(eff.exists())
			{
				pows.add(System.getProperty("user.dir") + "\\Sound\\Pow" + i + ".wav");
			}
			else
			{
				break;
			}
		}
		
		try
		{
			win = ImageIO.read(new File(System.getProperty("user.dir") + "\\" + "Images\\WIN.png"));
			lose = ImageIO.read(new File(System.getProperty("user.dir") + "\\" + "Images\\LOSE.png"));
		}catch(IOException e)
		{
			e.printStackTrace();

			return;
		}
	
		setVisible(true);
	}
	
	private void nextLevel()
	{
		gameState.resetScore();
		currentLevel++;
		//If this is the last level, you win
		if(currentLevel == levels.size())
		{
			gameState.gameWon();
		}
		else
		{
			//Otherwise, change the background
			reloadBackdrop();
		}
	}
	private void generateRandomNewBacteria()
	{
		Random random = new Random();

		int positionX, positionY;
		int positionOnBorder = random.nextInt(2*this.getWidth() + 2*this.getHeight() + 40 * 8);
		final int BORDER = 60;
		if(positionOnBorder < this.getWidth() + BORDER * 2)
		{
			positionX = positionOnBorder;
			positionY = -BORDER;  
		}
		else if(positionOnBorder < this.getWidth() + this.getHeight() + BORDER * 4)
		{
			positionX = this.getWidth() + 2 * BORDER;
			positionY = positionOnBorder - this.getWidth() - BORDER * 2;
		}
		else if(positionOnBorder < 2*this.getWidth() + this.getHeight() + BORDER * 6)
		{
			positionX = positionOnBorder - this.getWidth() + this.getHeight() + BORDER * 4;
			positionY = this.getHeight() + 2 * BORDER;
		}
		else
		{
			positionX = -40;
			positionY = positionOnBorder - 2*this.getWidth() + this.getHeight() + BORDER * 6; 
		}
		int color = random.nextInt(levels.get(currentLevel).getNumTypesOfBacteria());
		Bacteria b = new Bacteria(gameState, color, positionX, positionY, 0);
		this.gameState.getBouncingBacteria().add(b);
	}
	
	private void reloadBackdrop()
	{
		try
		{
			background = ImageIO.read(new File(System.getProperty("user.dir") + "\\" + levels.get(currentLevel).getBackdrop()));
		}catch(IOException e)
		{
			e.printStackTrace();

			return;
		}
	}
}
