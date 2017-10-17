import java.util.HashMap;

/*
 * Runnable class to represent a member of the KitchenStaff
 */

public class KitchenStaff implements Runnable {
	
	// Enum of possible statuses
	enum Status {
		WAITING, PREPARING, STOPPED
	}
	
	private volatile Status status; // Status variable
	
	private static final Object lock = new Object(); // Lock for checking dishes
	
	private SushiStock sushiStock;
	private IngredientStock ingredientStock;
	
	/*
	 * Constructor to create a new KitchenStaff
	 */
	public KitchenStaff(SushiStock sushiStock, IngredientStock ingredientStock) {
		this.status = Status.WAITING; // On creation status should be WAITING
		this.sushiStock = sushiStock;
		this.ingredientStock = ingredientStock;
	}
	
	/*
	 * Method run on Thread.start()
	 */
	public void run() {
		status = Status.WAITING; // Set status to WAITING in case it was STOPPED before
		
		// While status is not STOPPED
		while (status != Status.STOPPED) {
			// Get the first dish that needs restocking (if any)
			SushiDish dish = sushiStock.checkRestockLevels();
			
			// If there's a dish to restock
			if (dish != null) {
				// If we have the ingredients to prepare and there's a defecit
				if (checkDish(dish)) {
					status = Status.PREPARING; // Set status to PREPARING
					try {							
						// Sleep for a random time between 20 and 60 seconds
						Thread.sleep((long) ((Math.floor(Math.random() * 60) + 20) * 1000));
						
						// Then increase stock of that dish by 1
						sushiStock.incrementStock(dish.getName());
						
						// Reduce number in preparation
						dish.decrementInPreparation();
						
						// Set status back to WAITING
						status = Status.WAITING;
					} catch (InterruptedException e) {
						// If we were interrupted print an error
						System.err.println("Preparation of dish '" + dish.getName() + "' was interrupted!");
						status = Status.STOPPED;
					}
				}
			}
		}
	}
	
	public boolean checkDish(SushiDish dish) {
		synchronized(lock) {
			int currentStock = sushiStock.getStock(dish);
			int restockingLevel = sushiStock.getRestockingLevel(dish);
			int inPreparation = dish.getInPreparation();
			
			// Defecit is restocking level - current stock - number in preparation
			int defecit = restockingLevel - currentStock - inPreparation;
			
			// If there's a defecit
			if (defecit > 0) {
				// If we have the ingredients to prepare
				if (checkIngredients(dish)) {
					// Increase number in preparation and return true
					dish.incrementInPreparation();
					return true;
				}
			}
			
			// Otherwise return false
			return false;
		}
	}
	
	/*
	 * Synchronized method to check ingredients for a dish
	 */
	public boolean checkIngredients(SushiDish dish) {
		// Get the recipe for the dish
		HashMap<Ingredient, Integer> recipe = dish.getRecipe();
		
		// For each ingredient in the recipe
		for (Ingredient ingredient : recipe.keySet()) {
			// Get the required stock
			Integer requiredStock = recipe.get(ingredient);
			
			// Get the current stock (using name because Object references break when using recipes and persistence - serialization problems?)
			Integer currentStock = ingredientStock.getStock(ingredient.getName());
			
			// If current stuck is not null
			if (currentStock != null) {
				// If current stock is less than the stock we need return false
				if (currentStock < requiredStock) {	
					return false;
				}
			}
		}
		
		// Remove the ingredients for the dish
		dish.prepare(ingredientStock);
		
		// Otherwise all is good so return true
		return true;
	}
	
	/*
	 * Method to return status
	 */
	public Status getStatus() {
		return status;
	}
	
	/*
	 * Method to set status to STOPPED
	 */
	public void stop() {
		status = Status.STOPPED;
	}
	
}
