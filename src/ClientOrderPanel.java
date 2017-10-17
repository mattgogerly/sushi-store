import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/*
 * JPanel to allow a User to place orders
 */

public class ClientOrderPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JPanel basketPanel; // Panel to display basket
	private JPanel menuPanel; // Panel to display menu
	
	private double basketPrice; // Total price of all items in basket
	
	private ClientApplication clientApp;
	private User user;
	private SushiStock sushiStock;
	
	private HashMap<SushiDish, Integer> basket; // Basket mapping dishes to quantity
	
	/*
	 * Constructor to create a ClientOrderPanel
	 */
	public ClientOrderPanel(ClientApplication clientApp, User user) {
		super();
		
		this.clientApp = clientApp;
		this.user = user;
		
		this.basket = new HashMap<>();
		
		this.setPreferredSize(new Dimension(800, 800));
		this.setLayout(new BorderLayout());
		
		this.init();
	}
	
	/*
	 * Method to initialise the panel
	 */
	public void init() {
		// Initialise top bar, menu and basket
		initTopBar();
		initMenu();
		initBasket();
		
		// Create a MenuUpdater to update the menu regularly
		MenuUpdater menuUpdater = new MenuUpdater();
		Thread thread = new Thread(menuUpdater);
		thread.start();
	}
	
	/*
	 * Method to initialise the basket
	 */
	public void initBasket() {
		// Initialise the basket panel, give it a vertical BoxLayout and add it to a scroll pane
		basketPanel = new JPanel();
		basketPanel.setLayout(new BoxLayout(basketPanel, BoxLayout.Y_AXIS));
		JScrollPane basketPane = new JScrollPane(basketPanel);
		
		// Create a title label, centre it (sorta..) and add it
		JLabel basketLabel = new JLabel("BASKET");
		basketLabel.setAlignmentX(CENTER_ALIGNMENT);
		basketPanel.add(basketLabel);
		
		// Add the basket scroll pane to the main panel
		this.add(basketPane, BorderLayout.CENTER);
	}
	
	/*
	 * Method to update the basket
	 */
	private void updateBasket() {
		// Remove everything from the basket
		basketPanel.removeAll();
		
		// Create a title label, centre it (sorta..) and add it
		JLabel basketLabel = new JLabel("BASKET");
		basketLabel.setAlignmentX(CENTER_ALIGNMENT);
		basketPanel.add(basketLabel);
		
		// For each SushiDish in the basket HashMap
		for (SushiDish dish : basket.keySet()) {
			// Get current quantity
			int currentQuantity = basket.get(dish);
			
			// Create a containing panel
			JPanel dishPanel = new JPanel();
			
			// Add label to display name and quantity
			JLabel dishLabel = new JLabel(currentQuantity + "x " + dish.getName());
			
			// Add a JButton to remove a dish
			JButton removeBttn = new JButton("Remove (- £" + String.format("%.2f", dish.getPrice()) + ")");
			removeBttn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// If there's one of that dish in the basket
					if (currentQuantity == 1) {
						// Remove it completely
						basket.remove(dish);
					} else {
						// Otherwise remove one of it
						basket.put(dish, currentQuantity - 1);
					}
					
					// Update new basket price
					basketPrice -= dish.getPrice();
					
					// Update the basket
					updateBasket();
				}
			});
			
			// Add the label and button
			dishPanel.add(dishLabel);
			dishPanel.add(removeBttn);
			
			// Add the dish panel to the basket panel
			basketPanel.add(dishPanel);
		}
		
		// If the basket isn't empty
		if (!basket.isEmpty()) {
			// Add total price label, centre it (sorta..) and add it
			JLabel priceLabel = new JLabel("Total: £" + String.format("%.2f", basketPrice));
			priceLabel.setAlignmentX(CENTER_ALIGNMENT);
			basketPanel.add(priceLabel);
			
			// Create a panel to hold buttons and give it a horizontal BoxLayout
			JPanel orderBttnPanel = new JPanel();
			orderBttnPanel.setLayout(new BoxLayout(orderBttnPanel, BoxLayout.X_AXIS));
			
			// Create a cancel JButton
			JButton cancelBttn = new JButton("Cancel Order");
			cancelBttn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// Clear the basket and then update it
					basket.clear();
					basketPrice = 0.0;
					updateBasket();
				}
			});
			
			// Create a complete JButton
			JButton completeBttn = new JButton("Complete Order");
			completeBttn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// Add an order
					addOrder();
					
					// Clear the basket and reset the basket price and update the basket
					basket.clear();
					basketPrice = 0.0;
					updateBasket();
				}
			});
			
			// Add the buttons to the button panel
			orderBttnPanel.add(cancelBttn);
			orderBttnPanel.add(completeBttn);
			
			// Add the button panel to the basket panel
			basketPanel.add(orderBttnPanel);
		}
		
		// Repaint
		this.revalidate();
		this.repaint();
	}
	
	/*
	 * Method to add a new order
	 */
	private void addOrder() {
		// Get date then add order
		String date = new SimpleDateFormat("HH:mm:ss dd/M/yyyy").format(new Date());
		Order order = new Order(user, date, basket, basketPrice);
		
		// Send the order to the BusinessApplication
		clientApp.comms.sendUserOrder(order);
	}
	
	/*
	 * Method to initialise the top menu bar
	 */
	private void initTopBar() {
		// Create a panel to hold everything
		JPanel topBarPanel = new JPanel();
		// Add a logged in as label
		JLabel loggedInLabel = new JLabel("Logged in as: " + user.getUsername());
		
		// Create a logout JButton
		JButton logoutBttn = new JButton("Logout");
		logoutBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Set user to null and change back to LOGIN_PANEL
				user = null;
				clientApp.changeCard(clientApp.LOGIN_PANEL);
			}
		});
		
		// Create a past orders JButton
		JButton pastOrdersBttn = new JButton("View Past Orders");
		pastOrdersBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Create a new past orders panel, add it and change to it
				ClientPastOrdersPanel panel = new ClientPastOrdersPanel(clientApp, user);
				clientApp.addCard(panel, clientApp.ORDER_HISTORY_PANEL);
				clientApp.changeCard(clientApp.ORDER_HISTORY_PANEL);
			}
		});
		
		// Add everything to the container
		topBarPanel.add(loggedInLabel);
		topBarPanel.add(pastOrdersBttn);
		topBarPanel.add(logoutBttn);
		
		// Add the container to this
		this.add(topBarPanel, BorderLayout.NORTH);
	}
	
	/*
	 * Method to initialise the menu
	 */
	private void initMenu() {
		// Initialise menu panel, give it a vertical BoxLayout and add it to a scroll pane
		menuPanel = new JPanel();
		menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
		JScrollPane menuPane = new JScrollPane(menuPanel);
		
		// Update the menu
		updateMenu();
		
		// Add the menu to this
		this.add(menuPane, BorderLayout.WEST);
	}
	
	/*
	 * Method to update the menu
	 */
	private void updateMenu() {
		// Remove everything from the menu
		menuPanel.removeAll();
		
		// Create a title label, centre it (sorta..) and add it
		JLabel menuLabel = new JLabel("MENU");
		menuLabel.setAlignmentX(CENTER_ALIGNMENT);
		menuPanel.add(menuLabel);
		
		// Get latest SushiStock from BusinessApplication
		this.sushiStock = clientApp.comms.receiveSushiStock();
		
		// Get iterator to iterate through SushiStock
		Iterator<SushiDish> stockIterator = sushiStock.getStockIterator();
		while (stockIterator.hasNext()) {
			// Get dish and current stock of it
			SushiDish dish = stockIterator.next();
			int currentStock = sushiStock.getStock(dish);
			
			// Create a panel to hold everything
			JPanel dishPanel = new JPanel();
			
			// Create a label to display name, current stock and description
			JLabel label = new JLabel("<html>" + dish.getName() + "   (" + currentStock + " in stock)" +
					"<br>" + dish.getDescription() + "</html>");

			// Create a JButton to add the dish to basket
			JButton addBttn = new JButton("Add (+ £" + String.format("%.2f", dish.getPrice()) + ")");
			addBttn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// Boolean to check it's not already in basket (since Object references keep breaking and adding duplicate dishes)
					boolean found = false;
					
					// Loop over everything in the basket
					for (SushiDish basketDish : basket.keySet()) {
						if (basketDish.getName().equals(dish.getName())) {
							// If we found it set found to true and add one of it to the basket
							basket.put(basketDish, basket.get(basketDish) +  1);
							found = true;
						}
					}
					
					// If we didn't find it add it as a new dish
					if (found == false) {
						basket.put(dish, 1);
					}
					
					// Update the basket price
					basketPrice += dish.getPrice();
					
					// Update the basket and repaint
					updateBasket();
					revalidate();
					repaint();
				}
			});
			
			// Add the label and button to the dish panel
			dishPanel.add(label);
			dishPanel.add(addBttn);
			
			// Add the dish panel to the menu panel
			menuPanel.add(dishPanel);
		}
		
		// Repaint
		this.revalidate();
		this.repaint();
	}
	
	private class MenuUpdater implements Runnable {
		/*
		 * Runnable class to update the menu at regular intervals
		 */
		
		/*
		 * Method run on Thread.start()
		 */
		public void run() {
			// Run indefinitely
			while (true) {
				// Update the menu
				updateMenu();
				
				// Sleep for 5 seconds
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					//
				}
			}
		}
	}
	
}