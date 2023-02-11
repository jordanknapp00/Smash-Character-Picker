package data;

/**
 * The <code>Matchup</code> class represents a single battle. It stores the
 * fighters that appeared in the battle in an array, ordered by the player
 * who got them. However, the array of fighters is treated like a set when
 * comparisons (via <code>equals()</code>) are performed. In other words, a
 * <code>Matchup</code> is considered equal to another <code>Matchup</code>
 * as long as they contain the same <code>Fighter</code>s. The order is not
 * considered.
 * 
 * @author Jordan Knapp
 *
 */
public class Matchup {
	
	//TODO: update documentation and make it so player is always 0-indexed
	
	private Fighter[] fighters;
	
	/**
	 * Initialize an empty <code>Matchup</code> with the given size.
	 * 
	 * @param numPlayers	The number of players in this matchup.
	 */
	public Matchup(int numPlayers) {
		fighters = new Fighter[numPlayers];
	}
	
	/**
	 * Add the specified fighter for the specified player to the <code>Matchup</code>.
	 * Only use this to <i>add</i> a fighter to the <code>Matchup</code>. If
	 * you wish to overwrite a player's fighter, use <code>setPlayer()</code>
	 * instead.
	 * 
	 * @param player	The player who is having a fighter added.
	 * @param fighter	The fighter to add for that player.
	 * 
	 * @throws IndexOutOfBoundsException		Thrown if the player is less
	 * 											than zero or greater than the
	 * 											size of the <code>Matchup</code>,
	 * 											which is equal to
	 * 											<code>Settings.numPlayers</code>
	 * 											at the time of the object's
	 * 											creation.
	 * 
	 * @throws UnsupportedOperationException	Thrown if you attempt to
	 * 											set a player whose fighter
	 * 											has already been set.
	 */
	public void addFighter(int player, Fighter fighter) throws IndexOutOfBoundsException, UnsupportedOperationException {
		if(player > fighters.length || player <= 0) {
			throw new IndexOutOfBoundsException("Matchup was initialized with " + fighters.length +
					" fighters, but " + player + " was passed in as player.");
		}
		
		if(fighters[player - 1] == null) {
			setFighter(player, fighter);
		}
		else {
			throw new UnsupportedOperationException("Player " + (player + 1) +
					" has already been assigned. Please use setPlayer() instead.");
		}
	}
	
	/**
	 * Set the specified fighter for the specified player in this matchup.
	 * 
	 * @param player	The player who is having their fighter set.
	 * @param fighter	The fighter to set for that player.
	 * 
	 * @throws IndexOutOfBoundsException		Thrown if the player is less
	 * 											than zero or greater than the
	 * 											size of the matchup, which is
	 * 											equal to
	 * 											<code>Settings.numPlayers</code>
	 * 											at the time of the object's
	 * 											creation.
	 */
	public void setFighter(int player, Fighter fighter) throws IndexOutOfBoundsException {
		if(player > fighters.length || player <= 0) {
			throw new IndexOutOfBoundsException("Matchup was initialized with " + fighters.length +
					" fighters, but " + player + " was passed in as player.");
		}
		
		player--;
		fighters[player] = fighter;
	}
	
	/**
	 * Get the fighter that the specified player got.
	 * 
	 * @param player	The player whose fighter is to be returned.
	 * @return			The <code>Fighter</code> that the given player got.
	 * 
	 * @throws IndexOutOfBoundsException	Thrown if the player is less
	 * 										than zero or greater than the
	 * 										size of the matchup, which is
	 * 										equal to
	 * 										<code>Settings.numPlayers</code>
	 * 										at the time of the object's
	 * 										creation.
	 */
	public Fighter getFighter(int player) throws IndexOutOfBoundsException {
		if(player > fighters.length || player <= 0) {
			throw new IndexOutOfBoundsException("Matchup was initialized with " + fighters.length +
					" fighters, but " + player + " was passed in as player.");
		}
		
		player--;
		return fighters[player];
	}
	
	public int size() {
		return fighters.length;
	}
	
	public boolean contains(Fighter o) {
		for(Fighter at: fighters) {
			if(at.equals(o)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Matchup)) {
			return false;
		}
		
		Matchup other = (Matchup) o;
		
		//quick comparisons for where we know the result without doing a
		//full comparison
		if(other == this) {
			return true;
		}
		else if(size() != other.size()) {
			return false;
		}
		
		//otherwise, we know both matchups have the same size, so it's a
		//matter of making sure all the fighters in one are in the other
		for(Fighter at: fighters) {
			if(!other.contains(at)) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder retString = new StringBuilder();
		
		for(int playerAt = 0; playerAt < fighters.length; playerAt++) {
			retString.append("Player " + (playerAt + 1) + " got " + fighters[playerAt] + "\n");
		}
		
		return retString.toString();
	}

}
