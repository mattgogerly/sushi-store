import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class ClientLoginPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private ClientApplication clientApp;
	
	public ClientLoginPanel(ClientApplication clientApp) {
		super();
		this.clientApp = clientApp;
		
		this.init();
	}
	
	public void init() {
		JPanel container = new JPanel();
		container.setLayout(new GridLayout(3,2));
		
		JLabel usernameLabel = new JLabel("Username:");
		JTextField usernameBox = new JTextField(10);
		
		JLabel passwordLabel = new JLabel("Password:");
		JPasswordField passwordBox = new JPasswordField(10);
		
		JButton loginBttn = new JButton("Login");
		loginBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = usernameBox.getText();
				String password = new String(passwordBox.getPassword());
				
				User user = attemptLogin(username, password);
				
				if (user != null) {
					ClientOrderPanel orderPanel = new ClientOrderPanel(clientApp, user);
					clientApp.addCard(orderPanel, clientApp.ORDER_PANEL);
					clientApp.changeCard(clientApp.ORDER_PANEL);
				} else {
					JOptionPane.showMessageDialog(ClientLoginPanel.this, "Incorrect username or password!",	"Login Failed", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		JButton registerBttn = new JButton("Register");
		registerBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClientRegisterPanel registerPanel = new ClientRegisterPanel(clientApp);
				clientApp.addCard(registerPanel, clientApp.REGISTER_PANEL);
				clientApp.changeCard(clientApp.REGISTER_PANEL);
			}
		});
		
		container.add(usernameLabel);
		container.add(usernameBox);
		container.add(passwordLabel);
		container.add(passwordBox);
		
		container.add(loginBttn);
		container.add(registerBttn);
		
		this.add(container);
	}
	
	public User attemptLogin(String username, String password) {
		User user = clientApp.comms.receiveUser(username);
		
		if (user == null) {
			return null;
		} else if (user.getPassword().equals(password)) {
			return user;
		}
		
		return null;
	}
	
}
