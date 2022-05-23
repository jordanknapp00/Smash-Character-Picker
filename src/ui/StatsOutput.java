package ui;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JTextArea;

import util.Util;

/**
 * <code>StatsOutput</code> is a static class for controlling the output to the
 * stats portion of the UI.
 * 
 * @author Jordan Knapp
 *
 */
public final class StatsOutput {
	
	private static JTextArea statsOutput;
	
	/**
	 * This simple method only needs to be called once, it just initializes the
	 * stats <code>JTextArea</code> and sets it so it cannot be edited, as well
	 * as setting the correct font size.
	 * <br><br>
	 * This method will be called in the constructor of the
	 * <code>MainWindow</code> class, so there's no reason to ever call it at
	 * any other point in the program's execution.
	 */
	public static void initStats() {
		statsOutput = new JTextArea();
		statsOutput.setEditable(false);
		statsOutput.setFont(statsOutput.getFont().deriveFont(18f));
	}
	
	/**
	 * @return	<code>statsOutput</code>, the <code>JTextArea</code> field of
	 * 			this class, allowing it to be placed in the UI in the
	 * 			<code>MainWindow</code> class.
	 */
	public static JTextArea getTextArea() {
		return statsOutput;
	}
	
	/**
	 * Writes the given message to the stats output.
	 * 
	 * @param toPrint	The string to print.
	 */
	public static void writeToStats(String toPrint) {
		statsOutput.setText(toPrint);
	}
	
	/**
	 * This method updates the stats screen to whatever the current battle is.
	 * It is useful for this method to be accessible from any class in the
	 * program, however, it is hard to implement this method because this class
	 * is static, and this method requires using several aspects of the
	 * <code>ProgramState</code>. As a result, the necessary variables are
	 * simply passed in as parameters.
	 * 
	 * @param numPlayers
	 */
	public static void updateStatsScreen(int numPlayers, HashMap<String, double[]> stats, ArrayList<String> gotten) {
		statsOutput.setText("");
		statsOutput.append("Stats for this battle:\n");
		for(int at = 0; at < numPlayers; at++) {
			double[] statsForFighter = stats.get(gotten.get(at));
			double playerBattlesWon = statsForFighter[2 * at];
			double playerBattlesFought = statsForFighter[2 * at  + 1];
			double totalBattlesWon = 0;
			double totalBattlesFought = 0;
			for(int pAt = 0; pAt < 8; pAt++) {
				totalBattlesWon += statsForFighter[2 * pAt];
				totalBattlesFought += statsForFighter[2 * pAt + 1];
			}
			statsOutput.append("P" + (at + 1) + ": " + 
						Util.printDouble((playerBattlesWon / playerBattlesFought) * 100) + 
						"% (" + (int)playerBattlesWon + "/" + (int)playerBattlesFought + "). Total: " + 
						Util.printDouble((totalBattlesWon / totalBattlesFought) * 100) + "% (" + 
						(int)totalBattlesWon + "/" + (int)totalBattlesFought + ").\n");
		}
	}
}
