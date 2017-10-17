import java.awt.BasicStroke;
import java.awt.Color;

/*
 * Class to represent a line that will be rendered onto the screen. Provides methods to return starting and ending
 * coordinates as well as attributes such as thickness and color.
 */

public class Line {
	
	private int startX, startY, endX, endY; // ints that will be used to store the starting and ending coordinates of the line
	private BasicStroke thickness; // Describes the thickness and shape of the line
	private Color color; // To store the color of the line
	private boolean reflected; // To store whether or not the line is reflected in its sector
	
	/*
	 * Constructor to create a new line. Assigns member variables values from parameters.
	 */
	public Line(int startX, int startY, int endX, int endY, BasicStroke thickness, Color color, boolean reflected) {
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		
		this.thickness = thickness;
		this.color = color;
		this.reflected = reflected;
	}
	
	/*
	 * Method to return startX.
	 */
	public int getStartX() {
		return startX;
	}
	
	/*
	 * Method to return endX.
	 */
	public int getEndX() {
		return endX;
	}
	
	/*
	 * Method to return startY.
	 */
	public int getStartY() {
		return startY;
	}
	
	/*
	 * Method to return endY.
	 */
	public int getEndY() {
		return endY;
	}
	
	/*
	 * Method to return thickness stroke.
	 */
	public BasicStroke getThickness() {
		return thickness;
	}
	
	/*
	 * Method to return color.
	 */
	public Color getColor() {
		return color;
	}
	
	/*
	 * Method to return reflected.
	 */
	public boolean getReflected() {
		return reflected;
	}
	
}
