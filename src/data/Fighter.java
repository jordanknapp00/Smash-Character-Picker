package data;

import util.Util;

//TODO: add javadoc for stats once it's more fleshed out

/**
 * The <code>Fighter</code> class allows us to keep track of the fighters in
 * a tier list. Before v12, we simply kept lists of <code>String</code>s to
 * track fighters, but given that a fighter has both a name and a tier, it
 * makes more sense to have an object for it.
 * 
 * @author Jordan Knapp
 *
 */
public class Fighter {
	
	private String name;
	private int tier;
	
	private int[] playerWins;
	private int[] playerBattles;
	
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
	
	public Fighter(String name, int tier, double[] stats) {
		this(name, tier);
		
		for(int at = 0; at < 8; at++) {
			playerWins[at] = (int) stats[at * 2];
			playerBattles[at] = (int) stats[at * 2 + 1];
		}
	}
	
	public String getName() {
		return name;
	}
	
	public int getTier() {
		return tier;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Fighter)) {
			return false;
		}
		
		Fighter other = (Fighter) o;
		
		return (name == other.getName()) && (tier == other.getTier());
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public int getPlayerWins(int player) {
		return playerWins[player];
	}
	
	public int getPlayerBattles(int player) {
		return playerBattles[player];
	}
	
	public String getPlayerWinrate(int player) {
		return Util.printDouble(((float) playerWins[player] / playerBattles[player]) * 100) + "%";
	}
	
	public int getTotalWins() {
		int sum = 0;
		
		for(int at: playerWins) {
			sum += at;
		}
		
		return sum;
	}
	
	public int getTotalBattles() {
		int sum = 0;
		
		for(int at: playerBattles) {
			sum += at;
		}
		
		return sum;
	}
	
	public String getTotalWinrate() {
		return Util.printDouble(((float) getTotalWins() / getTotalBattles()) * 100) + "%";
	}
	
	public void recordWin(int player) {
		playerWins[player]++;
		playerBattles[player]++;
	}
	
	public void recordLoss(int player) {
		playerBattles[player]++;
	}
	
	public void removeWin(int player) {
		playerWins[player]--;
		playerBattles[player]--;
	}
	
	public void removeLoss(int player) {
		playerBattles[player]--;
	}
	
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
