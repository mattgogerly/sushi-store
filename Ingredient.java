import java.io.Serializable;

/*
 * Class to represent an Ingredient
 */

public class Ingredient implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String unit;
	private Supplier supplier;
	
	/*
	 * Constructor to create a new Ingredient
	 */
	public Ingredient(String name, String unit, Supplier supplier) {
		this.name = name;
		this.unit = unit;
		this.supplier = supplier;
	}
	
	/*
	 * Method to set the name of the Ingredient
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/*
	 * Method to set the unit of the Ingredient
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	/*
	 * Method to set the Supplier of the Ingredient
	 */
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}
	
	/*
	 * Method to get the name of the Ingredient
	 */
	public String getName() {
		return name;
	}
	
	/*
	 * Method to get the unit of the Ingredient
	 */
	public String getUnit() {
		return unit;
	}
	
	/*
	 * Method to get the Supplier of the Ingredient
	 */
	public Supplier getSupplier() {
		return supplier;
	}
	
}
