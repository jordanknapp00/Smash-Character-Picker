package data;

import util.Util;

/**
 * The <code>Fighter</code> class allows us to keep track of the fighters in
 * a tier list. Before v12, we simply kept lists of <code>String</code>s to
 * track fighters, but given that a fighter has both a name and a tier, it
 * makes more sense to have an object for it.
 * <br><br>
 * We also use this class to keep track of stats data. Each player's wins
 * and losses are kept here, and various methods are provided for accessing
 * that data.
 * 
 * @author Jordan Knapp
 */
public class Fighter {
	
	private String name;
	private int tier;
	
	private int[] playerWins;
	private int[] playerBattles;
	
	/**
	 * Creates a new <code>Fighter</code> with the specified name and tier.
	 * 
	 * @param name	The name of the <code>Fighter</code> to create.
	 * @param tier	The tier of the <code>Fighter</code> to create.
	 */
	public Fighter(String name, int tier) {
		this.name = name;
		this.tier = tier;
		
		playerWins = new int[8];
		playerBattles = new int[8];
		
		for(int at = 0; at < 8; at++) {
			playerWins[at] = 0;
			playerBattles[at] = 0;
		}
	}
	
	/**
	 * Creates a new <code>Fighter</code> with a given name and tier as well
	 * as stats data. Used when stats data is loaded, so the <code>Fighter</code>
	 * object can have the stats found in the <code>.sel</code> file.
	 * 
	 * @param name	The name of the <code>Fighter</code> to create.
	 * @param tier	The tier of the <code>Fighter</code> to create.
	 * @param stats	The statistics of this fighter. Should be a single array
	 * 				with 16 values -- each player's wins and battles. The
	 * 				ordering should be as follows: P1 wins, P1 battles, P2
	 * 				wins, P2 battles, and so on.
	 */
	public Fighter(String name, int tier, double[] stats) {
		this(name, tier);
		
		for(int at = 0; at < 8; at++) {
			playerWins[at] = (int) stats[at * 2];
			playerBattles[at] = (int) stats[at * 2 + 1];
		}
	}
	
	/**
	 * @return	The name of this <code>Fighter</code>.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return	The tier of this <code>Fighter</code>.
	 */
	public int getTier() {
		return tier;
	}
	
	/**
	 * Compares two <code>Fighter</code>s to determine if they are equa. Two
	 * <code>Fighter</code>s are considered equal if they have the same name
	 * and tier.
	 */
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Fighter)) {
			return false;
		}
		
		Fighter other = (Fighter) o;
		
		return (name == other.getName()) && (tier == other.getTier());
	}
	
	/**
	 * Returns a string representation of the <code>Fighter</code>, which in
	 * this case is just its name.
	 */
	@Override
	public String toString() {
		return name;
	}
	
	/**
	 * Gets the number of wins a player has with this fighter.
	 * 
	 * @param player	The player whose wins will be returned.
	 * @return			The number of battles that the given player has won
	 * 					when playing this <code>Fighter</code>.
	 */
	public int getPlayerWins(int player) {
		return playerWins[player];
	}
	
	/**
	 * Gets the number of battles a player has with this fighter.
	 * 
	 * @param player	The player whose battles will be returned.
	 * @return			The number of battles that this player has participated
	 * 					in when playing this <code>Fighter</code>.
	 */
	public int getPlayerBattles(int player) {
		return playerBattles[player];
	}
	
	/**
	 * Gets the winrate of a player for this fighter in <code>String</code>
	 * format.
	 * 
	 * @param player	The player whose winrate will be returned.
	 * @return			The player's winrate when playing as this
	 * 					<code>Fighter</code>, defined as the number of wins
	 * 					divided by the number of battles, multiplied by 100.
	 * 					A percentage sign ("%") is also appended to the end.
	 */
	public String getPlayerWinrate(int player) {
		return Util.printDouble(((float) playerWins[player] / playerBattles[player]) * 100) + "%";
	}
	
	/**
	 * @return	The total number of wins this fighter has across all battles
	 * 			it has participated in.
	 */
	public int getTotalWins() {
		int sum = 0;
		
		for(int at: playerWins) {
			sum += at;
		}
		
		return sum;
	}
	
	/**
	 * @return	The total number of battles this fighter has participated in.
	 */
	public int getTotalBattles() {
		int sum = 0;
		
		for(int at: playerBattles) {
			sum += at;
		}
		
		return sum;
	}
	
	/**
	 * Gets the total winrate of this fighter across all battles in
	 * <code>String</code> format.
	 * 
	 * @return	This fighter's total winrate across all battles it has
	 * 			participated in. Defined as the result of <code>getTotalWins()</code>
	 * 			divided by <code>getTotalBattles()</code>, multiplied by 100.
	 * 			A percentage sign ("%") is also appended to the end.
	 */
	public String getTotalWinrate() {
		return Util.printDouble(((float) getTotalWins() / getTotalBattles()) * 100) + "%";
	}
	
	/**
	 * Records a win for the specified player by incrementing that player's
	 * win and battle count by one.
	 * 
	 * @param player	The player who won as this fighter.
	 */
	public void recordWin(int player) {
		playerWins[player]++;
		playerBattles[player]++;
	}
	
	/**
	 * Records a loss for the specified player by incrementing that player's
	 * battle count, but not their win count.
	 * 
	 * @param player	The player who lost as this fighter.
	 */
	public void recordLoss(int player) {
		playerBattles[player]++;
	}
	
	/**
	 * Removes a win for the specified player by decrementing their win and
	 * battle count by one. Used when a different winner is picked for a
	 * battle -- the winner's win is removed and given to another player.
	 * 
	 * @param player	The player whose win is being removed.
	 */
	public void removeWin(int player) {
		playerWins[player]--;
		playerBattles[player]--;
	}
	
	/**
	 * Removes a loss for the specified player by decrementing their loss
	 * count by one. Used when a different winner is picked for a battle --
	 * everyone but the original winner has their losses removed, with the
	 * win being given to another player.
	 * 
	 * @param player	The player whose loss is being removed.
	 */
	public void removeLoss(int player) {
		playerBattles[player]--;
	}
	
	/**
	 * Gets a <code>String</code> representing this fighter's stats data
	 * for all players. Used by the sorting feature. The fighter's name,
	 * all 8 players' winrate, as well as the overall winrate are displayed.
	 * Along with the winrate, the win-loss ratio is also printed directly
	 * as a fraction.
	 * 
	 * @return	A <code>String</code> representing this fighter's stats data.
	 */
	public String getStatsData() {
		StringBuffer retString = new StringBuffer(225);
		retString.append("Stats for " + name + ":\n");
		
		for(int at = 0; at < 8; at++) {
			retString.append("Player " + (at + 1) + ": " + getPlayerWinrate(at) + 
					" (" + getPlayerWins(at) + "/" + getPlayerBattles(at) + ")\n");
		}
		
		retString.append("Overall: " + getTotalWinrate() + " (" +
				getTotalWins() + "/" + getTotalBattles() + ")");
		
		return retString.toString();
	}

}
