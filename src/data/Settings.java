package data;

/**
 * <code>Settings</code> provides a read-only interface for transmitting
 * the settings of the program. It is intended to be a short-lived object,
 * created and quickly passed before being discarded after the contents are
 * accessed. For example, <code>TierList</code> returns one after loading a
 * file, so <code>MainWindow</code> can change the UI to react to the
 * settings loaded in a file. And <code>MainWindow</code> passes one to
 * <code>TierList</code> so it knows what settings to use when generating a
 * battle.
 * 
 * @author Jordan Knapp
 *
 */
public class Settings {
	
	private int numPlayers;
	
	private int[] tierChances;
	private int[] bumpChances;
	
	private int cannotGetSize;
	private boolean allowSInCannotGet;
	private boolean allowSSInCannotGet;
	
	/**
	 * Creates a settings with the given parameters.
	 * 
	 * @param numPlayers			The current number of players. Should be
	 * 								between 2 and 8. This is not enforced
	 * 								here.
	 * @param tierChances			An array consisting of 8 integers, whose
	 * 								contents should add up to 100. Neither
	 * 								the size nor the summation rules are
	 * 								enforced by <code>Settings</code>,
	 * 								however.
	 * @param bumpChances			An array consisting of 3 integers, whose
	 * 								contents should add up to 100. Like with
	 * 								<code>tierChances</code>, these rules are
	 * 								not enforced here. It is up to the
	 * 								creator of the object to ensure these details.
	 * @param cannotGetSize			The size of the "Cannot Get" queue.
	 * 								Should be between 0 and
	 * 								<code>Util.<i><b>CANNOT_GET_MAX</b></i></code>.
	 * 								You guessed it, that isn't enforced here.
	 * @param allowSInCannotGet		Whether or not S tiers are allowed in the
	 * 								"Cannot Get" queue.
	 * @param allowSSInCannotGet	Whether or not SS tiers are allowed in
	 * 								the "Cannot Get" queue.
	 */
	public Settings(int numPlayers, int[] tierChances, int[] bumpChances,
			int cannotGetSize, boolean allowSInCannotGet, boolean allowSSInCannotGet) {
		this.numPlayers = numPlayers;
		
		this.tierChances = tierChances;
		this.bumpChances = bumpChances;
		
		this.cannotGetSize = cannotGetSize;
		this.allowSInCannotGet = allowSInCannotGet;
		this.allowSSInCannotGet = allowSSInCannotGet;
	}
	
	/**
	 * @return	The current number of players.
	 */
	public int getNumPlayers() {
		return numPlayers;
	}
	
	/**
	 * @param tier	The tier (from 0 to 7) to get the chance of.
	 * @return		The chance of getting the given tier, or -1 if the
	 * 				tier is invalid (or if the array passed when creating
	 * 				this object is invalid, resulting in an
	 * 				<code>ArrayIndexOutOfBoundsException</code>).
	 */
	public int getTierChance(int tier) {
		try {
			return tierChances[tier];
		} catch(ArrayIndexOutOfBoundsException e) {
			return -1;
		}
	}
	
	/**
	 * @param bumpAmount	The number of tiers to bump (from 0 to 2) to get
	 * 						the chance of.
	 * @return				The chance of bumping that number of tiers, or
	 * 						-1 if the given value is invalid (or if the
	 * 						array passed when creating this object is
	 * 						invalid, resulting in an
	 * 						<code>ArrayIndexOutOfBoundsException</code>).
	 */
	public int getBumpChance(int bumpAmount) {
		try {
			return bumpChances[bumpAmount];
		} catch(ArrayIndexOutOfBoundsException e) {
			return -1;
		}
	}
	
	/**
	 * @return	The max size of the "Cannot Get" queue.
	 */
	public int getCannotGetSize() {
		return cannotGetSize;
	}
	
	/**
	 * @return	<code>true</code> if S tiers are allowed in the "Cannot Get"
	 * 			queue, <code>false</code> if they are not.
	 */
	public boolean sAllowedInCannotGet() {
		return allowSInCannotGet;
	}
	
	/**
	 * @return	<code>true</code> if SS tiers are allowed in the "Cannot Get"
	 * 			queue, <code>false</code> if they are not.
	 */
	public boolean ssAllowedInCannotGet() {
		return allowSSInCannotGet;
	}

}
