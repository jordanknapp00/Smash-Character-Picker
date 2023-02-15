package exception;

/**
 * <code>TierListParseException</code> subclass representing invalid list
 * setting errors. This exception is used for any settings which are supposed
 * to take a list of a certain amount of numbers that sum to a certain
 * quantity.
 * 
 * @author Jordan Knapp
 *
 */
public class ListSettingParseException extends TierListParseException {
	
	private static final long serialVersionUID = 1L;

	public enum ListSetting {
		TIER_CHANCES,
		BUMP_CHANCES
	};
	
	/**
	 * Constructs a <code>ListSettingParseException</code> with the given
	 * parameters, including a <code>Throwable</code> cause.
	 * 
	 * @param badVal		The invalid value(s) that caused the exception.
	 * @param lineNumber	The line number of the tier list file that the
	 * 						code exception occurred on.
	 * @param numVals		The correct number of values that are supposed
	 * 						to be given.
	 * @param sum			The correct sum of the values.
	 * @param setting		An enum representing the setting that was being
	 * 						read when the exception was thrown. Used when
	 * 						creating the error message.
	 * @param cause			The <code>Throwable</code> that led to this
	 * 						exception being thrown, such as a
	 * 						<code>NumberFormatException</code>.
	 */
	public ListSettingParseException(String badVal, int lineNumber, int numVals,
			int sum, ListSetting setting, Throwable cause) {
		super(constructErrorMessage(badVal, lineNumber, numVals, sum, setting), cause);
	}
	
	/**
	 * Constructs a <code>ListSettingParseException</code> with the given
	 * parameters.
	 * 
	 * @param badVal		The invalid value(s) that caused the exception.
	 * @param lineNumber	The line number of the tier list file that the
	 * 						code exception occurred on.
	 * @param numVals		The correct number of values that are supposed
	 * 						to be given.
	 * @param sum			The correct sum of the values.
	 * @param setting		An enum representing the setting that was being
	 * 						read when the exception was thrown. Used when
	 * 						creating the error message.
	 */
	public ListSettingParseException(String badVal, int lineNumber, int numVals,
			int sum, ListSetting setting) {
		super(constructErrorMessage(badVal, lineNumber, numVals, sum, setting));
	}
	
	private static String constructErrorMessage(String badVal, int lineNumber,
			int numVals, int sum, ListSetting setting) {
		String settingText;
		switch(setting) {
		case TIER_CHANCES:
			settingText = "Tier Chances";
		case BUMP_CHANCES:
			settingText = "Bump Chances";
		default:
			settingText = "UNRECOGNIZED SETTING";
		}
		
		return "Tier List Parse Error on line " + lineNumber + ": \"" +
				badVal + "\" is invalid for setting \"" + settingText +
				"\" -- a list of " + numVals + " comma-separated numbers " +
				"adding to " + sum + " is required.";
	}
}
