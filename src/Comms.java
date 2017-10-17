import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

/*
 * Class to handle (file based) communication between ClientApplications and BusinessApplcation
 */

public class Comms {
	
	// Strings to store folder paths in one place
	private static final String ORDER_FOLDER = "Business/Orders/";
	private static final String USER_FOLDER = "Business/Users/";
	private static final String STOCK_FOLDER = "Business/Stock/";
	
	// Not in use
	// private static final String STAFF_FOLDER = "Business/Staff/";
	
	/*
	 * Constructor to create a new Comms
	 */
	public Comms() {
		
	}
	
	/*
	 * Synchronized ethod to check if a path currently exists
	 */
	public synchronized boolean checkPathExists(String path) {
		// Create a file using that path
		File file = new File(path);
		
		return file.exists();
	}
	
	/*
	 * Synchronized method to determine and return the path an order should be saved to
	 */
	public synchronized String determineOrderPath(Order order) {
		// Start at 1
		int i = 1;
		// First path
		String path = ORDER_FOLDER + "Order" + i + ".txt";
		
		// Recursively check if it already exists, increasing i each time
		while (checkPathExists(path)) {
			i++;
			path = ORDER_FOLDER + "Order" + i + ".txt";
		}
		
		order.setID(i); // Set the ID of the order to i

		return path;
	}
	
	/*
	 * Synchronized method to send an order from ClientApplication
	 */
	public synchronized void sendUserOrder(Order order) {
		// Create output streams
		OutputStream output = null;
		ObjectOutputStream objectOutput = null;
		
		// Create the order directory if it doesn't exist already
		makeOrderDir();
		
		// Determine the save path
		String path = determineOrderPath(order);
		
		try {
			// Initialise output streams for that path
			output = new FileOutputStream(path);
			objectOutput = new ObjectOutputStream(output);
			
			// Write the order to the stream
			objectOutput.writeObject(order);
			objectOutput.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Try and close the streams
			try {
				if (objectOutput != null) {
					objectOutput.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				if (output != null) {
					output.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * Method to get the path of the first unhandled order
	 */
	public String getNewOrderPath(int i) {
		// Create a file object of the ORDER_FOLDER and store an array of all files in it
		File orderFolder = new File(ORDER_FOLDER);
		String[] orders = orderFolder.list();
		
		// If there's no orders at all return null
		if (orders.length == 0) {
			return null;
		}
		
		// If the path doesn't exist return null
		String path = ORDER_FOLDER + "Order" + i + ".txt";
		if (!checkPathExists(path)) {
			return null;
		}
		
		// Otherwise return path
		return path;
	}
	
	/*
	 * Synchronized method to receive a new order
	 */
	public synchronized Order receiveNewOrder(int i) {
		// Create input streams
		InputStream input = null;
		ObjectInputStream objectInput = null;
		
		// Get the path from the ID supplied
		String path = getNewOrderPath(i);
		// If that path is null return null
		if (path == null) {
			return null;
		}
		
		try {
			// Initialise the input streams at the path specified
			input = new FileInputStream(path);
			objectInput = new ObjectInputStream(input);
			
			// Read in the order
			Order order = (Order) objectInput.readObject();
			
			// If the status of that order is not submitted then recursively call ourselves with the next ID along
			if (order.getStatus() != Order.Status.SUBMITTED) {
				return receiveNewOrder(i + 1);
			} else {
				// Otherwise update status to received and return the order
				updateOrderStatus(order, Order.Status.RECEIVED);
				return order;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (EOFException e) {
			// Nothing needs to be done here (readObject should return null not throw an exception...)
	    } catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			// Try and close the streams
			try {
				if (objectInput != null) {
					objectInput.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				if (input != null) {
					input.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	/*
	 * Method to return the path of an order
	 */
	public String getPathFromID(Order order) {
		int ID = order.getID();
		return ORDER_FOLDER + "Order" + ID + ".txt";
	}
	
	/*
	 * Method to update the status of an order
	 */
	public void updateOrderStatus(Order order, Order.Status status) {
		// Set the status of the order
		order.setStatus(status);
		// Get the order's path
		String path = getPathFromID(order);
		
		// Create the output streams
		OutputStream output = null;
		ObjectOutputStream objectOutput = null;
		
		try {
			// Initialise the streams at the path specified
			output = new FileOutputStream(path);
			objectOutput = new ObjectOutputStream(output);
			
			// Write the order
			objectOutput.writeObject(order);
			objectOutput.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Try and close the streams
			try {
				if (objectOutput != null) {
					objectOutput.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				if (output != null) {
					output.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * Method to remove (delete) an order
	 */
	public void removeOrder(Order order) {
		// Get the path and supply it to a File
		String path = getPathFromID(order);
		File file = new File(path);
		
		try {
			/*
			 * Using System.gc() because without it file.delete() doesn't work. God knows why - a Java bug apparently.
			 * See: http://stackoverflow.com/questions/991489/i-cant-delete-a-file-in-java
			 */
			System.gc();
			
			// Delete the file
			file.delete();
		} catch (Exception e) {
			System.err.println("Error deleting order: Order" + order.getID());
		}
	}
	
	/*
	 * Method to return an ArrayList of orders submitted by a specified user
	 */
	public ArrayList<Order> getUsersOrders(String username) {
		// Create an ArrayList containing every order
		ArrayList<Order> orders = getAllOrders();
		
		// If there are actually orders
		if (orders != null) {
			// Iterate over the ArrayList
			Iterator<Order> orderIt = orders.iterator();
			while (orderIt.hasNext()) {
				Order current = orderIt.next();
				
				// If the user who submitted that order is not our user, remove it
				if (!current.getUser().getUsername().equals(username)) {
					orderIt.remove();
				}
			}
		}
		
		// Return the trimmed ArrayList
		return orders;
	}
	
	/*
	 * Method to return an ArrayList of all orders
	 */
	public ArrayList<Order> getAllOrders() {
		// Initialise the ArrayList
		ArrayList<Order> orders = new ArrayList<>();
		
		// Create a File of the order folder
		File folder = new File(ORDER_FOLDER);
		
		// Create the input streams
		InputStream input = null;
		ObjectInputStream objectInput = null;
		
		// If the folder doesn't exist or there are no orders in it return the empty list
		if (!folder.exists() || folder.listFiles().length == 0) {
			return orders;
		} else {
			// Otherwise loop over each file in the order folder
			for (File file : folder.listFiles()) {
				try {
					// Initialise new streams for that file
					input = new FileInputStream(file);
					objectInput = new ObjectInputStream(input);
					
					// Read in the order
					Order order = (Order) objectInput.readObject();
					
					// Add the order to the ArrayList
					orders.add(order);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					// Try and close the streams
					try {
						if (objectInput != null) {
							objectInput.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					try {
						if (input != null) {
							input.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		// Return the ArrayList
		return orders;
	}
	
	/*
	 * Method to make the order folder
	 */
	public void makeOrderDir() {
		File dir = new File(ORDER_FOLDER);
		dir.mkdir();
	}
	
	/*
	 * Method to make the stock folder
	 */
	public void makeStockDir() {
		File file = new File(STOCK_FOLDER);
		file.mkdirs();
	}
	
	/*
	 * Method to make the user folder
	 */
	public void makeUserDir() {
		File file = new File(USER_FOLDER);
		file.mkdirs();
	}
	
	/*
	 * Method to save a SushiStock
	 */
	public synchronized void saveSushiStock(SushiStock stock) {
		// Create the output streams
		OutputStream output = null;
		ObjectOutputStream objectOutput = null;
		
		// Make the stock folder if it doesn't exist already
		makeStockDir();
		
		try {
			// Initialise the output streams for the path
			output = new FileOutputStream(STOCK_FOLDER + "SushiStock.txt");
			objectOutput = new ObjectOutputStream(output);
			
			// Write out the stock object
			objectOutput.writeObject(stock);
			objectOutput.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Try and close the streams
			try {
				if (objectOutput != null) {
					objectOutput.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				if (output != null) {
					output.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * Method to receive SushiStock
	 */
	public SushiStock receiveSushiStock() {
		// Create the input streams
		InputStream input = null;
		ObjectInputStream objectInput = null;
		
		// Check if it exists - if not return null
		File file = new File(STOCK_FOLDER + "SushiStock.txt");
		if (!file.exists()) {
			return null;
		}
		
		try {
			// Initialise the input stream
			input = new FileInputStream(file);
			objectInput = new ObjectInputStream(input);
			
			// Read in the SushiStock
			SushiStock stock = (SushiStock) objectInput.readObject();
			
			// Return the read object
			return stock;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			// Try and close the streams
			try {
				if (objectInput != null) {
					objectInput.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				if (input != null) {
					input.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// Otherwise return null
		return null;
	}
	
	/*
	 * Method to save an IngredientStock
	 */
	public void saveIngredientStock(IngredientStock stock) {
		// Create the output streams
		OutputStream output = null;
		ObjectOutputStream objectOutput = null;
		
		// Make the stock folder if it doesn't exist already
		makeStockDir();
		
		try {
			// Initialise output streams
			output = new FileOutputStream(STOCK_FOLDER + "IngredientStock.txt");
			objectOutput = new ObjectOutputStream(output);
			
			// Write the IngredientStock out
			objectOutput.writeObject(stock);
			
			objectOutput.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Try and close the streams
			try {
				if (objectOutput != null) {
					objectOutput.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				if (output != null) {
					output.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * Method to receive an IngredientStock
	 */
	public IngredientStock receiveIngredientStock() {
		// Create the input streams
		InputStream input = null;
		ObjectInputStream objectInput = null;
		
		// If the file doesn't exist already return null
		File file = new File(STOCK_FOLDER + "IngredientStock.txt");
		if (!file.exists()) {
			return null;
		}
		
		try {
			// Initialise the input streams
			input = new FileInputStream(file);
			objectInput = new ObjectInputStream(input);
			
			// Read in the IngredientStock
			IngredientStock stock = (IngredientStock) objectInput.readObject();
			
			// Return it
			return stock;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			// Try and close the streams
			try {
				if (objectInput != null) {
					objectInput.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				if (input != null) {
					input.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// Otherwise return null
		return null;
	}
	
	/*
	 * Method to save an ArrayList of Suppliers
	 */
	public void saveSuppliers(ArrayList<Supplier> suppliers) {
		// Create the output streams
		OutputStream output = null;
		ObjectOutputStream objectOutput = null;
		
		// Make the stock folder if it doesn't exist already
		makeStockDir();
		
		try {
			// Initialise the output streams
			output = new FileOutputStream(STOCK_FOLDER + "Suppliers.txt");
			objectOutput = new ObjectOutputStream(output);
			
			// Write out the ArrayList
			objectOutput.writeObject(suppliers);
			
			objectOutput.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Try and close the streams
			try {
				if (objectOutput != null) {
					objectOutput.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				if (output != null) {
					output.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * Method to receive an ArrayList of Suppliers
	 */
	public ArrayList<Supplier> receiveSuppliers() {
		// Create the input streams
		InputStream input = null;
		ObjectInputStream objectInput = null;
		
		// Create a file object using the path
		File file = new File(STOCK_FOLDER + "Suppliers.txt");
		
		// If it exists..
		if (file.exists()) {
			try {
				// Initialise the input streams
				input = new FileInputStream(file);
				objectInput = new ObjectInputStream(input);
				
				// Read in the ArrayList
				ArrayList<Supplier> suppliers = (ArrayList<Supplier>) objectInput.readObject();
				
				// Return it
				return suppliers;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				// Try and close the streams
				try {
					if (objectInput != null) {
						objectInput.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					if (input != null) {
						input.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		// Otherwise return null
		return null;
	}
	
	/*
	 * Method to save a new User
	 */
	public boolean saveUser(User user) {
		// Get the username of the User
		String username = user.getUsername();
		
		// If that User already exists return false
		if (checkPathExists(USER_FOLDER + username + ".txt")) {
			return false;
		}
		
		// Create the output streams
		OutputStream output = null;
		ObjectOutputStream objectOutput = null;
		
		// Create the user folder if it doesn't already exist
		makeUserDir();
		
		try {
			// Initialise the output streams
			output = new FileOutputStream(USER_FOLDER + username + ".txt");
			objectOutput = new ObjectOutputStream(output);
			
			// Write out the User
			objectOutput.writeObject(user);
			
			objectOutput.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Try and close the streams
			try {
				if (objectOutput != null) {
					objectOutput.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				if (output != null) {
					output.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// Return true if successfully saved
		return true;
	}
	
	/*
	 * Method to receive a User
	 */
	public User receiveUser(String username) {
		// Create the input streams
		InputStream input = null;
		ObjectInputStream objectInput = null;
		
		// Check if the file exists
		File file = new File(USER_FOLDER + username + ".txt");
		if (file.exists()) {
			try {
				// Initialise the input sterams
				input = new FileInputStream(file);
				objectInput = new ObjectInputStream(input);
				
				// Read in the User
				User user = (User) objectInput.readObject();
				
				// Return it
				return user;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				// Try and close the streams
				try {
					if (objectInput != null) {
						objectInput.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					if (input != null) {
						input.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		// Otherwise return null
		return null;
	}
	
	/* Wrote this block to save KitchenStaff and Drones but had to change approach to storing them
	 * (storing the threads rather than the instances.. bad I know) so now this is defunct. Leaving it
	 * here in case I go back and change how I'm storing them.
	  
	  public void saveKitchenStaff(ArrayList<KitchenStaff> staff) {
		OutputStream output = null;
		ObjectOutputStream objectOutput = null;
		
		makeStockDir();
		
		try {
			output = new FileOutputStream(STAFF_FOLDER + "KitchenStaff.txt");
			objectOutput = new ObjectOutputStream(output);
			objectOutput.writeObject(staff);
			objectOutput.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (objectOutput != null) {
					objectOutput.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				if (output != null) {
					output.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public ArrayList<KitchenStaff> receiveKitchenStaff() {
		InputStream input = null;
		ObjectInputStream objectInput = null;
		
		File userFile = new File(STAFF_FOLDER + "KitchenStaff.txt");
		if (userFile.exists()) {
			try {
				input = new FileInputStream(STAFF_FOLDER + "KitchenStaff.txt");
				objectInput = new ObjectInputStream(input);
				ArrayList<KitchenStaff> staff = (ArrayList<KitchenStaff>) objectInput.readObject();
				
				return staff;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					if (objectInput != null) {
						objectInput.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					if (input != null) {
						input.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
	
	public void saveDrones(ArrayList<Drone> drones) {
		OutputStream output = null;
		ObjectOutputStream objectOutput = null;
		
		makeStockDir();
		
		try {
			output = new FileOutputStream(STAFF_FOLDER + "Drones.txt");
			objectOutput = new ObjectOutputStream(output);
			objectOutput.writeObject(drones);
			objectOutput.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (objectOutput != null) {
					objectOutput.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				if (output != null) {
					output.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public ArrayList<Drone> receiveDrone() {
		InputStream input = null;
		ObjectInputStream objectInput = null;
		
		File userFile = new File(STAFF_FOLDER + "Drones.txt");
		if (userFile.exists()) {
			try {
				input = new FileInputStream(STAFF_FOLDER + "Drones.txt");
				objectInput = new ObjectInputStream(input);
				ArrayList<Drone> drones = (ArrayList<Drone>) objectInput.readObject();
				
				return drones;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					if (objectInput != null) {
						objectInput.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					if (input != null) {
						input.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
	
	*/
	
}