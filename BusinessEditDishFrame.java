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
 * Class to allow a user to edit an existing SushiDish
 */

public class BusinessEditDishFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JPanel ingredientsPanel; // Panel to hold ingredient rows
	
	private BusinessApplication businessApp;
	private BusinessStockPanel stockPanel;
	private SushiDish dish; // The dish to be edited
	
	private HashMap<JComboBox<Ingredient>, JTextField> ingredients;

	/*
	 * Constructor to create a new BusinessEditDishFrame
	 */
	public BusinessEditDishFrame(BusinessStockPanel stockPanel, BusinessApplication businessApp, SushiDish dish) {
		super("Modify Dish");
		
		this.ingredientsPanel = new JPanel();
		this.ingredients = new HashMap<JComboBox<Ingredient>, JTextField>();
		
		this.businessApp = businessApp;
		this.stockPanel = stockPanel;
		this.dish = dish;
		
		this.init();
		this.pack();
		this.setVisible(true);
	}
	
	/*
	 * Method to initialise the Frame
	 */
	private void init() {
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Only close this Frame on close
		
		// Create a container panel and give it a vertical BoxLayout
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		
		// Create a title JLabel and centre it (sorta..)
		JLabel title = new JLabel("EDIT DISH");
		title.setAlignmentX(CENTER_ALIGNMENT);
		container.add(title);
		
		// Create a name panel, add a label and a textbox and then add them to the panel
		JPanel namePanel = new JPanel();
		JLabel nameLabel = new JLabel("Name: ");
		JTextField nameTextBox = new JTextField(10);
		nameTextBox.setText(dish.getName()); // Set text to current name
		namePanel.add(nameLabel);
		namePanel.add(nameTextBox);
		
		// Create a description panel, add a label and a textarea and then add them to the panel
		JPanel descPanel = new JPanel();
		JLabel descLabel = new JLabel("Description: ");
		JTextArea descBox = new JTextArea(5,20);
		descBox.setLineWrap(true);
		descBox.setText(dish.getDescription()); // Set text to current description
		descPanel.add(descLabel);
		descPanel.add(descBox);
		
		// Create a price panel, add a label and a textbox and then add them to the panel
		JPanel pricePanel = new JPanel();
		JLabel priceLabel = new JLabel("Price: ");
		JTextField priceTextBox = new JTextField(4);
		priceTextBox.setText(String.valueOf(dish.getPrice())); // Set text to current price
		pricePanel.add(priceLabel);
		pricePanel.add(priceTextBox);
		
		// Create a restocking level panel, add a label and a textbox and then add them to the panel
		JPanel restockingPanel = new JPanel();
		JLabel restockingLabel = new JLabel("Restocking Level: ");
		JTextField restockingTextBox = new JTextField(3);
		restockingTextBox.setText(String.valueOf(businessApp.sushiStock.getRestockingLevel(dish))); // Set text to current restocking level
		restockingPanel.add(restockingLabel);
		restockingPanel.add(restockingTextBox);
		
		// Add everything to the container
		container.add(namePanel);
		container.add(descPanel);
		container.add(pricePanel);
		container.add(restockingPanel);
		
		// Give the ingredients panel a vertical BoxLayout and add it
		ingredientsPanel.setLayout(new BoxLayout(ingredientsPanel, BoxLayout.Y_AXIS));
		container.add(ingredientsPanel);
		
		// Get the current recipe and add an ingredient row for each item
		HashMap<Ingredient, Integer> recipe = dish.getRecipe();
		for (Ingredient ingredient : recipe.keySet()) {
			addIngredientRow(ingredient, recipe.get(ingredient));
		}
		
		// Create a panel to store buttons
		JPanel bttnPanel = new JPanel();
		
		// JButton to add another ingredient row
		JButton addIngredientBttn = new JButton("Add Ingredient");
		addIngredientBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addIngredientRow();
			}
		});
		bttnPanel.add(addIngredientBttn);
		
		// JButton to save the changes
		JButton saveBttn = new JButton("Save");
		saveBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					// Get the name and description
					String name = nameTextBox.getText();
					String desc = descBox.getText();
					
					// Name can't be empty
					if (name.equals("")) {
						throw new InvalidDetailsException();
					}
					
					// Try parsing price and restocking level
					Double price = Double.parseDouble(priceTextBox.getText());
					Integer restockingLevel = Integer.parseInt(restockingTextBox.getText());
					
					// Price can't be negative and restocking level can't be 0 or negative
					if (price < 0.0 || restockingLevel <= 0) {
						throw new InvalidDetailsException();
					}
					
					// Create HashMap to store recipe
					HashMap<Ingredient, Integer> recipe = new HashMap<>();
					
					// For each dropdown get the ingredient and corresponding quantity and add it to the HashMap
					for (JComboBox<Ingredient> ingredientDropdown : ingredients.keySet()) {
						Ingredient ingredient = (Ingredient) ingredientDropdown.getSelectedItem();
						
						JTextField quantityTextBox = ingredients.get(ingredientDropdown);
						Integer quantity = Integer.parseInt(quantityTextBox.getText());
						
						// Quantity can't be negative or 0
						if (quantity <= 0) {
							throw new InvalidDetailsException();
						}
						
						recipe.put(ingredient, quantity);
					}
					
					// Update the dish with the new info
					updateDish(name, desc, price, restockingLevel, recipe);
				} catch (Exception e1) {
					// If invalid details are provided show an error message dialog
					JOptionPane.showMessageDialog(BusinessEditDishFrame.this, "Invalid details!", "Editing Failed", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		bttnPanel.add(saveBttn);
		
		// Add the button panel
		container.add(bttnPanel);
		
		// Add the container to the frame
		this.add(container);
	}
	
	/*
	 * Method to add an empty ingredient row
	 */
	private void addIngredientRow() {
		addIngredientRow(null, 0); // Add an existing row will null and 0
	}
	
	/*
	 * Method to add an ingredient row for an existing recipe item
	 */
	private void addIngredientRow(Ingredient ingredient, int quantity) {
		JPanel row = new JPanel();
		
		// Create label for the boxes and add it
		JLabel ingredientLabel = new JLabel("Ingredient/Quantity: ");
		row.add(ingredientLabel);
		
		// Create an array of all possible ingredients
		Ingredient[] ingredientList = new Ingredient[businessApp.ingredientStock.getSize()];
		Iterator<Ingredient> ingredientIt = businessApp.ingredientStock.getStockIterator();
		
		// Iterate through ingredientStock and add each item to the array
		int i = 0;
		while (ingredientIt.hasNext()) {
			ingredientList[i] = ingredientIt.next();
			i++;
		}
		
		// Create a dropdown using the array
		JComboBox<Ingredient> ingredientDropdown = new JComboBox<Ingredient>(ingredientList);
		ingredientDropdown.setSelectedItem(ingredient);
		ingredientDropdown.setRenderer(new IngredientRenderer());
		
		// Add the dropdown
		row.add(ingredientDropdown);
		
		// Create a textbox for the quantity
		JTextField quantityTextBox = new JTextField(3);
		
		// If no ingredient was provided set default to 0, else set to current quantity
		if (ingredient == null) {
			quantityTextBox.setText("0");
		} else {
			ingredientDropdown.setSelectedItem(ingredient);
			quantityTextBox.setText(String.valueOf(quantity));
		}
		row.add(quantityTextBox);
		
		// Add JButton to remove the row
		JButton removeBttn = new JButton("X");
		removeBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ingredientsPanel.remove(row);
				ingredients.remove(ingredientDropdown);
				
				BusinessEditDishFrame.this.pack();
				BusinessEditDishFrame.this.revalidate();
				BusinessEditDishFrame.this.repaint();
			}
		});
		row.add(removeBttn);
		
		// Add the row and add the mapping from the new dropdown to the textbox
		ingredientsPanel.add(row);
		ingredients.put(ingredientDropdown, quantityTextBox);
		
		// Repack and repaint
		this.pack();
		this.revalidate();
		this.repaint();
	}
	
	/*
	 * Method to update a dish
	 */
	private void updateDish(String name, String desc, double price, int restockingLevel, HashMap<Ingredient, Integer> recipe) {	
		// Set name, description, price, and recipe to new values
		dish.setName(name);
		dish.setDescription(desc);
		dish.setPrice(price);
		dish.setRecipe(recipe);
		
		// Set the new restocking level
		businessApp.sushiStock.setRestockingLevel(dish, restockingLevel);
		
		// Refresh the stock panel to display changes then close frame
		stockPanel.refresh();
		this.dispose();
		this.setVisible(false);
	}
	
	public class IngredientRenderer extends DefaultListCellRenderer {
		/*
		 * Renderer to display ingredients in JComboBoxes
		 */
		private static final long serialVersionUID = 1L;

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			// If it's an ingredient..
	        if (value instanceof Ingredient) {
	        	// value is it's name
	            value = ((Ingredient) value).getName();
	        }
	        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	        return this;
	    }
		
	}
	
}