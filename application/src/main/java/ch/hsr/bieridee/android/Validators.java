package ch.hsr.bieridee.android;

/**
 * A collection of static validator methods that return a boolean value.
 */
public final class Validators {
	
	private Validators() {
		// do not instantiate
	}
	
	/**
	 * Validate a name.
	 * 
	 * @param name Name string
	 * @return boolean
	 */
	public static boolean validateName(String name) {
		return name.matches("\\w{2,}");
	}

	/**
	 * Validate a username.
	 * 
	 * @param username Username string
	 * @return boolean
	 */
	public static boolean validateUsername(String username) {
		return username.matches("\\w{3,}");
	}

	/**
	 * Validate an email address.
	 * 
	 * @param email Email string
	 * @return boolean
	 */
	public static boolean validateEmail(String email) {
		return email.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	}

	/**
	 * Validate a password.
	 *
	 * @param password Password string
	 * @return boolean
	 */
	public static boolean validatePassword(String password) {
		return password.matches("\\w{8,}");
	}
}
