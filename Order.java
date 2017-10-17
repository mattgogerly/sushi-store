import java.io.Serializable;
import java.util.HashMap;

/*
 * Class to represent an Order
 */

public class Order implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// Enum containing possible Order statuses
	enum Status {
		SUBMITTED, RECEIVED, PREPARED, DELIVERING, DELIVERED, CANCELLED
	}
	
	private int ID;
	private Status orderStatus;
	private User user;
	private String date;
	private HashMap<SushiDish, Integer> content;
	private double price;
	
	/*
	 * Constructor to create a new Order
	 */
	public Order(User user, String date, HashMap<SushiDish, Integer> content, double price) {
		this.orderStatus = Status.SUBMITTED; // By default status should be SUBMITTED
		this.user = user;
		this.date = date;
		this.content = content;
		this.price = price;
	}
	
	/*
	 * Method to set the Order ID (aka the number in the file path, e.g. Order1.txt)
	 */
	public void setID(int ID) {
		this.ID = ID;
	}
	
	/*
	 * Method to set the Order status
	 */
	public void setStatus(Status status) {
		this.orderStatus = status;
	}
	
	/*
	 * Method to return the Order ID
	 */
	public int getID() {
		return ID;
	}
	
	/*
	 * Method to return the Order status
	 */
	public Status getStatus() {
		return orderStatus;
	}
	
	/*
	 * Method to return the associated User
	 */
	public User getUser() {
		return user;
	}
	
	/*
	 * Method to return the creation date
	 */
	public String getDate() {
		return date;
	}
	
	/*
	 * Method to return a HashMap mapping dishes to their quantities within the Order
	 */
	public HashMap<SushiDish, Integer> getContent() {
		return content;
	}
	
	/*
	 * Method to return the total price of the Order
	 */
	public double getPrice() {
		return price;
	}
	
}
