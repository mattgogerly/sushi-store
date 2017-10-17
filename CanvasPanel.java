import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*
 * Class to represent a canvas on which the user draws. Implements MouseListener and MouseMotionListener
 * to allow the user to draw. Also provides methods to return a BufferedImage (to display in the gallery)
 * and set pen controls (used by the ControlPanel).
 */

public class CanvasPanel extends JPanel implements MouseListener, MouseMotionListener {
	
	private boolean sectorLinesVisible = true; // Boolean to flag whether or not sector lines should be visible.
	private int noOfSectors; // int to store the number of sectors.
	
	private BufferedImage image; // BufferedImage onto which the user will actually draw.
	private JLabel imageLabel; // JLabel to be used to actually display the image.
	
	private ArrayList<Line> lines;  // ArrayList so store the drawn Lines.
	private boolean reflect; // Boolean to flag whether drawn lines should be reflected.
	
	private int xOrigin, yOrigin; // ints to store the origin of the "graph".
	
	private int beginX, beginY; // ints to store the starting point of the current line.
	
	private Color currentColor; // Color object to store the current drawing color.
	private BasicStroke currentThickness; // BasicStroke to store the current line thickness..
	
	/*
	 * Constructor to create a new CanvasPanel
	 */
	public CanvasPanel() {
		super();
		this.setPreferredSize(new Dimension(800, 800)); // If we don't set a size then our BufferedImage won't be happy
	}
	
	/*
	 * Method to initialise the canvas.
	 */
	public void init() {
		this.setBackground(Color.black); // Set the (largely invisible) background to black.
        
        this.image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB); // Initialise our BufferedImage as the size of the panel.
        this.imageLabel = new JLabel(new ImageIcon(image)); // Initialise our image label to store an ImageIcon of the BufferedImage.
        this.add(imageLabel); // Add the image label to the panel to display the BufferedImage.
        
        this.currentColor = Color.white; // Set our beginning color to white.
        this.currentThickness = new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND); // Set initial thickness. Rounded for circular single points.
   
        this.lines = new ArrayList<Line>(); // Initialise the ArrayList of Lines.
        this.noOfSectors = 12; // Set initial number of sectors to be 12.
        this.reflect = false; // By default we don't want to reflect points.
        		
        this.xOrigin = image.getWidth() / 2; // Our x origin is half the width of the image.
        this.yOrigin = image.getHeight() / 2; // Our y origin is half the height of the image.
        
        // Add ourself as a MouseListener and MouseMotionListener (since we implement them).
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        
        this.resetImage(); // Reset the image to draw the initial sector lines
	}
	
	/*
	 * Method to set the number of sectors displayed.
	 */
	public void setNoOfSectors(int number) {
		this.noOfSectors = number;
		resetImage(); // Redraw the image with the new number of sectors.
	}
	
	/*
	 * Method to set the current drawing color.
	 */
	public void setColor(Color c) {
		this.currentColor = c;
	}
	
	/*
	 * Method to set the current drawing thickness.
	 */
	public void setThickness(BasicStroke thickness) {
		this.currentThickness = thickness;
	}
	
	/*
	 * Method to toggle displaying the sector lines.
	 */
	public void toggleSectorLines(boolean visible) {
		this.sectorLinesVisible = visible;
		resetImage(); // Redraw the image with/without sector lines visible.
	}
	
	/*
	 * Method to toggle reflecting lines within each sector.
	 */
	public void toggleReflection(boolean reflect) {
		this.reflect = reflect;
	}
	
	/*
	 * Method to get the current BufferedImage (to store in the gallery).
	 */
	public BufferedImage getImage() {
		return image;
	}
	
	/*
	 * Overridden method to handle drawing onto the BufferedImage. Draws whatever is currently offscreen onto
	 * the main BufferedImage.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = image.createGraphics(); // Create a new Graphics2D object to draw onto the BufferedImage.
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Add anti-aliasing to get rid of jaggies.
		
		g2d.drawImage(image, 0, 0, null); // Draw all of this onto the BufferedImage.
		g2d.dispose(); // Dispose of resources used by Graphics2D object.
		repaint(); // To get the updated BufferedImage to display
	}
	
	/*
	 * Method to clear all Lines from the screen.
	 */
	public void clear() {
		lines.clear(); // Remove all Lines from the ArrayList.
		resetImage(); // Reset the image.
	}
	
	/*
	 * Method to reset the BufferedImage (wipe it and redraw any Lines remaining in the ArrayList).
	 */
	public void resetImage() {
		Graphics2D g2d = image.createGraphics(); // Create a Graphics2D object to draw with.
		
		g2d.setColor(Color.black); // Set the drawing color to black.
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight()); // Draw a rectangle the size of the BufferedImage (to hide everything beneath it).
		
		// If set to visible then paint sector lines onto the image.
		if (sectorLinesVisible) {
			paintSectorLines(g2d);
		}
		
		redrawAllLines();
		
		g2d.dispose(); // Dispose of the resources used by the Graphics2D object.
	}
	
	/*
	 * Method to remove the last 5 drawn Lines (undo).
	 */
	public void removeLines() {
		int size = lines.size(); // Get the current size of the ArrayList of stored Lines.
		
		// If there are less than 5 then just clear the ArrayList.
		if (size < 5) {
			this.clear();
		} else {
			lines.subList(size - 5, size).clear(); // Otherwise create a sublist from end - 5 to end and clear that instead.
		}
		
		resetImage(); // Redraw the image without the last 5 lines.
	}
	
	private void drawLine(Line l) {
		Graphics2D g2d = image.createGraphics(); // Create a new Graphics2D object to draw onto the BufferedImage.
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Add anti-aliasing to get rid of jaggies.
		g2d.setColor(l.getColor()); // Set the drawing color to the color of the Line.
		g2d.setStroke(l.getThickness()); // Set the drawing thickness to the thickness of the Line.
		
		AffineTransform old = g2d.getTransform(); // Store the current transformation (i.e. none) so we can revert to it later.
		double angle = 2 * Math.PI / noOfSectors; // The angle to rotate by is equal to 360 / number of sectors (in radians).
		
		// Loop until we reach the specified number of sectors.
		for (int i = 0; i < noOfSectors; i++) {	
			g2d.drawLine(l.getStartX(), l.getStartY(), l.getEndX(), l.getEndY()); // Draw a new line for this rotation.
			g2d.rotate(angle, xOrigin, yOrigin); // Rotate about our origin by the calculated angle.
			
			// If the original Line was set to be reflected...
			if (l.getReflected()) {
				int startX, endX; // ints to store our new start and end x values.
				startX = this.getWidth() - l.getStartX();
				endX = this.getWidth() - l.getEndX();
				
				g2d.drawLine(startX, l.getStartY(), endX, l.getEndY()); // Draw the reflected Line.
			}
		}

		g2d.setTransform(old); // Go back to the original transformation (none).
		g2d.dispose(); // Dispose of any resources used by the Graphics2D object.
	}
	
	/*
	 * Method to redraw all lines in the ArrayList.
	 */
	private void redrawAllLines() {
		// Loop over the lines ArrayList.
		for (Line l : lines) {
			drawLine(l); // Draw each Line.
		}
	}
	
	/*
	 * Method to paint the sector lines onto the BufferedImage
	 */
	private void paintSectorLines(Graphics2D g2d) {
		double sectorAngle = 2 * Math.PI / noOfSectors; // Angle to rotate by is 360 / the number of sectors.
		
		g2d.setColor(Color.WHITE); // Set our drawing color to white.
		
		AffineTransform old = g2d.getTransform(); // Store the current transformation (i.e. none) so we can revert to it later.
		
		// Loop until we reach the number of sectors.
		for (int i = 0; i < noOfSectors; i++) {
			g2d.drawLine(xOrigin, yOrigin, xOrigin, -200); // Draw a line from the origin to the end coordinates (-200 so that the lines reach the edge for diagonals).
			g2d.rotate(sectorAngle, xOrigin, yOrigin); // Rotate by the sector angle to where we need to draw the next sector line.
		}
		
		g2d.setTransform(old); // Go back to the original transformation (none).
	}
	
	/*
	 * Method to add a Line to the ArrayList of Lines and draw it.
	 */
	private void addLine(Line l) {
		lines.add(l); // Add the Line to the ArrayList.
		drawLine(l); // Draw the Line.
	}
	
	/*
	 * Method to handle mouse clicks (singly drawn points).
	 */
	public void mouseClicked(MouseEvent e) {
		Line l = new Line(e.getX(), e.getY(), e.getX(), e.getY(), this.currentThickness, this.currentColor, this.reflect); // Create a new Line.
		addLine(l); // Add this Line to the ArrayList and draw it.
	}
	
	/*
	 * Method to handle the mouse being dragged (mouse button down and moving).
	 */
	public void mouseDragged(MouseEvent e) {
		Line l = new Line(beginX, beginY, e.getX(), e.getY(), this.currentThickness, this.currentColor, this.reflect); // Create a new Line.
		addLine(l); // Add a new Line from the stored beginning position to this position and draw it.
		
		// Set the beginning position to this position.
		beginX = e.getX(); 
		beginY = e.getY();
	}
	
	/* 
	 * Method to handle a mouse button being pressed.
	 */
	public void mousePressed(MouseEvent e) {
		// Set the beginning position to this position.
		beginX = e.getX();
		beginY = e.getY();
	}

	public void mouseReleased(MouseEvent e) {
		// Method intentionally left empty
	}

	public void mouseMoved(MouseEvent e) {
		// Method intentionally left empty
	}

	public void mouseEntered(MouseEvent e) {
		// Method intentionally left empty
	}

	public void mouseExited(MouseEvent e) {
		// Method intentionally left empty
	}
	
}