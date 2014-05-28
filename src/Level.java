import java.io.*;
import java.util.Scanner;
public class Level 
{
	private double doubleRate;
	private int numTypesOfBacteria;
	private int numInitialBacteria;
	private int maxScore;
	private String backdrop;
	
	public Level(String filePath)
	{
		try
		{
		Scanner s = new Scanner(new FileReader(filePath));
		doubleRate = s.nextDouble();
		numTypesOfBacteria = s.nextInt();
		numInitialBacteria = s.nextInt();
		maxScore = s.nextInt();
		backdrop = s.next().replaceAll("/", File.separator);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	
	public double getDoubleRate(){return doubleRate;}
	public int getNumTypesOfBacteria(){return numTypesOfBacteria;}
	public int getNumInitialBacteria(){return numInitialBacteria;}
	public int getMaxScore(){return maxScore;}
	public String getBackdrop(){return backdrop;}
}
