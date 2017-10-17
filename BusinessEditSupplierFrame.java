import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/*
 * Class to edit an existing supplier
 */

public class BusinessEditSupplierFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private BusinessStockPanel stockPanel;
	private Supplier supplier; // The supplier to be edited

	/*
	 * Constructor to create a new BusinessEditSupplierFrame
	 */
	public BusinessEditSupplierFrame(BusinessStockPanel stockPanel, Supplier supplier) {
		super("Modify Supplier");
		
		this.stockPanel = stockPanel;
		this.supplier = supplier;
		
		this.init();
		this.pack();
		this.setVisible(true);
	}
	
	/*
	 * Method to initialise the Frame
	 */
	private void init() {
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close this frame only on close
		
		// Create a container panel and give it a vertical BoxLayout
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		
		// Create a title JLabel and centre it (sorta..)
		JLabel title = new JLabel("EDIT SUPPLIER");
		title.setAlignmentX(CENTER_ALIGNMENT);
		container.add(title);
		
		// Create a name panel, add a label and a textbox and then add them to the panel
		JPanel namePanel = new JPanel();
		JLabel nameLabel = new JLabel("Name: ");
		JTextField nameTextBox = new JTextField(10);
		nameTextBox.setText(supplier.getName()); // Set text to current name
		namePanel.add(nameLabel);
		namePanel.add(nameTextBox);
		
		// Create a unit panel, add a label and a textbox and then add them to the panel
		JPanel distancePanel = new JPanel();
		JLabel distanceLabel = new JLabel("Distance: ");
		JTextField distanceTextBox = new JTextField(4);
		distanceTextBox.setText(String.valueOf(supplier.getDistance())); // Set text to current distance
		distancePanel.add(distanceLabel);
		distancePanel.add(distanceTextBox);
		
		// Add everything to the container
		container.add(namePanel);
		container.add(distancePanel);
		
		// Create a JButton to save the changes
		JButton saveBttn = new JButton("Save");
		saveBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					// Get name and distance
					String name = nameTextBox.getText();
					Integer distance = Integer.parseInt(distanceTextBox.getText());
					
					// Name must be provided and distance can't be negative or 0
					if (name.equals("") || distance <= 0) {
						throw new InvalidDetailsException();
					}
					
					// Update the supplier
					updateSupplier(name, distance);
				} catch (Exception e1) {
					// If invalid details were provided show an error message dialog
					JOptionPane.showMessageDialog(BusinessEditSupplierFrame.this, "Invalid details!", "Editing Failed", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		container.add(saveBttn);
		
		// Add the container to the frame
		this.add(container);
	}
	
	/*
	 * Method to update an Ingredient
	 */
	private void updateSupplier(String name, int distance) {		
		// Set the new name and distance
		supplier.setName(name);
		supplier.setDistance(distance);
		
		// Refresh the panel to display changes and close the frame
		stockPanel.refresh();
		this.dispose();
		this.setVisible(false);
	}
	
}
