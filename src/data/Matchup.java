package data;

import util.Util;

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
	
	//TODO: update documentation to eliminate references to Settings class
	
	private Fighter[] fighters;
	private boolean winnerSelected;
	private int winner;
	
	/**
	 * Initialize an empty <code>Matchup</code> with the given size.
	 * 
	 * @param numPlayers	The number of players in this matchup.
	 */
	public Matchup(int numPlayers) {
		fighters = new Fighter[numPlayers];
		
		winnerSelected = false;
		winner = -1;
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
	 * 											size of the <code>Matchup</code>.
	 * 
	 * @throws UnsupportedOperationException	Thrown if you attempt to
	 * 											set a player whose fighter
	 * 											has already been set.
	 */
	public void addFighter(int player, Fighter fighter) throws IndexOutOfBoundsException, UnsupportedOperationException {
		if(player >= fighters.length || player < 0) {
			throw new IndexOutOfBoundsException("Matchup was initialized with " + fighters.length +
					" fighters, but " + player + " was passed in as player.");
		}
		
		if(fighters[player] == null) {
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
	 * 											size of the matchup.
	 */
	public void setFighter(int player, Fighter fighter) throws IndexOutOfBoundsException {
		if(player >= fighters.length || player < 0) {
			throw new IndexOutOfBoundsException("Matchup was initialized with " + fighters.length +
					" fighters, but " + player + " was passed in as player.");
		}
		
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
	 * 										size of the matchup.
	 */
	public Fighter getFighter(int player) throws IndexOutOfBoundsException {
		if(player >= fighters.length || player < 0) {
			throw new IndexOutOfBoundsException("Matchup was initialized with " + fighters.length +
					" fighters, but " + player + " was passed in as player.");
		}

		return fighters[player];
	}
	
	public int size() {
		return fighters.length;
	}
	
	public boolean contains(Fighter o) {
		for(Fighter at: fighters) {
			if(at != null && at.equals(o)) {
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

	/**
	 * Swaps the fighters belonging to the two given players.
	 * 
	 * @param player1	The first player whose fighter is being swapped.
	 * @param player2	The second player whose fighter is being swapped.
	 * 
	 * @throws IndexOutOfBoundsException	Thrown if either player is less
	 * 										than 0 or greater than the size
	 * 										of the matchup.
	 */
	public void swapFighters(int player1, int player2) throws IndexOutOfBoundsException {
		if(player1 >= fighters.length || player1 < 0) {
			throw new IndexOutOfBoundsException("Matchup was initialized with " + fighters.length +
					" fighters, but " + player1 + " was passed in as player.");
		}
		else if(player2 >= fighters.length || player2 < 0) {
			throw new IndexOutOfBoundsException("Matchup was initialized with " + fighters.length +
					" fighters, but " + player2 + " was passed in as player.");
		}
		
		Fighter temp = fighters[player1];
		fighters[player1] = fighters[player2];
		fighters[player2] = temp;
	}
	
	@Override
	public String toString() {
		StringBuilder retString = new StringBuilder(120);
		
		for(int playerAt = 0; playerAt < fighters.length; playerAt++) {
			retString.append("Player " + (playerAt + 1) + " got " +
					fighters[playerAt] + ", " +
					Util.tierToString(fighters[playerAt].getTier()) + "\n");
		}
		
		return retString.toString();
	}
	
	public String getStatsOutput() {
		StringBuilder retString = new StringBuilder(120);
		retString.append("Stats for this battle:\n");
		
		for(int playerAt = 0; playerAt < fighters.length; playerAt++) {
			Fighter fighterAt = fighters[playerAt];
			retString.append("P" + (playerAt + 1) + ": " + fighterAt.getPlayerWinrate(playerAt) +
					" (" + fighterAt.getPlayerWins(playerAt) + "/" +
					fighterAt.getPlayerBattles(playerAt) + "). Total: " +
					fighterAt.getTotalWinrate() + " (" +
					fighterAt.getTotalWins() + "/" + fighterAt.getTotalBattles() + ").\n");
		}
		
		return retString.toString();
	}
	
	public void setWinner(int player) throws IndexOutOfBoundsException {
		if(player < 0 || player >= fighters.length) {
			throw new IndexOutOfBoundsException("Matchup was initialized with " + fighters.length +
					" fighters, but " + player + " was passed in as player.");
		}
		
		//if a winner has already been selected, the old values need to be removed
		if(winnerSelected) {
			for(int playerAt = 0; playerAt < fighters.length; playerAt++) {
				if(playerAt == winner) {
					fighters[playerAt].removeWin(playerAt);
				}
				else {
					fighters[playerAt].removeLoss(playerAt);
				}
			}
		}
		
		//then record the win for the right player, and loss for everyone else
		for(int playerAt = 0; playerAt < fighters.length; playerAt++) {
			if(playerAt == player) {
				winner = player;
				fighters[playerAt].recordWin(playerAt);
			}
			else {
				fighters[playerAt].recordLoss(playerAt);
			}
		}
		
		winnerSelected = true;
	}

}
