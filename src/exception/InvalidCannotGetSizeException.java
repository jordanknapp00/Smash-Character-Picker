package exception;

import util.Util;

/**
 * <code>TierListParseException</code> that represents an invalid Cannot Get
 * size. The Cannot Get size must be a number between 0 and
 * <code>Util.<b><i>CANNOT_GET_MAX</i></b></code>.
 * 
 * @author Jordan Knapp
 *
 */
public class InvalidCannotGetSizeException extends TierListParseException {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructs an <code>InvalidCannotGetSizeException</code> with an
	 * error message containing the given bad value and line number, caused
	 * by the given <code>Throwable</code>.
	 * 
	 * @param badValue		The invalid Cannot Get size value.
	 * @param lineNumber	The number of the invalid line.
	 * @param cause			The <code>Throwable</code> that caused the
	 * 						exception.
	 */
	public InvalidCannotGetSizeException(String badValue, int lineNumber, Throwable cause) {
		super(constructErrorMessage(badValue, lineNumber), cause);
	}
	
	/**
	 * Constructs an <code>InvalidCannotGetSizeException</code> with an
	 * error message containing the given bad value and line number, without
	 * an associated <code>Throwable</code>.
	 * 
	 * @param badValue		The invalid Cannot Get size value.
	 * @param lineNumber	The number of the invalid line.
	 */
	public InvalidCannotGetSizeException(String badValue, int lineNumber) {
		super(constructErrorMessage(badValue, lineNumber));
	}
	
	private static String constructErrorMessage(String badValue, int lineNumber) {
		return "Tier List Parse Error: InvalidCannotGetSizeException on " +
				"line " + lineNumber + ": Value must be a number between " +
				"0 and " + Util.CANNOT_GET_MAX + " (\"" + badValue + "\" " +
				"given).";
	}
}
