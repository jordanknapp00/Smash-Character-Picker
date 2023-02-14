package exception;

/**
 * <code>TierListParseException</code> subclass that represents invalid
 * lines. Lines that do not have a valid identifier (such as "mid a = " or
 * "players = ") and are not blank or comments are considered invalid.
 * 
 * @author Jordan Knapp
 *
 */
public class InvalidLineException extends TierListParseException {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an <code>InvalidLineException</code> with an error message
	 * containing the given line text and number, caused by the given
	 * <code>Throwable</code>.
	 * 
	 * @param lineText		The text of the invalid line.
	 * @param lineNumber	The number of the invalid line.
	 * @param cause			The <code>Throwable</code> that caused the
	 * 						exception.
	 */
	public InvalidLineException(String lineText, int lineNumber, Throwable cause) {
		super(constructErrorMessage(lineText, lineNumber), cause);
	}
	
	/**
	 * Constructs an <code>InvalidLineException</code> with an error message
	 * containing the given line text and number, without an associated
	 * <code>Throwable</code>.
	 * 
	 * @param lineText		The text of the invalid line.
	 * @param lineNumber	The number of the invalid line.
	 */
	public InvalidLineException(String lineText, int lineNumber) {
		super(constructErrorMessage(lineText, lineNumber));
	}
	
	private static String constructErrorMessage(String lineText, int lineNumber) {
		return "Tier List Parse Error: InvalidLineException on line " +
				lineNumber + ": \"" + lineText + "\" does not contain a " +
				"valid line identifier (tier or setting).";
	}

}
