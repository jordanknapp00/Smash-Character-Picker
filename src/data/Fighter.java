package data;

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
	
	public double getPlayerWinrate(int player) {
		return playerWins[player] / playerBattles[player];
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
	
	public int getTotalWinrate() {
		return getTotalWins() / getTotalBattles();
	}

}
