import java.io.Serializable;

/*
 * Class to represent a User of the ClientApplication
 */

public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;	
	
	String username;
	String password;
	String email;
	String postcode;
	
	/*
	 * Constructor to create a new User
	 */
	public User(String username, String password, String email, String postcode) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.postcode = postcode;
	}
	
	/*
	 * Method to return username
	 */
	public String getUsername() {
		return username;
	}
	
	/*
	 * Method to return password
	 */
	public String getPassword() {
		return password;
	}
	
	/*
	 * Method to return email
	 */
	public String getEmail() {
		return email;
	}
	
	/*
	 * Method to return postcode
	 */
	public String getPostcode() {
		return postcode;
	}
	
}
