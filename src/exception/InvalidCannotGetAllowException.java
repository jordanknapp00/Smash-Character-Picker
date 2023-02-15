package exception;

/**
 * <code>TierListParseException</code> that represents in invalid value for
 * either "S allowed in cannot get" or "SS allowed in cannot get" setting.
 * A boolean value (or 0 or 1) must be provided.
 * 
 * @author Jordan Knapp
 *
 */
public class InvalidCannotGetAllowException extends TierListParseException {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an <code>InvalidCannotGetAllowException</code> with an
	 * error message containing the given bad value and line number, caused
	 * by the given <code>Throwable</code>.
	 * 
	 * @param badValue		The invalid cannot get allow value.
	 * @param lineNumber	The number of the invalid line.
	 * @param cause			The <code>Throwable</code> that caused the
	 * 						exception.
	 */
	public InvalidCannotGetAllowException(String badVal, int lineNumber, Throwable cause) {
		super(constructErrorMessage(badVal, lineNumber), cause);
	}
	
	/**
	 * Constructs an <code>InvalidCannotGetAllowException</code> with an
	 * error message containing the given bad value and line number, without
	 * an associated <code>Throwable</code>.
	 * 
	 * @param badValue		The invalid cannot get allow value.
	 * @param lineNumber	The number of the invalid line.
	 */
	public InvalidCannotGetAllowException(String badVal, int lineNumber) {
		super(constructErrorMessage(badVal, lineNumber));
	}
	
	private static String constructErrorMessage(String badVal, int lineNumber) {
		return "Tier List Parse Error: InvalidCannotGetAllowException on " +
				"line " + lineNumber + ": Value must be either \"true\", " +
				"\"false\", 0, or 1 (\"" + badVal + "\" given).";
	}

}
