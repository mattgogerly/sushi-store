import java.util.HashMap;

/*
 * Class to represent a Drone - picks up ingredients and delivers orders
 */

public class Drone implements Runnable {

	// Enum containing possible statuses
	enum Status {
		WAITING, COLLECTING, RETURNING, DELIVERING, STOPPED
	}
	
	private volatile Status status; // Current status of the Drone
	
	private static final Object lock = new Object(); // Lock for checking orders
	
	private BusinessApplication businessApp;
	private double speed;
	
	/*
	 * Constructor to create a new Drone
	 */
	public Drone(BusinessApplication businessApp, double speed) {
		this.speed = speed;
		this.businessApp = businessApp;
		
		this.status = Status.WAITING; // On creation we want to be WAITING
	}
	
	/*
	 * Method run on Thread.start()
	 */
	public void run() {		
		status = Status.WAITING; // On start set status to WAITING in case it was STOPPED before
		
		// While we're not stopped
		while (status != Status.STOPPED) {
			// Get the first ingredient that requires restocking
			Ingredient ingredient = businessApp.ingredientStock.checkRestockLevels();
			
			// If there is one to restock and no other drone is collecting it..
			if (ingredient != null && businessApp.ingredientStock.setCollecting(ingredient)) {
				try {
					// Change status to COLLECTING and sleep for outward journey
					status = Status.COLLECTING;
					Thread.sleep((long) ((ingredient.getSupplier().getDistance() / speed) * 60000));
					
					// Change status to RETURNING and sleep for return journey
					status = Status.RETURNING;
					Thread.sleep((long) ((ingredient.getSupplier().getDistance() / speed) * 60000));
					
					// Get the restocking amount and increase current stock by that amount
					int amount = businessApp.ingredientStock.getRestockingLevel(ingredient);
					businessApp.ingredientStock.increaseStock(ingredient, amount);
					
					// Set status back to WAITING
					status = Status.WAITING;
				} catch (InterruptedException e) {
					// If interrupted set collected to true so other Drones can pickup
					businessApp.ingredientStock.setCollected(ingredient);
					System.err.println("Collection of ingredient '" + ingredient.getName() + "' was interrupted!");
					status = Status.STOPPED;
				} finally {
					// Set collected to true so other Drones can pickup
					businessApp.ingredientStock.setCollected(ingredient);
				}
			} else {
				// Else if there's no ingredients to pick up check if there are any orders
				if (businessApp.orders != null) {
					int size = businessApp.orders.size();
					
					if (size != 0) {
						// Define a loop so we can break from it
						orderloop:
						// Loop over the orders ArrayList
						for (int i = 0; i < size; i++) {
							// Get the current order
							Order order = businessApp.orders.get(i);
							
							if (order != null) {
								if (checkOrderStatus(order)) {
									try {
										// Get the distance to the User's postcode
										double distance = getDistance(order.getUser().getPostcode());
										
										// Get contents of the order
										HashMap<SushiDish, Integer> contents = order.getContent();
										
										// For each SushiDish get the quantity and decrement current stock by that amount
										for (SushiDish orderDish : contents.keySet()) {
											int amount = contents.get(orderDish);
											businessApp.sushiStock.decreaseStock(orderDish.getName(), amount);
										}
										
										// Set Drone status to DELIVERING and sleep for outward journey
										status = Status.DELIVERING;
										Thread.sleep((long) ((distance / speed) * 60000));	
										
										// Set order status to DELIVERED
										businessApp.comms.updateOrderStatus(order, Order.Status.DELIVERED);
										
										// Set Drone status to RETURNING and sleep for return journey
										status = Status.RETURNING;
										Thread.sleep((long) ((distance / speed) * 60000));
										
										// Set status back to WAITING
										status = Status.WAITING;
									} catch (InterruptedException e) {
										// If interrupted just say it's delivered
										businessApp.comms.updateOrderStatus(order, Order.Status.DELIVERED);
										status = Status.STOPPED;
									}
									
									// Break from the loop (one delivery then check for ingredients to restock)
									break orderloop;
								}
							}
						}
					}
				}
			}
		}
	}
	
	/*
	 * Method to get the distance to the User's postcode
	 */
	private double getDistance(String postcode) {
		switch (postcode) {
			case "SO14":
				return 50.0;
			case "SO15":
				return 200.0;
			case "SO16":
				return 150.0;
			case "SO17":
				return 120.0;
			case "SO19":
				return 70.0;
			default:
				return 0.0;
		}
	}
	
	/*
	 * Synchronized method to check that an order is open for delivery
	 */
	private boolean checkOrderStatus(Order order) {
		synchronized(lock) {
			// If that order is set to RECEIVED...
			if (order.getStatus() == Order.Status.RECEIVED) {
				// Check there's enough of the contents so we can deliver..
				if (checkDishesPrepared(order)) {
					// Update status to DELIVERING and return true
					businessApp.comms.updateOrderStatus(order, Order.Status.DELIVERING);
					return true;
				}
			}
			
			return false;
		}
	}
	
	/*
	 * Synchronized method to check that stock is sufficient to cover the contents of an Order
	 */
	private synchronized boolean checkDishesPrepared(Order order) {
		// For each SushiDish in the order
		for (SushiDish orderDish : order.getContent().keySet()) {
			int amount = order.getContent().get(orderDish); // Get the quantity
			
			// If current stock is less than that amount return false
			if (businessApp.sushiStock.getStock(orderDish.getName()) < amount) {
				return false;
			}
		}
		
		// Else all is good so return true
		return true;
	}
	
	/*
	 * Method to get Drone status
	 */
	public Status getStatus() {
		return status;
	}
	
	/*
	 * Method to stop the Drone (breaks the while loop)
	 */
	public void stop() {
		status = Status.STOPPED;
	}
	
}