package picker;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;

enum LowTierState {NO_LOW_TIERS, USE_PLAYABLE_LOW_TIERS, JUST_D_TIER, D_AND_E_TIER, ALL_LOW_TIERS};

public class Picker {
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Picker p = new Picker();
			}
		});
	}
	
	//frame and panels
	private JFrame frame;
	private JPanel leftPanel;
	private JPanel rightPanel;
	private JPanel leftUpperPanel;
	private JPanel leftLowerPanel;
	
	//menu bar components
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu helpMenu;
	private JMenuItem loadButton;
	private JMenuItem closeButton;
	private JMenuItem formatButton;
	private JMenuItem aboutButton;
	
	//settings components
	private JLabel numPlayersLabel;
	private JLabel forceTier;
	private JRadioButton noLowTiersButton;
	private JRadioButton usePlayableLowTiers;
	private JRadioButton justDTier;
	private JRadioButton useDandETier;
	private JRadioButton allLowTiers;
	private ButtonGroup settingsButtons;
	private JSpinner numPlayersSpinner;
	private JComboBox forceTierSelector;
	private JButton additionalSettingsButton;
	
	//generate button and text area
	private JButton generateButton;
	private JTextArea results;
	
	//other vars
	private boolean fileLoaded;
	private int numPlayers;
	private ArrayList<ArrayList<String>> linesOfFile;
	private int battles;
	private LowTierState currentLowTierState;
	private int tierToForce;
	private int[] tierSizes;
	private ArrayList<ArrayDeque<String>> cannotGet;
	
	private static Random rand;
	
	public Picker() {
		//initialize frame
		frame = new JFrame("Smash Character Picker 2.0");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 300);
		
		rightPanel = new JPanel();
		Dimension panelSize = new Dimension();
		panelSize.width = 225;
		rightPanel.setPreferredSize(panelSize);
		rightPanel.setBorder(BorderFactory.createTitledBorder("Results"));
		rightPanel.setLayout(new GridBagLayout());
		GridBagConstraints rightGC = new GridBagConstraints();
		rightGC.weightx = 1;
		rightGC.weighty = 1;
		rightGC.fill = GridBagConstraints.BOTH;
		results = new JTextArea();
		results.setEditable(false);
		rightPanel.add(results, rightGC);
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		//initialize panels
		leftPanel = new JPanel();
		panelSize = leftPanel.getPreferredSize();
		panelSize.width = 200;
		leftPanel.setPreferredSize(panelSize);
		leftPanel.setBorder(BorderFactory.createTitledBorder("Settings"));
		leftPanel.setLayout(new GridBagLayout());
		GridBagConstraints leftGC = new GridBagConstraints();
		
		leftUpperPanel = new JPanel();
		leftLowerPanel = new JPanel();
		leftUpperPanel.setLayout(new GridBagLayout());
		leftLowerPanel.setLayout(new GridBagLayout());
		GridBagConstraints ulgc = new GridBagConstraints();
		GridBagConstraints llgc = new GridBagConstraints();
		
		//left panel items
		noLowTiersButton = new JRadioButton("No tiers below C");
		noLowTiersButton.addActionListener(new NoLowTiersActionListener());
		usePlayableLowTiers = new JRadioButton("Use \"Playable Low Tiers\"");
		usePlayableLowTiers.addActionListener(new UsePlayableLowTiersActionListener());
		justDTier = new JRadioButton("Allow D tier and above");
		justDTier.addActionListener(new JustDTierActionListener());
		useDandETier = new JRadioButton("Allow D & E tiers and above");
		useDandETier.addActionListener(new UseDandETierActionListener());
		allLowTiers = new JRadioButton("Allow all low tiers");
		allLowTiers.addActionListener(new AllLowTiersButtonActionListener());
		settingsButtons = new ButtonGroup();
		settingsButtons.add(noLowTiersButton);
		settingsButtons.add(usePlayableLowTiers);
		settingsButtons.add(justDTier);
		settingsButtons.add(useDandETier);
		settingsButtons.add(allLowTiers);
		noLowTiersButton.setSelected(true);
		SpinnerNumberModel actualSpinner = new SpinnerNumberModel(2, 2, 8, 1);
		numPlayersSpinner = new JSpinner(actualSpinner);
		numPlayersSpinner.addChangeListener(new NumPlayersChangeListener());
		String[] options = {"Do not force tier", "S tier", "A tier", "B tier", "C tier", "D tier", "E tier", "F tier"};
		forceTierSelector = new JComboBox(options);
		forceTierSelector.setSelectedIndex(0);
		forceTierSelector.addActionListener(new ForceTierSelectorActionListener());
		numPlayersLabel = new JLabel("Number of players: ");
		forceTier = new JLabel("Force tier: ");
		additionalSettingsButton = new JButton("Additional Settings");
		additionalSettingsButton.addActionListener(new AdditionalSettingsButtonActionListener());
		
		//actually add left panel items
		//top panel column 1 (labels)
		ulgc.gridx = 0;
		ulgc.gridy = 0;
		ulgc.weightx = .6;
		ulgc.anchor = GridBagConstraints.LINE_END;
		leftUpperPanel.add(numPlayersLabel, ulgc);
		ulgc.gridy = 1;
		leftUpperPanel.add(forceTier, ulgc);
		//top panel column 2 (controls)
		ulgc.gridx = 1;
		ulgc.gridy = 0;
		ulgc.weightx = .4;
		ulgc.anchor = GridBagConstraints.LINE_START;
		leftUpperPanel.add(numPlayersSpinner, ulgc);
		ulgc.gridy = 1;
		leftUpperPanel.add(forceTierSelector, ulgc);
		
		//bottom panel
		llgc.gridx = 0;
		llgc.gridy = 0;
		leftLowerPanel.add(noLowTiersButton, llgc);
		llgc.gridy = 1;
		leftLowerPanel.add(usePlayableLowTiers, llgc);
		llgc.gridy = 2;
		leftLowerPanel.add(justDTier, llgc);
		llgc.gridy = 3;
		leftLowerPanel.add(useDandETier, llgc);
		llgc.gridy = 4;
		leftLowerPanel.add(allLowTiers, llgc);
		llgc.gridy = 5;
		leftLowerPanel.add(additionalSettingsButton, llgc);
		
		leftGC.gridx = 0;
		leftGC.gridy = 0;
		leftGC.anchor = GridBagConstraints.CENTER;
		leftGC.fill = GridBagConstraints.BOTH;
		leftPanel.add(leftUpperPanel, leftGC);
		leftGC.gridy = 1;
		leftPanel.add(leftLowerPanel, leftGC);
		
		//initialize menu bar
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		helpMenu = new JMenu("Help");
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);
		loadButton = new JMenuItem("Load tier list");
		closeButton = new JMenuItem("Exit");
		formatButton = new JMenuItem("How to format a tier list file");
		aboutButton = new JMenuItem("About");
		closeButton.addActionListener(new ExitButtonActionListener());
		loadButton.addActionListener(new LoadButtonActionListener());
		formatButton.addActionListener(new FormatButtonActionListener());
		aboutButton.addActionListener(new AboutButtonActionListener());
		fileMenu.add(loadButton);
		fileMenu.add(closeButton);
		helpMenu.add(formatButton);
		helpMenu.add(aboutButton);
		frame.setJMenuBar(menuBar);
		
		//initialize generate button
		generateButton = new JButton("Generate");
		generateButton.addActionListener(new GenerateButtonActionListener());
		
		//initialize other vars
		fileLoaded = false;
		linesOfFile = new ArrayList<ArrayList<String>>();
		cannotGet = new ArrayList<ArrayDeque<String>>();
		for(int x = 0; x < 8; x++) {
			cannotGet.add(new ArrayDeque<String>());
		}
		battles = 0;
		numPlayers = 2;
		tierToForce = -1;
		currentLowTierState = LowTierState.NO_LOW_TIERS;
		rand = new Random();
		tierSizes = new int[9];
		
		//put it all together
		frame.getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints frameGC = new GridBagConstraints();
		frameGC.weightx = .75;
		frameGC.weighty = .98;
		frameGC.gridx = 0;
		frameGC.gridy = 0;
		frameGC.anchor = GridBagConstraints.LINE_START;
		frameGC.fill = GridBagConstraints.BOTH;
		frame.getContentPane().add(leftPanel, frameGC);
		frameGC.weightx = .25;
		frameGC.gridx = 1;
		frameGC.anchor = GridBagConstraints.LINE_END;
		frame.getContentPane().add(rightPanel, frameGC);
		frameGC.gridx = 0;
		frameGC.gridy = 1;
		frameGC.weighty = .02;
		frameGC.anchor = GridBagConstraints.CENTER;
		frameGC.fill = GridBagConstraints.HORIZONTAL;
		frame.getContentPane().add(generateButton, frameGC);
		
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/Icon.png")));
		
		frame.setVisible(true);
	}
	
	//action listener for additionalSettingsButton
	private class AdditionalSettingsButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			SettingsWindow s = new SettingsWindow();
		}
	}
	
	//action listener for forceTierSelector
	private class ForceTierSelectorActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			tierToForce = forceTierSelector.getSelectedIndex();
		}
	}
	
	//action listener for noLowTiersButton
	private class NoLowTiersActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			currentLowTierState = LowTierState.NO_LOW_TIERS;
		}
	}

	//action listener for usePlayableTiersButton
	private class UsePlayableLowTiersActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			currentLowTierState = LowTierState.USE_PLAYABLE_LOW_TIERS;
		}
	}
	
	//action listener for justDTier button
	private class JustDTierActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			currentLowTierState = LowTierState.JUST_D_TIER;
		}
	}
	
	//action listener for useDandETier button
	private class UseDandETierActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			currentLowTierState = LowTierState.D_AND_E_TIER;
		}
	}
	
	//action listener for allLowTiers button
	private class AllLowTiersButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			currentLowTierState = LowTierState.ALL_LOW_TIERS;
		}
	}
	
	//change listener for numplayers spinner
	private class NumPlayersChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			numPlayers = (int) numPlayersSpinner.getValue();
		}
	}
	
	//action listener for generate button
	private class GenerateButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(fileLoaded) {
				int lines = results.getLineCount();
				int end;
				try {
					end = results.getLineEndOffset(lines - 1);
					results.replaceRange("", 0, end);
				} catch (BadLocationException e1) {}
				battles++;
				results.append("Battle #" + battles + ":\n");				
				//choose tier
				int[] playerTiers = new int[numPlayers];
				if(tierToForce == -1) {
					playerTiers = getPlayerTiers();
					//ensure validity of tiers
					boolean alreadyGotDoubleSTier = false;
					for(int at = 0; at < numPlayers; at++) {
						if(currentLowTierState == LowTierState.NO_LOW_TIERS && playerTiers[at] > 4) {
							playerTiers[at] = 4;
						}
						else if(currentLowTierState == LowTierState.JUST_D_TIER && playerTiers[at] > 5) {
							playerTiers[at] = 5;
						}
						else if(currentLowTierState == LowTierState.D_AND_E_TIER && playerTiers[at] > 6) {
							playerTiers[at] = 6;
						}
						else if(currentLowTierState == LowTierState.ALL_LOW_TIERS && playerTiers[at] > 7) {
							playerTiers[at] = 7;
						}
						else if(currentLowTierState == LowTierState.USE_PLAYABLE_LOW_TIERS && playerTiers[at] == 5) {
							playerTiers[at] = 8;
						}
						
						if(alreadyGotDoubleSTier && playerTiers[at] == 0) {
							playerTiers[at] = 1;
						}
						
						if(!alreadyGotDoubleSTier && playerTiers[at] == 0) {
							alreadyGotDoubleSTier = true;
						}
					}
				}
				else {
					for(int at = 0; at < numPlayers; at++) {
						playerTiers[at] = tierToForce;
					}
				}
				
				ArrayList<String> alreadyGotten = new ArrayList<String>();
				for(int x = 1; x <= numPlayers; x++) {
					String got = "";
					if(cannotGet.get(x - 1).size() > 4) {
						cannotGet.get(x - 1).removeFirst();
					}
					while(got.equals("") || linesOfFile.get(8 + x).contains(got) || alreadyGotten.contains(got) || cannotGet.get(x - 1).contains(got)) {
						int numGot = rand.nextInt(linesOfFile.get(playerTiers[x - 1]).size());
						got = linesOfFile.get(playerTiers[x - 1]).get(numGot);
					}
					if(playerTiers[x - 1] != 0 && playerTiers[x - 1] != 1 && playerTiers[x - 1] != 8) {
						cannotGet.get(x - 1).addLast(got);
					}
					alreadyGotten.add(got);
					results.append("Player " + x + " got " + got + ", tier " + tierToString(playerTiers[x - 1]) + ".\n");
				}
			}
			else {
				JOptionPane.showMessageDialog(frame, "You must load a file.", "Smash"
						+ " Character Picker", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private String tierToString(int tier) {
		if(tier == 0) {
			return "SS";
		}
		else if(tier == 1) {
			return "S";
		}
		else if(tier == 2) {
			return "A";
		}
		else if(tier == 3) {
			return "B";
		}
		else if(tier == 4) {
			return "C";
		}
		else if(tier == 5) {
			return "D";
		}
		else if(tier == 6) {
			return "E";
		}
		else if(tier == 7){
			return "F";
		}
		else {
			return "Lower";
		}
	}
	
	private int[] getPlayerTiers() {
		int tierChance = rand.nextInt(100);
		int midTier;
		
		if(currentLowTierState == LowTierState.USE_PLAYABLE_LOW_TIERS || currentLowTierState == LowTierState.NO_LOW_TIERS) {
			if(tierChance <= 25) {
				midTier = 3;
			}
			else if(tierChance <= 55) {
				midTier = 2;
			}
			else if(tierChance <= 80) {
				midTier = 1;
			}
			else {
				midTier = 0;
			}
		}
		else if(currentLowTierState == LowTierState.JUST_D_TIER) {
			if(tierChance <= 15) {
				midTier = 4;
			}
			else if(tierChance <= 35) {
				midTier = 3;
			}
			else if(tierChance <=60) {
				midTier = 2;
			}
			else if(tierChance <=80) {
				midTier = 1;
			}
			else {
				midTier = 0;
			}
		}
		else if(currentLowTierState == LowTierState.D_AND_E_TIER) {
			if(tierChance <= 10) {
				midTier = 5;
			}
			else if(tierChance <= 20) {
				midTier = 4;
			}
			else if(tierChance <= 40) {
				midTier = 3;
			}
			else if(tierChance <= 65) {
				midTier = 2;
			}
			else if(tierChance <= 85) {
				midTier = 1;
			}
			else {
				midTier = 0;
			}
		}
		else {
			if(tierChance <= 5) {
				midTier = 6;
			}
			else if(tierChance <=12) {
				midTier = 5;
			}
			else if(tierChance <= 25) {
				midTier = 4;
			}
			else if(tierChance <= 45) {
				midTier = 3;
			}
			else if(tierChance <= 70) {
				midTier = 2;
			}
			else if(tierChance <= 85) {
				midTier = 1;
			}
			else {
				midTier = 0;
			}
		}
		//adjust for addition of SS tier
		//yeah, it's the lazy way out, i don't care
		midTier++;
		
		int[] playerTiers = new int[numPlayers];
		for(int at = 0; at < playerTiers.length; at++) {
			playerTiers[at] = midTier;
		}
		if(rand.nextInt(100) >= 50) {
			int[] modifiers = new int[numPlayers];
			if(midTier == 2 || midTier == 3) {
				for(int at = 0; at < playerTiers.length; at++) {
					modifiers[at] = rand.nextInt(2);
					playerTiers[at] += modifiers[at];
				}
			}
			else if(midTier == 1) {
				for(int at = 0; at < playerTiers.length; at++) {
					modifiers[at] = rand.nextInt(2);
					if(rand.nextInt(100) < 50) {
						playerTiers[at] -= modifiers[at];
					}
				}
			}
			else {
				for(int at = 0; at < playerTiers.length; at++) {
					modifiers[at] = rand.nextInt(2);
				}
				if(rand.nextInt(100) < 50) {
					for(int at = 0; at < playerTiers.length; at++) {
						playerTiers[at] -= modifiers[at];
					}
				}
				else {
					for(int at = 0; at < playerTiers.length; at++) {
						playerTiers[at] += modifiers[at];
					}
				}
			}
		}
		
		return playerTiers;
	}
	
	//action listener class for the load button
	private class LoadButtonActionListener implements ActionListener {	
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			int r = fileChooser.showOpenDialog(loadButton);
			
			if(r == JFileChooser.APPROVE_OPTION) {
				readFile(fileChooser.getSelectedFile());
			}
		}	
	}
	
	private void readFile(File tierListFile) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(tierListFile));
			
			//read first line
			String lineAt = in.readLine();
			int on = 0;
			//check if line is valid, basically scroll through all lines
			while(lineAt != null) {
				//scroll through all chars in a line
				String next = "";
				int currentSize = 0;
				ArrayList<String> currentLine = new ArrayList<String>();
				for(int x = 6; x < lineAt.length(); x++) {
					if(lineAt.charAt(x) == ',') {
						currentLine.add(next);
						next = "";
						currentSize++;
					}
					else {
						next += lineAt.charAt(x);
					}
				}
				currentLine.add(next);
				currentSize++;
				if(on < 9) {
					tierSizes[on] = currentSize;
				}
				linesOfFile.add(currentLine);
				lineAt = in.readLine();
				on++;
			}
			in.close();
		} catch (FileNotFoundException e) {} catch (IOException e) {}
		
		fileLoaded = true;
		results.append("Loaded file: ");
		results.append(tierListFile.getName() + "\n");
	}
	
	//action listener for exit button
	private class ExitButtonActionListener implements ActionListener {	
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}
	
	//action listener for format button
	private class FormatButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(frame, "All lines should contain "
					+ "comma-separated entries.\n"
					+ "Leave area after equals sign blank if N/A.\nDo not "
					+ "omit lines, otherwise it will not load properly.\n"
					+ "Note that you cannot put the lines in any order,"
					+ "either. They must be in this order.\n\ndoubS=\n"
					+ "tierS=\ntierA=\ntierB=\ntierC=\ntierD=\ntierE=\ntierF=\n"
					+ "lowTs=(list of playable low tiers, if you like to have "
					+ "just a few low tiers in the pool)\n"
					+ "p1Exc=(exclusion lists for each player, for those "
					+ "fighters you just can't stand playing)\n"
					+ "p2Exc=\np3Exc=\np4Exc=\np5Exc=\np6Exc=\np7Exc=\n"
					+ "p8Exc=\n\nRefer to the tier list that came with this "
					+ "download if necessary.",
					"Smash Character Picker 2.0 Help", JOptionPane.PLAIN_MESSAGE);
		}
	}

	private class AboutButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			BufferedImage buffImg = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
			ImageIcon iconToShow = new ImageIcon(frame.getIconImage());
			Image img = iconToShow.getImage();
			Graphics graph = buffImg.createGraphics();
			graph.drawImage(img, 0, 0, 40, 40, null);
			ImageIcon redrawn = new ImageIcon(buffImg);
			JOptionPane.showMessageDialog(frame, "Smash Character Picker v2.1 "
					+ "by J.K.\nNew in v2.1:\nSS tier support", 
					"About Smash Character Picker",
					JOptionPane.INFORMATION_MESSAGE, redrawn);
		}
	}
	
	private class SettingsWindow {
		
	}
}
