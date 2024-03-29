package exception;

/**
 * <code>TierListParseException</code> subclass representing invalid integer
 * setting errors. This exception is used for any settings which are supposed
 * to take an integer between two values.
 * 
 * @author Jordan Knapp
 *
 */
public class IntegerSettingParseException extends TierListParseException {
	
	private static final long serialVersionUID = 1L;

	public enum IntegerSetting {
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
			int minVal, int maxVal, IntegerSetting setting, Throwable cause) {
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
			int minVal, int maxVal, IntegerSetting setting) {
		super(constructErrorMessage(badVal, lineNumber, minVal, maxVal, setting));
	}
	
	/**
	 * Constructs the proper error message based on the values passed into
	 * the constructor. The error message will have the following format:
	 * <br><br>
	 * "Line <code>{line number}</code>: "<code>{invalid value}</code> is
	 * invalid for setting "<code>{setting text based on IntegerSetting
	 * value}</code>" -- a number between <code>{minimum value}</code> and
	 * <code>{maximum value{</code> is required."
	 * 
	 * @param badVal		The <code>badVal</code> value from the
	 * 						constructor, i.e. the value that caused the
	 * 						exception.
	 * @param lineNumber	The line number that the exception occurred on.
	 * @param minVal		The minimum value for this integer setting.
	 * @param maxVal		The maximum value for this integer setting.
	 * @param setting		The <code>setting</code> value from the
	 * 						constructor, i.e. which integer setting it was
	 * 						that was being parsed when the exception occurred.
	 * @return				A string with the format described above.
	 */
	private static String constructErrorMessage(String badVal, int lineNumber,
			int minVal, int maxVal, IntegerSetting setting) {
		String settingText;
		
		switch(setting) {
		case NUM_PLAYERS:
			settingText = "Number of players";
			break;
		case CANNOT_GET_SIZE:
			settingText = "'Cannot Get' size";
			break;
		default:
			settingText = "UNRECOGNIZED SETTING";
		}
		
		return "Line " + lineNumber + ": \"" + badVal +
				"\" is invalid for setting \"" + settingText +
				"\" -- a number between " + minVal + " and " + maxVal +
				" is required.";
	}

}
