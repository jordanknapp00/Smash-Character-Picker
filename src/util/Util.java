package util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;

import javax.swing.JTextArea;

import data.ComparableArray;

/**
 * A class that contains a few static utility methods that are used throughout
 * the program.
 * 
 * @author Jordan Knapp
 *
 */
public final class Util {
	
	/**
	 * <i><code>debug</code></i> is the text field that the
	 * <code>DebugWindow</code> will display. Using this class' <code>log</code>
	 * and <code>error</code> methods, any class in the program can print
	 * debug messages.
	 */
	public static JTextArea debug = new JTextArea();
	
	/**
	 * The maximum size of the "Cannot Get" queue. Value is used in both
	 * <code>MainWindow</code> and <code>TierList</code> for verifying
	 * inputs, so I'm putting it here.
	 */
	public static final int CANNOT_GET_MAX = 15;
	
	/**
	 * Private constructor to prevent instantiating the <code>Util</code> class.
	 */
	private Util() {
		throw new UnsupportedOperationException("Cannot instantiate Util class");
	}
	
	/**
	 * Static method that converts a given tier number (used for indexing into
	 * the <code>linesOfFile</code> array, for example) into a human-readable
	 * tier name.
	 * 
	 * @param tier	The number of the tier to be converted to a
	 * 				<code>String</code>
	 * 
	 * @return		The given tier in <code>String</code> format.
	 */
	public static String tierToString(int tier) {
		switch(tier) {
		case 0:
			return "Upper Double S tier";
		case 1:
			return "Double S tier";
		case 2:
			return "Lower Double S tier";
		case 3:
			return "Upper S tier";
		case 4:
			return "Mid S tier";
		case 5:
			return "Lower S tier";
		case 6:
			return "Upper A tier";
		case 7:
			return "Mid A tier";
		case 8:
			return "Lower A tier";
		case 9:
			return "Upper B tier";
		case 10:
			return "Mid B tier";
		case 11:
			return "Lower B tier";
		case 12:
			return "Upper C tier";
		case 13:
			return "Mid C tier";
		case 14:
			return "Lower C tier";
		case 15:
			return "Upper D tier";
		case 16:
			return "Mid D tier";
		case 17:
			return "Lower D tier";
		case 18:
			return "Upper E tier";
		case 19:
			return "Mid E tier";
		case 20:
			return "Lower E tier";
		case 21:
			return "Upper F tier";
		case 22:
			return "Mid F tier";
		case 23:
			return "Lower F tier";
		default:
			return "Invalid tier";
		}
	}
	
	/**
	 * Converts a tier in <code>String</code> format to integer format, used
	 * for indexing into the <code>tierList</code> object in the
	 * <code>TierList</code> class. At this time, this method exists so that
	 * when loading a file, we know which index we're reading based on the
	 * name of that line. As such, this method assumes that the word "tier"
	 * will not be present at the end of the string, as it isn't when
	 * reading a file.
	 * 
	 * @param tier	The name of the tier to get the index of. i.e.
	 * 				"upper double s" or "mid c"
	 * @return		The integer representation of that tier, or -1 if the
	 * 				input is not a recognized tier.
	 */
	public static int stringToTier(String tier) {
		tier = tier.toLowerCase();
		
		switch(tier) {
		case "upper double s":
			return 0;
		case "mid double s":
			return 1;
		case "lower double s":
			return 2;
		case "upper s":
			return 3;
		case "mid s":
			return 4;
		case "lower s":
			return 5;
		case "upper a":
			return 6;
		case "mid a":
			return 7;
		case "lower a":
			return 8;
		case "upper b":
			return 9;
		case "mid b":
			return 10;
		case "lower b":
			return 11;
		case "upper c":
			return 12;
		case "mid c":
			return 13;
		case "lower c":
			return 14;
		case "upper d":
			return 15;
		case "mid d":
			return 16;
		case "lower d":
			return 17;
		case "upper e":
			return 18;
		case "mid e":
			return 19;
		case "lower e":
			return 20;
		case "upper f":
			return 21;
		case "mid f":
			return 22;
		case "lower f":
			return 23;
		default:
			return -1;
		}
	}
	
	/**
	 * Method that converts a subtier value (from 0 to 23) to a tier value
	 * (0 to 8). Each tier has 3 subtiers. So this method would convert the
	 * numerical value of "upper s tier" (4) to a value representing S tier
	 * in the tier chance system (1).
	 * 
	 * @param subTier	The sub-tier to convert, from 0 to 23.
	 * @return			The higher-level tier value used in the tier chance
	 * 					system (from 0 to 8), or -1 if the given subtier is
	 * 					not valid.
	 */
	public static int subTierToTier(int subTier) {
		if(subTier >= 0 && subTier <= 2) {
			return 0;
		}
		else if(subTier >= 3 && subTier <= 5) {
			return 1;
		}
		else if(subTier >= 6 && subTier <= 8) {
			return 2;
		}
		else if(subTier >= 9 && subTier <= 11) {
			return 3;
		}
		else if(subTier >= 12 && subTier <= 14) {
			return 4;
		}
		else if(subTier >= 15 && subTier <= 17) {
			return 5;
		}
		else if(subTier >= 18 && subTier <= 20) {
			return 6;
		}
		else if(subTier >= 21 && subTier <= 23) {
			return 7;
		}
		else {
			return -1;
		}
	}
	
	/**
	 * A static method for converting <code>double</code> values into a
	 * <code>String</code> to be printed on the stats screen.
	 * 
	 * @param num	The <code>double</code> to be converted.
	 * 
	 * @return		The given <code>double</code>, rounded to 2 decimal places
	 * 				and converted to a <code>String</code>. If the given value
	 * 				is invalid, then "NaN" will be returned.
	 */
	public static String printDouble(double num) {
		if(num >= 0) {
			return new BigDecimal(String.valueOf(num)).setScale(2, RoundingMode.FLOOR).toString();
		}
		else if(num < 0) {
			return new BigDecimal(String.valueOf(num)).setScale(2, RoundingMode.CEILING).toString();
		}
		else {
			return "NaN";
		}
	}
	
	/**
	 * This method adds a new debug line to the debug log. "[DEBUG]:" will be
	 * appended to the front of the message, and a newline will automatically be
	 * appended to the end, so there is no need to add that when calling the
	 * method. Messages will also automatically be printed to the standard
	 * output.
	 * 
	 * @param log	The message to be logged.
	 */
	public static void log(String log) {		
		System.out.println("[DEBUG]:\t\t" + log);
		debug.append("[DEBUG]:\t\t" + log + "\n");
	}
	
	/**
	 * This method adds a new line to the debug log. A time stamp will be
	 * appended to the front of the message, and a newline will be appended to
	 * the end. Messages will also automatically be printed to standard error.
	 * 
	 * @param err	A <code>String</code> representing the error message.
	 */
	public static void error(String err) {
		String timestamp = generateTimestamp();
		
		System.err.println(timestamp + err);
		debug.append(timestamp + err + "\n");
	}
	
	/**
	 * This method adds a new line to the debug log. A time stamp will be
	 * appended to the front of the message, and a newline will be appended to
	 * the end. Messages will also automatically be printed to standard error.
	 * 
	 * @param err	The <code>Exception</code> that occurred.
	 */
	public static void error(Exception err) {
		String timestamp = generateTimestamp();
		
		System.err.println(timestamp + err);
		debug.append(timestamp + err + "\n");
	}
	
	/**
	 * Generates a timestamp with the following format:<br>
	 * "<code>level</code> [HH:MM:SS]:<code>(tab)</code>"
	 * 
	 * @return		A timestamp with the format described above.
	 */
	private static String generateTimestamp() {
		int hourVal = ZonedDateTime.now().getHour();
		int minVal = ZonedDateTime.now().getMinute();
		int secVal = ZonedDateTime.now().getSecond();
		
		//do this so we can ensure hour/min/sec are always 2 digits
		DecimalFormat formatter = new DecimalFormat("00");
		String hour = formatter.format(hourVal);
		String min = formatter.format(minVal);
		String sec = formatter.format(secVal);
		
		return "[" + hour + ":" + min + ":" + sec + "]:\t";
	}
	
	/**
	 * A method that returns a better string representation of an array of
	 * <code>double</code>s than just the memory address. Given that this is
	 * only used in the context of printing things to the debug log, we're
	 * assuming that the array will be 16 entries in length.
	 * 
	 * @param arr	The array of doubles.
	 * @return		The values in the array, comma separated.
	 */
	public static String doubleArrString(double[] arr) {
		String returnString = "";
		for(int at = 0; at < 14; at++) {
			returnString += arr[at] + ",";
		}
		returnString += arr[15];
		return returnString;
	}
	
	/**
	 * A method that reverses a <code>ComparableArray</code>. Not an in-place
	 * reverse, it returns a new object.
	 * 
	 * @param start	The <code>ComparableArray</code> to reverse.
	 * @return		A new <code>ComparableArray</code>, consisting of the given
	 * 				<code>ComparableArray</code>, but reversed.
	 */
	public static ComparableArray[] reverse(ComparableArray[] start) {
		ComparableArray[] retArr = new ComparableArray[start.length];
		int j = start.length;
		for(int at = 0; at < start.length; at++) {
			retArr[j - 1] = start[at];
			j--;
		}
		
		return retArr;
	}

}
