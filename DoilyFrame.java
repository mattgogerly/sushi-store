import java.awt.BorderLayout;

import javax.swing.JFrame;

/*
 * DoilyFrame represents the window that holds the controls and canvas, as well as creates the gallery. Extends
 * JFrame to provide the functionality.
 */

public class DoilyFrame extends JFrame {
	
	/* 
	 * Constructor to create a new DoilyFrame with the title Digital Doily.
	 */
	public DoilyFrame() {
		super("Digital Doily");
	}
	
	/*
	 * Method to initialise the window and make it visible. 
	 */
	public void init() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // We want to exit the program when this window is closed.
		this.setLayout(new BorderLayout()); // Using a BorderLayout to allow for easy positioning.
		this.setResizable(false); // BufferedImages hate resizing so prevent the user from resizing the window.
		
		CanvasPanel canvas = new CanvasPanel(); // Create a new CanvasPanel to draw on.
		GalleryPanel gallery = new GalleryPanel(); // Create a new GalleryPanel to display saved doilys.
		ControlPanel controls = new ControlPanel(canvas, gallery); // Create a new ControlPanel to handle drawing controls.
		
		this.add(controls, BorderLayout.NORTH); // Add the controls to the top of the window.
		this.add(canvas, BorderLayout.CENTER); // Add the canvas to the centre.
		this.add(gallery, BorderLayout.EAST); // Add the gallery to the east.
		
		this.pack(); // Pack the frame.
		
		canvas.init(); // We need to initialise the canvas after we pack otherwise BufferedImage will have size 0 and throw an error.
		
		setVisible(true); // Make the window visible.
	}
	
}
