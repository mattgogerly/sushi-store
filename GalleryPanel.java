import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/*
 * Class to represent the Gallery of Doilys. This is a panel on the main DoilyFrame. We store up to 12 
 * BufferedImages (each a Doily). Users can remove a Doily from the gallery by clicking on it and then 
 * clicking the remove button.
 */

public class GalleryPanel extends JPanel {
	
	private JPanel gallery; // The main gallery JPanel.
	
	private ArrayList<JPanel> images = new ArrayList<JPanel>(); // An ArrayList to store the JPanels of the images.
	private ArrayList<JPanel> selected = new ArrayList<JPanel>(); // An ArrayList (since size is dynamic) of JPanels to store the selected panels.
	
	private final Color SELECTED_BLUE = new Color(135, 206, 250); // Pale blue to indicate an image has been selected.
	
	private final int DISPLAY_WIDTH = 140;
	private final int DISPLAY_HEIGHT = 115;
	
	/*
	 * Constructor to create a new GalleryPanel.
	 */
	public GalleryPanel() {
		super();
		init(); // Initialise the GalleryPanel.
	}
	
	/*
	 * Method to initialise the JPanel. Adds placeholder JPanels and the remove button.
	 */
	public void init() {
		this.setPreferredSize(new Dimension(400, 800)); // Allows a decent size for the saved Doilys.
		this.setLayout(new BorderLayout()); // A BorderLayout allows a large central area and small button.
		this.setBackground(Color.white); // Background colour consistent with the ControlPanel.
		
		gallery = new JPanel(); // A JPanel that will hold the JPanels consisting of saved images.
		gallery.setLayout(new GridLayout(6, 2)); // Make a grid of 12 cells to store the JPanels.
		
		JButton removeBttn = new JButton("Remove Selected Doilys"); // Button to allow the user to remove Doilys.
		// Add an ActionListener to the button so that removeSelectedImages() is called on click.
		removeBttn.addActionListener(new ActionListener() {
			// Method called on click.
			public void actionPerformed(ActionEvent e) {
				removeSelectedImages(); // Remove the currently selected Doilys.
			}
		});
		
		this.add(gallery, BorderLayout.CENTER); // Add the gallery JPanel to the centre of the layout.
		this.add(removeBttn, BorderLayout.SOUTH); // Add the remove button to the bottom of the layout.
	}
	
	/*
	 * Method to remove all currently selected images.
	 */
	public void removeSelectedImages() {
		Iterator<JPanel> it = selected.iterator(); // Iterator to iterate over the selected ArrayList (so we can remove while looping).
		
		// Iterate through the ArrayList
		while (it.hasNext()) {
			JPanel panel = it.next(); // Store the current JPanel for manipulation.
			
			// Loop over the images array
			for (int i = 0; i < images.size(); i++) {
				// If the current panel is the same panel as the current panel at images' index...
				if (panel == images.get(i)) {
					images.remove(panel); // Remove it from the images ArrayList.
					it.remove(); // Remove the current JPanel from the selected ArrayList.
				}
			}
		}
		
		refreshGallery(); // Redraw the gallery to show the changes.
	}
	
	/*
	 * Method to add an image of a Doily to the gallery.
	 */
	public void addImage(BufferedImage image) {
		// We can only store 12 so if there's no freeIndexes left display an error popup message.
		if (images.size() == 12) {
			JOptionPane.showMessageDialog(this, "You must delete a doily before trying to add another!", "Error", JOptionPane.ERROR_MESSAGE); // Show error message.
		} else {
			BufferedImage resizedImage = resizeImage(image); // Resize the BufferedImage so that it fits in our gallery.

			JPanel imagePanel = new JPanel(); // Create a JPanel to display the image's label (so we can setBackground() on select).
			JLabel imageLabel = new JLabel(new ImageIcon(resizedImage)); // Create a JLabel holding an ImageIcon to display the image.
			imagePanel.add(imageLabel); // Add the label to the panel.
			imagePanel.setBorder(BorderFactory.createLineBorder(Color.gray)); // Give the panel a border.
			
			// Add a mouse listener to the JPanel to allow us to select it.
			imagePanel.addMouseListener(new MouseAdapter() {
				// Method is called on click
				public void mousePressed(MouseEvent e) {
					JPanel clicked = (JPanel) e.getComponent(); // Get the JPanel that has been clicked (i.e. the new JPanel).
					
					// If the JPanel is already in the selected ArrayList.
					if (selected.contains(clicked)) {
						selected.remove(clicked); // Remove it from the selected ArrayList.
						clicked.setBackground(Color.white); // Change the background colour back to white.
					} else {
						selected.add(clicked); // Otherwise add it to the selected ArrayList.
						clicked.setBackground(SELECTED_BLUE); // Change the background to the selected background colour.
					}
				}
			});
			
			images.add(imagePanel); // Add the JPanel to the images ArrayList.
			refreshGallery(); // Redraw the gallery.
		}
	}
	
	/*
	 * Method to resize a BufferedImage.
	 * Implementation adapated from: http://stackoverflow.com/questions/9417356/bufferedimage-resize
	 */
	public BufferedImage resizeImage(BufferedImage image) {
		Image tempImage = image.getScaledInstance(DISPLAY_WIDTH, DISPLAY_HEIGHT, Image.SCALE_SMOOTH); // Our temporary image is a scaled version of the original.
	    BufferedImage resizedImage = new BufferedImage(DISPLAY_WIDTH, DISPLAY_HEIGHT, BufferedImage.TYPE_INT_ARGB); // Create a BufferedImage to draw the resized Image onto.

	    Graphics2D g2d = resizedImage.createGraphics(); // Create a Graphics2D instance to draw onto the resized BufferedImage.
	    g2d.drawImage(tempImage, 0, 0, null); // Draw the resized image onto the BufferedImage.
	    g2d.dispose(); // Dispose of the resources used by the Graphics2D instance.

	    return resizedImage; // Return the resized BufferedImage.
	}
	
	/*
	 * Method to redraw the gallery.
	 */
	public void refreshGallery() {
		gallery.removeAll(); // Remove every component from the main gallery JPanel.
		
		// Loop over the images ArrayList and add the JPanels in it to the gallery panel.
		for (JPanel panel : images) {
			gallery.add(panel); // Add the JPanel the gallery JPanel.
		}
		
		this.revalidate(); // Tell the JPanel we've changed components so that it can display them.
		this.repaint(); // Repaint to make sure it's updated (ocasionally breaks otherwise).
	}
	
}
