package ch.hsr.bieridee.android.exceptions;


/**
 * Catch-all exception (unchecked) that is thrown if something goes wrong.
 */
public class BierIdeeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param message The exception message
	 */
	public BierIdeeException(String message) {
		super(message);
	}

	/**
	 * @param message The exception message
	 * @param throwable A throwable to be attached to the exception
	 */
	public BierIdeeException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
}