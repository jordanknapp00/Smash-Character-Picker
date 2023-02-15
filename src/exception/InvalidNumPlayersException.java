package exception;

/**
 * <code>TierListParseException</code> that represents an invalid number of
 * players setting. The number of players must be between 0 and 8.
 * 
 * @author Jordan Knapp
 *
 */
public class InvalidNumPlayersException extends TierListParseException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an <code>InvalidNumPlayersException</code> with an
	 * error message containing the given bad value and line number, caused
	 * by the given <code>Throwable</code>.
	 * 
	 * @param badValue		The invalid number of players.
	 * @param lineNumber	The number of the invalid line.
	 * @param cause			The <code>Throwable</code> that caused the
	 * 						exception.
	 */
	public InvalidNumPlayersException(String badVal, int lineNumber, Throwable cause) {
		super(constructErrorMessage(badVal, lineNumber), cause);
	}
	
	/**
	 * Constructs an <code>InvalidNumPlayersException</code> with an
	 * error message containing the given bad value and line number, without
	 * an associated <code>Throwable</code>.
	 * 
	 * @param badValue		The invalid number of players.
	 * @param lineNumber	The number of the invalid line.
	 */
	public InvalidNumPlayersException(String badVal, int lineNumber) {
		super(constructErrorMessage(badVal, lineNumber));
	}
	
	private static String constructErrorMessage(String badVal, int lineNumber) {
		return "Tier List Parse Error: InvalidNumPlayersException on line " +
				lineNumber + ": Value must be a number between 0 and 8 (" +
				badVal + " given).";
	}
}
