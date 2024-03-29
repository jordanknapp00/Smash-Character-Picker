package exception;

/**
 * <code>TierListParseException</code> subclass representing invalid list
 * setting errors. This exception is used for any settings which are supposed
 * to take a list of a certain amount of numbers.
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
	 * @param setting		An enum representing the setting that was being
	 * 						read when the exception was thrown. Used when
	 * 						creating the error message.
	 * @param cause			The <code>Throwable</code> that led to this
	 * 						exception being thrown, such as a
	 * 						<code>NumberFormatException</code>.
	 */
	public ListSettingParseException(String badVal, int lineNumber, int numVals,
			ListSetting setting, Throwable cause) {
		super(constructErrorMessage(badVal, lineNumber, numVals, setting), cause);
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
	 * @param setting		An enum representing the setting that was being
	 * 						read when the exception was thrown. Used when
	 * 						creating the error message.
	 */
	public ListSettingParseException(String badVal, int lineNumber, int numVals,
			ListSetting setting) {
		super(constructErrorMessage(badVal, lineNumber, numVals, setting));
	}
	
	/**
	 * Constructs the proper error message based on the values passed into
	 * the constructor. The error message will have the following format:
	 * <br><br>
	 * "Line <code>{line number}</code>: "<code>{invalid value}</code> is
	 * invalid for setting "<code>{setting text based on ListSetting
	 * value}</code>" -- a list of <code>{correct number of values}</code>
	 * comma-separated numbers is required. Those numbers must be positive
	 * integers between 0 and 100."
	 * 
	 * @param badVal		The <code>badVal</code> value from the
	 * 						constructor, i.e. the value that caused the
	 * 						exception.
	 * @param lineNumber	The line number that the exception occurred on.
	 * @param numVals		The correct number of values to have in the list.
	 * @param setting		The <code>setting</code> value from the
	 * 						constructor, i.e. which list setting it was that
	 * 						was being parsed when the exception occurred.
	 * @return				A string with the format described above.
	 */
	private static String constructErrorMessage(String badVal, int lineNumber,
			int numVals, ListSetting setting) {
		String settingText;
		switch(setting) {
		case TIER_CHANCES:
			settingText = "Tier Chances";
			break;
		case BUMP_CHANCES:
			settingText = "Bump Chances";
			break;
		default:
			settingText = "UNRECOGNIZED SETTING";
		}
		
		return "Line " + lineNumber + ": \"" + badVal +
				"\" is invalid for setting \"" + settingText +
				"\" -- a list of " + numVals + " comma-separated numbers " +
				"is required. Those numbers must be positive integers " +
				"between 0 and 100.";
	}
}
