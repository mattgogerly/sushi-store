import java.io.Serializable;
import java.util.HashMap;

/*
 * Class to represent a SushiDish
 */

public class SushiDish implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String description;
	private double price;
	private HashMap<Ingredient, Integer> recipe;
	
	private int inPreparation; // Number currently being prepared by KitchenStaff
	
	/*
	 * Constructor to create a new SushiDish
	 */
	public SushiDish(String name, String description, double price, HashMap<Ingredient, Integer> recipe) {
		this.name = name;
		this.description = description;
		this.price = price;
		this.recipe = recipe;
		
		this.inPreparation = 0; // 0 being prepared at creation
	}
	
	/*
	 * Constructor to create a SushiDish without a recipe
	 */
	public SushiDish(String name, String description, double price) {
		this(name, description, price, new HashMap<>());
	}
	
	/*
	 * Method to "prepare" a SushiDish
	 */
	public void prepare(IngredientStock stock) {
		// For every Ingredient in the recipe get the amount and decrement current stock by that amount
		for (Ingredient ingredient : recipe.keySet()) {
			Integer amount = recipe.get(ingredient);
			stock.decreaseStock(ingredient.getName(), amount);
		}
	}
	
	/*
	 * Synchronized method to increase the number of this dish currently in preparation
	 */
	public synchronized void incrementInPreparation() {
		inPreparation += 1;
	}
	
	/*
	 * Synchronized method to deccrease the number of this dish currently in preparation
	 */
	public synchronized void decrementInPreparation() {
		inPreparation -= 1;
	}
	
	/*
	 * Method to set the name of the SushiDish
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/*
	 * Method to set the description of the SushiDish
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/*
	 * Method to set the price of the SushiDish
	 */
	public void setPrice(double price) {
		this.price = price;
	}
	
	/*
	 * Method to set the recipe of the SushiDish
	 */
	public void setRecipe(HashMap<Ingredient, Integer> recipe) {
		this.recipe = recipe;
	}
	
	/*
	 * Method to return the name of the SushiDish
	 */
	public String getName() {
		return name;
	}
	
	/*
	 * Method to return the description of the SushiDish
	 */
	public String getDescription() {
		return description;
	}
	
	/*
	 * Method to return the price of the SushiDish
	 */
	public double getPrice() {
		return price;
	}
	
	/*
	 * Method to return the number of the SushiDish currently in preparation
	 */
	public synchronized int getInPreparation() {
		return inPreparation;
	}
	
	/*
	 * Synchronized method to reset in preparation
	 */
	public synchronized void resetInPreparation() {
		this.inPreparation = 0;
	}
	
	/*
	 * Method to return the recipe of the SushiDish
	 */
	public synchronized HashMap<Ingredient, Integer> getRecipe() {
		return recipe;
	}
	
}