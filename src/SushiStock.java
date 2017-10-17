import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

/*
 * Class to handle stock of SushiDishes
 */

public class SushiStock implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private HashMap<SushiDish, Integer> stock;
	private HashMap<SushiDish, Integer> restockingLevels;
	
	/*
	 * Constructor to create a new SushiStock
	 */
	public SushiStock() {
		stock = new HashMap<>();
		restockingLevels = new HashMap<>();
	}
	
	/*
	 * Synchronized method to get a SushiDish using its name as a reference
	 */
	private synchronized SushiDish getDishFromName(String name) {
		// For every dish in stock, check its name and return it if it matches
		for (SushiDish dish : stock.keySet()) {
			if (dish.getName().equals(name)) {
				return dish;
			}
		}
		
		// Otherwise return null
		return null;
	}
	
	/*
	 * Synchronized method to add an item of stock
	 */
	public synchronized void addStockItem(SushiDish dish, int restockingLevel) {
		// Add the item with current stock of 0
		stock.put(dish, 0);
		// Add its restocking level
		restockingLevels.put(dish, restockingLevel);
	}
	
	/*
	 * Synchronized method to remove an item of stock
	 */
	public synchronized void removeStockItem(SushiDish dish) {
		// Remove it from both mappings
		stock.remove(dish);
		restockingLevels.remove(dish);
	}
	
	/*
	 * Synchronized method to return the stock of a dish
	 */
	public synchronized Integer getStock(SushiDish dish) {
		return stock.get(dish);
	}
	
	/*
	 * Method to return the stock of a dish using its name as a reference
	 */
	public synchronized Integer getStock(String name) {
		// Find the dish
		SushiDish dish = getDishFromName(name);
		
		return stock.get(dish);
	}
	
	/*
	 * Synchronized mehtod to increase the stock of a dish by 1
	 */
	public synchronized void incrementStock(SushiDish dish) {
		Integer currentStock = stock.get(dish);
		stock.put(dish, currentStock + 1);
	}
	
	/*
	 * Synchronized method to increase the stock of a dish by 1 using name as a reference
	 */
	public synchronized void incrementStock(String name) {
		// Find the dish
		SushiDish dish = getDishFromName(name);
		
		Integer currentStock = stock.get(dish);
		stock.put(dish, currentStock + 1);
	}
	
	/*
	 * Synchronized method to decrease the stock of a dish by amount
	 */
	public synchronized void decreaseStock(SushiDish dish, int amount) {
		Integer currentStock = stock.get(dish);
		stock.put(dish, currentStock - amount);
	}
	
	/*
	 * Synchronized method to decrease the stock of a dish by amount using name as a reference
	 */
	public synchronized void decreaseStock(String name, int amount) {
		// Find the dish
		SushiDish dish = getDishFromName(name);
		
		// Get current stock
		Integer currentStock = stock.get(dish);
		
		// Update the mapping
		stock.put(dish, currentStock - amount);
	}
	
	/*
	 * Synchronized method to return the restocking level of a dish
	 */
	public synchronized Integer getRestockingLevel(SushiDish dish) {
		return restockingLevels.get(dish);
	}
	
	/*
	 * Synchronized method to return the restocking level of a dish using name as a reference
	 */
	public synchronized Integer getRestockingLevel(String name) {
		// Find the dish
		SushiDish dish = getDishFromName(name);
		
		return restockingLevels.get(dish);
	}
	
	/*
	 * Synchronized method to set the restocking level of a dish
	 */
	public synchronized void setRestockingLevel(SushiDish dish, int restockLevel) {
		restockingLevels.put(dish, restockLevel);
	}
	
	/*
	 * Synchronized method to set the restocking level of a dish using name as a reference
	 */
	public synchronized void setRestockingLevel(String name, int restockLevel) {
		// Find the dish
		SushiDish dish = getDishFromName(name);
				
		restockingLevels.put(dish, restockLevel);
	}
	
	/*
	 * Synchronized method to check the restocking level of all dishes and return the first dish
	 * that needs restocking.
	 */
	public synchronized SushiDish checkRestockLevels() {
		// For every dish in restockingLevels
		for (SushiDish dish : restockingLevels.keySet()) {
			// Get current stock
			Integer currentStock = stock.get(dish);
			
			// If current stock is less than restocking level return the dish
			if (currentStock < restockingLevels.get(dish)) {
				return dish;
			}
		}
		
		// If nothing needs restocking return null
		return null;
	}
	
	/*
	 * Synchronized method to reset inPreparation for all dishes ready for closing
	 */
	public synchronized void prepareForClose() {
		for (SushiDish dish : stock.keySet()) {
			dish.resetInPreparation();
		}
	}
	
	/*
	 * Method to return an iterator for the stock HashMap
	 */
	public Iterator<SushiDish> getStockIterator() {
		return stock.keySet().iterator();
	}
	
}
