import java.awt.*;

public class Mathbot
{
	public static double getDistance(double firstX, double firstY, double secondX, double secondY)
	{
		return Math.sqrt((firstX - secondX)*(firstX - secondX) + (firstY - secondY)*(firstY - secondY));
	}
	
	public static double toNiceAngle(double angle)
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
	
	public static double getPointX(double centerX, double centerY, double distance, double angle)
	{
		return centerX + distance*Math.cos(angle);
	}
	public static double getPointY(double centerX, double centerY, double distance, double angle)
	{

		return centerY + distance*Math.sin(angle);
	}
	public static double getAngle(double centerX, double centerY, double pointX, double pointY)
	{
		double opposite = pointY - centerY;
		double adjacent = pointX - centerX;
		double angle = Math.atan(opposite/adjacent);
		if(pointX < centerX)
		{
			angle = Math.PI + angle;
		}
		
		return toNiceAngle(angle);
	}
}
