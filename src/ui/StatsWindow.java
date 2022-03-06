package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import data.ProgramState;
import util.Util;

/**
 * This class is responsible for displaying the statistics during a matchup.
 * It also allows the user to select the winner of a battle and access the
 * lookup menu, which is handled in its own class.
 * 
 * @author Jordan Knapp
 */
public class StatsWindow {
	
	private JFrame frame;
	private JPanel upperPanel;
	private JPanel lowerPanel;
	
	private JLabel playerLabel;
	private JSpinner winnerSpinner;
	private JButton pickWinnerButton;
	private JButton lookupButton;
	private JButton reloadButton;
	
	private int selectedWinner;
	private int battleWhenLastPressed;
	private int lastSelectedWinner;
	
	private ProgramState state;
	
	public StatsWindow(MainWindow parent, ProgramState state) {
		frame = new JFrame("Smash Character Picker Stats");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(375, 435);
		frame.setResizable(false);
		
		this.state = state;
		
		frame.setLocation(parent.getX() + parent.getWidth(), parent.getY());
		frame.addWindowListener(new StatsWindowListener());
		
		upperPanel = new JPanel();
		upperPanel.setLayout(new BorderLayout());
		upperPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));
		upperPanel.add(Util.statsOutput, BorderLayout.CENTER);
		
		lowerPanel = new JPanel();
		lowerPanel.setLayout(new GridBagLayout());
		playerLabel = new JLabel("Player: ");
		SpinnerNumberModel winnerSpinnerModel = new SpinnerNumberModel(1, 1, 8, 1);
		winnerSpinner = new JSpinner(winnerSpinnerModel);
		winnerSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				selectedWinner = (int) winnerSpinner.getValue();
			}
		});
		pickWinnerButton = new JButton("Select winner");
		pickWinnerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pickWinner();
			}
		});
		
		lookupButton = new JButton("Look up stats");
		lookupButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!state.openedLookup) {
					new LookupWindow(state);
				}
			}
		});
		
		reloadButton = new JButton("⭯");
		reloadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Util.updateStatsScreen(state.numPlayers, state.stats, state.gotten);
			}
		});
		
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weighty = 1;
		gc.weightx = .1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		lowerPanel.add(playerLabel, gc);
		gc.gridx = 1;
		gc.weightx = .05;
		lowerPanel.add(winnerSpinner, gc);
		gc.gridx = 2;
		gc.weightx = .3;
		lowerPanel.add(Box.createRigidArea(new Dimension(3, 0)), gc);
		gc.gridx = 3;
		gc.weightx = .35;
		lowerPanel.add(pickWinnerButton, gc);
		gc.gridx = 4;
		gc.weightx = .25;
		lowerPanel.add(lookupButton, gc);
		gc.gridx = 5;
		lowerPanel.add(reloadButton, gc);
		
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = .98;
		gc.fill = GridBagConstraints.BOTH;
		gc.anchor = GridBagConstraints.CENTER;
		frame.setLayout(new GridBagLayout());
		JScrollPane scrollPane = new JScrollPane(upperPanel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		frame.add(scrollPane, gc);
		gc.gridy = 1;
		gc.weighty = .02;
		frame.add(lowerPanel, gc);
		
		selectedWinner = 1;
		battleWhenLastPressed = -1;
		lastSelectedWinner = -1;
		
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
		
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/Icon.png")));
		
		state.needToSaveStats = true;
		
		frame.setVisible(true);
	}
	
	/**
	 * This method updates the statistics, adding a win for the player who is
	 * selected with the spinner, and a loss for all other players currently in
	 * the session. If this battle has already had a winner selected, then the
	 * previous winner pick is reverted, first.
	 */
	private void pickWinner() {
		if(state.numBattles == battleWhenLastPressed && !(selectedWinner > state.numPlayers)) {
			state.stats.get(state.gotten.get(lastSelectedWinner - 1))[2 * (lastSelectedWinner - 1)]--;
			state.stats.get(state.gotten.get(selectedWinner - 1))[2 * (selectedWinner - 1)]++;
			lastSelectedWinner = selectedWinner;
			Util.updateStatsScreen(state.numPlayers, state.stats, state.gotten);
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
			Util.updateStatsScreen(state.numPlayers, state.stats, state.gotten);
		}
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
			
			Util.log(fighter + ": " + doubleArrString(statValues));
		}
	}
	
	/**
	 * A method that returns a better string representation of an array of
	 * <code>double</code>s than just the memory address. Given that this is
	 * only used in the context of printing things to the debug log, we're
	 * assuming that the array will be 16 entries in length.
	 * 
	 * @param arr	The array of doubles.
	 * @return		The values in the array, comma separated.
	 */
	private String doubleArrString(double[] arr) {
		String returnString = "";
		for(int at = 0; at < 14; at++) {
			returnString += arr[at] + ",";
		}
		returnString += arr[15];
		return returnString;
	}
	
	private class StatsWindowListener implements WindowListener {

		public void windowOpened(WindowEvent e) {
			Util.log("You've opened the stats panel.");
			state.openedStats = true;
		}

		public void windowClosing(WindowEvent e) {
			
		}

		public void windowClosed(WindowEvent e) {
			Util.log("You've closed the stats panel.");
			state.openedStats = false;
		}

		public void windowIconified(WindowEvent e) {

		}

		public void windowDeiconified(WindowEvent e) {

		}

		public void windowActivated(WindowEvent e) {

		}

		public void windowDeactivated(WindowEvent e) {

		}
	}

}
