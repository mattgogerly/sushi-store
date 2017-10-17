import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/*
 * JPanel to display current stock and suppliers
 */

public class BusinessStockPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	// Containers for each part
	private JPanel sushiContainer;
	private JPanel ingredientsContainer;
	private JPanel supplierContainer;
	
	// Mappings from dishes, ingredients and suppliers to panels
	private HashMap<SushiDish, JPanel> dishPanelMapping;
	private HashMap<Ingredient, JPanel> ingredientPanelMapping;
	private HashMap<Supplier, JPanel> supplierPanelMapping;
	
	// Mappings from dishes and ingredients to restock stock labels
	private HashMap<SushiDish, JLabel> dishLabelMapping;
	private HashMap<Ingredient, JLabel> ingredientLabelMapping;
	
	// Mappings from dishes and ingredients to restock stock labels
	private HashMap<SushiDish, JLabel> dishRestockLabelMapping;
	private HashMap<Ingredient, JLabel> ingredientRestockLabelMapping;
	
	private BusinessApplication businessApp;
	
	/*
	 * Constructor to create a new BusinessStockPanel
	 */
	public BusinessStockPanel(BusinessApplication businessApp) {
		super();
		
		this.sushiContainer = new JPanel();
		this.ingredientsContainer = new JPanel();
		this.supplierContainer = new JPanel();
		
		dishPanelMapping = new HashMap<>();
		ingredientPanelMapping = new HashMap<>();
		supplierPanelMapping = new HashMap<>();
		
		dishLabelMapping = new HashMap<>();
		ingredientLabelMapping = new HashMap<>();
		
		dishRestockLabelMapping = new HashMap<>();
		ingredientRestockLabelMapping = new HashMap<>();
		
		this.businessApp = businessApp;
		
		this.setLayout(new BorderLayout());
		
		this.init();
	}
	
	/*
	 * Method to initialise the panel
	 */
	private void init() {
		// Create a contents JPanel, give it a vertical BoxLayout and add it to a scroll pane
		JPanel contents = new JPanel();
		contents.setLayout(new BoxLayout(contents, BoxLayout.Y_AXIS));
		JScrollPane contentsPane = new JScrollPane(contents);
		
		// Give sushi container a vertical BoxLayout too
		sushiContainer.setLayout(new BoxLayout(sushiContainer, BoxLayout.Y_AXIS));
		
		// Create a title label, centre it (sorta), add it
		JLabel sushiLabel = new JLabel("SUSHI STOCK");
		sushiLabel.setAlignmentX(CENTER_ALIGNMENT);
		sushiContainer.add(sushiLabel);
		
		// Get iterator for all sushi stock
		Iterator<SushiDish> sushiIt = businessApp.sushiStock.getStockIterator();
		while (sushiIt.hasNext()) {
			// Iterate through and add panel for each dish
			SushiDish dish = sushiIt.next();
			addDishPanel(dish);
		}
		
		// Give ingredients container a vertical BoxLayout
		ingredientsContainer.setLayout(new BoxLayout(ingredientsContainer, BoxLayout.Y_AXIS));
		
		// Create a title label, centre it (sorta), add it
		JLabel ingredientLabel = new JLabel("INGREDIENT STOCK");
		sushiLabel.setAlignmentX(CENTER_ALIGNMENT);
		ingredientsContainer.add(ingredientLabel);
		
		// Get iterator for all ingredients
		Iterator<Ingredient> ingredientIt = businessApp.ingredientStock.getStockIterator();
		while (ingredientIt.hasNext()) {
			// Iterate through and add panel for each ingredient
			Ingredient ingredient = ingredientIt.next();
			addIngredientPanel(ingredient);
		}
		
		// Give supplier container a vertical BoxLayout
		supplierContainer.setLayout(new BoxLayout(supplierContainer, BoxLayout.Y_AXIS));
		
		// Create a title label, centre it (sorta), add it
		JLabel supplierLabel = new JLabel("SUPPLIERS");
		supplierLabel.setAlignmentX(CENTER_ALIGNMENT);
		supplierContainer.add(supplierLabel);
		
		// For each Supplier in the suppliers ArrayList, add a panel
		for (Supplier supplier : businessApp.suppliers) {
			addSupplierPanel(supplier);
		}
		
		// Add the three containers
		contents.add(sushiContainer);
		contents.add(ingredientsContainer);
		contents.add(supplierContainer);
		
		// Add the contents scroll pane
		this.add(contentsPane);
		
		// Create a JButton to go back to the order panel
		JButton backBttn = new JButton("Back");
		backBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Change card back to the order panel
				businessApp.changeCard(businessApp.ORDER_PANEL);
			}
		});
		this.add(backBttn, BorderLayout.SOUTH);
		
		// Create a StockUpdater thread to update current stock levels and start it
		StockUpdater stockUpdater = new StockUpdater();
		Thread thread = new Thread(stockUpdater);
		thread.start();
	}
	
	/*
	 * Method to add a SushiDish panel
	 */
	public void addDishPanel(SushiDish dish) {
		// Get current stock of the dish
		int stock = businessApp.sushiStock.getStock(dish);
		// Get dish's restocking level
		int restockingLevel = businessApp.sushiStock.getRestockingLevel(dish);
		
		// Create a panel to hold everything and give it a GridLayout
		JPanel sushiPanel = new JPanel();
		sushiPanel.setLayout(new GridLayout(1,5));
		
		// Add a JLabel for its name
		sushiPanel.add(new JLabel(dish.getName()));
		
		// Add a label for its stock
		JLabel stockLabel = new JLabel("Current Stock: " + stock);
		// Add a mapping from dish to its stock label
		dishLabelMapping.put(dish, stockLabel);
		sushiPanel.add(stockLabel);
		
		// Add a label for its restocking level
		JLabel restockLabel = new JLabel("Restocking Level: " + restockingLevel);
		// Add a mapping from dish to its restock label
		dishRestockLabelMapping.put(dish, restockLabel);
		sushiPanel.add(restockLabel);
		
		// Create a JButton to edit dish
		JButton editBttn = new JButton("Edit");
		editBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Create new frame
				BusinessEditDishFrame editDish = new BusinessEditDishFrame(BusinessStockPanel.this, businessApp, dish);
			}
		});
		sushiPanel.add(editBttn);
		
		// Create a JButton to remove dish
		JButton removeBttn = new JButton("Delete");
		removeBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Remove it
				removeDish(dish);
			}
		});
		sushiPanel.add(removeBttn);
		
		// Add the new panel to the container
		sushiContainer.add(sushiPanel);
		// Add mapping from dish to panel
		dishPanelMapping.put(dish, sushiPanel);
		
		// Repaint
		this.revalidate();
		this.repaint();
	}
	
	/*
	 * Method to add an Ingredient panel
	 */
	public void addIngredientPanel(Ingredient ingredient) {
		// Get current stock of the ingredient
		int stock = businessApp.ingredientStock.getStock(ingredient);
		// Get ingredient's restocking level
		int restockingLevel = businessApp.ingredientStock.getRestockingLevel(ingredient);
		
		// Create a panel to hold everything and give it a GridLayout
		JPanel ingredientPanel = new JPanel();
		ingredientPanel.setAlignmentX(CENTER_ALIGNMENT);
		ingredientPanel.setLayout(new GridLayout(1,5));
		
		// Add a JLabel for its name
		ingredientPanel.add(new JLabel(ingredient.getName()));
		
		// Add a label for its stock
		JLabel stockLabel = new JLabel("Current Stock: " + stock);
		// Add a mapping from dish to its stock label
		ingredientLabelMapping.put(ingredient, stockLabel);
		ingredientPanel.add(stockLabel);
		
		// Add a label for its restocking level
		JLabel restockLabel = new JLabel("Restocking Level: " + restockingLevel);
		// Add a mapping from dish to its stock label
		ingredientRestockLabelMapping.put(ingredient, restockLabel);
		ingredientPanel.add(restockLabel);
		
		// Create a JButton to edit ingredient
		JButton editBttn = new JButton("Edit");
		editBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Create new frame
				BusinessEditIngredientFrame editIngredient = new BusinessEditIngredientFrame(BusinessStockPanel.this, businessApp, ingredient);
			}
		});
		ingredientPanel.add(editBttn);
		
		// Create a JButton to remove ingredient
		JButton removeBttn = new JButton("Delete");
		removeBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Remove it
				removeIngredient(ingredient);
			}
		});
		ingredientPanel.add(removeBttn);
		
		// Add the new panel to the container
		ingredientsContainer.add(ingredientPanel);
		// Add mapping from dish to panel
		ingredientPanelMapping.put(ingredient, ingredientPanel);
		
		// Repaint
		this.revalidate();
		this.repaint();
	}
	
	/*
	 * Method to add a Supplier panel
	 */
	public void addSupplierPanel(Supplier supplier) {
		// Get name of the supplier
		String name = supplier.getName();
		// Get distance to the supplier
		String distance = String.valueOf(supplier.getDistance());
		
		// Create a panel to hold everything and give it a GridLayout
		JPanel supplierPanel = new JPanel();
		supplierPanel.setLayout(new GridLayout(1,4));
		
		// Add a JLabel for its name
		supplierPanel.add(new JLabel(name));
		
		// Add a JLabel for its distance
		supplierPanel.add(new JLabel("Distance: " + distance));
		
		// Create a JButton to edit supplier
		JButton editBttn = new JButton("Edit");
		editBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Create frame
				BusinessEditSupplierFrame editSupplier = new BusinessEditSupplierFrame(BusinessStockPanel.this, supplier);
			}
		});
		supplierPanel.add(editBttn);
		
		// Create a JButton to remove supplier
		JButton removeBttn = new JButton("Delete");
		removeBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Remove it
				removeSupplier(supplier);
			}
		});
		supplierPanel.add(removeBttn);
		
		// Add the new panel to the container
		supplierContainer.add(supplierPanel);
		// Add mapping from dish to panel
		supplierPanelMapping.put(supplier, supplierPanel);
		
		// Repaint
		this.revalidate();
		this.repaint();
	}
	
	/*
	 * Method to remove a dish
	 */
	private void removeDish(SushiDish dish) {
		// Remove it as an item of stock
		businessApp.sushiStock.removeStockItem(dish);
		
		// Reset number in preparation
		dish.resetInPreparation();
		
		// Get the panel associated with the dish and remove it
		JPanel panel = dishPanelMapping.get(dish);
		sushiContainer.remove(panel);
		
		// Remove its mappings
		dishPanelMapping.remove(dish);
		dishLabelMapping.remove(dish);
		dishRestockLabelMapping.remove(dish);
		
		// Repaint
		this.revalidate();
		this.repaint();
	} 
	
	/*
	 * Method to remove an ingredient
	 */
	private void removeIngredient(Ingredient ingredient) {
		// Boolean to check we can remove it
		boolean removable = true;
		// Get iterator for all dishes
		Iterator<SushiDish> dishIt = businessApp.sushiStock.getStockIterator();
		
		while (dishIt.hasNext()) {
			SushiDish dish = dishIt.next();
			// For every item in each dish's ingredient, if it contains the ingredient set removable to false
			HashMap<Ingredient, Integer> recipe = dish.getRecipe();
			for (Ingredient recipeIng : recipe.keySet()) {
				if (recipeIng.getName().equals(ingredient.getName())) {
					removable = false;
				}
			}
		}
		
		// If we can remove it
		if (removable) {
			// Remove it as an item of stock
			businessApp.ingredientStock.removeStockItem(ingredient);
			
			// Get the panel associated with the ingredient and remove it
			JPanel panel = ingredientPanelMapping.get(ingredient);
			ingredientsContainer.remove(panel);
			
			// Remove its mappings
			ingredientPanelMapping.remove(ingredient);
			ingredientLabelMapping.remove(ingredient);
			ingredientRestockLabelMapping.remove(ingredient);
			
			// Repaint
			this.revalidate();
			this.repaint();
		} else {
			// Otherwise we can't remove it because its in current dishes, so display error message dialog
			JOptionPane.showMessageDialog(BusinessStockPanel.this, "Could not remove ingredient, remove dishes "
					+ "that use it first!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/*
	 * Method to remove a supplier
	 */
	private void removeSupplier(Supplier supplier) {
		// Boolean to check we can remove it
		boolean removable = true;
		// Get iterator for all ingredients
		Iterator<Ingredient> ingredientIt = businessApp.ingredientStock.getStockIterator();
		
		// Check every ingredient for the supplier
		while (ingredientIt.hasNext()) {
			Ingredient ingredient = ingredientIt.next();
			if (ingredient.getSupplier().getName().equals(supplier.getName())) {
				// Set to false if the ingredient has that supplier
				removable = false;
			}
		}
		
		// If we can remove it
		if (removable) {
			// Remove the supplier
			businessApp.suppliers.remove(supplier);
			
			// Get the panel associated with it and remove that
			JPanel panel = supplierPanelMapping.get(supplier);
			supplierContainer.remove(panel);
			
			// Remove its mapping
			supplierPanelMapping.remove(supplier);
			
			// Repaint
			this.revalidate();
			this.repaint();
		} else {
			// Otherwise an ingredient has that supplier and we can't remove it, so display error message dialog
			JOptionPane.showMessageDialog(BusinessStockPanel.this, "Could not remove supplier, remove ingredients "
					+ "that use it first!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/*
	 * Method to refresh the panel (really ungraceful but oh well)
	 */
	public void refresh() {
		// Remove everything in its entirety
		this.removeAll();
		sushiContainer.removeAll();
		ingredientsContainer.removeAll();
		supplierContainer.removeAll();
		
		// Clear all mappings
		dishPanelMapping.clear();
		ingredientPanelMapping.clear();
		supplierPanelMapping.clear();
		dishLabelMapping.clear();
		ingredientLabelMapping.clear();
		
		// Reinitialise the panel
		this.init();
	}
	
	public class StockUpdater implements Runnable {
		/*
		 * Runnable class to update stock of dishes and ingredients
		 */
		
		/*
		 * Method run on Thread.start()
		 */
		public void run() {
			// Run indefinitely
			while (true) {
				// For each SushiDish get its label and update it to display current stock
				for (SushiDish dish : dishLabelMapping.keySet()) {
					JLabel label = dishLabelMapping.get(dish);
					label.setText("Current Stock: " + businessApp.sushiStock.getStock(dish));
				}
				
				// For each Ingredient get its label and update it to display current stock
				for (Ingredient ingredient : ingredientLabelMapping.keySet()) {
					JLabel label = ingredientLabelMapping.get(ingredient);
					label.setText("Current Stock: " + businessApp.ingredientStock.getStock(ingredient));
				}
				
				// For each SushiDish get its label and update it to display restock level
				for (SushiDish dish : dishRestockLabelMapping.keySet()) {
					JLabel label = dishRestockLabelMapping.get(dish);
					label.setText("Restocking Level: " + businessApp.sushiStock.getRestockingLevel(dish));
				}
				
				// For each Ingredient get its label and update it to display restock level
				for (Ingredient ingredient : ingredientRestockLabelMapping.keySet()) {
					JLabel label = ingredientRestockLabelMapping.get(ingredient);
					label.setText("Restocking Level: " + businessApp.ingredientStock.getRestockingLevel(ingredient));
				}
				
				// Sleep for 3 seconds
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// Shouldn't be interrupted but just in case..
				}
			}
		}
	}
	
}
