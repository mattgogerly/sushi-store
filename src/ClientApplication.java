import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ClientApplication extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private CardLayout layout;
	private JPanel cards;
	
	protected final String LOGIN_PANEL = "LOGINPANEL";
	protected final String REGISTER_PANEL = "REGISTERPANEL";
	protected final String ORDER_PANEL = "ORDERPANEL";
	protected final String ORDER_HISTORY_PANEL = "ORDERHISTORYPANEL";
	
	protected Comms comms;
	
	public ClientApplication() {
		super("Mosuri Sushi");
		this.init();
	}
	
	private void init() {
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		comms = new Comms();
		
		layout = new CardLayout();
		cards = new JPanel(layout);
		
		ClientLoginPanel loginPanel = new ClientLoginPanel(this);
		
		this.add(cards);
		
		addCard(loginPanel, LOGIN_PANEL);
		changeCard(LOGIN_PANEL);
	
		this.pack();
		this.setVisible(true);
	}
	
	public void changeCard(String name) {
		layout.show(cards, name);
		this.pack();
		this.revalidate();
	}
	
	public void addCard(JPanel panel, String name) {
		cards.add(panel, name);
	}
	
}