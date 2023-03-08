package exception;

/**
 * Exception class representing what happens when a player has no valid
 * fighters available when generating a battle. The exception can be thrown
 * either before a tier is selected (in which case the player has exactly 0
 * available fighters, and no battle can be generated), or after a tier has
 * been selected, in which case another attempt can be made to generate a
 * battle. This distinction is made via the <code>tierRangeSelected</code>
 * variable.
 * 
 * @author Jordan Knapp
 *
 */
public class NoValidFightersException extends Exception {
	
	private static final long serialVersionUID = 1L;

	private boolean tierRangeSelected;
	
	/**
	 * Constructs a <code>NoValidFightersException</code> with the given
	 * parameters, including a <code>Throwable</code> cause.
	 * 
	 * @param player				The player who had no valid fighters.
	 * @param tierRangeSelected		<code>true</code> if a tier range had
	 * 								already been selected, <code>false</code>
	 * 								if this occurred when generating the
	 * 								initial valid set for each player.
	 * @param cause					The <code>Throwable</code> that led to
	 * 								this exception being thrown.
	 */
	public NoValidFightersException(int player, boolean tierRangeSelected, Throwable cause) {
		super(constructErrorMessage(player, tierRangeSelected), cause);
		
		this.tierRangeSelected = tierRangeSelected;
	}
	
	/**
	 * Constructs a <code>NoValidFightersException</code> with the given
	 * parameters.
	 * 
	 * @param player				The player who had no valid fighters.
	 * @param tierRangeSelected		<code>true</code> if a tier range had
	 * 								already been selected, <code>false</code>
	 * 								if this occurred when generating the
	 * 								initial valid set for each player.
	 */
	public NoValidFightersException(int player, boolean tierRangeSelected) {
		super(constructErrorMessage(player, tierRangeSelected));
		
		this.tierRangeSelected = tierRangeSelected;
	}
	
	/**
	 * Constructs the proper error message based on the values passed into
	 * the constructor. The error message will have the following format:
	 * <br><br>
	 * "Player <code>{player}</code> has no valid fighters <code>{</code>in
	 * generated tier range<code>}/{</code>available at all
	 * <code>}</code>.
	 * 
	 * The text above depends on the value of <code>tierRangeSelected</code>.
	 * 
	 * @param player			The player whose fighter was being chosen.
	 * @param tierRangeSelected	Whether or not this exception occurred after
	 * 							the tier range had been selected.
	 * @return					A string with the format described above.
	 */
	private static String constructErrorMessage(int player, boolean tierRangeSelected) {
		String retString = "Player " + (player + 1) + " has no valid fighters ";
		
		if(tierRangeSelected) {
			return retString + " in generated tier range.";
		}
		else {
			return retString + " available at all.";
		}
	}
	
	/**
	 * Get the value of the <code>tierRangeSelected</code> variable. If an
	 * exception occurs after the tier range is selected, another attempt
	 * can be made to generate a battle. However, if there are no valid
	 * fighters in a player's valid set at all, then a battle cannot get
	 * generated. This value is checked by <code>MainWindow</code> to
	 * determine whether another attempt should be made.
	 * 
	 * @return	<code>true</code> if this exception occurred after the
	 * 			tier range was already selected, <code>false</code> if it
	 * 			happened when generating the initial set of valid fighters
	 * 			for a player.
	 */
	public boolean tierRangeSelected() {
		return tierRangeSelected;
	}

}
