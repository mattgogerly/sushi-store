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
 * JPanel to display staff and drones
 */

public class BusinessStaffPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private JPanel staffContainer; // Container to hold staff panels
	private JPanel droneContainer; // Container to hold drone panels
	
	// Mappings from threads to staff and drones
	private HashMap<Thread, KitchenStaff> staffThreadMapping;
	private HashMap<Thread, Drone> droneThreadMapping;
	
	// Mappings from staff and drones to their status labels
	private HashMap<KitchenStaff, JLabel> staffStatusMapping;
	private HashMap<Drone, JLabel> droneStatusMapping;
	
	private BusinessApplication businessApp;
	
	/*
	 * Constructor to create a new BusinessStaffPanel
	 */
	public BusinessStaffPanel(BusinessApplication businessApp) {
		super();
		this.businessApp = businessApp;
		
		this.staffThreadMapping = new HashMap<>();
		this.droneThreadMapping = new HashMap<>();
		
		this.staffStatusMapping = new HashMap<>();
		this.droneStatusMapping = new HashMap<>();
		
		this.setLayout(new BorderLayout());
		
		this.init();
	}
	
	/*
	 * Method to initialise the panel
	 */
	private void init() {
		// Create a content panel, give it a vertical BoxLayout and add it to a scroll pane
		JPanel contents = new JPanel();
		contents.setLayout(new BoxLayout(contents, BoxLayout.Y_AXIS));
		JScrollPane contentsPane = new JScrollPane(contents);
		
		// Initialise staff container  and give it a vertical BoxLayout
		staffContainer = new JPanel();
		staffContainer.setLayout(new BoxLayout(staffContainer, BoxLayout.Y_AXIS));
		
		// Create a title label, centre it (sorta..) and add it
		JLabel staffLabel = new JLabel("KITCHEN STAFF");
		staffLabel.setAlignmentX(CENTER_ALIGNMENT);
		staffContainer.add(staffLabel);
		
		// Create JButton to add a new KitchenStaff
		JButton addStaffBttn = new JButton("Add Staff");
		addStaffBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Create the staff member
				KitchenStaff staff = new KitchenStaff(businessApp.sushiStock, businessApp.ingredientStock);
				
				// Create a thread using it and start it
				Thread thread = new Thread(staff);
				thread.start();
				
				// Add the thread to the ArrayList of staff threads
				businessApp.staff.add(thread);
				// Add the mapping from thread to staff
				staffThreadMapping.put(thread, staff);
				// Add a panel for the new staff member
				addStaffPanel(thread);
			}
		});
		staffContainer.add(addStaffBttn);
		
		// Get iterator for staff threads
		Iterator<Thread> staffIt = businessApp.staff.iterator();
		while (staffIt.hasNext()) {
			// For each add a staff panel
			Thread staff = staffIt.next();
			addStaffPanel(staff);
		}
		
		// Add the staff container to the contents panel
		contents.add(staffContainer);
		
		// Initialise drone container  and give it a vertical BoxLayout
		droneContainer = new JPanel();
		droneContainer.setLayout(new BoxLayout(droneContainer, BoxLayout.Y_AXIS));
		
		// Create a title label, centre it (sorta..) and add it
		JLabel droneLabel = new JLabel("DRONES");
		droneLabel.setAlignmentX(CENTER_ALIGNMENT);
		droneContainer.add(droneLabel);
		
		// Create JButton to add a new Drone
		JButton addDroneBttn = new JButton("Add Drone");
		addDroneBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Double speed;
				try {
					speed = Double.parseDouble(JOptionPane.showInputDialog("Enter a speed:")); // Prompt user to enter a speed
					if (speed <= 0) {
						throw new InvalidDetailsException();
					}
					
					Drone drone = new Drone(businessApp, speed); // Create a new drone using that speed
					
					// Create a thread using the drone and start it
					Thread thread = new Thread(drone);
					thread.start();
					
					// Add the thread to the drones ArrayList
					businessApp.drones.add(thread);
					// Add the mapping from thread to drone
					droneThreadMapping.put(thread, drone);
					// Add a panel for the Drone
					addDronePanel(thread);
				} catch (Exception e1) {
					// Invalid speed so show error message dialog
					JOptionPane.showMessageDialog(BusinessStaffPanel.this, "Invalid speed entered!", "Error", JOptionPane.OK_OPTION);
				}
			}
		});
		droneContainer.add(addDroneBttn);
		
		// Get iterator for drone threads
		Iterator<Thread> droneIt = businessApp.drones.iterator();
		while (droneIt.hasNext()) {
			// For each add a drone panel
			Thread drone = droneIt.next();
			addDronePanel(drone);
		}
		
		// Add the drone container to the contents panel
		contents.add(droneContainer);
		
		// Add the scroll pane to the main panel
		this.add(contentsPane, BorderLayout.CENTER);
		
		// Create a JButton to go back to the orders panel
		JButton backBttn = new JButton("Back");
		backBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Change card back to the order panel
				businessApp.changeCard(businessApp.ORDER_PANEL);
			}
		});
		this.add(backBttn, BorderLayout.SOUTH);
		
		// Create a StatusUpdater, start a thread using it
		StatusUpdater statusUpdater = new StatusUpdater();
		Thread thread = new Thread(statusUpdater);
		thread.start();
	}
	
	/*
	 * Method to add a staff panel
	 */
	private void addStaffPanel(Thread thread) {
		// Get the corresponding staff member and then get their status
		KitchenStaff staff = staffThreadMapping.get(thread);
		KitchenStaff.Status status = staff.getStatus();
		
		// Create a panel and give it a GridLayout
		JPanel staffPanel = new JPanel();
		staffPanel.setLayout(new GridLayout(1,5));
		
		// Create labels for name and status
		JLabel nameLabel = new JLabel("Kitchen Staff");
		JLabel statusLabel = new JLabel(status.toString());
		
		// Add a mapping from staff to the new label
		staffStatusMapping.put(staff, statusLabel);
		
		// Create start, stop and remove buttons
		JButton startBttn = new JButton("Start");
		JButton stopBttn = new JButton("Stop");
		JButton removeBttn = new JButton("Remove");
		
		// If staff is not currently stopped then disable remove and start
		if (status != KitchenStaff.Status.STOPPED) {
			removeBttn.setEnabled(false);
			startBttn.setEnabled(false);
		}
		
		// Add an action listener to the start button
		startBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Create new thread using that staff member and start it
				Thread thread = new Thread(staff);
				thread.start();
				
				// Update the statusLabel to the new status
				statusLabel.setText(staff.getStatus().toString());
				
				// Disable start and remove, enable stop
				startBttn.setEnabled(false);
				removeBttn.setEnabled(false);
				stopBttn.setEnabled(true);
			}
		});
		
		// Add an action listener to the stop button
		stopBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Set staff's status variable to STOPPED
				staff.stop();
				// Interrupt the thread
				thread.interrupt();
				
				// Update the statusLabel to the new status
				statusLabel.setText(staff.getStatus().toString());
				
				// Enable start and remove, disable stop
				startBttn.setEnabled(true);
				removeBttn.setEnabled(true);
				stopBttn.setEnabled(false);
			}
		});
		
		// Add an action listener to the remove button
		removeBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Make sure the thread is stopped
				staff.stop();
				thread.interrupt();
				
				// Remove it from the ArrayList of threads
				businessApp.staff.remove(staff);
				// Remove its mapping
				staffThreadMapping.remove(thread);
				// Remove its panel
				removeStaffPanel(staffPanel);
			}
		});
		
		// Add everything to the new panel
		staffPanel.add(nameLabel);
		staffPanel.add(statusLabel);
		staffPanel.add(startBttn);
		staffPanel.add(stopBttn);
		staffPanel.add(removeBttn);
		
		// Add the new panel to the container
		staffContainer.add(staffPanel);
		
		// Repaint
		this.revalidate();
		this.repaint();
	}
	
	/*
	 * Method to add a drone panel
	 */
	private void addDronePanel(Thread thread) {
		// Get the corresponding drone and then get its status
		Drone drone = droneThreadMapping.get(thread);
		Drone.Status status = drone.getStatus();
		
		// Create a panel and give it a GridLayout
		JPanel dronePanel = new JPanel();
		dronePanel.setLayout(new GridLayout(1,3));
		
		// Create labels for name and status
		JLabel nameLabel = new JLabel("Drone");
		JLabel statusLabel = new JLabel(status.toString());
		
		// Add a mapping from drone to the new label
		droneStatusMapping.put(drone, statusLabel);
		
		// Create start, stop and remove buttons
		JButton startBttn = new JButton("Start");
		JButton stopBttn = new JButton("Stop");
		JButton removeBttn = new JButton("Remove");
		
		// If drone is not currently stopped then disable remove and start
		if (status != Drone.Status.STOPPED) {
			removeBttn.setEnabled(false);
			startBttn.setEnabled(false);
		}
		
		// Add an action listener to the start button
		startBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Create new thread using that drone and start it
				Thread thread = new Thread(drone);
				thread.start();
				
				// Update the statusLabel to the new status
				statusLabel.setText(drone.getStatus().toString());
				
				// Disable start and remove, enable stop
				startBttn.setEnabled(false);
				removeBttn.setEnabled(false);
				stopBttn.setEnabled(true);
			}
		});
		
		// Add an action listener to the stop button
		stopBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Set drone's status variable to STOPPED
				drone.stop();
				// Interrupt the thread
				thread.interrupt();
				
				// Update the statusLabel to the new status
				statusLabel.setText(drone.getStatus().toString());
				
				// Enable start and remove, disable stop
				startBttn.setEnabled(true);
				removeBttn.setEnabled(true);
				stopBttn.setEnabled(false);
			}
		});
		
		// Add an action listener to the remove button
		removeBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Make sure the thread is stopped
				drone.stop();
				thread.interrupt();
				
				// Remove it from the ArrayList of threads
				businessApp.staff.remove(drone);
				// Remove its mapping
				droneThreadMapping.remove(drone);
				// Remove its panel
				removeDronePanel(dronePanel);
			}
		});
		
		// Add everything to the new panel
		dronePanel.add(nameLabel);
		dronePanel.add(statusLabel);
		dronePanel.add(startBttn);
		dronePanel.add(stopBttn);
		dronePanel.add(removeBttn);
		
		// Add the new panel to the container
		droneContainer.add(dronePanel);
		
		// Repaint
		this.revalidate();
		this.repaint();
	}
	
	/*
	 * Method to update a staff's status label
	 */
	private void updateStatus(KitchenStaff staff) {
		try {
			// Get the label associated with the staff
			JLabel label = staffStatusMapping.get(staff);
			// Set text to its current status
			label.setText(staff.getStatus().toString());
			
			// Repaint
			this.revalidate();
			this.repaint();
		} catch (Exception e) {
			// Shouldn't be any exceptions here but just in case mapping goes wrong..
		}
	}
	
	private void updateStatus(Drone drone) {
		try {
			// Get the label associated with the drone
			JLabel label = droneStatusMapping.get(drone);
			// Set text to its current status
			label.setText(drone.getStatus().toString());
			
			// Repaint
			this.revalidate();
			this.repaint();
		} catch (Exception e) {
			// Shouldn't be any exceptions here but just in case mapping goes wrong..
		}
	}
	
	/*
	 * Method to remove a staff panel
	 */
	private void removeStaffPanel(JPanel panel) {
		// Remove it from the container and repaint
		staffContainer.remove(panel);
		this.revalidate();
		this.repaint();
	}
	
	/*
	 * Method to remove a drone panel
	 */
	private void removeDronePanel(JPanel panel) {
		// Remove it from the container and repaint
		droneContainer.remove(panel);
		this.revalidate();
		this.repaint();
	}
	
	private class StatusUpdater implements Runnable {
		/*
		 * Runnable class to update status labels
		 */
		
		/*
		 * Method run on Thread.start()
		 */
		public void run() {
			// Run indefinitely
			while (true) {
				// For each thread in staff ArrayList
				for (Thread staff : businessApp.staff) {
					// Get associated staff and update their status
					KitchenStaff staffMember = staffThreadMapping.get(staff);
					updateStatus(staffMember);
				}
				
				// For each thread in drone ArrayList
				for (Thread drone : businessApp.drones) {
					// Get associated drone and update its status
					Drone droneMember = droneThreadMapping.get(drone);
					updateStatus(droneMember);
				}
				
				// Sleep for 3 seconds
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// Shouldn't be interrupted
				}
			}
		}
	}

}
