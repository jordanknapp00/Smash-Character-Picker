package picker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import data.ProgramState;
import util.Util;

/**
 * <code>StatsManager</code> is responsible for managing the stat-tracking
 * system. It handles the logic for selecting a winner and updating the
 * internal stats <code>HashMap</code>. It also contains the methods that are
 * responsible for updating the stats UI.
 * 
 * @author Jordan Knapp
 *
 */
public class StatsManager {
	
	private int selectedWinner;
	private int battleWhenLastPressed;
	private int lastSelectedWinner;
	
	private JTextArea statsOutput;
	
	private ProgramState state;
	
	public StatsManager(ProgramState state) {
		statsOutput = new JTextArea();
		statsOutput.setEditable(false);
		statsOutput.setFont(statsOutput.getFont().deriveFont(18f));
		
		selectedWinner = 1;
		battleWhenLastPressed = -1;
		lastSelectedWinner = -1;
		
		this.state = state;
	}
	
	/**
	 * This method updates the statistics, adding a win for the player who is
	 * selected with the spinner, and a loss for all other players currently in
	 * the session. If this battle has already had a winner selected, then the
	 * previous winner pick is reverted, first.
	 */
	public void pickWinner() {
		if(state.numBattles == battleWhenLastPressed && !(selectedWinner > state.numPlayers)) {
			state.stats.get(state.gotten.get(lastSelectedWinner - 1))[2 * (lastSelectedWinner - 1)]--;
			state.stats.get(state.gotten.get(selectedWinner - 1))[2 * (selectedWinner - 1)]++;
			lastSelectedWinner = selectedWinner;
			updateStatsScreen();
		}
		else if(state.numBattles == 0) {
			JOptionPane.showMessageDialog(null, "There has to be a battle before there can be a winner.",
					"Smash Character Picker", JOptionPane.WARNING_MESSAGE);
			return;
		}
		else if(selectedWinner > state.numPlayers) {
			JOptionPane.showMessageDialog(null, "Player " + selectedWinner + " cannot win, there are only "
					+ state.numPlayers + " players.", "Smash Character Picker", JOptionPane.WARNING_MESSAGE);
		}
		else {
			battleWhenLastPressed = state.numBattles;
			lastSelectedWinner = selectedWinner;
			for(int at = 0; at < state.numPlayers; at++) {
				if((at + 1) == selectedWinner) {
					state.stats.get(state.gotten.get(at))[2 * at]++;
					state.stats.get(state.gotten.get(at))[2 * at + 1]++;
				}
				else {
					state.stats.get(state.gotten.get(at))[2 * at + 1]++;
				}
			}
			updateStatsScreen();
		}
	}
	
	/**
	 * @return	<code>statsOutput</code>, the <code>JTextArea</code> field of
	 * 			this class, allowing it to be placed in the UI in the
	 * 			<code>MainWindow</code> class.
	 */
	public JTextArea getTextArea() {
		return statsOutput;
	}
	
	/**
	 * Writes the given message to the stats output.
	 * 
	 * @param toPrint	The string to print.
	 */
	public void writeToStats(String toPrint) {
		statsOutput.setText(toPrint);
	}
	
	/**
	 * This method updates the stats screen to whatever the current battle is.
	 */
	public void updateStatsScreen() {
		//do nothing if there isn't a battle yet
		if(state.numBattles < 1) {
			return;
		}
		
		statsOutput.setText("");
		statsOutput.append("Stats for this battle:\n");
		for(int at = 0; at < state.numPlayers; at++) {
			double[] statsForFighter = state.stats.get(state.gotten.get(at));
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
	
	/**
	 * Set the <code>selectedWinner</code> variable to a given value. Called
	 * whenever the spinner in the UI is changed.
	 * 
	 * @param toSet	The new value that <code>selectedWinner</code> is set to.
	 */
	public void setSelectedWinner(int toSet) {
		selectedWinner = toSet;
	}
	
	/**
	 * Attempt to load the stats file. Only called once the tier list file has
	 * been loaded.
	 */
	public void attemptLoad()
	{
		state.statsFile = new File("smash stats.sel");
		try {
			if(state.statsFile.createNewFile()) {
				JOptionPane.showMessageDialog(null, "A stats file has not been detected in this folder. "
						+ "One will now be created.", "Smash Character Picker",
						JOptionPane.INFORMATION_MESSAGE);
				for(int at = 0; at < 24; at++) {
					for(String fighter: state.linesOfFile.get(at)) {
						double[] freshDouble = new double[] {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
						state.stats.put(fighter, freshDouble);
					}
				}
			}
			else {
				loadFile(state.statsFile);
			}
		} catch(IOException e) {
			Util.error(e);
		} catch(ClassNotFoundException e) {
			Util.error(e);
		}
		
		state.needToSaveStats = true;
	}
	
	/**
	 * This method is responsible for actually loading a file. The file should
	 * contain a <code>HashMap</code> object which can be loaded using an
	 * <code>ObjectInputStream</code>. If this is not the case, an exception is
	 * thrown.
	 * 
	 * If the file is successfully loaded, the tier list is scanned to see if
	 * there are any fighters present who are not currently in the stats system.
	 * If this is the case, they will be added to the system.
	 * 
	 * @param fileToLoad				The file to load.
	 * @throws IOException				Thrown if the file is not found, or if
	 * 									any other error happens while opening
	 * 									the file.
	 * @throws ClassNotFoundException	Thrown if the loaded file does not
	 * 									contain a <code>HashMap</code> object.
	 */
	@SuppressWarnings("unchecked")
	private void loadFile(File fileToLoad) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(fileToLoad);
		ObjectInputStream ois = new ObjectInputStream(fis);
		state.stats = (HashMap<String, double[]>) ois.readObject();
		ois.close();
		fis.close();
		
		//check if there are any fighters in the tier list not currently in the
		//system.
		for(int at = 0; at < 24; at++) {
			for(String fighter: state.linesOfFile.get(at)) {
				if(!state.stats.containsKey(fighter)) {
					Util.log(fighter + " was not found in the system. Adding them.");
					double[] freshDouble = new double[] {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
					state.stats.put(fighter, freshDouble);
				}
			}
		}
		
		Util.log("The following data has been loaded into the stats system:");
		for(String fighter: state.stats.keySet()) {
			double[] statValues = state.stats.get(fighter);
			
			Util.log(fighter + ": " + Util.doubleArrString(statValues));
		}
	}

}
