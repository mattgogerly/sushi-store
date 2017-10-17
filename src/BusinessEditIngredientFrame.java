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
 * Class to edit an existing ingredient
 */

public class BusinessEditIngredientFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private BusinessApplication businessApp;
	private BusinessStockPanel stockPanel;
	private Ingredient ingredient; // The ingredient to be edited

	/*
	 * Constructor to create a new BusinessEditIngredientFrame
	 */
	public BusinessEditIngredientFrame(BusinessStockPanel stockPanel, BusinessApplication businessApp, Ingredient ingredient) {
		super("Modify Ingredient");
		
		this.businessApp = businessApp;
		this.stockPanel = stockPanel;
		this.ingredient = ingredient;
		
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
		JLabel title = new JLabel("EDIT INGREDIENT");
		title.setAlignmentX(CENTER_ALIGNMENT);
		container.add(title);
		
		// Create a name panel, add a label and a textbox and then add them to the panel
		JPanel namePanel = new JPanel();
		JLabel nameLabel = new JLabel("Name: ");
		JTextField nameTextBox = new JTextField(10);
		nameTextBox.setText(ingredient.getName()); // Set text to current name
		namePanel.add(nameLabel);
		namePanel.add(nameTextBox);
		
		// Create a unit panel, add a label and a textbox and then add them to the panel
		JPanel unitPanel = new JPanel();
		JLabel unitLabel = new JLabel("Unit: ");
		JTextField unitTextBox = new JTextField(4);
		unitTextBox.setText(ingredient.getUnit()); // Set text to current unit
		unitPanel.add(unitLabel);
		unitPanel.add(unitTextBox);
		
		// Create an array containing all current suppliers
		Supplier[] supplierList = new Supplier[businessApp.suppliers.size()];
		for (int i = 0; i < businessApp.suppliers.size(); i++) {
			supplierList[i] = businessApp.suppliers.get(i);
		}
		
		// Create a supplier panel, add a label and create a dropdown with these suppliers
		JPanel supplierPanel = new JPanel();
		JLabel supplierLabel = new JLabel("Supplier: ");
		JComboBox<Supplier> supplierDropdown = new JComboBox<Supplier>(supplierList);
		supplierDropdown.setSelectedItem(ingredient.getSupplier()); // Set selected to current suppliers
		supplierDropdown.setRenderer(new SupplierRenderer()); // Give it our custom renderer
		supplierPanel.add(supplierLabel);
		supplierPanel.add(supplierDropdown);
		
		// Create a restocking level panel, add a label and a textbox and then add them to the panel
		JPanel restockingPanel = new JPanel();
		JLabel restockingLabel = new JLabel("Restocking Level: ");
		JTextField restockingTextBox = new JTextField(3);
		restockingTextBox.setText(String.valueOf(businessApp.ingredientStock.getRestockingLevel(ingredient)));
		restockingPanel.add(restockingLabel);
		restockingPanel.add(restockingTextBox);
		
		// Add everything to the container
		container.add(namePanel);
		container.add(unitPanel);
		container.add(supplierPanel);
		container.add(restockingPanel);
		
		// Create a JButton to save the changes
		JButton saveBttn = new JButton("Save");
		saveBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					// Get name and unit
					String name = nameTextBox.getText();
					String unit = unitTextBox.getText();
					
					// Try parsing supplier and restocking level
					Supplier supplier = (Supplier) supplierDropdown.getSelectedItem();
					Integer restockingLevel = Integer.parseInt(restockingTextBox.getText());
					
					// Name/desc must be provided and restocking level can't be negative or 0
					if (name.equals("") || unit.equals("") || restockingLevel <= 0) {
						throw new InvalidDetailsException();
					}
					
					// Update the ingredient
					updateIngredient(name, unit, supplier, restockingLevel);
				} catch (Exception e1) {
					// If invalid details were provided show an error message dialog
					JOptionPane.showMessageDialog(BusinessEditIngredientFrame.this, "Invalid details!", "Editing Failed", JOptionPane.ERROR_MESSAGE);
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
	private void updateIngredient(String name, String unit, Supplier supplier, int restockingLevel) {	
		// Set the new name, unit and supplier
		ingredient.setName(name);
		ingredient.setUnit(unit);
		ingredient.setSupplier(supplier);
		
		// Set the new restocking level
		businessApp.ingredientStock.setRestockingLevel(ingredient, restockingLevel);
		
		// Refresh the panel to display changes and close the frame
		stockPanel.refresh();
		this.dispose();
		this.setVisible(false);
	}
	
	public class SupplierRenderer extends DefaultListCellRenderer {
		/*
		 * Renderer to display Suppliers in a JComboBox
		 */
		private static final long serialVersionUID = 1L;

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			// If it's a Supplier..
	        if (value instanceof Supplier) {
	        	// value is the name
	            value = ((Supplier) value).getName();
	        }
	        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	        return this;
	    }
		
	}
	
}