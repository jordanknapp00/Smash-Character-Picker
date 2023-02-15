package exception;

/**
 * <code>TierListParseException</code> subclass representing invalid boolean
 * setting errors. This exception is used for any settings which are supposed
 * to take a boolean value.
 * 
 * @author Jordan Knapp
 *
 */
public class BooleanSettingParseException extends TierListParseException {

	private static final long serialVersionUID = 1L;

	public enum BooleanSetting {
		ALLOW_S,
		ALLOW_SS
	};
	
	/**
	 * Constructs a <code>BooleanSettingParseException</code> with the given
	 * parameters, including a <code>Throwable</code> cause.
	 * 
	 * @param badVal		The invalid value that caused the exception.
	 * @param lineNumber	The line number of the tier list file that the
	 * 						code exception occurred on.
	 * @param setting		An enum representing the setting that was being
	 * 						read when the exception was thrown. Used when
	 * 						creating the error message.
	 * @param cause			The <code>Throwable</code> that led to this
	 * 						exception being thrown, such as a
	 * 						<code>NumberFormatException</code>.
	 */
	public BooleanSettingParseException(String badVal, int lineNumber,
			BooleanSetting setting, Throwable cause) {
		super(constructErrorMessage(badVal, lineNumber, setting), cause);
	}
	
	/**
	 * Constructs a <code>BooleanSettingParseException</code> with the given
	 * parameters.
	 * 
	 * @param badVal		The invalid value that caused the exception.
	 * @param lineNumber	The line number of the tier list file that the
	 * 						code exception occurred on.
	 * @param setting		An enum representing the setting that was being
	 * 						read when the exception was thrown. Used when
	 * 						creating the error message.
	 * @param cause			The <code>Throwable</code> that led to this
	 * 						exception being thrown, such as a
	 * 						<code>NumberFormatException</code>.
	 */
	public BooleanSettingParseException(String badVal, int lineNumber,
			BooleanSetting setting) {
		super(constructErrorMessage(badVal, lineNumber, setting));
	}
	
	private static String constructErrorMessage(String badVal, int lineNumber, BooleanSetting setting) {
		String settingText;
		switch(setting) {
		case ALLOW_S:
			settingText = "Allow S in 'Cannot Get'";
		case ALLOW_SS:
			settingText = "Allow SS in 'Cannot Get'";
		default:
			settingText = "UNRECOGNIZED SETTING";
		}
		
		return "Tier List Parse Error on line " + lineNumber + ": \"" +
				badVal + "\" is invalid for setting \"" + settingText +
				"\" -- a boolean value (\"true\"/\"false\" or 0\1) is required.";
	}
}
