package picker;

import java.util.ArrayDeque;

/**
 * A <code>CannotGetQueue</code> is designed to work with <code>Picker</code>,
 * allowing a list of characters and associated tiers to be easily added and
 * removed, to meet the functionality of older versions of <code>Picker</code>'s
 * use of a single <code>ArrayDeque</code> to accomplish this task.
 * <p>
 * It uses two <code>ArrayDeque</code> objects to store the names and the
 * associated tier of characters added to <code>Picker's CannotGet Queue</code>.
 * 
 * @author Jordan Knapp
 */
public class CannotGetQueue {
	
	private ArrayDeque<String> charsCantGet;
	private ArrayDeque<Integer> tiersOfCantGet;
	
	/**
	 * Constructor for <code>CannotGetQueue</code>, simply initializing both
	 * <code>ArrayDeque</code> objects.<p>
	 */
	public CannotGetQueue() {
		charsCantGet = new ArrayDeque<String>();
		tiersOfCantGet = new ArrayDeque<Integer>();
	}

	/** Returns the size of the <code>charsCantGet</code> <code>ArrayDeque</code>,
	 *  which should be equal to the size of <code>tiersOfCantGet</code>. If they
	 *  are not the same, something is wrong.
	 */
	public int size() {
		return charsCantGet.size();
	}

	/** Returns <code>true</code> if <code>charsCantGet</code> is empty. Again,
	 *  <code>charsCantGet</code> and <code>tiersOfCantGet</code> should be the
	 *  same size, so there is no need to check both.
	 */
	public boolean isEmpty() {
		return charsCantGet.isEmpty();
	}

	/** Returns <code>true</code> if:
	 * <p><ul>
	 * <li> The parameter <code>o</code> is a <code>String</code> <b>and</b>
	 * 		<code>charsCantGet</code> contains <code>o</code>.
	 * <li> The parameter <code>o</code> is an <code>Integer</code> <b>and</b>
	 * 		<code>tiersOfCantGet</code> contains <code>o</code>.
	 * <p></ul>
	 * Any other case, and this will return <code>false</code>.<p>
	 * 
	 * @return	<code>True</code> if either condition in the above list is met,
	 * 			and <code>false</code> if neither condition is met.
	 */
	public boolean contains(Object o) {
		if(o instanceof String) {
			return charsCantGet.contains(o);
		}
		else if(o instanceof Integer) {
			return tiersOfCantGet.contains(o);
		}
		else {
			return false;
		}
	}

	/** Removes all elements from <code>charsCantGet</code> and <code>tiersOfCantGet</code>.
	 * 	The <code>CannotGetQueue</code> will be considered empty after doing so.
	 */
	public void clear() {
		charsCantGet.clear();
		tiersOfCantGet.clear();
	}

	/** Adds the specified <code>String</code> and <code>Integer</code> to
	 *  <code>charsCantGet</code> and <code>tiersOfCantGet</code>, respectively.
	 * @param character	The name of the character to be added to the <code>Queue</code>.
	 * @param tier		The tier of the character to be added to the <code>Queue</code>.
	 * @return			<code>True</code>, unless there is no more space in either
	 * 					<code>Queue</code>, meaning there is no memory left. In
	 * 					that case, an <code>Exception</code> is thrown.
	 */
	public boolean add(String character, Integer tier) {
		charsCantGet.addLast(character);
		tiersOfCantGet.addLast(tier);
		return true;
	}

	/** Gets the first element of <code>charsCantGet</code>, removes it from the
	 *  <code>Queue</code>, and returns it.
	 *  
	 * @return	The first entry in <code>charsCantGet</code>, which is also
	 * 			removed from the <code>Queue</code>.
	 */
	public String getAndRemoveFirstChar() {
		String returnString = charsCantGet.getFirst();
		charsCantGet.removeFirst();
		return returnString;
	}
	
	/** Gets the first element of <code>tiersOfCantGet</code>, removes it from the
	 *  <code>Queue</code>, and returns it.
	 *  
	 * @return	The first entry in <code>tiersOfCantGet</code>, which is also
	 * 			removed from the <code>Queue</code>.
	 */
	public int getAndRemoveFirstTier() {
		int returnInt = tiersOfCantGet.getFirst();
		tiersOfCantGet.removeFirst();
		return returnInt;
	}
	
	/** Removes first elements from both <code>Queues</code>.
	 */
	public void removeFirst() {
		charsCantGet.removeFirst();
		tiersOfCantGet.removeFirst();
	}
	
	/** Just returns the first element of <code>tiersOfCantGet</code>, without
	 * 	removing it.
	 * 
	 * @return	The first entry of <code>tiersOfCantGet</code>.
	 */
	public int getFirstTier() {
		return tiersOfCantGet.getFirst();
	}
	
	public String toString() {
		return charsCantGet.toString();
	}
}
