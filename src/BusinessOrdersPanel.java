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
 * JPanel to display all orders and a navigation menu
 */

public class BusinessOrdersPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private BusinessApplication businessApp;
	
	private BusinessStaffPanel staffPanel; // View staff/drones
	private BusinessStockPanel stockPanel; // View stock
	
	private JPanel orderContainer; // JPanel to hold orders
	private HashMap<Order, JPanel> orderPanelMapping; // Maps orders to their JPanel
	private HashMap<Order, JLabel> orderLabelMapping; // Maps orders to their status JLabel
	
	/*
	 * Constructor to create a new BusinessOrdersPanel
	 */
	public BusinessOrdersPanel(BusinessApplication businessApp) {
		super();
		
		this.businessApp = businessApp;		
		
		this.orderContainer = new JPanel();
		this.orderPanelMapping = new HashMap<>();
		this.orderLabelMapping = new HashMap<>();
		
		this.setLayout(new BorderLayout());
		
		this.init();
	}
	
	/*
	 * Method to initialise the BusinessOrdersPanel
	 */
	private void init() {
		// Create staff panel and add card for it
		staffPanel = new BusinessStaffPanel(businessApp);
		businessApp.addCard(staffPanel, businessApp.STAFF_PANEL);
		
		// Create stock panel and add card for it
		stockPanel = new BusinessStockPanel(businessApp);
		businessApp.addCard(stockPanel, businessApp.STOCK_PANEL);
		
		// Initialise top bar and order history
		initTopBar();
		initOrderHistory();
		
		// Create a status updater thread to update order statuses automatically
		StatusUpdater statusUpdater = new StatusUpdater();
		Thread thread = new Thread(statusUpdater);
		thread.start();
	}
	
	/*
	 * Method to initialise order history
	 */
	private void initOrderHistory() {
		// Set orderContainer's layout to a vertical BoxLayout and add it to a scroll pane
		orderContainer.setLayout(new BoxLayout(orderContainer, BoxLayout.Y_AXIS));
		JScrollPane tablePane = new JScrollPane(orderContainer);
		
		// Get all orders and store them in the main orders ArrayList
		businessApp.orders = businessApp.comms.getAllOrders();
		
		// If the ArrayList isn't null..
		if (businessApp.orders != null) {
			int size = businessApp.orders.size();
			
			// Create a title label, centre it (sorta..) and add it
			JLabel ordersLabel = new JLabel("All Orders");
			ordersLabel.setAlignmentX(CENTER_ALIGNMENT);
			orderContainer.add(ordersLabel);
			
			// Loop over the orders ArrayList and add each order
			for (int i = 0; i < size; i++) {
				Order order = businessApp.orders.get(i);
				addOrderPanel(order);
			}
		}
		
		// Add the scroll pane to the main panel
		this.add(tablePane, BorderLayout.CENTER);
	}
	
	/*
	 * Method to add a JPanel for an order
	 */
	protected void addOrderPanel(Order order) {
		// Create a containing panel and give it a GridLayout
		JPanel orderPanel = new JPanel();
		orderPanel.setLayout(new GridLayout(1, 5));
		
		// Create JLabels to display username, date, price and status
		JLabel usernameLabel = new JLabel(order.getUser().getUsername());
		JLabel dateLabel = new JLabel(order.getDate());
		JLabel priceLabel = new JLabel(String.format("£%.2f", order.getPrice()));
		JLabel statusLabel = new JLabel(order.getStatus().toString());
		
		// Add a mapping from order to the status label
		orderLabelMapping.put(order, statusLabel);
		
		// Centre all the labels
		usernameLabel.setAlignmentX(CENTER_ALIGNMENT);
		dateLabel.setAlignmentX(CENTER_ALIGNMENT);
		priceLabel.setAlignmentX(CENTER_ALIGNMENT);
		statusLabel.setAlignmentX(CENTER_ALIGNMENT);
		
		// Add all the labels
		orderPanel.add(usernameLabel);
		orderPanel.add(dateLabel);
		orderPanel.add(priceLabel);
		orderPanel.add(statusLabel);
		
		// JButton to remove an order
		JButton removeBttn = new JButton("Remove");
		removeBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// If it's RECEIVED it can still be cancelled
				if (order.getStatus() == Order.Status.RECEIVED) {
					// Cancel the order
					businessApp.comms.updateOrderStatus(order, Order.Status.CANCELLED);
				}
				
				// Remove the order file
				businessApp.comms.removeOrder(order);
				// Remove it from the ArrayList
				businessApp.orders.remove(order);
				
				// Remove its panel
				orderContainer.remove(orderPanel);
				
				// Repaint
				orderContainer.revalidate();
				orderContainer.repaint();
			}
		});
		
		// Add a mapping from order to its JPanel
		orderPanelMapping.put(order, orderPanel);
		// Add the new panel to the container panel
		orderContainer.add(orderPanel);
		
		// Repaint
		this.revalidate();
		this.repaint();
	}
	
	/*
	 * Method to remove a JPanel
	 */
	private void removePanel(JPanel panel) {
		// Remove it, then repaint
		orderContainer.remove(panel);
		
		this.revalidate();
		this.repaint();
	}
	
	/*
	 * Method to initialise the top menu bar
	 */
	private void initTopBar() {
		// Create a panel and give it a GridLayout
		JPanel topBarPanel = new JPanel();
		topBarPanel.setLayout(new GridLayout(1, 6));
		
		// Create a JButton to open the stock panel
		JButton stockBttn = new JButton("View Stock");
		stockBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Change card to stock panel
				businessApp.changeCard(businessApp.STOCK_PANEL);
			}
		});
		
		// Create a JButton to add a new SushiDish
		JButton addDishBttn = new JButton("Add Dish");
		addDishBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// If there's no ingredients show an error dialog..
				if (businessApp.ingredientStock.getSize() == 0) {
					JOptionPane.showMessageDialog(BusinessOrdersPanel.this, "You must add an ingredient first!", "Error", JOptionPane.ERROR_MESSAGE);
				} else {
					// Else create the frame
					BusinessAddDishFrame addDishFrame = new BusinessAddDishFrame(stockPanel, businessApp);
				}
			}
		});
		
		// Create a Jbutton to add a new Ingredient
		JButton addIngredientBttn = new JButton("Add Ingredient");
		addIngredientBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// If there's no suppliers show an error dialog..
				if (businessApp.suppliers.size() == 0) {
					JOptionPane.showMessageDialog(BusinessOrdersPanel.this, "You must add a supplier first!", "Error", JOptionPane.ERROR_MESSAGE);
				} else {
					// Else create the frame
					BusinessAddIngredientFrame addIngredientFrame = new BusinessAddIngredientFrame(stockPanel, businessApp);
				}
			}
		});
		
		// Create a JButton to add a new Supplier
		JButton addSupplierBttn = new JButton("Add Supplier");
		addSupplierBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Create the frame
				BusinessAddSupplierFrame addSupplierFrame = new BusinessAddSupplierFrame(stockPanel, businessApp);
			}
		});
		
		// Create a JButton to show the staff panel
		JButton staffBttn = new JButton("Staff/Drones");
		staffBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Change card to the staff panel
				businessApp.changeCard(businessApp.STAFF_PANEL);
			}
		});
		
		// Create a JButton to remove completed orders (cancelled/delivered)
		JButton removeCompletedBttn = new JButton("Remove Completed");
		removeCompletedBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Iterator for the orders ArrayList
				Iterator<Order> orderIt = businessApp.orders.iterator();
				
				// Loop
				while (orderIt.hasNext()) {
					Order order = orderIt.next();
					
					// If the order status is DELIVERED or CANCELLED
					if (order.getStatus() == Order.Status.DELIVERED || order.getStatus() == Order.Status.CANCELLED) {
						orderIt.remove(); // Remove it from the ArrayList
						removePanel(orderPanelMapping.get(order)); // Remove its panel
					}
				}
			}
		});
		
		// Add all the buttons
		topBarPanel.add(stockBttn);
		topBarPanel.add(addDishBttn);
		topBarPanel.add(addIngredientBttn);
		topBarPanel.add(addSupplierBttn);
		topBarPanel.add(staffBttn);
		topBarPanel.add(removeCompletedBttn);
		
		// Add the panel
		this.add(topBarPanel, BorderLayout.NORTH);
	}
	
	public class StatusUpdater implements Runnable {
		/*
		 * Runnable class to update order statuses
		 */
		
		/*
		 * Method run on Thread.start()
		 */
		public void run() {
			// Loop indefinitely..
			while (true) {
				// For each order in the orderLabel mapping
				for (Order order : orderLabelMapping.keySet()) {
					// Get the label and change its text to that order's current status
					JLabel label = orderLabelMapping.get(order);
					label.setText(order.getStatus().toString());
				}
				
				// Sleep for 3 seconds
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// Shouldn't be anything thrown here
				}
			}
		}
	}
	
}