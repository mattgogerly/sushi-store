import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/*
 * JPanel to allow users to register for the ClientApplication
 */

public class ClientRegisterPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private ClientApplication clientApp;
	
	/*
	 * Constructor to create a new ClientRegisterPanel
	 */
	public ClientRegisterPanel(ClientApplication clientApp) {
		super();
		this.clientApp = clientApp;
		
		this.init();
	}
	
	/*
	 * Method to initialise the panel
	 */
	public void init() {
		// Create a container and give it a GridLayout
		JPanel container = new JPanel();
		container.setLayout(new GridLayout(6,2));
		
		// Create a username label and textbox
		JLabel usernameLabel = new JLabel("Username:");
		JTextField usernameBox = new JTextField(15);
		
		// Create a password label and textbox
		JLabel passwordLabel = new JLabel("Password:");
		JPasswordField passwordBox = new JPasswordField(15);
		
		// Create a email label and textbox
		JLabel emailLabel = new JLabel("Email:");
		JTextField emailBox = new JTextField(15);
		
		// Create a postcode label and dropdown filled with presupplied postcodes
		JLabel postcodeLabel = new JLabel("Postcode:");
		String[] postcodes = { "SO14", "SO15", "SO16", "SO17", "SO19" };
		JComboBox<String> postcodeDropdown = new JComboBox<String>(postcodes);
		
		// Three empty JPanels to fill GridLayout gaps for button placement
		JPanel spacer = new JPanel();
		JPanel spacer2 = new JPanel();
		JPanel spacer3 = new JPanel();
		
		// Create a JButton to register on click
		JButton registerBttn = new JButton("Register");
		registerBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				try {
					// Get contents of each textbox and dropdown and store them
					String username = usernameBox.getText();
					String password = new String(passwordBox.getPassword());
					String email = emailBox.getText();
					String postcode = (String) postcodeDropdown.getSelectedItem();
					
					// Register using these details
					register(username, password, email, postcode);
					
					// Show a success dialog message
					JOptionPane.showMessageDialog(ClientRegisterPanel.this, "Succesfully registered, you may log in!", "Registered", JOptionPane.OK_OPTION);
					
					// Change back to the login panel
					clientApp.changeCard(clientApp.LOGIN_PANEL);
				} catch (UserAlreadyExistsException e1) {
					// Duplicate user so show error
					JOptionPane.showMessageDialog(ClientRegisterPanel.this, "User already exists!", "Registration Failed", JOptionPane.ERROR_MESSAGE);
				} catch (InvalidDetailsException e2) {
					// Invalid details provided so show error
					JOptionPane.showMessageDialog(ClientRegisterPanel.this, "Invalid registration details!", "Registration Failed", JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});
		
		// Add everything to the container
		container.add(usernameLabel);
		container.add(usernameBox);
		container.add(passwordLabel);
		container.add(passwordBox);
		container.add(emailLabel);
		container.add(emailBox);
		container.add(postcodeLabel);
		container.add(postcodeDropdown);
		container.add(spacer);
		container.add(spacer2);
		container.add(spacer3);
		container.add(registerBttn);
		
		// Add the container to the panel
		this.add(container);
	}
	
	/*
	 * Method to register a new User
	 */
	private boolean register(String username, String password, String email, String postcode) throws UserAlreadyExistsException, InvalidDetailsException {
		User user = clientApp.comms.receiveUser(username); // Get the user with the provided username (if exists)
		
		// If a User with that username already exists..
		if (user != null) {
			// Throw exception
			throw new UserAlreadyExistsException();
		} else if (username.equals("") || password.equals("") || email.equals("") || postcode.equals("")) {
			// If any of the required details are empty, throw exception
			throw new InvalidDetailsException();
		} else {
			// Create new User and save them
			User newUser = new User(username, password, email, postcode);
			clientApp.comms.saveUser(newUser);
			return true;
		}
	}
	
}