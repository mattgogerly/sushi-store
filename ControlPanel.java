import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/*
 * This class represents the ControlPanel that is displayed on the main DoilyFrame. This panel allows users to control settings
 * such as drawing size and colour as well as enabling/disabling reflections and changing the number of sectors. This panel also
 * provides buttons to allow the user to save, undo and clear the current Doily.
 */

public class ControlPanel extends JPanel {
	
	private CanvasPanel canvas; // The CanvasPanel we will be controlling the settings for.
	private GalleryPanel gallery; // The GalleryPanel we will be saving Doilys to.
	
	/*
	 * Constructor to create a new ControlPanel. Takes the CanvasPanel and GalleryPanel it will be interacting with as parameters.
	 */
	public ControlPanel(CanvasPanel canvas, GalleryPanel gallery) {
		super();

		this.canvas = canvas; // Set the member variable canvas to the one supplied.
		this.gallery = gallery; // Set the member variable gallery to the one supplied.
		
		init(); // Initialise the ControlPanel.
	}
	
	/*
	 * Method to initialise the ControlPanel upon creation.
	 */
	public void init() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT)); // Set the layout to a GridLayout. One row and four columns for four sections.
		this.setBackground(Color.white); // Set the background to white for consistency.
		
		setupDrawingControls(); // Set up the drawing controls (colour/size).
		setupSectorControls(); // Set up controls for sectors (number of/displaying lines/reflecting).
		setupMiscControls(); // Set up undo, save and clear.
	}
	
	/*
	 * Method to set up drawing controls (colour/size).
	 */
	public void setupDrawingControls() {
		ColorPickerFrame colorPicker = new ColorPickerFrame(canvas); // Create a new ColorPickerFrame.
		JButton colorBttn = new JButton("Choose Colour"); // Create a button that will allow us to open the ColorPickerFrame.
		colorBttn.addActionListener(new ActionListener() {
			// Method is run when the button is clicked.
			public void actionPerformed(ActionEvent e) {
				colorPicker.setVisible(true); // Set the ColorPickerFrame to be visible.
			}
		});
		
		JLabel drawingSizeLabel = new JLabel("Drawing Size:"); // Create a label for the drawing size slider.
		JSlider drawingSizeSlider = new JSlider(JSlider.HORIZONTAL, 2, 20, 10); // Create a JSlider to control drawing size (min 2, max 20, default 10).
		drawingSizeSlider.setBackground(Color.white); // Set the background colour to white to match the ControlPanel.
		drawingSizeSlider.setMajorTickSpacing(2); // Show a label for each increment of 2.
		drawingSizeSlider.setPaintLabels(true); // Display these labels.
		drawingSizeSlider.addChangeListener(new ChangeListener() {
			// Method is run when the slider is moved.
			public void stateChanged(ChangeEvent e) {
				canvas.setThickness(new BasicStroke(drawingSizeSlider.getValue(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); // Set the drawing size on the CanvasPanel.
			}
		});
		
		// Add all components to the ControlPanel.
		this.add(drawingSizeLabel);
		this.add(drawingSizeSlider);
		this.add(colorBttn);
	}
	
	/*
	 * Method to set up sector controls (number of sectors/reflection/display of lines).
	 */
	public void setupSectorControls() {
		
		JLabel noOfSectorsLabel = new JLabel("# of Sectors:"); // Create a label for the number of sectors slider.
		JSlider noOfSectorsSlider = new JSlider(JSlider.HORIZONTAL, 0, 80, 12); // Create a JSlider to control the number of sectors (min 0, max 80, default 12).
		noOfSectorsSlider.setBackground(Color.white); // Set the background colour to white to match the ControlPanel.
		noOfSectorsSlider.setMajorTickSpacing(10); // Show a label for each increment of 10.
		noOfSectorsSlider.setPaintLabels(true); // Display these labels.
		noOfSectorsSlider.addChangeListener(new ChangeListener() {
			// Method is run when the slider is moved.
			public void stateChanged(ChangeEvent e) {
				canvas.setNoOfSectors(noOfSectorsSlider.getValue()); // Set the number of sectors on the CanvasPanel to the new value.
			}
		});
		
		JCheckBox sectorLinesCheckbox = new JCheckBox("Show sector lines"); // Create a JCheckBox for displaying sector lines.
		sectorLinesCheckbox.setBackground(Color.white); // Set the background colour to white to match the ControlPanel.
		sectorLinesCheckbox.setSelected(true); // By default we want to display sector lines.
		// Add an ActionListener for when the JCheckBox is clicked.
		sectorLinesCheckbox.addItemListener(new ItemListener() {
			// Method run when the state is changed (i.e. ticked or unticked).
			public void itemStateChanged(ItemEvent e) {
				canvas.toggleSectorLines(sectorLinesCheckbox.isSelected()); // Toggle sector lines on/off.
			}
		});
        
        JCheckBox reflectionCheckbox = new JCheckBox("Reflect points"); // Create a JCheckBox for reflecting subsequently drawn points.
        reflectionCheckbox.setBackground(Color.white); // Set the background colour to white to match the ControlPanel.
        reflectionCheckbox.setSelected(false); // By default we don't want to reflect points.
        // Add an ActionListener for when the JCheckBox is clicked.
        reflectionCheckbox.addItemListener(new ItemListener() {
        	// Method run when the state is changed (i.e. ticked or unticked).
			public void itemStateChanged(ItemEvent e) {
				canvas.toggleReflection(reflectionCheckbox.isSelected()); // Toggle reflection on/off.
			}
		});
        
        
        // Add all these components to the ControlPanel.
        this.add(noOfSectorsLabel);
        this.add(noOfSectorsSlider);
		this.add(sectorLinesCheckbox);
		this.add(reflectionCheckbox);
	}
	
	/*
	 * Method to set up miscellaneous controls (undo/save/clear).
	 */
	public void setupMiscControls() {
		JButton undoBttn = new JButton("Undo"); // Create a new JButton for undoing the last 5 points.
		// Add an ActionListener for when the JButton is clicked.
		undoBttn.addActionListener(new ActionListener() {
			// Method run when the JButton is clicked.
			public void actionPerformed(ActionEvent e) {
				canvas.removeLines(); // Remove the last 5 lines from the canvas.
			}
		});
		
		JButton saveBttn = new JButton("Save"); // Create a new JButton for saving the current Doily to the gallery.
		// Add an ActionListener for when the JButton is clicked.
		saveBttn.addActionListener(new ActionListener() {
			// Method run when the JButton is clicked.
			public void actionPerformed(ActionEvent e) {
				gallery.addImage(canvas.getImage()); // Add the current image to the gallery.
				gallery.revalidate();
			}
		});
		
		JButton clearBttn = new JButton("Clear"); // Create a new JButton for clearing the canvas.
		// Add an ActionListener for when the JButton is clicked.
		clearBttn.addActionListener(new ActionListener() {
			// Method run when the JButton is clicked.
			public void actionPerformed(ActionEvent e) {
				canvas.clear(); // Clear the canvas.
			}
		});
		
		// Add the new JButtons to the main ControlPanel.
		this.add(undoBttn);
		this.add(saveBttn);
		this.add(clearBttn);
	}
	
}