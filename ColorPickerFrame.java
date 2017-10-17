import java.awt.Color;

import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/*
 * JFrame to allow the user to select the drawing colour to be used on the canvas.
 */

public class ColorPickerFrame extends JFrame {
	
	private CanvasPanel canvas; // The canvas that we will be changing the colour for.
	
	/*
	 * Constructor to create a new ColorPickerFrame.
	 */
	public ColorPickerFrame(CanvasPanel canvas) {
		super("Colour Picker"); // Create the JFrame.
		this.canvas = canvas; // Set the CanvasPanel member variable.
		
		init(); // Initialise the ColorPickerFrame.
	}
	
	/*
	 * Method to initialise the ColorPickerFrame.
	 */
	private void init() {
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // We want to only close the Color Picker if the X is clicked.
		
		JColorChooser colorPicker = new JColorChooser(); // Create a new JColorChooser to allow selecting colours.
		ColorSelectionModel colorModel = colorPicker.getSelectionModel(); // Create a new ColorSelectionModel to handle events.
		
		// Add a change listener to the model.
		colorModel.addChangeListener(new ChangeListener() {
			// Method is run when the JColorChooser is clicked (i.e. a colour is selected).
			public void stateChanged(ChangeEvent e) {
		        Color drawColor = colorPicker.getColor(); // Get the Colour that was selected.
		        canvas.setColor(drawColor); // Set the drawing colour on the canvas to this Colour.
			}
		});
		
		this.add(colorPicker); // Add the JColorChooser to the frame.
		
		this.pack(); // Pack the window to size it appropriately.
	}
	
}
