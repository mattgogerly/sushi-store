import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * Class acting as the main business window. Handles logic and contains sub panels and frames.
 */

public class BusinessApplication extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private CardLayout layout; // Allows us to switch between panels dynamically
	private JPanel cards; // Sub panels to be added to this
	
	private BusinessOrdersPanel orderPanel; // Main display panel
	
	// Create card names here so they can be easily edited
	protected final String ORDER_PANEL = "ORDERPANEL";
	protected final String STOCK_PANEL = "STOCKPANEL";
	protected final String STAFF_PANEL = "STAFFPANEL";
	
	protected Comms comms; // Comms instance to handle all communication
	protected IngredientStock ingredientStock; // IngredientStock to store stock of ingredients
	protected SushiStock sushiStock; // SushiStock to store stock of dishes
	
	protected ArrayList<Order> orders; // ArrayList of Orders
	protected ArrayList<Supplier> suppliers; // ArrayList of Suppliers
	protected ArrayList<Thread> staff; // ArrayList of threads (running KitchenStaff)
	protected ArrayList<Thread> drones; // ArrayList of threads (running Drone)
	
	/*
	 * Constructor to create a new BusinessApplication
	 */
	public BusinessApplication() {
		super("Mosuri Sushi Admin");
		
		this.setPreferredSize(new Dimension(1000, 800));
		
		setupBusiness(); // Initialise all business variables and lists first
		
		this.init(); // Now initialise the Frame
		
		// When the business application is closed
		this.addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent e) {
				// Set every ingredient to collected
				ingredientStock.prepareForClose();
				
				// Set every SushiDish's inPreparation to 0
				sushiStock.prepareForClose();
				
				// Set any orders that are "DELIVERING" to DELIVERED
				for (Order order : orders) {
					if (order.getStatus() == Order.Status.DELIVERING) {
						comms.updateOrderStatus(order, Order.Status.DELIVERED);
					}
				}
				
				// Save everything
				comms.saveIngredientStock(ingredientStock);
				comms.saveSushiStock(sushiStock);
				comms.saveSuppliers(suppliers);
				
				// Close the frame
				((JFrame)(e.getComponent())).dispose();
			}
		});
	}
	
	/*
	 * Method to initialise the business logic
	 */
	private void setupBusiness() {
		comms = new Comms(); // Initialise the comms object
		
		comms.makeOrderDir(); // Make the orders folder
		
		orders = comms.getAllOrders(); // Store all current orders in the orders ArrayList
		
		// Try to get an exisitng IngredientStock, if not create a new one
		ingredientStock = comms.receiveIngredientStock();
		if (ingredientStock == null) {
			ingredientStock = new IngredientStock();
			comms.saveIngredientStock(ingredientStock);
		}
		
		// Try to get an exisitng SushiStock, if not create a new one
		sushiStock = comms.receiveSushiStock();
		if (sushiStock == null) {
			sushiStock = new SushiStock();
			comms.saveSushiStock(sushiStock);
		}
		
		// Try to get a existing Suppliers, if not initialise the ArrayList as empty
		suppliers = comms.receiveSuppliers();
		if (suppliers == null) {
			suppliers = new ArrayList<>();
			comms.saveSuppliers(suppliers);
		}

		// Not able to save staff and drones (due to them being stored as threads here) so just initialise ArrayList
		staff = new ArrayList<>();
		drones = new ArrayList<>();
	}
	
	/*
	 * Method to initialise the JFrame
	 */
	private void init() {
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); // If this is closed everything should close
		layout = new CardLayout(); // Initialise the CardLayout
		cards = new JPanel(layout); // Set the JPanel to use it
		
		this.orderPanel = new BusinessOrdersPanel(this); // Initialise the BusinessOrdersPanel
		
		this.add(cards); // Add the card panel to the Frame
		
		// Add the order panel to the card panel and then change to it
		addCard(orderPanel, ORDER_PANEL);
		changeCard(ORDER_PANEL);
		
		// Pack and show
		this.pack();
		this.setVisible(true);
		
		// Create a thread to receive new orders
		OrderReceiver orderReceiver = new OrderReceiver(orders);
		Thread orderUpdateThread = new Thread(orderReceiver);
		orderUpdateThread.start();
		
		// Create a thread to save the state at regular intervals
		StateSaver stateSaver = new StateSaver();
		Thread saveStateThread = new Thread(stateSaver);
		saveStateThread.start();
	}
	
	/*
	 * Method to change the card being displayed
	 */
	public void changeCard(String name) {
		layout.show(cards, name); // Show the specified card
		
		// Repack everything and display
		this.pack();
		this.revalidate();
		this.repaint();
	}
	
	/*
	 * Method to add a new card given an identifier
	 */
	public void addCard(JPanel panel, String name) {
		cards.add(panel, name); // Add the card to the cards panel
	}
	
	/*
	 * Runnable class to receive new orders
	 */
	public class OrderReceiver implements Runnable {

		private ArrayList<Order> orders; // ArrayList of all current orders
		
		public OrderReceiver(ArrayList<Order> orders) {
			this.orders = orders; // Set equal to the BusinessApplication's orders ArrayList
		}
		
		/*
		 * Method run on Thread.start()
		 */
		public void run() {
			// Loop forever
			while (true) {
				/*
				 * Note: starting from 1 as when orders are deleted new orders are created to replace them
				 */
				Order order = comms.receiveNewOrder(1); // Get the latest unread order starting from 1

				// If there's actually a new order
				if (order != null) {
					orders.add(order); // Add it to the ArrayList
					
					HashMap<SushiDish, Integer> contents = new HashMap<>();
					contents = order.getContent();
					
					// Check none of the contents exceed restocking levels
					for (SushiDish dish : contents.keySet()) {
						int restockingLevel = sushiStock.getRestockingLevel(dish.getName());
						int quantity = contents.get(dish);
						
						// If it is just change the restocking level to stop it from breaking
						if (quantity > restockingLevel) {
							sushiStock.setRestockingLevel(dish.getName(), quantity);
							
						}
					}
					
					orderPanel.addOrderPanel(order); // Display it on the BusinessOrdersPanel
				}
				
				// Sleep for 1 second
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// Intentionally blank (shouldn't be interrupted)
				}
			}
		}
	}
	
	/*
	 * Runnable class to save the state of the business at regular intervals
	 */
	public class StateSaver implements Runnable {
		/*
		 * Method run on Thread.start()
		 */
		public void run() {
			// Run continuously
			while (true) {
				// Save ingredients, dishes and suppliers
				comms.saveIngredientStock(ingredientStock);
				comms.saveSushiStock(sushiStock);
				comms.saveSuppliers(suppliers);
				
				// Sleep for 5 seconds (aka save every 5 seconds)
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
					//
				}
			}
		}
	}

}
