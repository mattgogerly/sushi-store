import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/*
 * JFrame to add a new Ingredient to the business
 */

public class BusinessAddIngredientFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private BusinessApplication businessApp;
	private BusinessStockPanel stockPanel;

	/*
	 * Constructor to create a new BusinessAddIngredientFrame
	 */
	public BusinessAddIngredientFrame(BusinessStockPanel stockPanel, BusinessApplication businessApp) {
		super("Add Ingredient");
		
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
		JLabel title = new JLabel("ADD INGREDIENT");
		title.setAlignmentX(CENTER_ALIGNMENT);
		container.add(title);

		// Create a name panel, add a label and a textbox and then add them to the panel
		JPanel namePanel = new JPanel();
		JLabel nameLabel = new JLabel("Name: ");
		JTextField nameTextBox = new JTextField(10);
		namePanel.add(nameLabel);
		namePanel.add(nameTextBox);

		// Create a unit panel, add a label and a textbox and then add them to the panel
		JPanel unitPanel = new JPanel();
		JLabel unitLabel = new JLabel("Unit: ");
		JTextField unitTextBox = new JTextField(4);
		unitPanel.add(unitLabel);
		unitPanel.add(unitTextBox);
		
		// Create a supplier panel, add a label
		JPanel supplierPanel = new JPanel();
		JLabel supplierLabel = new JLabel("Supplier: ");
		
		// Create a dropdown and add all the suppliers to it
		JComboBox<Supplier> supplierDropdown = new JComboBox<Supplier>();
		for (Supplier supplier : businessApp.suppliers) {
			supplierDropdown.addItem(supplier);
		}
		supplierDropdown.setRenderer(new SupplierRenderer()); // Give it a custom renderer so the name is displayed
		supplierPanel.add(supplierLabel); // Add the label and dropdown
		supplierPanel.add(supplierDropdown);
		
		// Create a restocking level panel, add a label and a textbox and then add them to the panel
		JPanel restockingPanel = new JPanel();
		JLabel restockingLabel = new JLabel("Restocking Level: ");
		JTextField restockingTextBox = new JTextField(3);
		restockingPanel.add(restockingLabel);
		restockingPanel.add(restockingTextBox);

		// Add everything to the container
		container.add(namePanel);
		container.add(unitPanel);
		container.add(supplierPanel);
		container.add(restockingPanel);

		// JButton to save the new Ingredient
		JButton saveBttn = new JButton("Save");
		saveBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					// Get the name and unit
					String name = nameTextBox.getText();
					String unit = unitTextBox.getText();

					// Name and unit can't be blank
					if (name.equals("") || unit.equals("")) {
						throw new InvalidDetailsException();
					}

					// Get the supplier and restocking level
					Supplier supplier = (Supplier) supplierDropdown.getSelectedItem();
					Integer restockingLevel = Integer.parseInt(restockingTextBox.getText());

					addIngredient(name, unit, supplier, restockingLevel); // Add the new ingredient
				} catch (Exception e1) {
					// If something went wrong (invalid input..) display an error dialog
					JOptionPane.showMessageDialog(BusinessAddIngredientFrame.this, "Invalid details!", "Adding Failed",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		container.add(saveBttn);

		this.add(container); // Add everything to the JFrame
	}

	/*
	 * Method to add a new Ingredient to the business
	 */
	private void addIngredient(String name, String unit, Supplier supplier, int restockingLevel) {
		Ingredient ingredient = new Ingredient(name, unit, supplier); // Create the new ingredient
		
		businessApp.ingredientStock.addStockItem(ingredient, restockingLevel); // Add it as a new stock item
		
		stockPanel.refresh(); // Refresh the panel and close the frame
		this.dispose();
		this.setVisible(false);
	}

	public class SupplierRenderer extends DefaultListCellRenderer {
		/*
		 * Renderer to allow us to display the names of the suppliers in the JComboBox
		 */
		private static final long serialVersionUID = 1L;

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			// If it's a supplier...
			if (value instanceof Supplier) {
				// value is the name of it
				value = ((Supplier) value).getName();
			}
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			return this;
		}

	}

}