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
	 * @param name
	 *            Name string
	 * @return boolean
	 */
	public static boolean validateName(String name) {
		return name.matches("\\w{2,}");
	}

	/**
	 * Validate a username.
	 * 
	 * @param username
	 *            Username string
	 * @return boolean
	 */
	public static boolean validateUsername(String username) {
		return username.matches("\\w{3,}");
	}

	/**
	 * Validate an email address.
	 * 
	 * @param email
	 *            Email string
	 * @return boolean
	 */
	public static boolean validateEmail(String email) {
		return email.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	}

	/**
	 * Checks if a string is not emtpy.
	 * 
	 * @param string
	 *            String to be checked
	 * @return True if not emtpy else if otherwise
	 */
	public static boolean validateNonEmpty(String string) {
		return string != null && string.length() > 0;
	}
}
