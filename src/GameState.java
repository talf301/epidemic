import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.sound.sampled.*;
import java.io.*;
public class GameState implements KeyListener{
	public static final int KEY_UP = 0, KEY_DOWN = 1, KEY_LEFT = 2, KEY_RIGHT = 3, KEY_A = 4, KEY_S = 5, KEY_W = 6, KEY_D = 7;
	private int screenWidth, screenHeight;
	private int score;
	private boolean[] keyState;
	private boolean gameOver, gameWon;
	private Player player;
	private ArrayList<String> slurps;
	private ArrayList<String> pows;
	private ArrayList<Bacteria> bouncingBacteria;
	private ArrayList<Bacteria> stickingBacteria;
	private ArrayList<Bacteria> dyingBacteria;
	public GameState(JFrame f, int newWidth, int newHeight, ArrayList<Bacteria> bouncingBacteria, ArrayList<Bacteria> stickingBacteria, ArrayList<Bacteria> dyingBacteria,
			ArrayList<String> slurps, ArrayList<String> pows)
	{
		f.addKeyListener(this);
		keyState = new boolean[8];
		screenWidth = newWidth;
		screenHeight = newHeight;
		this.bouncingBacteria = bouncingBacteria;
		this.stickingBacteria = stickingBacteria;
		this.dyingBacteria = dyingBacteria;
		this.slurps = slurps;
		this.pows = pows;
	}
	
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode())
		{
		case KeyEvent.VK_UP:
			keyState[KEY_UP] = true; break;
		case KeyEvent.VK_DOWN:
			keyState[KEY_DOWN] = true; break;
		case KeyEvent.VK_LEFT:
			keyState[KEY_LEFT] = true; break;
		case KeyEvent.VK_RIGHT:
			keyState[KEY_RIGHT] = true; break;
		case KeyEvent.VK_A:
			keyState[KEY_A] = true; break;
		case KeyEvent.VK_S:
			keyState[KEY_S] = true; break;
		case KeyEvent.VK_D:
			keyState[KEY_D] = true; break;
		case KeyEvent.VK_W:
			keyState[KEY_W] = true; break;
		}
	}

	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode())
		{
		case KeyEvent.VK_UP:
			keyState[KEY_UP] = false; break;
		case KeyEvent.VK_DOWN:
			keyState[KEY_DOWN] = false; break;
		case KeyEvent.VK_LEFT:
			keyState[KEY_LEFT] = false; break;
		case KeyEvent.VK_RIGHT:
			keyState[KEY_RIGHT] = false; break;
		case KeyEvent.VK_A:
			keyState[KEY_A] = false; break;
		case KeyEvent.VK_S:
			keyState[KEY_S] = false; break;
		case KeyEvent.VK_D:
			keyState[KEY_D] = false; break;
		case KeyEvent.VK_W:
			keyState[KEY_W] = false; break;
		}
	}
	
	//Method checks given input if a key is down
	public boolean isKeyDown(int key)
	{
		return keyState[key];
	}
	
	public void keyTyped(KeyEvent e) {
		
	}
	
	public int getScreenWidth()
	{
		return screenWidth;
	}
	
	public int getScreenHeight()
	{
		return screenHeight;
	}
	
	public Player getPlayer(){return player;}
	public void setPlayer(Player newPlayer){player = newPlayer;}
	//Moves a bacteria from bouncing arraylist to sticking arraylist
	public void stick(Bacteria bacteria)
	{
		bouncingBacteria.remove(bacteria);
		stickingBacteria.add(bacteria);
	}
	
	public void playSound(ArrayList<String> soundArray)
	{
		Random random = new Random();
		try{
		Clip clip = AudioSystem.getClip();
		AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(soundArray.get(random.nextInt(soundArray.size()))));
		clip.open(inputStream);
		clip.start();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void gameWon(){gameWon = true;}
	public void gameOver(){gameOver = true;}
	public boolean isGameWon(){return gameWon;}
	public boolean isGameOver(){return gameOver;}
	public void increaseScore(int x){score += x;}
	public void resetScore(){score = 0;}
	public int getScore(){return score;}
	public ArrayList<Bacteria> getBouncingBacteria(){return bouncingBacteria;}
	public ArrayList<Bacteria> getStickingBacteria(){return stickingBacteria;}
	public ArrayList<Bacteria> getDyingBacteria(){return dyingBacteria;}
	public ArrayList<String> getSlurps(){return slurps;}
	public ArrayList<String> getPows(){return pows;}
}
