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
	public static JTextArea debug;
	
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
	 * This simple method only needs to be called once, it just initializes the
	 * debug <code>JTextArea</code> and sets it so it cannot be edited.
	 * <br><br>
	 * This method will be called in the constructor of the
	 * <code>MainWindow</code> class, so there's no reason to ever call it at
	 * any other point in the program's execution.
	 */
	public static void initDebug() {
		debug = new JTextArea();
		debug.setEditable(false);
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
		System.out.println("[DEBUG]:\t" + log);
		debug.append("[DEBUG]:\t" + log + "\n");
	}
	
	/**
	 * This method adds a new line to the debug log. A time stamp will be
	 * appended to the front of the message, and a newline will be appended to
	 * the end. Messages will also automatically be printed to standard error.
	 * 
	 * @param err	A <code>String</code> representing the error message.
	 */
	public static void error(String err) {
		int hourVal = ZonedDateTime.now().getHour();
		int minVal = ZonedDateTime.now().getMinute();
		int secVal = ZonedDateTime.now().getSecond();
		//do this so we can ensure hour/min/sec are always 2 digits
		DecimalFormat formatter = new DecimalFormat("00");
		String hour = formatter.format(hourVal);
		String min = formatter.format(minVal);
		String sec = formatter.format(secVal);
		
		System.err.println("[" + hour + ":" + min + ":" + sec + "]:\t" + err);
		debug.append("[" + hour + ":" + min + ":" + sec + "]:\t" + err + "\n");
	}
	
	/**
	 * This method adds a new line to the debug log. A time stamp will be
	 * appended to the front of the message, and a newline will be appended to
	 * the end. Messages will also automatically be printed to standard error.
	 * 
	 * @param err	The <code>Exception</code> that occurred.
	 */
	public static void error(Exception err) {
		int hourVal = ZonedDateTime.now().getHour();
		int minVal = ZonedDateTime.now().getMinute();
		int secVal = ZonedDateTime.now().getSecond();
		//do this so we can ensure hour/min/sec are always 2 digits
		DecimalFormat formatter = new DecimalFormat("00");
		String hour = formatter.format(hourVal);
		String min = formatter.format(minVal);
		String sec = formatter.format(secVal);
		
		System.err.println("[" + hour + ":" + min + ":" + sec + "]:\t" + err);
		debug.append("[" + hour + ":" + min + ":" + sec + "]:\t" + err + "\n");
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
