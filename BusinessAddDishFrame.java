import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/*
 * JFrame to add a new SushiDish to the business
 */

public class BusinessAddDishFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JPanel ingredientsPanel; // JPanel to hold ingredients ComboBoxes
	
	private BusinessApplication businessApp;
	private BusinessStockPanel stockPanel;
	private HashMap<JComboBox<Ingredient>, JTextField> ingredients; // Mapping to map selected ingredients to their quantity
	
	/*
	 * Constructor to create a new BusinessAddDishFrame
	 */
	public BusinessAddDishFrame(BusinessStockPanel stockPanel, BusinessApplication businessApp) {
		super("Add Dish");

		this.ingredientsPanel = new JPanel();
		
		this.businessApp = businessApp;
		this.stockPanel = stockPanel;
		this.ingredients = new HashMap<>();
		
		this.init();
		this.pack();
		this.setVisible(true);
	}
	
	/*
	 * Method to initialise the JFrame
	 */
	private void init() {
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close this Frame only on close
		
		// Create a container panel and give it a vertical BoxLayout
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		
		// Create a title label and align it to the centre (sorta..)
		JLabel title = new JLabel("ADD DISH");
		title.setAlignmentX(CENTER_ALIGNMENT);
		container.add(title);
		
		// Create a name panel, add a label and a textbox and then add them to the panel
		JPanel namePanel = new JPanel();
		JLabel nameLabel = new JLabel("Name: ");
		JTextField nameTextBox = new JTextField(10);
		namePanel.add(nameLabel);
		namePanel.add(nameTextBox);
		
		// Create a description panel, add a label and a textarea and then add them to the panel
		JPanel descPanel = new JPanel();
		JLabel descLabel = new JLabel("Description: ");
		JTextArea descBox = new JTextArea(5,20);
		descBox.setLineWrap(true);
		descPanel.add(descLabel);
		descPanel.add(descBox);
		
		// Create a price panel, add a label and a textbox and then add them to the panel
		JPanel pricePanel = new JPanel();
		JLabel priceLabel = new JLabel("Price: ");
		JTextField priceTextBox = new JTextField(4);
		pricePanel.add(priceLabel);
		pricePanel.add(priceTextBox);
		
		// Create a restocking level panel, add a label and a textbox and then add them to the panel
		JPanel restockingPanel = new JPanel();
		JLabel restockingLabel = new JLabel("Restocking Level: ");
		JTextField restockingTextBox = new JTextField(3);
		restockingPanel.add(restockingLabel);
		restockingPanel.add(restockingTextBox);
		
		// Add all the above panels to the container
		container.add(namePanel);
		container.add(descPanel);
		container.add(pricePanel);
		container.add(restockingPanel);
		
		ingredientsPanel.setLayout(new BoxLayout(ingredientsPanel, BoxLayout.Y_AXIS));
		container.add(ingredientsPanel);
		addIngredientRow(); // Add, by default, one row for ingredients
		
		JPanel bttnPanel = new JPanel(); // Panel to hold buttons
		
		// JButton to add another ingredient row
		JButton addIngredientBttn = new JButton("Add Ingredient");
		addIngredientBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addIngredientRow();
			}
		});
		bttnPanel.add(addIngredientBttn);
		
		// JButton to save the new SushiDish
		JButton saveBttn = new JButton("Save");
		saveBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					// Get the name and description
					String name = nameTextBox.getText();
					String desc = descBox.getText();
					
					// Name can't be blank but descriptionc can
					if (name.equals("")) {
						throw new InvalidDetailsException();
					}
					
					// Try and get the price and restocking level through parsing
					Double price = Double.parseDouble(priceTextBox.getText());
					Integer restockingLevel = Integer.parseInt(restockingTextBox.getText());
					
					// HashMap to act as the recipe for the dish
					HashMap<Ingredient, Integer> recipe = new HashMap<>();
					
					// For each ComboBox in the mapping created way back at the start
					for (JComboBox<Ingredient> ingredientDropdown : ingredients.keySet()) {
						Ingredient ingredient = (Ingredient) ingredientDropdown.getSelectedItem(); // Ingredient is the content of the dropdown
						
						JTextField quantityTextBox = ingredients.get(ingredientDropdown); // Quantity box is mapped to value
						Integer quantity = Integer.parseInt(quantityTextBox.getText()); // Try and parse the contents
						
						recipe.put(ingredient, quantity); // Add these to the recipe
					}
					
					addDish(name, desc, price, restockingLevel, recipe); // Add the new dish
				} catch (Exception e1) {
					// If something went wrong (invalid input..) display an error dialog
					JOptionPane.showMessageDialog(BusinessAddDishFrame.this, "Invalid details!", "Adding Failed", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		bttnPanel.add(saveBttn);
		
		container.add(bttnPanel);
		
		this.add(container); // Add everything to the JFrame
	}
	
	/*
	 * Method to add a new ingredient row
	 */
	private void addIngredientRow() {
		JPanel row = new JPanel(); // Panel to hold it
		
		// Label to tell them what to do
		JLabel ingredientLabel = new JLabel("Ingredient/Quantity: ");
		row.add(ingredientLabel);
		
		// Create an array of all possible ingredients
		Ingredient[] ingredientList = new Ingredient[businessApp.ingredientStock.getSize()];
		Iterator<Ingredient> ingredientIt = businessApp.ingredientStock.getStockIterator();
		
		int i = 0;
		// Loop over the array and set each element equal to the next element in the ingredientStock HashMap
		while (ingredientIt.hasNext()) {
			ingredientList[i] = ingredientIt.next();
			i++;
		}
		
		// Create a JComboBox from this array
		JComboBox<Ingredient> ingredientDropdown = new JComboBox<Ingredient>(ingredientList);
		ingredientDropdown.setSelectedIndex(0);
		ingredientDropdown.setRenderer(new IngredientRenderer());
		
		row.add(ingredientDropdown); // Add it to the row
		
		// Create a textfield for quantity
		JTextField quantityTextBox = new JTextField(3);
		quantityTextBox.setText("0");
		row.add(quantityTextBox);
		
		ingredientsPanel.add(row);
		ingredients.put(ingredientDropdown, quantityTextBox); // Add the dropdown/textfield to the mapping
		
		// Redraw the window etc.
		this.pack();
		this.revalidate();
		this.repaint();
	}
	
	/*
	 * Method to add a new SushiDish to the business
	 */
	private void addDish(String name, String desc, double price, int restockingLevel, HashMap<Ingredient, Integer> recipe) {
		SushiDish dish = new SushiDish(name, desc, price, recipe); // Create the new dish
		
		businessApp.sushiStock.addStockItem(dish, restockingLevel); // Add it as a new stock item
		
		stockPanel.refresh(); // Refresh the panel and close the frame
		this.dispose();
		this.setVisible(false);
	}
	
	public class IngredientRenderer extends DefaultListCellRenderer {
		/*
		 * Renderer to allow us to display the names of the ingredients in the JComboBoxes
		 */
		private static final long serialVersionUID = 1L;

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			// If it's an ingredient..
	        if (value instanceof Ingredient) {
	        	// value is the name of it
	            value = ((Ingredient) value).getName();
	        }
	        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	        return this;
	    }
		
	}
	
}