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
	
	private static String constructErrorMessage(int player, boolean tierRangeSelected) {
		String retString = "Player " + (player + 1) + " has no valid fighters ";
		
		if(tierRangeSelected) {
			return retString + " in generated tier range.";
		}
		else {
			return retString + " available at all.";
		}
	}
	
	public boolean tierRangeSelected() {
		return tierRangeSelected;
	}

}
