import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/*
 * JPanel to display a User's past orders
 */

public class ClientPastOrdersPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private ClientApplication clientApp;
	private User user;
	
	/*
	 * Constructor to create a new ClientPastOrdersPanel
	 */
	public ClientPastOrdersPanel(ClientApplication clientApp, User user) {
		super();
		
		this.user = user;
		this.clientApp = clientApp;
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		this.init();
	}
	
	/*
	 * Method to initialise the panel
	 */
	private void init() {
		// Create an array of column names
		String[] columns = { "Username", "Date", "Order Total", "Status" };
		
		// Create an ArrayList of the User's orders
		ArrayList<Order> orders = clientApp.comms.getUsersOrders(user.getUsername());
		
		// Get size of the orders ArrayList
		int size = orders.size();
		
		// Create a data array of that length
		String[][] data = new String[size][4];
		
		// Loop over the size of the ArrayList and populate the data array with relevant information
		for (int i = 0; i < size; i++) {
			Order currentOrder = orders.get(i);
			data[i][0] = currentOrder.getUser().getUsername();
			data[i][1] = currentOrder.getDate();
			data[i][2] = String.format("£%.2f", currentOrder.getPrice());
			data[i][3] = currentOrder.getStatus().toString();
		}
		
		// Create a new JTable with the data and columns
		JTable table = new JTable(data, columns);
		
		// Create and set a table model to disable cell editing
		DefaultTableModel tableModel = new DefaultTableModel(data, columns) {
			private static final long serialVersionUID = 1L;

		    public boolean isCellEditable(int row, int column) {
		       return false;
		    }
		};
		table.setModel(tableModel);
		
		// Create a tablePane to display the JTable and add it
		JScrollPane tablePane = new JScrollPane(table);
		this.add(tablePane);
		
		// Create a JButton to go back
		JButton backBttn = new JButton("Back");
		backBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Change back to the order panel
				clientApp.changeCard(clientApp.ORDER_PANEL);
			}
		});
		this.add(backBttn);
	}
	
}
