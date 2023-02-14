package exception;

/**
 * An abstract superclass of all exceptions that can occur during tier list
 * parsing.
 * 
 * @author Jordan Knapp
 *
 */
public abstract class TierListParseException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Superclass constructor for tier list parse exceptions that takes a
	 * <code>Throwable</code>.
	 * 
	 * @param message	A message describing the error, to be constructed
	 * 					differently in each subclass.
	 * @param cause		The <code>Throwable</code> that caused the exception.
	 */
	public TierListParseException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Superclass constructor for tier list parse exceptions that does not
	 * take a <code>Throwable</code>.
	 * 
	 * @param message	A message describing the error, to be constructed
	 * 					differently in each subclass.
	 */
	public TierListParseException(String message) {
		super(message);
	}
	
}
