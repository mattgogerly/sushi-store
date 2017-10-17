import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

/*
 * Class to handle stock of SushiDishes
 */

public class IngredientStock implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private HashMap<Ingredient, Integer> stock;
	private HashMap<Ingredient, Integer> restockingLevels;
	private HashMap<Ingredient, Boolean> collecting;
	
	/*
	 * Constructor to create a new IngredientStock
	 */
	public IngredientStock() {
		stock = new HashMap<>();
		restockingLevels = new HashMap<>();
		collecting = new HashMap<>();
	}
	
	/*
	 * Method to return size of stock HashMap
	 */
	public int getSize() {
		return stock.size();
	}
	
	/*
	 * Synchronized method to get an Ingredient using its name as a reference
	 */
	private synchronized Ingredient getIngredientFromName(String name) {
		// For every ingredient in stock, check its name and return it if it matches
		for (Ingredient ingredient : stock.keySet()) {
			if (ingredient.getName().equals(name)) {
				return ingredient;
			}
		}
		
		// Otherwise return null
		return null;
	}
	
	/*
	 * Synchronized method to add an item of stock
	 */
	public synchronized void addStockItem(Ingredient ingredient, int restockingLevel) {
		// Add the item with current stock of 0
		stock.put(ingredient, 0);
		// Add its restocking level
		restockingLevels.put(ingredient, restockingLevel);
		// Initially we aren't collecting it
		collecting.put(ingredient, false);
	}
	
	/*
	 * Synchronized method to remove an item of stock
	 */
	public synchronized void removeStockItem(Ingredient ingredient) {
		// Remove it from all mappings
		stock.remove(ingredient);
		restockingLevels.remove(ingredient);
		collecting.remove(ingredient);
	}
	
	/*
	 * Synchronized method to return the stock of an ingredient
	 */
	public synchronized Integer getStock(Ingredient ingredient) {
		return stock.get(ingredient);
	}
	
	/*
	 * Method to return the stock of an ingredient using its name as a reference
	 */
	public synchronized Integer getStock(String name) {
		// Find the ingredient
		Ingredient ingredient = getIngredientFromName(name);
		
		return stock.get(ingredient);
	}
	
	/*
	 * Synchronized mehtod to increase the stock of an ingredient by amount
	 */
	public synchronized void increaseStock(Ingredient ingredient, Integer amount) {
		Integer currentStock = stock.get(ingredient);
		stock.put(ingredient, currentStock + amount);
	}
	
	/*
	 * Synchronized method to increase the stock of an ingredient by amount using name as a reference
	 */
	public synchronized void increaseStock(String name, Integer amount) {
		// Find the ingredient
		Ingredient ingredient = getIngredientFromName(name);
		
		Integer currentStock = stock.get(ingredient);
		stock.put(ingredient, currentStock + amount);
	}
	
	/*
	 * Synchronized method to decrease the stock of an ingredient by amount
	 */
	public synchronized void decreaseStock(Ingredient ingredient, Integer amount) {
		Integer currentStock = stock.get(ingredient);
		stock.put(ingredient, currentStock - amount);
	}
	
	/*
	 * Synchronized method to decrease the stock of an ingredient by amount using name as a reference
	 */
	public synchronized void decreaseStock(String name, Integer amount) {
		// Find the ingredient
		Ingredient ingredient = getIngredientFromName(name);
		
		// Get current stock
		Integer currentStock = stock.get(ingredient);
		
		// Update the mapping
		stock.put(ingredient, currentStock - amount);
	}
	
	/*
	 * Synchronized method to return the restocking level of an ingredient
	 */
	public synchronized Integer getRestockingLevel(Ingredient ingredient) {
		return restockingLevels.get(ingredient);
	}
	
	/*
	 * Synchronized method to set the restocking level of an ingredient
	 */
	public synchronized void setRestockingLevel(Ingredient ingredient, int restockLevel) {
		restockingLevels.put(ingredient, restockLevel);
	}
	
	/*
	 * Synchronized method to check the restocking level of all ingredients and return the first
	 * ingredient that needs restocking.
	 */
	public synchronized Ingredient checkRestockLevels() {
		// For every ingredient in restockingLevels
		for (Ingredient ingredient : restockingLevels.keySet()) {
			// Get current stock
			Integer currentStock = stock.get(ingredient);
			
			// If current stock is less than restocking level return the ingredient
			if (currentStock < restockingLevels.get(ingredient) && !isCollecting(ingredient)) {
				return ingredient;
			}
		}
		
		// If nothing needs restocking return null
		return null;
	}
	
	/*
	 * Synchronized method to try and set an ingredients collecting flag to true
	 */
	public synchronized boolean setCollecting(Ingredient ingredient) {
		// If we're not already collecting it..
		if (!isCollecting(ingredient)) {
			// Replace mapping with true and return true
			collecting.put(ingredient, true);
			return true;
		}
		
		// Otherwise return false
		return false;
	}
	
	/*
	 * Synchronized method to set an ingredient's collecting flag to false
	 */
	public synchronized void setCollected(Ingredient ingredient) {
		collecting.put(ingredient, false);
	}
	
	/*
	 * Synchronized method to return whether we're currently collecting an ingredient
	 */
	public synchronized boolean isCollecting(Ingredient ingredient) {
		return collecting.get(ingredient);
	}
	
	/*
	 * Synchronized method to set everything to collected
	 */
	public synchronized void prepareForClose() {
		for (Ingredient ingredient : collecting.keySet()) {
			collecting.put(ingredient, false);
		}
	}
	
	/*
	 * Method to return an iterator for the stock HashMap
	 */
	public Iterator<Ingredient> getStockIterator() {
		return stock.keySet().iterator();
	}
	
}