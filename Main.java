import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * Class to handle launching Applications
 */

public class Main {
	
	/*
	 * Main launch method
	 */
	public static void main(String[] args) {
		// Create a JFrame
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Create a contents panel
		JPanel contents = new JPanel();
		
		// Create a JButton to launch a BusinessApplication
		JButton businessAppBttn = new JButton("Open Business App");
		businessAppBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BusinessApplication businessApp = new BusinessApplication();
			}
		});
		
		// Create a JButton to launch a ClientApplication
		JButton clientAppBttn = new JButton("Open Client App");
		clientAppBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClientApplication clientApp = new ClientApplication();
			}
		});
		
		// Add the buttons to the panel
		contents.add(businessAppBttn);
		contents.add(clientAppBttn);
		
		// Add the panel to the frame
		frame.add(contents);
		
		// Pack and display
		frame.pack();
		frame.setVisible(true);
	}
	
}