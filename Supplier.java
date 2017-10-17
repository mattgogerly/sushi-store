import java.io.Serializable;

/*
 * Class to represent an Ingredient's Supplier
 */

public class Supplier implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private int distance;
	
	/*
	 * Constructor to create a new Supplier
	 */
	public Supplier(String name, int distance) {
		this.name = name;
		this.distance = distance;
	}
	
	/*
	 * Method to set the Supplier's name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/*
	 * Method to set the Supplier's distance
	 */
	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	/*
	 * Method to return the Supplier's name
	 */
	public String getName() {
		return name;
	}
	
	/*
	 * Method to return the Supplier's distance
	 */
	public int getDistance() {
		return distance;
	}
	
}
