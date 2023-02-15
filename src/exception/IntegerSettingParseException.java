package exception;

/**
 * <code>TierListParseException</code> subclass representing invalid integer
 * setting errors. This exception is used for any settings which are supposed
 * to take an integer between two values.
 * 
 * @author Jordan Knap
 *
 */
public class IntegerSettingParseException extends TierListParseException {
	
	private static final long serialVersionUID = 1L;

	public enum Setting {
		NUM_PLAYERS,
		CANNOT_GET_SIZE
	};
	
	/**
	 * Constructs a <code>IntegerSettingParseException</code> with the given
	 * parameters, including a <code>Throwable</code> cause.
	 * 
	 * @param badVal		The invalid value that caused the exception.
	 * @param lineNumber	The line number of the tier list file that the
	 * 						code exception occurred on.
	 * @param minVal		The correct minimum value for this setting.
	 * @param maxVal		The correct maximum value for this setting.
	 * @param setting		An enum representing the setting that was being
	 * 						read when the exception was thrown. Used when
	 * 						creating the error message.
	 * @param cause			The <code>Throwable</code> that led to this
	 * 						exception being thrown, such as a
	 * 						<code>NumberFormatException</code>.
	 */
	public IntegerSettingParseException(String badVal, int lineNumber,
			int minVal, int maxVal, Setting setting, Throwable cause) {
		super(constructErrorMessage(badVal, lineNumber, minVal, maxVal, setting), cause);
	}
	
	/**
	 * Constructs a <code>IntegerSettingParseException</code> with the given
	 * parameters.
	 * 
	 * @param badVal		The invalid value that caused the exception.
	 * @param lineNumber	The line number of the tier list file that the
	 * 						code exception occurred on.
	 * @param minVal		The correct minimum value for this setting.
	 * @param maxVal		The correct maximum value for this setting.
	 * @param setting		An enum representing the setting that was being
	 * 						read when the exception was thrown. Used when
	 * 						creating the error message.
	 */
	public IntegerSettingParseException(String badVal, int lineNumber,
			int minVal, int maxVal, Setting setting) {
		super(constructErrorMessage(badVal, lineNumber, minVal, maxVal, setting));
	}
	
	private static String constructErrorMessage(String badVal, int lineNumber,
			int minVal, int maxVal, Setting setting) {
		String settingText;
		switch(setting) {
		case NUM_PLAYERS:
			settingText = "Number of players";
		case CANNOT_GET_SIZE:
			settingText = "'Cannot Get' size";
		default:
			settingText = "UNRECOGNIZED SETTING";
		}
		
		return "Tier List Parse Error on line " + lineNumber + ": \"" +
				badVal + "\" is invalid for setting \"" + settingText +
				"\" -- a number between " + minVal + " and " + maxVal +
				" is required.";
	}

}
