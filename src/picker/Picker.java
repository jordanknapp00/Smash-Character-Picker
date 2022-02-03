package picker;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
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

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;

enum LowTierState {NO_LOW_TIERS, USE_PLAYABLE_LOW_TIERS, JUST_D_TIER, D_AND_E_TIER, ALL_LOW_TIERS, USING_CUSTOM_VALS};

public class Picker {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				@SuppressWarnings("unused")
				Picker p = new Picker();
			}
		});
	}
	
	//**** INITIALIZATION OF ALL OBJECTS/VARS ****
	
	//** Swing Objects Initialization **
	
	//frame and panels of main frame
	private JFrame mainFrame;
	private JPanel topPanel;
	private JPanel bottomPanel;
	
	//menu bar components
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu mainMenu;
	private JMenu helpMenu;
	private JMenuItem loadButton;
	private JMenuItem exitButton;
	private JMenuItem settingsButton;
	private JMenuItem whyCrashButton;
	private JMenuItem formatButton;
	private JMenuItem aboutButton;
	private JMenuItem soundBoardButton;
	
	//top panel components
	private JTextArea results;
		
	//bottom panel components
	private JButton generateButton;
	private JSpinner numPlayersSpinner;
	private JLabel spinnerLabel;
	
	//** Field Initialization **
	
	//main picker variables
	private boolean fileLoaded;
	private ArrayList<ArrayList<String>> linesOfFile;
	private int numBattles;
	private ArrayList<ArrayDeque<String>> cannotGet;
	private int numPlayers;
	
	//settings variables
	private int tierToForce;
	private LowTierState currentLowTierState;
	private boolean usingCustomChances;
	private int[] standardTierChances;
	private int[] customTierChances;
	private int cannotGetSize;
	private boolean SSAllowedInCannotGetBuffer;
	private boolean SAllowedInCannotGetBuffer;
	private boolean customVerified;
	private boolean everyoneSameTier;
	
	private static Random rand;
	
	public Picker() {
		//Initialize main frame
		mainFrame = new JFrame("Smash Character Picker");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(325, 325);
		mainFrame.setResizable(false);
		
		//Initialize TextAreas before setting look and feel, to preserve small
		//font size
		results = new JTextArea();
		results.setEditable(false);
		results.setFont(results.getFont().deriveFont(16f));
		
		//Set look and feel
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
		
		//Initialize top panel
		topPanel = new JPanel();
		topPanel.setBorder(BorderFactory.createTitledBorder("Results"));
		topPanel.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		gc.fill = GridBagConstraints.BOTH;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = 1;
		topPanel.add(results, gc);
		
		//Initialize bottom panel
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridBagLayout());
		generateButton = new JButton("Generate");
		generateButton.addActionListener(new GenerateButtonActionListener());
		SpinnerNumberModel spinner = new SpinnerNumberModel(2, 2, 8, 1);
		numPlayersSpinner = new JSpinner(spinner);
		numPlayersSpinner.addChangeListener(new NumPlayersChangeListener());
		spinnerLabel = new JLabel("Number of players: ");
		gc.weightx = .85;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.anchor = GridBagConstraints.CENTER;
		bottomPanel.add(generateButton, gc);
		gc.gridx = 1;
		gc.weightx = .1;
		gc.fill = GridBagConstraints.NONE;
		bottomPanel.add(spinnerLabel, gc);
		gc.gridx = 2;
		gc.weightx = .05;
		bottomPanel.add(numPlayersSpinner, gc);
		
		//Initialize menu bar
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		mainMenu = new JMenu("Picker");
		helpMenu = new JMenu("Help");
		loadButton = new JMenuItem("Load a tier list");
		loadButton.addActionListener(new LoadButtonActionListener());
		exitButton = new JMenuItem("Exit");
		exitButton.addActionListener(new ExitButtonActionListener());
		settingsButton = new JMenuItem("Change settings");
		settingsButton.addActionListener(new SettingsButtonActionListener());
		soundBoardButton = new JMenuItem("Soundboard");
		soundBoardButton.addActionListener(new SoundBoardActionListener());
		whyCrashButton = new JMenuItem("Why does the program freeze/crash?");
		whyCrashButton.addActionListener(new WhyCrashButtonActionListener());
		formatButton = new JMenuItem("How to format a tier list");
		formatButton.addActionListener(new FormatButtonActionListener());
		aboutButton = new JMenuItem("About");
		aboutButton.addActionListener(new AboutButtonActionListener());
		fileMenu.add(loadButton);
		fileMenu.add(exitButton);
		mainMenu.add(settingsButton);
		mainMenu.add(soundBoardButton);
		helpMenu.add(whyCrashButton);
		helpMenu.add(formatButton);
		helpMenu.add(aboutButton);
		menuBar.add(fileMenu);
		menuBar.add(mainMenu);
		menuBar.add(helpMenu);
		
		//Put it all together
		mainFrame.setJMenuBar(menuBar);
		mainFrame.getContentPane().setLayout(new GridBagLayout());
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = .95;
		gc.anchor = GridBagConstraints.CENTER;
		gc.fill = GridBagConstraints.BOTH;
		mainFrame.getContentPane().add(topPanel, gc);
		gc.gridy = 1;
		gc.weighty = .05;
		mainFrame.getContentPane().add(bottomPanel, gc);
		
		//Initialize fields
		fileLoaded = false;
		linesOfFile = new ArrayList<ArrayList<String>>();
		numBattles = 0;
		cannotGet = new ArrayList<ArrayDeque<String>>();
		for(int x = 0; x < 8; x++) {
			cannotGet.add(new ArrayDeque<String>());
		}
		numPlayers = 2;
		tierToForce = -1;
		currentLowTierState = LowTierState.NO_LOW_TIERS;
		usingCustomChances = false;
		customTierChances = new int[8];
		standardTierChances = new int[8];
		standardTierChances[0] = 8;
		standardTierChances[1] = 22;
		standardTierChances[2] = 25;
		standardTierChances[3] = 25;
		standardTierChances[4] = 20;
		standardTierChances[5] = 0;
		standardTierChances[6] = 0;
		standardTierChances[7] = 0;
		customTierChances[0] = 5;
		customTierChances[1] = 15;
		customTierChances[2] = 25;
		customTierChances[3] = 30;
		customTierChances[4] = 25;
		customTierChances[5] = 0;
		customTierChances[6] = 0;
		customTierChances[7] = 0;
		cannotGetSize = 5;
		SSAllowedInCannotGetBuffer = false;
		SAllowedInCannotGetBuffer = true;
		everyoneSameTier = true;
		
		customVerified = true;
		
		rand = new Random(System.currentTimeMillis());
		
		//Create icon
		mainFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/Icon.png")));
				
		//Finally make it visible
		mainFrame.setVisible(true);
		
	}
	
	private class GenerateButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(!fileLoaded) {
				JOptionPane.showMessageDialog(mainFrame, "You must load a tier "
						+ "list first!", "Smash Character Picker v4.2", 
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			//clear the results area
			int lines = results.getLineCount();
			int end;
			try {
				end = results.getLineEndOffset(lines - 1);
				results.replaceRange("", 0, end);
			} catch (BadLocationException e1) {
				results.append("BadLocationException!\n");
				return;
			}
			//show number of battles
			numBattles++;
			results.append("Battle #" + numBattles + ":\n");
			int[] playerTiers = new int[numPlayers];
			if(usingCustomChances && tierToForce == -1) {
				playerTiers = getPlayerTiers(customTierChances);
			}
			else if(!usingCustomChances && tierToForce == -1) {
				playerTiers = getPlayerTiers(standardTierChances);
			}
			else {
				for(int at = 0; at < numPlayers; at++) {
					playerTiers[at] = tierToForce;
				}
			}
			//actually pick the characters now
			ArrayList<String> alreadyGottenThisRound = new ArrayList<String>();
			for(int x = 1; x <= numPlayers; x++) {
				String got = "";
				if(cannotGet.get(x - 1).size() > cannotGetSize) {
					cannotGet.get(x - 1).removeFirst();
				}
				while(got.equals("") || linesOfFile.get(7 + x).contains(got) || alreadyGottenThisRound.contains(got) || cannotGet.get(x - 1).contains(got)) {
					int numGot = rand.nextInt(linesOfFile.get(playerTiers[x - 1]).size());
					got = linesOfFile.get(playerTiers[x - 1]).get(numGot);
				}
				if(playerTiers[x - 1] != 0 && playerTiers[x - 1] != 1 && playerTiers[x - 1] != 8) {
					cannotGet.get(x - 1).addLast(got);
				}
				if(SAllowedInCannotGetBuffer && playerTiers[x - 1] == 1) {
					cannotGet.get(x - 1).addLast(got);
				}
				if(SSAllowedInCannotGetBuffer && playerTiers[x - 1] == 0) {
					cannotGet.get(x - 1).addLast(got);
				}
				alreadyGottenThisRound.add(got);
				results.append("Player " + x + " got " + got + ", tier " + tierToString(playerTiers[x - 1]) + ".\n");
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
			else {
				return "F";
			}
		}
		
		private int[] getPlayerTiers(int[] chances) {		
			int tierChance = rand.nextInt(100);
			tierChance++;
			int tier = -1;
			
			//i tried to do this with a for loop, it didn't work
			//now just trying it manually to see if it even works at all
			//may or may not implement a proper for loop in the future
			int sum = chances[0];
			if(tierChance <= sum) {
				tier = 0;
			}
			sum += chances[1];
			if(tierChance <= sum && tier == -1) {
				tier = 1;
			}
			sum += chances[2];
			if(tierChance <= sum && tier == -1) {
				tier = 2;
			}
			sum += chances[3];
			if(tierChance <= sum && tier == -1) {
				tier = 3;
			}
			sum += chances[4];
			if(tierChance <= sum && tier == -1) {
				tier = 4;
			}
			sum += chances[5];
			if(tierChance <= sum && tier == -1) {
				tier = 5;
			}
			sum += chances[6];
			if(tierChance <= sum && tier == -1) {
				tier = 6;
			}
			sum += chances[7];
			if(tierChance <= sum && tier == -1) {
				tier = 7;
			}
			
			//set everyone's tier to the decided tier, then modify it if
			//applicable. SS tier will be modified down, but every other tier
			//will modify up
			int[] playerTiers = new int[numPlayers];
			for(int at = 0; at < numPlayers; at++) {
				playerTiers[at] = tier;
			}
			if(!everyoneSameTier && rand.nextInt(100) >= 50) {
				for(int at = 0; at < numPlayers; at++) {
					int bumpChance = rand.nextInt(100);
					if(tier == 0 && bumpChance < 25) {
						playerTiers[at]++;
					}
					else if(bumpChance < 25) {
						playerTiers[at]--;
					}
				}
			}
			
			return playerTiers;
		}
	}
	
	private class SettingsButtonActionListener implements ActionListener {
		//** Swing Objects Initialization **
				
		//frame and panels
		private JFrame settingsFrame;
		private JPanel generalTierSettingsPanel;
		private JPanel lowTierRulesPanel;
		private JPanel customChancesPanel;
		private JPanel cannotGetPanel;
		
		//generalTierSettingsPanel components
		@SuppressWarnings("rawtypes")
		private JComboBox tierToForceSelector;
		private JCheckBox everyoneSameTierBox;
		
		//lowTierRulesPanel components
		private JLabel lowTierRulesLabel;
		private ButtonGroup lowTierRulesButtons;
		private JRadioButton noLowTiersButton;
		private JRadioButton justDTierButton;
		private JRadioButton useDandETierButton;
		private JRadioButton allLowTiersButton;
		
		//customChancesPanel components
		private JLabel tierChanceLabel;
		private JSpinner SSTierSpinner;
		private JLabel SSTierLabel;
		private JSpinner STierSpinner;
		private JLabel STierLabel;
		private JSpinner ATierSpinner;
		private JLabel ATierLabel;
		private JSpinner BTierSpinner;
		private JLabel BTierLabel;
		private JSpinner CTierSpinner;
		private JLabel CTierLabel;
		private JSpinner DTierSpinner;
		private JLabel DTierLabel;
		private JSpinner ETierSpinner;
		private JLabel ETierLabel;
		private JSpinner FTierSpinner;
		private JLabel FTierLabel;
		private JCheckBox useCustomChances;
		private JButton applyButton;
		
		//cannotGetPanel components
		private JLabel cannotGetSizeLabel;
		private JSpinner cannotGetSizeSpinner;
		private JCheckBox allowSSInCannotGet;
		private JCheckBox allowSInCannotGet;
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void actionPerformed(ActionEvent e) {
			////**** INITIALIZING NEW WINDOW ****
			
			//Frame
			settingsFrame = new JFrame("Smash Character Picker v4.2 Settings");
			settingsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			settingsFrame.setSize(375, 565);
			settingsFrame.setResizable(false);
			
			//Set look and feel
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException e2) {
				e2.printStackTrace();
			} catch (InstantiationException e2) {
				e2.printStackTrace();
			} catch (IllegalAccessException e2) {
				e2.printStackTrace();
			} catch (UnsupportedLookAndFeelException e2) {
				e2.printStackTrace();
			}
			
			//initialize forceTierPanel
			JLabel forceTierLabel = new JLabel("Tier to force: ");
			String[] options = {"Do not force tier", "SS Tier", "S Tier",
					"A Tier", "B Tier", "C Tier", "D Tier", "E Tier", "F Tier"};
			tierToForceSelector = new JComboBox(options);
			tierToForceSelector.setSelectedIndex(tierToForce + 1);
			tierToForceSelector.addActionListener(new TierToForceSelectorActionListener());
			everyoneSameTierBox = new JCheckBox("Every player gets the same tier");
			everyoneSameTierBox.setSelected(everyoneSameTier);
			everyoneSameTierBox.setToolTipText("If unchecked, there will be a "
					+ "small chance that some players will be bumped up a tier.");
			everyoneSameTierBox.addActionListener(new EveryoneSameTierBoxActionListener());
			generalTierSettingsPanel = new JPanel();
			generalTierSettingsPanel.setBorder(BorderFactory.createTitledBorder("Force a tier")); //panel border
			generalTierSettingsPanel.setLayout(new GridBagLayout()); //allows use of gridbaglayout
			GridBagConstraints settingsGC = new GridBagConstraints(); //gridbagconstraints for all of settingsFrame
			settingsGC.anchor = GridBagConstraints.ABOVE_BASELINE_LEADING; //new components will start at top left
			settingsGC.gridx = 0;										   //at least in fucking theory
			settingsGC.gridy = 0;										   //not sure why they aren't
			generalTierSettingsPanel.add(forceTierLabel, settingsGC);
			settingsGC.gridx = 1;
			generalTierSettingsPanel.add(tierToForceSelector, settingsGC);
			settingsGC.gridwidth = 2;
			settingsGC.gridx = 0;
			settingsGC.gridy = 1;
			generalTierSettingsPanel.add(everyoneSameTierBox, settingsGC);
			settingsGC.gridwidth = 1;
			generalTierSettingsPanel.setPreferredSize(new Dimension(350, 65)); //allow panel to fit
			
			//Initialize lowTierRules panel
			lowTierRulesLabel = new JLabel("Set rules for lower tiers.");
			noLowTiersButton = new JRadioButton("Only C tier and above");
			noLowTiersButton.addActionListener(new NoLowTiersButtonActionListener());
			justDTierButton = new JRadioButton("Allow D tier and above");
			justDTierButton.addActionListener(new JustDTierButtonActionListener());
			useDandETierButton = new JRadioButton("Allow tiers D, E, and above");
			useDandETierButton.addActionListener(new UseDandETierButtonActionListener());
			allLowTiersButton = new JRadioButton("Allow all tiers");
			allLowTiersButton.addActionListener(new AllLowTiersButtonActionListener());
			lowTierRulesButtons = new ButtonGroup();
			lowTierRulesButtons.add(noLowTiersButton);
			lowTierRulesButtons.add(useDandETierButton);
			lowTierRulesButtons.add(justDTierButton);
			lowTierRulesButtons.add(allLowTiersButton);
			if(currentLowTierState == LowTierState.NO_LOW_TIERS) {
				noLowTiersButton.setSelected(true);
			}
			else if(currentLowTierState == LowTierState.JUST_D_TIER) {
				justDTierButton.setSelected(true);
			}
			else if(currentLowTierState == LowTierState.D_AND_E_TIER) {
				useDandETierButton.setSelected(true);
			}
			else {
				allLowTiersButton.setSelected(true);
			}
			lowTierRulesPanel = new JPanel();
			lowTierRulesPanel.setLayout(new GridBagLayout());
			lowTierRulesPanel.setBorder(BorderFactory.createTitledBorder("Low tier rules"));
			settingsGC.anchor = GridBagConstraints.ABOVE_BASELINE_LEADING;
			settingsGC.gridx = 0;
			settingsGC.gridy = 0;
			lowTierRulesPanel.add(lowTierRulesLabel, settingsGC);
			settingsGC.gridy = 1;
			lowTierRulesPanel.add(noLowTiersButton, settingsGC);
			settingsGC.gridx = 1;
			settingsGC.gridy = 1;
			lowTierRulesPanel.add(justDTierButton, settingsGC);
			settingsGC.gridx = 0;
			settingsGC.gridy = 2;
			lowTierRulesPanel.add(useDandETierButton, settingsGC);
			settingsGC.gridx = 1;
			settingsGC.gridy = 2;
			lowTierRulesPanel.add(allLowTiersButton, settingsGC);
			lowTierRulesPanel.setPreferredSize(new Dimension(350, 85));
			
			//customchancerules panel
			tierChanceLabel = new JLabel("You can set custom chances for "
					+ "each tier. Remember to hit apply!");
			useCustomChances = new JCheckBox("Use custom chances");
			useCustomChances.addActionListener(new UseCustomChancesButtonActionListener());
			if(usingCustomChances) {
				useCustomChances.setSelected(true);
			}
			if(!customVerified) {
				useCustomChances.setEnabled(false);
			}
			SpinnerNumberModel SSmod = new SpinnerNumberModel(customTierChances[0], 0, 100, 1);
			SSTierSpinner = new JSpinner(SSmod);
			SSTierSpinner.addChangeListener(new ChangeListenerSS());
			SSTierLabel = new JLabel("SS tier chance: ");
			SpinnerNumberModel Smod = new SpinnerNumberModel(customTierChances[1], 0, 100, 1);
			STierSpinner = new JSpinner(Smod);
			STierSpinner.addChangeListener(new ChangeListenerS());
			STierLabel = new JLabel("S tier chance: ");
			SpinnerNumberModel Amod = new SpinnerNumberModel(customTierChances[2], 0, 100, 1);
			ATierSpinner = new JSpinner(Amod);
			ATierSpinner.addChangeListener(new ChangeListenerA());
			ATierLabel = new JLabel("A tier chance: ");
			SpinnerNumberModel Bmod = new SpinnerNumberModel(customTierChances[3], 0, 100, 1);
			BTierSpinner = new JSpinner(Bmod);
			BTierSpinner.addChangeListener(new ChangeListenerB());
			BTierLabel = new JLabel("B tier chance: ");
			SpinnerNumberModel Cmod = new SpinnerNumberModel(customTierChances[4], 0, 100, 1);
			CTierSpinner = new JSpinner(Cmod);
			CTierSpinner.addChangeListener(new ChangeListenerC());
			CTierLabel = new JLabel("C tier chance: ");
			SpinnerNumberModel Dmod = new SpinnerNumberModel(customTierChances[5], 0, 100, 1);
			DTierSpinner = new JSpinner(Dmod);
			DTierSpinner.addChangeListener(new ChangeListenerD());
			DTierLabel = new JLabel("D tier chance: ");
			SpinnerNumberModel Emod = new SpinnerNumberModel(customTierChances[6], 0, 100, 1);
			ETierSpinner = new JSpinner(Emod);
			ETierSpinner.addChangeListener(new ChangeListenerE());
			ETierLabel = new JLabel("E tier chance: ");
			SpinnerNumberModel Fmod = new SpinnerNumberModel(customTierChances[7], 0, 100, 1);
			FTierSpinner = new JSpinner(Fmod);
			FTierSpinner.addChangeListener(new ChangeListenerF());
			FTierLabel = new JLabel("F tier chance: ");
			applyButton = new JButton("Apply tier chance settings");
			applyButton.addActionListener(new ApplyButtonActionListener());
			applyButton.setToolTipText("Will ensure that the given values are "
					+ "valid, i.e. they add up to 100 and do not conflict with "
					+ "the chosen \"Low tier rules\" setting.");
			customChancesPanel = new JPanel();
			customChancesPanel.setBorder(BorderFactory.createTitledBorder("Tier chance settings"));
			customChancesPanel.setLayout(new GridBagLayout());
			settingsGC.gridx = 0;
			settingsGC.gridy = 0;
			settingsGC.gridwidth = 2;
			customChancesPanel.add(tierChanceLabel, settingsGC);
			settingsGC.gridy = 1;
			settingsGC.anchor = GridBagConstraints.CENTER;
			customChancesPanel.add(useCustomChances, settingsGC);
			settingsGC.anchor = GridBagConstraints.LINE_END;
			settingsGC.gridwidth = 1;
			settingsGC.gridy = 2;
			customChancesPanel.add(SSTierLabel, settingsGC);
			settingsGC.gridy = 3;
			customChancesPanel.add(STierLabel, settingsGC);
			settingsGC.gridy = 4;
			customChancesPanel.add(ATierLabel, settingsGC);
			settingsGC.gridy = 5;
			customChancesPanel.add(BTierLabel, settingsGC);
			settingsGC.gridy = 6;
			customChancesPanel.add(CTierLabel, settingsGC);
			settingsGC.gridy = 7;
			customChancesPanel.add(DTierLabel, settingsGC);
			settingsGC.gridy = 8;
			customChancesPanel.add(ETierLabel, settingsGC);
			settingsGC.gridy = 9;
			customChancesPanel.add(FTierLabel, settingsGC);
			settingsGC.gridx = 1;
			settingsGC.gridy = 2;
			settingsGC.anchor = GridBagConstraints.LINE_START;
			customChancesPanel.add(SSTierSpinner, settingsGC);
			settingsGC.gridy = 3;
			customChancesPanel.add(STierSpinner, settingsGC);
			settingsGC.gridy = 4;
			customChancesPanel.add(ATierSpinner, settingsGC);
			settingsGC.gridy = 5;
			customChancesPanel.add(BTierSpinner, settingsGC);
			settingsGC.gridy = 6;
			customChancesPanel.add(CTierSpinner, settingsGC);
			settingsGC.gridy = 7;
			customChancesPanel.add(DTierSpinner, settingsGC);
			settingsGC.gridy = 8;
			customChancesPanel.add(ETierSpinner, settingsGC);
			settingsGC.gridy = 9;
			customChancesPanel.add(FTierSpinner, settingsGC);
			settingsGC.gridx = 0;
			settingsGC.gridy = 10;
			settingsGC.gridwidth = 2;
			settingsGC.anchor = GridBagConstraints.CENTER;
			JLabel blank = new JLabel(" ");
			customChancesPanel.add(blank, settingsGC);
			settingsGC.gridy = 11;
			customChancesPanel.add(applyButton, settingsGC);
			customChancesPanel.setPreferredSize(new Dimension(350, 260));
			
			//cannotgetrules panel
			cannotGetSizeLabel = new JLabel("Size of the \"Cannot get\" "
					+ "buffer: ");
			SpinnerNumberModel bleh = new SpinnerNumberModel(cannotGetSize, 0, 15, 1);
			cannotGetSizeSpinner = new JSpinner(bleh);
			cannotGetSizeSpinner.addChangeListener(new CannotGetSizeChangeListener());
			cannotGetSizeLabel.setToolTipText("If this is set too high, the "
					+ "program could freeze, because there may be no valid "
					+ "fighters left for it to pick. 5 is recommended size. "
					+ "Note that you have the option of whether or not S & SS "
					+ "tiers are allowed in this buffer.");
			allowSSInCannotGet = new JCheckBox("Allow SS tiers in \"Cannot "
					+ "get\" buffer");
			allowSSInCannotGet.addActionListener(new AllowSSInCannotGetButtonActionListener());
			allowSSInCannotGet.setSelected(SSAllowedInCannotGetBuffer);
			allowSInCannotGet = new JCheckBox("Allow S tiers in \"Cannot "
					+ "get\" buffer");
			allowSInCannotGet.addActionListener(new AllowSInCannotGetButtonActionListener());
			allowSInCannotGet.setSelected(SAllowedInCannotGetBuffer);
			cannotGetPanel = new JPanel();
			cannotGetPanel.setBorder(BorderFactory.createTitledBorder("\"Cannot get\" buffer settings"));
			cannotGetPanel.setLayout(new GridBagLayout());
			settingsGC.gridwidth = 1;
			settingsGC.gridheight = 1;
			settingsGC.gridy = 0;
			settingsGC.gridx = 0;
			settingsGC.anchor = GridBagConstraints.LINE_END;
			cannotGetPanel.add(cannotGetSizeLabel, settingsGC);
			settingsGC.gridx = 1;
			settingsGC.anchor = GridBagConstraints.LINE_START;
			cannotGetPanel.add(cannotGetSizeSpinner, settingsGC);
			settingsGC.gridx = 0;
			settingsGC.gridy = 1;
			settingsGC.anchor = GridBagConstraints.LINE_END;
			cannotGetPanel.add(allowSSInCannotGet, settingsGC);
			settingsGC.gridy = 2;
			cannotGetPanel.add(allowSInCannotGet, settingsGC);
			cannotGetPanel.setPreferredSize(new Dimension(350, 90));
			
			//put everything together
			FlowLayout finalLayout = new FlowLayout();
			settingsFrame.getContentPane().setLayout(finalLayout);
			settingsFrame.getContentPane().add(generalTierSettingsPanel, finalLayout);
			settingsFrame.getContentPane().add(lowTierRulesPanel, finalLayout);
			settingsFrame.getContentPane().add(customChancesPanel, finalLayout);
			settingsFrame.getContentPane().add(cannotGetPanel, finalLayout);
			
			settingsFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/Icon.png")));
			settingsFrame.setVisible(true);
			
		}
		
		private class EveryoneSameTierBoxActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				if(everyoneSameTier) {
					everyoneSameTier = false;
				}
				else {
					everyoneSameTier = true;
				}
			}
		}
		
		private class TierToForceSelectorActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				tierToForce = tierToForceSelector.getSelectedIndex() - 1;
			}
		}
		
		private class NoLowTiersButtonActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				currentLowTierState = LowTierState.NO_LOW_TIERS;
				standardTierChances[0] = 8;
				standardTierChances[1] = 22;
				standardTierChances[2] = 25;
				standardTierChances[3] = 25;
				standardTierChances[4] = 20;
				standardTierChances[5] = 0;
				standardTierChances[6] = 0;
				standardTierChances[7] = 0;
			}
		}
		
		private class JustDTierButtonActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				currentLowTierState = LowTierState.JUST_D_TIER;
				standardTierChances[0] = 5;
				standardTierChances[1] = 15;
				standardTierChances[2] = 20;
				standardTierChances[3] = 25;
				standardTierChances[4] = 20;
				standardTierChances[5] = 15;
				standardTierChances[6] = 0;
				standardTierChances[7] = 0;
			}
		}
		
		private class UseDandETierButtonActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				currentLowTierState = LowTierState.D_AND_E_TIER;
				standardTierChances[0] = 3;
				standardTierChances[1] = 12;
				standardTierChances[2] = 15;
				standardTierChances[3] = 25;
				standardTierChances[4] = 20;
				standardTierChances[5] = 15;
				standardTierChances[6] = 10;
				standardTierChances[7] = 0;
			}
		}
		
		private class AllLowTiersButtonActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				currentLowTierState = LowTierState.ALL_LOW_TIERS;
				standardTierChances[0] = 2;
				standardTierChances[1] = 10;
				standardTierChances[2] = 25;
				standardTierChances[3] = 20;
				standardTierChances[4] = 20;
				standardTierChances[5] = 15;
				standardTierChances[6] = 10;
				standardTierChances[7] = 8;
			}
		}
		
		private class UseCustomChancesButtonActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				if(usingCustomChances) {
					usingCustomChances = false;
				}
				else {
					usingCustomChances = true;
				}
			}
		}
		
		private void voidCustom() {
			customVerified = false;
			useCustomChances.setEnabled(false);
			usingCustomChances = false;
			useCustomChances.setSelected(false);
		}
		
		private class ChangeListenerSS implements ChangeListener {
			public void stateChanged(ChangeEvent e) {
				customTierChances[0] = (int) SSTierSpinner.getValue();
				voidCustom();
			}
		}
		
		private class ChangeListenerS implements ChangeListener {
			public void stateChanged(ChangeEvent e) {
				customTierChances[1] = (int) STierSpinner.getValue();
				voidCustom();
			}
		}
		
		private class ChangeListenerA implements ChangeListener {
			public void stateChanged(ChangeEvent e) {
				customTierChances[2] = (int) ATierSpinner.getValue();
				voidCustom();
			}
		}
		
		private class ChangeListenerB implements ChangeListener {
			public void stateChanged(ChangeEvent e) {
				customTierChances[3] = (int) BTierSpinner.getValue();
				voidCustom();
			}
		}
		
		private class ChangeListenerC implements ChangeListener {
			public void stateChanged(ChangeEvent e) {
				customTierChances[4] = (int) CTierSpinner.getValue();
				voidCustom();
			}
		}
		
		private class ChangeListenerD implements ChangeListener {
			public void stateChanged(ChangeEvent e) {
				customTierChances[5] = (int) DTierSpinner.getValue();
				voidCustom();
			}
		}
		
		private class ChangeListenerE implements ChangeListener {
			public void stateChanged(ChangeEvent e) {
				customTierChances[6] = (int) ETierSpinner.getValue();
				voidCustom();
			}
		}
		
		private class ChangeListenerF implements ChangeListener {
			public void stateChanged(ChangeEvent e) {
				customTierChances[7] = (int) FTierSpinner.getValue();
				voidCustom();
			}
		}

		private class ApplyButtonActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				int total = 0;
				for(int x = 0; x < 8; x++) {
					total += customTierChances[x];
				}
				//TODO: add check for conflict with low tier rules
				if(total == 100) {
					customVerified = true;
					useCustomChances.setEnabled(true);
					usingCustomChances = true;
					useCustomChances.setSelected(true);
					JOptionPane.showMessageDialog(settingsFrame, "Custom chances"
							+ " are valid and have been applied.", "Smash "
							+ "Character Picker v4.2",
							JOptionPane.INFORMATION_MESSAGE);
				}
				else {
					JOptionPane.showMessageDialog(settingsFrame, "Your custom "
							+ "chances are invalid. They must add up to 100.\n"
							+ "They currently add up to " + total + ".",
							"Smash Character Picker v4.2",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		private class CannotGetSizeChangeListener implements ChangeListener {
			public void stateChanged(ChangeEvent e) {
				cannotGetSize = (int) cannotGetSizeSpinner.getValue();
			}
		}

		private class AllowSSInCannotGetButtonActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				if(SSAllowedInCannotGetBuffer) {
					SSAllowedInCannotGetBuffer = false;
				}
				else {
					SSAllowedInCannotGetBuffer = true;
				}
			}
		}
		
		private class AllowSInCannotGetButtonActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				if(SAllowedInCannotGetBuffer) {
					SAllowedInCannotGetBuffer = false;
				}
				else {
					SAllowedInCannotGetBuffer = true;
				}
			}
		}
	}

	
	//action listener for the sound board
	//TODO: y'know... this
	private class SoundBoardActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(mainFrame, "Coming soon!", "Smash "
					+ "Character Picker v4.2 Soundboard", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	//action listener for the why crash button
	private class WhyCrashButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			BufferedImage buffImg = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
			ImageIcon iconToShow = new ImageIcon(mainFrame.getIconImage());
			Image img = iconToShow.getImage();
			Graphics graph = buffImg.createGraphics();
			graph.drawImage(img, 0, 0, 40, 40, null);
			ImageIcon redrawn = new ImageIcon(buffImg);
			JOptionPane.showMessageDialog(mainFrame, "The program is likely "
					+ "freezing/crashing because it gets caught in a loop.\n"
					+ "Typically this will happen when it is trying to pick a "
					+ "character of a particular tier,\nbut one is not available. "
					+ "Make sure that there are enough fighters in each tier\n"
					+ "that everyone can get a fighter of the same tier. Also "
					+ "make sure that the\n \"Cannot get\" buffer is not so large "
					+ "that it prevents there being a valid character\nfor every "
					+ "player.", "Smash Character Picker v4.2", 
					JOptionPane.INFORMATION_MESSAGE, redrawn);
		}
	}
	
	//action listener for the load button
	private class LoadButtonActionListener implements ActionListener {	
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt files", "txt");
			fileChooser.setFileFilter(filter);
			int r = fileChooser.showOpenDialog(loadButton);
			
			if(r == JFileChooser.APPROVE_OPTION) {
				readFile(fileChooser.getSelectedFile());
			}
		}	
	}
	
	//method which actually reads the file
	private void readFile(File tierListFile) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(tierListFile));
			
			//read first line
			String lineAt = in.readLine();
			//check if line is valid, basically scroll through all lines
			while(lineAt != null) {
				//scroll through all chars in a line
				String next = "";
				ArrayList<String> currentLine = new ArrayList<String>();
				for(int x = 6; x < lineAt.length(); x++) {
					if(lineAt.charAt(x) == ',') {
						currentLine.add(next);
						next = "";
					}
					else {
						next += lineAt.charAt(x);
					}
				}
				currentLine.add(next);
				linesOfFile.add(currentLine);
				lineAt = in.readLine();
			}
			if(linesOfFile.size() != 16) {
				in.close();
				throw new IOException();
			}
			in.close();
		} catch (FileNotFoundException e) {
			results.append("File not found!\n");
			fileLoaded = false;
			return;
		} catch (IOException e) {
			results.append("I/O exception! This means that the file is not a "
					+ "valid tier\nlist. Check 'How to format a tier list' "
					+ "under the Help menu\nto format your list correctly.\n");
			fileLoaded = false;
			return;
		}
		
		fileLoaded = true;
		results.append("Loaded file: ");
		results.append(tierListFile.getName() + "\n");
	}
	
	//Action listener for format button
	private class FormatButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			////Initialize new window
			
			//Frame and panels
			JFrame formatFrame = new JFrame("Smash Character Picker v4.2");
			formatFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			formatFrame.setSize(325, 400);
			formatFrame.setResizable(false);
			
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException e2) {
				e2.printStackTrace();
			} catch (InstantiationException e2) {
				e2.printStackTrace();
			} catch (IllegalAccessException e2) {
				e2.printStackTrace();
			} catch (UnsupportedLookAndFeelException e2) {
				e2.printStackTrace();
			}
			
			JPanel formatTopPanel = new JPanel();
			JPanel formatBottomPanel = new JPanel();
			
			//Initialize top panel
			formatTopPanel.setLayout(new GridBagLayout());
			GridBagConstraints formatGC = new GridBagConstraints();
			JLabel veryTop = new JLabel("Formatting tier list files:");
			JLabel topLine1 = new JLabel("All lines should contain comma-separated entries.");
			JLabel topLine2 = new JLabel("Do not put spaces after the commas.");
			JLabel topLine3 = new JLabel("Leave area after the equals sign blank if N/A.");
			JLabel topLine4 = new JLabel("Do not omit lines, or it will not load properly.");
			JLabel topLine5 = new JLabel("Ensure that the lines are in this order, too.");
			JLabel topLine6 = new JLabel("Hover over a line for an explanation of what goes there.");
			formatGC.anchor = GridBagConstraints.CENTER;
			formatTopPanel.add(veryTop, formatGC);
			formatGC.gridy = 1;
			formatGC.anchor = GridBagConstraints.LINE_START;
			formatTopPanel.add(topLine1, formatGC);
			formatGC.gridy = 2;
			formatTopPanel.add(topLine2, formatGC);
			formatGC.gridy = 3;
			formatTopPanel.add(topLine3, formatGC);
			formatGC.gridy = 4;
			formatTopPanel.add(topLine4, formatGC);
			formatGC.gridy = 5;
			formatTopPanel.add(topLine5, formatGC);
			formatGC.gridy = 6;
			formatTopPanel.add(topLine6, formatGC);
			
			//Initialize bottom panel
			formatBottomPanel.setLayout(new GridBagLayout());
			formatGC.gridx = 0;
			formatGC.gridy = 0;
			JLabel line1 = new JLabel("doubS=");
			line1.setToolTipText("List of SS tier characters goes here.");
			JLabel line2 = new JLabel("tierS=");
			line2.setToolTipText("List of S tier characters goes here.");
			JLabel line3 = new JLabel("tierA=");
			line3.setToolTipText("List of A tier characters goes here.");
			JLabel line4 = new JLabel("tierB=");
			line4.setToolTipText("List of B tier characters goes here.");
			JLabel line5 = new JLabel("tierC=");
			line5.setToolTipText("List of C tier characters goes here.");
			JLabel line6 = new JLabel("tierD=");
			line6.setToolTipText("List of D tier characters goes here.");
			JLabel line7 = new JLabel("tierE=");
			line7.setToolTipText("List of E tier characters goes here.");
			JLabel line8 = new JLabel("tierF=");
			line8.setToolTipText("List of F tier characters goes here.");
			JLabel line9 = new JLabel("p1Exc=");
			line9.setToolTipText("Player 1's exclusion list goes here. "
					+ "These are for fighters you simply never want to play, "
					+ "even you admit they're good.");
			JLabel line10 = new JLabel("p2Exc=");
			line10.setToolTipText("Player 2's exclusion list goes here.");
			JLabel line11 = new JLabel("p3Exc=");
			line11.setToolTipText("Player 3's exclusion list goes here.");
			JLabel line12 = new JLabel("p4Exc=");
			line12.setToolTipText("Player 4's exclusion list goes here.");
			JLabel line13 = new JLabel("p5Exc=");
			line13.setToolTipText("Player 5's exclusion list goes here.");
			JLabel line14 = new JLabel("p6Exc=");
			line14.setToolTipText("Player 6's exclusion list goes here.");
			JLabel line15 = new JLabel("p7Exc=");
			line15.setToolTipText("Player 7's exclusion list goes here.");
			JLabel line16 = new JLabel("p8Exc=");
			line16.setToolTipText("Player 8's exclusion list goes here.");
			formatBottomPanel.add(line1, formatGC);
			formatGC.gridy = 1;
			formatBottomPanel.add(line2, formatGC);
			formatGC.gridy = 2;
			formatBottomPanel.add(line3, formatGC);
			formatGC.gridy = 3;
			formatBottomPanel.add(line4, formatGC);
			formatGC.gridy = 4;
			formatBottomPanel.add(line5, formatGC);
			formatGC.gridy = 5;
			formatBottomPanel.add(line6, formatGC);
			formatGC.gridy = 6;
			formatBottomPanel.add(line7, formatGC);
			formatGC.gridy = 7;
			formatBottomPanel.add(line8, formatGC);
			formatGC.gridy = 8;
			formatBottomPanel.add(line9, formatGC);
			formatGC.gridy = 9;
			formatBottomPanel.add(line10, formatGC);
			formatGC.gridy = 10;
			formatBottomPanel.add(line11, formatGC);
			formatGC.gridy = 11;
			formatBottomPanel.add(line12, formatGC);
			formatGC.gridy = 12;
			formatBottomPanel.add(line13, formatGC);
			formatGC.gridy = 13;
			formatBottomPanel.add(line14, formatGC);
			formatGC.gridy = 14;
			formatBottomPanel.add(line15, formatGC);
			formatGC.gridy = 15;
			formatBottomPanel.add(line16, formatGC);
			
			//Put it all together
			formatFrame.getContentPane().setLayout(new GridBagLayout());
			formatGC.anchor = GridBagConstraints.CENTER;
			formatGC.gridx = 0;
			formatGC.gridy = 0;
			formatGC.weighty = .25;
			formatFrame.getContentPane().add(formatTopPanel, formatGC);
			formatGC.anchor = GridBagConstraints.LINE_START;
			formatGC.gridy = 1;
			formatGC.weighty = .75;
			formatFrame.getContentPane().add(formatBottomPanel, formatGC);
			
			formatFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/Icon.png")));
			
			formatFrame.setLocationRelativeTo(mainFrame);
			formatFrame.setVisible(true);
		}
	}
	
	//Change listener for numplayers spinner
	private class NumPlayersChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			numPlayers = (int) numPlayersSpinner.getValue();
		}
	}
	
	//Action listener for about button
	private class AboutButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			BufferedImage buffImg = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
			ImageIcon iconToShow = new ImageIcon(mainFrame.getIconImage());
			Image img = iconToShow.getImage();
			Graphics graph = buffImg.createGraphics();
			graph.drawImage(img, 0, 0, 40, 40, null);
			ImageIcon redrawn = new ImageIcon(buffImg);
			JOptionPane.showMessageDialog(mainFrame, "Smash Character Picker v4.2 "
					+ "by J.K.", 
					"About Smash Character Picker",
					JOptionPane.INFORMATION_MESSAGE, redrawn);
		}
	}
	
	//Action listener for exit button
	private class ExitButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}

}
