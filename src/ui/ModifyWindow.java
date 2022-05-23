package ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import data.ProgramState;
import picker.StatsManager;
import util.Util;

public class ModifyWindow {
	
	private JFrame frame;
	private JPanel panel;
	
	private JLabel playerLabel;
	private JSpinner player;
	private JLabel winsLabel;
	private JSpinner wins;
	private JLabel battlesLabel;
	private JSpinner battles;
	private JTextField rename;
	private JButton renameButton;
	private JButton resetAll;
	private JButton remove;
	private JButton apply;
	
	private JLabel warningLabel;
	
	private double[] fighterStats;
	private int playerSelected;
	private String newName;
	private double[] newValues;
	private String oldName;
	
	private ProgramState state;
	
	public ModifyWindow(ProgramState state, StatsManager statsManager, String toModify) {
		this.state = state;
		
		if(!state.stats.containsKey(toModify)) {
			statsManager.writeToStats("Fighter " + toModify + " not found!");
			return;
		}
		
		playerSelected = 1;
		fighterStats = state.stats.get(toModify);
		newName = oldName = toModify;
		newValues = new double[16];
		
		for(int at = 0; at < 16; at++) {
			newValues[at] = fighterStats[at];
		}
		
		frame = new JFrame("Modify");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(250, 230);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		
		panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Modify " + toModify + " stats"));
		
		warningLabel = new JLabel("<html>WARNING: Once you hit apply,<br>changes are permanent!</html>");
		
		playerLabel = new JLabel("Select player: ");
		SpinnerNumberModel playerModel = new SpinnerNumberModel(1, 1, 8, 1);
		player = new JSpinner(playerModel);
		player.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				playerSelected = (int) player.getValue();
				wins.setValue(newValues[2 * (playerSelected - 1)]);
				battles.setValue(newValues[2 * (playerSelected - 1) + 1]);
			}
		});
		
		winsLabel = new JLabel("Adjust number of wins: ");
		SpinnerNumberModel winsModel = new SpinnerNumberModel(fighterStats[0], 0, 9999, 1);
		wins = new JSpinner(winsModel);
		wins.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				newValues[2 * (playerSelected - 1)] = (double) wins.getValue();
			}
		});
		
		battlesLabel = new JLabel("Adjust number of battles: ");
		SpinnerNumberModel battleModel = new SpinnerNumberModel(fighterStats[1], 0, 9999, 1);
		battles = new JSpinner(battleModel);
		battles.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				newValues[2 * (playerSelected - 1) + 1] = (double) battles.getValue();
			}
		});
		
		rename = new JTextField(16);
		renameButton = new JButton("Rename");
		rename.addActionListener(new RenameActionListener());
		renameButton.addActionListener(new RenameActionListener());
		
		resetAll = new JButton("Reset all stats");
		resetAll.addActionListener(new ResetAllActionListener());
		remove = new JButton("Remove character");
		remove.addActionListener(new RemoveActionListener());
		apply = new JButton("Apply changes");
		apply.addActionListener(new ApplyActionListener());
		
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.anchor = GridBagConstraints.LINE_START;
		gc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(playerLabel, gc);
		gc.gridx = 1;
		panel.add(player, gc);
		gc.gridx = 0;
		gc.gridy = 1;
		panel.add(winsLabel, gc);
		gc.gridx = 1;
		panel.add(wins, gc);
		gc.gridx = 0;
		gc.gridy = 2;
		panel.add(battlesLabel, gc);
		gc.gridx = 1;
		panel.add(battles, gc);
		gc.gridx = 0;
		gc.gridy = 3;
		panel.add(rename, gc);
		gc.gridx = 1;
		panel.add(renameButton, gc);
		gc.gridx = 0;
		gc.gridy = 4;
		gc.gridwidth = 2;
		panel.add(resetAll, gc);
		gc.gridy = 5;
		panel.add(remove, gc);
		gc.gridy = 6;
		panel.add(apply, gc);
		gc.gridy = 7;
		gc.anchor = GridBagConstraints.CENTER;
		panel.add(warningLabel, gc);
		
		frame.add(panel);
		
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/Icon.png")));
		
		frame.addWindowListener(new ModWindowListener());
		
		frame.setVisible(true);
	}
	
	private class ApplyActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			//check for validity -- there can't be more wins than battles
			for(int at = 0; at < 15; at += 2) {
				if(newValues[at] > newValues[at + 1]) {
					JOptionPane.showMessageDialog(null, "There cannot be more wins than battles.",
							"Smash Character Picker", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			
			state.stats.remove(oldName);
			state.stats.put(newName, newValues);
			JOptionPane.showMessageDialog(null, "Stats updated. You can close the mod window now.",
					"Smash Character Picker", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	private class RemoveActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(JOptionPane.showConfirmDialog(null, "Are you sure you want to remove " + oldName + " from the system?", 
					"Smash Character Picker", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)
					== JOptionPane.YES_OPTION) {
				state.stats.remove(oldName);
				JOptionPane.showMessageDialog(null, oldName + " removed. You can close the mod window now.",
						"Smash Character Picker", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}
	
	private class ResetAllActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			newName = oldName;
			double[] oldStats = state.stats.get(oldName);
			for(int at = 0; at < 16; at++) {
				newValues[at] = oldStats[at];
			}
			wins.setValue(newValues[2 * (playerSelected - 1)]);
			battles.setValue(newValues[2 * (playerSelected - 1) + 1]);
		}
	}
	
	private class RenameActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String toChange = rename.getText();
			
			if(state.stats.containsKey(toChange)) {
				JOptionPane.showMessageDialog(null, "There's already a character called " + toChange + ". "
						+ "Characters cannot have duplicate names", "Smash Character Picker",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			else {
				newName = toChange;
				rename.setText("");
			}
		}
	}
	
	private class ModWindowListener implements WindowListener {

		public void windowOpened(WindowEvent e) {
			Util.log("You've opened the mod window.");
			state.openedModify = true;
		}

		public void windowClosing(WindowEvent e) {

		}

		public void windowClosed(WindowEvent e) {
			Util.log("You've closed the mod panel.");
			state.openedModify = false;
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
