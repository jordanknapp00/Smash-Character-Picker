package data;

import state.Settings;

public class Matchup implements Comparable<Matchup> {
	
	private Fighter[] fighters;
	
	/**
	 * Initialize the matchup. To begin with, it will be empty, with a
	 * capacity equal to <code>Settings.numPlayers</code> at creation time.
	 */
	public Matchup() {
		fighters = new Fighter[Settings.numPlayers];
	}
	
	/**
	 * Add the specified fighter for the specified player to the matchup.
	 * Only use this to <i>add</i> a fighter to the matchup. If you wish to
	 * overwrite a player's fighter, use <code>setPlayer()</code> instead.
	 * 
	 * @param player	The player who is having a fighter added.
	 * @param fighter	The fighter to add for that player.
	 * 
	 * @throws IndexOutOfBoundsException		Thrown if the player is less
	 * 											than zero or greater than the
	 * 											size of the matchup, which is
	 * 											equal to
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

	@Override
	public int compareTo(Matchup o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
