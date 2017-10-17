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
 * JFrame to add a new Supplier to the business
 */

public class BusinessAddSupplierFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private BusinessApplication businessApp;
	private BusinessStockPanel stockPanel;

	/*
	 * Constructor to create a new BusinessAddSupplierFrame
	 */
	public BusinessAddSupplierFrame(BusinessStockPanel stockPanel, BusinessApplication businessApp) {
		super("Add Supplier");
		
		this.businessApp = businessApp;
		this.stockPanel = stockPanel;
		
		this.init();
		this.pack();
		this.setVisible(true);
	}

	/*
	 * Method to initialise the frame
	 */
	private void init() {
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close this Frame only on close
		
		// Create a container panel and give it a vertical BoxLayout
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

		// Create a title label and align it to the centre (sorta..)
		JLabel title = new JLabel("ADD SUPPLIER");
		title.setAlignmentX(CENTER_ALIGNMENT);
		container.add(title);

		// Create a name panel, add a label and a textbox and then add them to the panel
		JPanel namePanel = new JPanel();
		JLabel nameLabel = new JLabel("Name: ");
		JTextField nameTextBox = new JTextField(10);
		namePanel.add(nameLabel);
		namePanel.add(nameTextBox);

		// Create a unit panel, add a label and a textbox and then add them to the panel
		JPanel distancePanel = new JPanel();
		JLabel distanceLabel = new JLabel("Distance: ");
		JTextField distanceTextBox = new JTextField(4);
		distancePanel.add(distanceLabel);
		distancePanel.add(distanceTextBox);

		// Add them to the container
		container.add(namePanel);
		container.add(distancePanel);

		// JButton to save the new Supplier
		JButton saveBttn = new JButton("Save");
		saveBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					// Get the name and try to parse the distance
					String name = nameTextBox.getText();
					Integer distance = Integer.parseInt(distanceTextBox.getText());

					// Neither can be invalid
					if (name.equals("") || distance <= 0) {
						throw new InvalidDetailsException();
					}

					addSupplier(name, distance); // Add the new supplier
				} catch (Exception e1) {
					// If something went wrong (invalid input..) display an error dialog
					JOptionPane.showMessageDialog(BusinessAddSupplierFrame.this, "Invalid details!", "Adding Failed",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		container.add(saveBttn);

		this.add(container); // Add everything to the JFrame
	}

	/*
	 * Method to add a new Supplier to the business
	 */
	private void addSupplier(String name, int distance) {
		Supplier supplier = new Supplier(name, distance); // Create the new Supplier
		
		businessApp.suppliers.add(supplier); // Add it to the business' suppliers list
		
		stockPanel.refresh(); // Refresh the panel and close the frame
		this.dispose();
		this.setVisible(false);
	}

}