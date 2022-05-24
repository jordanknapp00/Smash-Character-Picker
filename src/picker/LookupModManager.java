package picker;

import java.util.Arrays;

import data.ComparableArray;
import data.ProgramState;
import util.Util;

public class LookupModManager {
	
	private ProgramState state;
	private StatsManager statsManager;
	
	public LookupModManager(ProgramState state, StatsManager statsManager) {
		this.state = state;
		this.statsManager = statsManager;
	}
	
	public void sort(int selectedOption) {
		String toOutput = "";
		ComparableArray[] data = new ComparableArray[state.stats.size()];
		switch(selectedOption) {
			case 0:
				toOutput += "Sorted by fighters' overall win rate:\n";
				data = getTotalStats(3);
				break;
			case 1:
				toOutput += "Players' overall win rate:\n";
				data = getPlayerWinrateStats();
				for(int at = 0; at < 8; at++) {
					toOutput += data[at] + "\n";
				}
				
				statsManager.writeToStats(toOutput);
				return;
			case 2:
				toOutput += "Sorted by Player 1's win rate:\n";
				data = getPlayerStats(0);
				break;
			case 3:
				toOutput += "Sorted by Player 2's win rate:\n";
				data = getPlayerStats(1);
				break;
			case 4:
				toOutput += "Sorted by Player 3's win rate:\n";
				data = getPlayerStats(2);
				break;
			case 5:
				toOutput += "Sorted by Player 4's win rate:\n";
				data = getPlayerStats(3);
				break;
			case 6:
				toOutput += "Sorted by Player 5's win rate:\n";
				data = getPlayerStats(4);
				break;
			case 7:
				toOutput += "Sorted by Player 6's win rate:\n";
				data = getPlayerStats(5);
				break;
			case 8:
				toOutput += "Sorted by Player 7's win rate:\n";
				data = getPlayerStats(6);
				break;
			case 9:
				toOutput += "Sorted by Player 8's win rate:\n";
				data = getPlayerStats(7);
				break;
			case 10:
				toOutput += "Sorted by total battles:\n";
				data = getTotalStats(2);
				Arrays.sort(data);
				data = Util.reverse(data);
				int at = 1;
				for(ComparableArray arrAt: data) {
					toOutput += at + ". " + arrAt.getName() + " - " + arrAt.getBattles() + " battles\n";
					at++;
				}
				
				statsManager.writeToStats(toOutput);
				return;
			default:
				toOutput += "Error, unrecognized sort ID.\n";
				return;
		}
		
		Arrays.sort(data);
		data = Util.reverse(data);
		int at = 1;
		for(ComparableArray arrAt: data) {
			toOutput += at + ". " + arrAt + "\n";
			at++;
		}
		
		statsManager.writeToStats(toOutput);
	}
	
	private ComparableArray[] getPlayerWinrateStats() {
		ComparableArray[] data = new ComparableArray[8];
		for(int indexAt = 0; indexAt < 8; indexAt++) {
			double totalWins = 0;
			double totalBattles = 0;
			for(String fighter: state.stats.keySet()) {
				double[] fighterStats = state.stats.get(fighter);
				totalWins += fighterStats[2 * indexAt];
				totalBattles += fighterStats[2 * indexAt + 1];
			}
			data[indexAt] = new ComparableArray("P" + (indexAt + 1) + " W%", totalWins, totalBattles, 3);
		}
		
		return data;
	}
	
	private ComparableArray[] getTotalStats(int column) {
		ComparableArray[] data = new ComparableArray[state.stats.size()];
		int indexAt = 0;
		for(String fighter: state.stats.keySet()) {
			double totalWins = 0;
			double totalBattles = 0;
			double[] fighterStats = state.stats.get(fighter);
			for(int at = 0; at < 8; at++) {
				totalWins += fighterStats[2 * at];
				totalBattles += fighterStats[2 * at + 1];
			}
			data[indexAt] = new ComparableArray(fighter, totalWins, totalBattles, column);
			indexAt++;
		}
		
		return data;
	}
	
	private ComparableArray[] getPlayerStats(int player) {
		ComparableArray[] data = new ComparableArray[state.stats.size()];
		int at = 0;
		for(String fighter: state.stats.keySet()) {
			double[] fighterStats = state.stats.get(fighter);
			data[at] = new ComparableArray(fighter, fighterStats[2 * player], fighterStats[2 * player + 1], 3);
			at++;
		}
		
		return data;
	}
	
	public void lookup(String lookup) {
		String toOutput = "";
		if(state.stats.containsKey(lookup)) {
			toOutput += "Stats for " + lookup + ":\n";
			double[] fighterStats = state.stats.get(lookup);
			double totalWins = 0;
			double totalFights = 0;
			for(int at = 0; at < 8; at++) {
				totalWins += fighterStats[2 * at];
				totalFights += fighterStats[2 * at + 1];
				toOutput += "Player " + (at + 1) + ": " + Util.printDouble((fighterStats[2 * at] / fighterStats[2 * at + 1]) * 100) + "% (" + (int)fighterStats[2 * at] + "/" + (int)fighterStats[2 * at + 1] + ")\n";
			}
			toOutput += "Overall: " + Util.printDouble((totalWins / totalFights) * 100) + "% (" + (int)totalWins + "/" + (int)totalFights + ")\n";
		}
		else if(lookup != null) {
			toOutput += "Fighter " + lookup + " not found!\n";
		}
		
		statsManager.writeToStats(toOutput);
	}

}
