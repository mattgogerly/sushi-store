/*
 * Exception thrown when a user tries to register a duplicate account
 */

public class UserAlreadyExistsException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/*
	 * Constructor to create the Exception
	 */
	public UserAlreadyExistsException() {
		super();
	}
	
}
