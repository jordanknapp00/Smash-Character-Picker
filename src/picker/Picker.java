package picker;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Picker {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				@SuppressWarnings("unused")
				Picker p = new Picker();
			}
		});

	}
	
	// *** INITIALIZE FIELDS *** \\
	
	// * SWING OBJECTS * \\
	
	//frame & panels
	private JFrame frame;
	private JPanel leftPanel;
	private JPanel rightPanel;
	private JPanel tierChancesPanel;
	private JPanel cannotGetPanel;
	private JPanel bottomPanel;
	private JPanel farRightPanel;
	
	//blank label for alignment and stuff
	private JLabel blank = new JLabel(" ");
	
	//right panel components
	private JTextArea results;
	
	//bottom panel components
	private JButton generateButton;
	private JSpinner numPlayersSpinner;
	private JLabel numPlayersLabel;
	private JButton loadButton;
	private JButton soundBoardButton;
	private JButton skipButton;
	private JButton debugButton;
	private JButton statsButton;
	
	//left panel components:
	
	//tier chance panel components
	private JLabel tierChanceLabel1;
	private JLabel tierChanceLabel2;
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
	private JButton applyButton;

	//cannot get panel components
	private JLabel cannotGetSizeLabel;
	private JSpinner cannotGetSizeSpinner;
	private JCheckBox allowSSInCannotGet;
	private JCheckBox allowSInCannotGet;
	
	//far right panel components
	private JCheckBox player1Box;
	private JCheckBox player2Box;
	private JCheckBox player3Box;
	private JCheckBox player4Box;
	private JCheckBox player5Box;
	private JCheckBox player6Box;
	private JCheckBox player7Box;
	private JCheckBox player8Box;
	private JButton switchButton;
	
	// * OTHER FIELDS * \\
	private boolean fileLoaded;
	private ArrayList<ArrayList<String>> linesOfFile;
	private int numBattles;
	private CannotGetQueue cannotGet;
	private int numPlayers;
	private CannotGetQueue[] individualCannotGet;
	private boolean skipping;
	private GenerateButtonActionListener gbal = new GenerateButtonActionListener();
	private ArrayList<String> gotten;
	
	private HashMap<String, double[]> stats;
	private File statsFile;
	private boolean needToSaveStats;
	
	//settings variables
	private int[] tierChances;
	private int[] newTierChances;
	private boolean SSAllowedInCannotGetBuffer;
	private boolean SAllowedInCannotGetBuffer;
	private int cannotGetSize;
	private boolean openedSoundPanel;
	private boolean openedDebugPanel;
	private boolean openedStatsPanel;
	private boolean openedLookupPanel;
	private boolean openedModPanel;
	
	private JTextArea debug;
	private JTextArea statsOutput;
	
	//key for synchronization, hopefully prevent crashes
	private static Object key = new Object();
	
	public Picker() {
		//initialize main frame
		frame = new JFrame("Smash Character Picker");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(835, 435);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new PickerWindowListener());
		
		//Initialize TextAreas before setting look and feel
		results = new JTextArea();
		results.setEditable(false);
		results.setFont(results.getFont().deriveFont(18f));
		statsOutput = new JTextArea();
		statsOutput.setEditable(false);
		statsOutput.setFont(statsOutput.getFont().deriveFont(18f));
		
		//Set look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			int hour = ZonedDateTime.now().getHour();
			int min = ZonedDateTime.now().getMinute();
			int sec = ZonedDateTime.now().getSecond();
			System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + e);
		} catch (InstantiationException e) {
			int hour = ZonedDateTime.now().getHour();
			int min = ZonedDateTime.now().getMinute();
			int sec = ZonedDateTime.now().getSecond();
			System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + e);
		} catch (IllegalAccessException e) {
			int hour = ZonedDateTime.now().getHour();
			int min = ZonedDateTime.now().getMinute();
			int sec = ZonedDateTime.now().getSecond();
			System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + e);
		} catch (UnsupportedLookAndFeelException e) {
			int hour = ZonedDateTime.now().getHour();
			int min = ZonedDateTime.now().getMinute();
			int sec = ZonedDateTime.now().getSecond();
			System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + e);
		}
		
		//initialize fields
		fileLoaded = false;
		linesOfFile = new ArrayList<ArrayList<String>>();
		for(int at = 0; at < 39; at++) {
			linesOfFile.add(new ArrayList<String>());
		}
		cannotGet = new CannotGetQueue();
		numPlayers = 2;
		newTierChances = new int[8];
		tierChances = new int[8];
		tierChances[0] = 10;
		tierChances[1] = 20;
		tierChances[2] = 25;
		tierChances[3] = 25;
		tierChances[4] = 20;
		tierChances[5] = 0;
		tierChances[6] = 0;
		tierChances[7] = 0;
		newTierChances[0] = 10;
		newTierChances[1] = 20;
		newTierChances[2] = 25;
		newTierChances[3] = 25;
		newTierChances[4] = 20;
		newTierChances[5] = 0;
		newTierChances[6] = 0;
		newTierChances[7] = 0;
		cannotGetSize = 5;
		SSAllowedInCannotGetBuffer = false;
		SAllowedInCannotGetBuffer = false;
		openedSoundPanel = false;
		individualCannotGet = new CannotGetQueue[8];
		for(int at = 0; at < 8; at++) {
			individualCannotGet[at] = new CannotGetQueue();
		}
		skipping = false;
		gotten = new ArrayList<String>();
		openedDebugPanel = false;
		openedStatsPanel = false;
		stats = new HashMap<String, double[]>();
		needToSaveStats = false;
		openedLookupPanel = false;
		openedModPanel = false;
		
		//initialize right panel
		rightPanel = new JPanel();
		rightPanel.setBorder(BorderFactory.createTitledBorder("Results"));
		GridBagConstraints gc = new GridBagConstraints();
		rightPanel.setLayout(new BorderLayout());
		rightPanel.add(results);
		rightPanel.setPreferredSize(new Dimension(425, 260));
		
		//initialize far right panel
		farRightPanel = new JPanel();
		farRightPanel.setBorder(BorderFactory.createTitledBorder("Switch"));
		farRightPanel.setLayout(new BoxLayout(farRightPanel, BoxLayout.Y_AXIS));
		farRightPanel.setPreferredSize(new Dimension(40, 260));
		farRightPanel.setToolTipText("Select 2 players and hit the switch button, " +
									 "and they will switch fighters.");
		
		SwitchManager sm = new SwitchManager();
		player1Box = new JCheckBox("Player 1");
		player1Box.addActionListener(sm.new Player1BoxActionListener());
		player2Box = new JCheckBox("Player 2");
		player2Box.addActionListener(sm.new Player2BoxActionListener());
		player3Box = new JCheckBox("Player 3");
		player3Box.addActionListener(sm.new Player3BoxActionListener());
		player4Box = new JCheckBox("Player 4");
		player4Box.addActionListener(sm.new Player4BoxActionListener());
		player5Box = new JCheckBox("Player 5");
		player5Box.addActionListener(sm.new Player5BoxActionListener());
		player6Box = new JCheckBox("Player 6");
		player6Box.addActionListener(sm.new Player6BoxActionListener());
		player7Box = new JCheckBox("Player 7");
		player7Box.addActionListener(sm.new Player7BoxActionListener());
		player8Box = new JCheckBox("Player 8");
		player8Box.addActionListener(sm.new Player8BoxActionListener());
		switchButton = new JButton("Switch");
		switchButton.addActionListener(sm.new SwitchButtonActionListener());
		farRightPanel.add(Box.createRigidArea(new Dimension(0, 25)));
		farRightPanel.add(player1Box);
		farRightPanel.add(Box.createRigidArea(new Dimension(0, 3)));
		farRightPanel.add(player2Box);
		farRightPanel.add(Box.createRigidArea(new Dimension(0, 3)));
		farRightPanel.add(player3Box);
		farRightPanel.add(Box.createRigidArea(new Dimension(0, 3)));
		farRightPanel.add(player4Box);
		farRightPanel.add(Box.createRigidArea(new Dimension(0, 3)));
		farRightPanel.add(player5Box);
		farRightPanel.add(Box.createRigidArea(new Dimension(0, 3)));
		farRightPanel.add(player6Box);
		farRightPanel.add(Box.createRigidArea(new Dimension(0, 3)));
		farRightPanel.add(player7Box);
		farRightPanel.add(Box.createRigidArea(new Dimension(0, 3)));
		farRightPanel.add(player8Box);
		farRightPanel.add(Box.createRigidArea(new Dimension(0, 110)));
		farRightPanel.add(switchButton);
		
		//Initialize bottom panel
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridBagLayout());
		generateButton = new JButton("Generate");
		skipButton = new JButton("Skip");
		skipButton.addActionListener(new SkipButtonActionListener());
		
		//attempt to set up the load button correctly
		loadButton = new JButton("Load");
		try {
			Image loadImage = ImageIO.read(getClass().getResource("/img/Open.png"));
			loadButton.setIcon(new ImageIcon(loadImage));
		} catch (IOException e) {
			int hour = ZonedDateTime.now().getHour();
			int min = ZonedDateTime.now().getMinute();
			int sec = ZonedDateTime.now().getSecond();
			System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + e);
		}
		
		statsButton = new JButton("Stats");
		statsButton.addActionListener(new StatsButtonActionListener());
		debugButton = new JButton("Debug");
		debugButton.addActionListener(new DebugButtonActionListener());
		soundBoardButton = new JButton("Soundboard");
		soundBoardButton.addActionListener(new SoundBoardButtonActionListener());
		generateButton.addActionListener(gbal);
		loadButton.addActionListener(new LoadButtonActionListener());
		SpinnerNumberModel spinner = new SpinnerNumberModel(2, 2, 8, 1);
		numPlayersSpinner = new JSpinner(spinner);
		numPlayersSpinner.addChangeListener(new NumPlayersChangeListener());
		numPlayersLabel = new JLabel("Number of players: ");
		gc.weightx = .03;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.anchor = GridBagConstraints.CENTER;
		bottomPanel.add(loadButton, gc);
		gc.weightx = .1;
		gc.gridx = 1;
		bottomPanel.add(soundBoardButton, gc);
		gc.weightx = .67;
		gc.gridx = 2;
		bottomPanel.add(generateButton, gc);
		gc.weightx = .05;
		gc.gridx = 3;
		bottomPanel.add(skipButton, gc);
		gc.gridx = 4;
		bottomPanel.add(debugButton, gc);
		gc.gridx = 5;
		gc.weightx = .03;
		gc.fill = GridBagConstraints.NONE;
		bottomPanel.add(numPlayersLabel, gc);
		gc.gridx = 6;
		gc.weightx = .05;
		bottomPanel.add(numPlayersSpinner, gc);
		gc.gridx = 7;
		bottomPanel.add(statsButton, gc);
		
		//customChanceRules panel
		tierChanceLabel1 = new JLabel("You can set custom chances for "
				+ "each tier.");
		tierChanceLabel2 = new JLabel("Remember to hit apply!");
		SpinnerNumberModel SSmod = new SpinnerNumberModel(tierChances[0], 0, 100, 1);
		SSTierSpinner = new JSpinner(SSmod);
		SSTierSpinner.addChangeListener(new ChangeListenerSS());
		SSTierLabel = new JLabel("SS tier chance: ");
		SpinnerNumberModel Smod = new SpinnerNumberModel(tierChances[1], 0, 100, 1);
		STierSpinner = new JSpinner(Smod);
		STierSpinner.addChangeListener(new ChangeListenerS());
		STierLabel = new JLabel("S tier chance: ");
		SpinnerNumberModel Amod = new SpinnerNumberModel(tierChances[2], 0, 100, 1);
		ATierSpinner = new JSpinner(Amod);
		ATierSpinner.addChangeListener(new ChangeListenerA());
		ATierLabel = new JLabel("A tier chance: ");
		SpinnerNumberModel Bmod = new SpinnerNumberModel(tierChances[3], 0, 100, 1);
		BTierSpinner = new JSpinner(Bmod);
		BTierSpinner.addChangeListener(new ChangeListenerB());
		BTierLabel = new JLabel("B tier chance: ");
		SpinnerNumberModel Cmod = new SpinnerNumberModel(tierChances[4], 0, 100, 1);
		CTierSpinner = new JSpinner(Cmod);
		CTierSpinner.addChangeListener(new ChangeListenerC());
		CTierLabel = new JLabel("C tier chance: ");
		SpinnerNumberModel Dmod = new SpinnerNumberModel(tierChances[5], 0, 100, 1);
		DTierSpinner = new JSpinner(Dmod);
		DTierSpinner.addChangeListener(new ChangeListenerD());
		DTierLabel = new JLabel("D tier chance: ");
		SpinnerNumberModel Emod = new SpinnerNumberModel(tierChances[6], 0, 100, 1);
		ETierSpinner = new JSpinner(Emod);
		ETierSpinner.addChangeListener(new ChangeListenerE());
		ETierLabel = new JLabel("E tier chance: ");
		SpinnerNumberModel Fmod = new SpinnerNumberModel(tierChances[7], 0, 100, 1);
		FTierSpinner = new JSpinner(Fmod);
		FTierSpinner.addChangeListener(new ChangeListenerF());
		FTierLabel = new JLabel("F tier chance: ");
		applyButton = new JButton("Apply tier chance settings");
		applyButton.addActionListener(new ApplyButtonActionListener());
		applyButton.setToolTipText("Will ensure that the given values are "
				+ "valid, meaning they add up to 100.");
		tierChancesPanel = new JPanel();
		tierChancesPanel.setBorder(BorderFactory.createTitledBorder("Tier chance settings"));
		tierChancesPanel.setLayout(new GridBagLayout());
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = 2;
		tierChancesPanel.add(tierChanceLabel1, gc);
		gc.gridy = 1;
		tierChancesPanel.add(tierChanceLabel2, gc);
		gc.anchor = GridBagConstraints.CENTER;
		gc.anchor = GridBagConstraints.LINE_END;
		gc.gridwidth = 1;
		gc.gridy = 2;
		tierChancesPanel.add(SSTierLabel, gc);
		gc.gridy = 3;
		tierChancesPanel.add(STierLabel, gc);
		gc.gridy = 4;
		tierChancesPanel.add(ATierLabel, gc);
		gc.gridy = 5;
		tierChancesPanel.add(BTierLabel, gc);
		gc.gridy = 6;
		tierChancesPanel.add(CTierLabel, gc);
		gc.gridy = 7;
		tierChancesPanel.add(DTierLabel, gc);
		gc.gridy = 8;
		tierChancesPanel.add(ETierLabel, gc);
		gc.gridy = 9;
		tierChancesPanel.add(FTierLabel, gc);
		gc.gridx = 1;
		gc.gridy = 2;
		gc.anchor = GridBagConstraints.LINE_START;
		tierChancesPanel.add(SSTierSpinner, gc);
		gc.gridy = 3;
		tierChancesPanel.add(STierSpinner, gc);
		gc.gridy = 4;
		tierChancesPanel.add(ATierSpinner, gc);
		gc.gridy = 5;
		tierChancesPanel.add(BTierSpinner, gc);
		gc.gridy = 6;
		tierChancesPanel.add(CTierSpinner, gc);
		gc.gridy = 7;
		tierChancesPanel.add(DTierSpinner, gc);
		gc.gridy = 8;
		tierChancesPanel.add(ETierSpinner, gc);
		gc.gridy = 9;
		tierChancesPanel.add(FTierSpinner, gc);
		gc.gridx = 0;
		gc.gridy = 10;
		gc.gridwidth = 2;
		gc.anchor = GridBagConstraints.CENTER;
		tierChancesPanel.add(blank, gc);
		gc.gridy = 11;
		tierChancesPanel.add(applyButton, gc);
		tierChancesPanel.setPreferredSize(new Dimension(275, 260));
		
		//cannotGetRules panel
		cannotGetSizeLabel = new JLabel("Size of the \"Cannot get\" "
				+ "buffer: ");
		SpinnerNumberModel bleh = new SpinnerNumberModel(cannotGetSize, 0, 15, 1);
		cannotGetSizeSpinner = new JSpinner(bleh);
		cannotGetSizeSpinner.addChangeListener(new CannotGetSizeChangeListener());
		cannotGetSizeLabel.setToolTipText("<html>If this is set too high, the "
				+ "program could freeze, because there may be no valid "
				+ "fighters left for it to pick.<br>5 is the recommended size. "
				+ "Note that you have the option of whether or not S & SS "
				+ "tiers are allowed in this buffer.</html>");
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
		gc.gridwidth = 1;
		gc.gridheight = 1;
		gc.gridy = 0;
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.LINE_END;
		cannotGetPanel.add(cannotGetSizeLabel, gc);
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.LINE_START;
		cannotGetPanel.add(cannotGetSizeSpinner, gc);
		gc.gridx = 0;
		gc.gridy = 1;
		gc.anchor = GridBagConstraints.LINE_END;
		cannotGetPanel.add(allowSSInCannotGet, gc);
		gc.gridy = 2;
		cannotGetPanel.add(allowSInCannotGet, gc);
		
		//combine left panels
		leftPanel = new JPanel();
		leftPanel.setLayout(new GridBagLayout());
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = .5;
		gc.anchor = GridBagConstraints.CENTER;
		gc.fill = GridBagConstraints.BOTH;
		leftPanel.add(tierChancesPanel, gc);
		gc.gridy = 1;
		leftPanel.add(cannotGetPanel, gc);
		
		//put it all together
		frame.getContentPane().setLayout(new GridBagLayout());
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = .30;
		gc.weighty = .85;
		gc.anchor = GridBagConstraints.CENTER;
		gc.fill = GridBagConstraints.BOTH;
		frame.add(leftPanel, gc);
		gc.weightx = 1;
		gc.gridx = 1;
		frame.add(rightPanel, gc);
		gc.gridy = 1;
		gc.gridx = 0;
		gc.weightx = 1;
		gc.weighty = .15;
		gc.gridwidth = 3;
		frame.add(bottomPanel, gc);
		gc.gridy = 0;
		gc.gridx = 2;
		gc.gridwidth = 1;
		frame.add(farRightPanel, gc);
		
		debug = new JTextArea(5, 101);
		debug.setEditable(false);
		
		//Create icon
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/Icon.png")));
				
		//Finally make it visible
		frame.setVisible(true);
		
	}
	
	private class StatsButtonActionListener implements ActionListener {
		private JFrame statsFrame;
		private JPanel upperStatsPanel;
		private JPanel lowerStatsPanel;
		
		private JLabel playerLabel;
		private JSpinner winnerSpinner;
		private JButton pickWinnerButton;
		private JButton lookupButton;
		private JButton reloadButton;
		
		private int selectedWinner;
		private int battleWhenLastPressed;
		private int lastSelectedWinner;
		
		//this warning is suppressed because it'll never actually cause a problem
		//unless the user specifically does something stupid. under normal use
		//it won't happen, and honestly i'm just tired of the warning.
		@SuppressWarnings("unchecked")
		public void actionPerformed(ActionEvent e) {
			if(!fileLoaded) {
				JOptionPane.showMessageDialog(frame, "You must load a tier list"
						+ " file before you can use the stats panel.", "Smash "
						+ "Character Picker", JOptionPane.WARNING_MESSAGE);
				return;
			}
			else if(openedStatsPanel) {
				return;
			}
			
			if(!needToSaveStats) {
				statsFile = new File("smash stats.sel");
				try {
					if(statsFile.createNewFile()) {
						JOptionPane.showMessageDialog(frame, "A stats file has not been detected in this folder. "
								+ "One will now be created.", "Smash Character Picker",
								JOptionPane.INFORMATION_MESSAGE);
						for(int at = 0; at < 24; at++) {
							for(String fighter: linesOfFile.get(at)) {
								double[] freshDouble = new double[] {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
								stats.put(fighter, freshDouble);
							}
						}
					}
					else {
						FileInputStream fis = new FileInputStream(statsFile);
						ObjectInputStream ois = new ObjectInputStream(fis);
						stats = (HashMap<String, double[]>) ois.readObject();
						ois.close();
						fis.close();
						//add any new fighters if need be
						for(int at = 0; at < 24; at++) {
							for(String fighter: linesOfFile.get(at)) {
								if(!stats.containsKey(fighter)) {
									System.out.println("[DEBUG]: " + fighter + " was not found in the stats file. Adding them.");
									debug.append("[DEBUG]: " + fighter + " was not found in the stats file. Adding them.\n");
									double[] freshDouble = new double[] {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
									stats.put(fighter, freshDouble);
								}
							}
						}
					}
				} catch (IOException e1) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + e1);
					debug.append("[" + hour + ":" + min + ":" + sec + "]: " + e1 + "\n");
					return;
				} catch (ClassNotFoundException e1) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + e1);
					debug.append("[" + hour + ":" + min + ":" + sec + "]: " + e1 + "\n");
					return;
				}
				
				System.out.println("[DEBUG]: The following data has been loaded as the stats file:");
				debug.append("[DEBUG]: The following data has been loaded as the stats file:\n");
				for(String fighter: stats.keySet()) {
					System.out.print("         " + fighter + ",");
					debug.append("         " + fighter + ",");
					double[] statValues = stats.get(fighter);
					for(int at = 0; at < 14; at++) {
						System.out.print(statValues[at] + ",");
						debug.append(statValues[at] + ",");
					}
					System.out.println(statValues[15]);
					debug.append(statValues[15] + "\n");
				}
			}
			
			statsFrame = new JFrame("Smash Character Picker");
			statsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			statsFrame.setSize(325, 435);
			statsFrame.setResizable(false);
			statsFrame.setLocation(frame.getX() + frame.getWidth(), frame.getY());
			statsFrame.addWindowListener(new StatsWindowListener());
			
			upperStatsPanel = new JPanel();
			upperStatsPanel.setLayout(new BorderLayout());
			upperStatsPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));
			upperStatsPanel.add(statsOutput, BorderLayout.CENTER);
			
			lowerStatsPanel = new JPanel();
			lowerStatsPanel.setLayout(new GridBagLayout());
			playerLabel = new JLabel("Player: ");
			SpinnerNumberModel winnerSpinnerModel = new SpinnerNumberModel(1, 1, 8, 1);
			winnerSpinner = new JSpinner(winnerSpinnerModel);
			winnerSpinner.addChangeListener(new WinnerSpinnerChangeListener());
			pickWinnerButton = new JButton("Select winner");
			pickWinnerButton.addActionListener(new PickWinnerActionListener());
			lookupButton = new JButton("Look up stats");
			lookupButton.addActionListener(new LookupActionListener());
			reloadButton = new JButton("⭯");
			reloadButton.addActionListener(new ReloadActionListener());
			
			GridBagConstraints gc = new GridBagConstraints();
			gc.gridx = 0;
			gc.gridy = 0;
			gc.weighty = 1;
			gc.weightx = .1;
			gc.fill = GridBagConstraints.HORIZONTAL;
			lowerStatsPanel.add(playerLabel, gc);
			gc.gridx = 1;
			gc.weightx = .05;
			lowerStatsPanel.add(winnerSpinner, gc);
			gc.gridx = 2;
			gc.weightx = .3;
			lowerStatsPanel.add(Box.createRigidArea(new Dimension(3, 0)), gc);
			gc.gridx = 3;
			gc.weightx = .35;
			lowerStatsPanel.add(pickWinnerButton, gc);
			gc.gridx = 4;
			gc.weightx = .25;
			lowerStatsPanel.add(lookupButton, gc);
			gc.gridx = 5;
			lowerStatsPanel.add(reloadButton, gc);
			
			gc.gridx = 0;
			gc.gridy = 0;
			gc.weightx = 1;
			gc.weighty = .98;
			gc.fill = GridBagConstraints.BOTH;
			gc.anchor = GridBagConstraints.CENTER;
			statsFrame.setLayout(new GridBagLayout());
			JScrollPane scrollPane = new JScrollPane(upperStatsPanel);
			scrollPane.getVerticalScrollBar().setUnitIncrement(16);
			statsFrame.add(scrollPane, gc);
			gc.gridy = 1;
			gc.weighty = .02;
			statsFrame.add(lowerStatsPanel, gc);
			
			openedStatsPanel = true;
			selectedWinner = 1;
			battleWhenLastPressed = -1;
			needToSaveStats = true;
			lastSelectedWinner = -1;
			
			statsFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/Icon.png")));
			
			statsFrame.setVisible(true);
		}
		
		private class ReloadActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				if(numBattles != 0) {
					GenerateButtonActionListener gbal = new GenerateButtonActionListener();
					gbal.updateStatsScreen();
				}
			}
		}
		
		private class LookupActionListener implements ActionListener {
			private JFrame lookupFrame;
			private JPanel topPanel;
			private JPanel midPanel;
			private JPanel bottomPanel;
			
			private JTextField toLookUp;
			private JButton lookupButton;
			
			private JComboBox<String> sortOptions;
			private JButton sortButton;
			
			private JTextField toModify;
			private JButton modifyButton;
			
			private int selectedOption;
			
			public void actionPerformed(ActionEvent e) {
				if(openedLookupPanel) {
					return;
				}
				
				lookupFrame = new JFrame("Smash Character Picker");
				lookupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				lookupFrame.setSize(325, 200);
				lookupFrame.setResizable(false);
				lookupFrame.setLocationRelativeTo(frame);
				
				topPanel = new JPanel();
				topPanel.setBorder(BorderFactory.createTitledBorder("Lookup"));
				toLookUp = new JTextField(20);
				toLookUp.addActionListener(new LookupButtonActionListener());
				lookupButton = new JButton("Look up");
				lookupButton.addActionListener(new LookupButtonActionListener());
				topPanel.add(toLookUp);
				topPanel.add(lookupButton);
				
				midPanel = new JPanel();
				midPanel.setBorder(BorderFactory.createTitledBorder("Sort by"));
				String[] options = {"Overall win rate", "P1 win rate", "P2 win rate",
						"P3 win rate", "P4 win rate", "P5 win rate", "P6 win rate",
						"P7 win rate", "P8 win rate", "Total battles"};
				sortOptions = new JComboBox<String>(options);
				sortOptions.setPreferredSize(new Dimension(200, 19));
				sortOptions.addActionListener(new SortOptionsActionListener());
				sortButton = new JButton("Sort");
				sortButton.addActionListener(new SortButtonActionListener());
				midPanel.add(sortOptions);
				midPanel.add(sortButton);
				
				bottomPanel = new JPanel();
				bottomPanel.setBorder(BorderFactory.createTitledBorder("Modify data"));
				toModify = new JTextField(20);
				toModify.addActionListener(new ModifyActionListener());
				modifyButton = new JButton("Modify data");
				modifyButton.addActionListener(new ModifyActionListener());
				bottomPanel.add(toModify);
				bottomPanel.add(modifyButton);
				
				lookupFrame.setLayout(new GridBagLayout());
				GridBagConstraints gc = new GridBagConstraints();
				gc.gridx = 0;
				gc.gridy = 0;
				gc.weightx = 1;
				gc.weighty = .33;
				gc.anchor = GridBagConstraints.CENTER;
				gc.fill = GridBagConstraints.BOTH;
				lookupFrame.add(topPanel, gc);
				gc.gridy = 1;
				lookupFrame.add(midPanel, gc);
				gc.gridy = 2;
				lookupFrame.add(bottomPanel, gc);
				
				selectedOption = 0;
				
				lookupFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/Icon.png")));
				
				lookupFrame.addWindowListener(new LookupWindowListener());
				
				lookupFrame.setVisible(true);
				openedLookupPanel = true;
			}
			
			private class LookupWindowListener implements WindowListener {
				public void windowOpened(WindowEvent e) {
				}

				public void windowClosing(WindowEvent e) {
					openedLookupPanel = false;
				}

				public void windowClosed(WindowEvent e) {
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
			
			private class ModifyActionListener implements ActionListener {
				private JFrame modFrame;
				
				private JPanel modPanel;
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
				
				private int playerSelected;
				private String newName;
				private double[] newValues;
				private String oldName;
				
				public void actionPerformed(ActionEvent e) {
					if(openedModPanel) {
						return;
					}
					
					String modify = toModify.getText();
					if(stats.containsKey(modify)) {
						double[] fighterStats = stats.get(modify);
						newName = modify;
						oldName = modify;
						newValues = new double[16];
						
						for(int at = 0; at < 16; at++) {
							newValues[at] = fighterStats[at];
						}
						
						playerSelected = 1;
						
						modFrame = new JFrame();
						modFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
						modFrame.setSize(250, 230);
						modFrame.setResizable(false);
						modFrame.setLocationRelativeTo(frame);
						
						modPanel = new JPanel();
						modPanel.setBorder(BorderFactory.createTitledBorder("Modify " + modify + " stats"));
						
						warningLabel = new JLabel("<html>WARNING: Once you hit apply,<br>changes are permanent!</html>");
						
						playerLabel = new JLabel("Select player: ");
						SpinnerNumberModel playerModel = new SpinnerNumberModel(1, 1, 8, 1);
						player = new JSpinner(playerModel);
						player.addChangeListener(new PlayerChangeListener());
						
						winsLabel = new JLabel("Adjust number of wins: ");
						SpinnerNumberModel winsModel = new SpinnerNumberModel(fighterStats[0], 0, 9999, 1);
						wins = new JSpinner(winsModel);
						wins.addChangeListener(new WinsChangeListener());
						
						battlesLabel = new JLabel("Adjust number of battles: ");
						SpinnerNumberModel battleModel = new SpinnerNumberModel(fighterStats[1], 0, 9999, 1);
						battles = new JSpinner(battleModel);
						battles.addChangeListener(new BattlesChangeListener());
						
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
						
						modPanel.setLayout(new GridBagLayout());
						GridBagConstraints gc = new GridBagConstraints();
						gc.gridx = 0;
						gc.gridy = 0;
						gc.anchor = GridBagConstraints.LINE_START;
						gc.fill = GridBagConstraints.HORIZONTAL;
						modPanel.add(playerLabel, gc);
						gc.gridx = 1;
						modPanel.add(player, gc);
						gc.gridx = 0;
						gc.gridy = 1;
						modPanel.add(winsLabel, gc);
						gc.gridx = 1;
						modPanel.add(wins, gc);
						gc.gridx = 0;
						gc.gridy = 2;
						modPanel.add(battlesLabel, gc);
						gc.gridx = 1;
						modPanel.add(battles, gc);
						gc.gridx = 0;
						gc.gridy = 3;
						modPanel.add(rename, gc);
						gc.gridx = 1;
						modPanel.add(renameButton, gc);
						gc.gridx = 0;
						gc.gridy = 4;
						gc.gridwidth = 2;
						modPanel.add(resetAll, gc);
						gc.gridy = 5;
						modPanel.add(remove, gc);
						gc.gridy = 6;
						modPanel.add(apply, gc);
						gc.gridy = 7;
						gc.anchor = GridBagConstraints.CENTER;
						modPanel.add(warningLabel, gc);
						
						modFrame.add(modPanel);
						
						modFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/Icon.png")));
						
						modFrame.addWindowListener(new ModWindowListener());
						
						modFrame.setVisible(true);
						openedModPanel = true;
					}
					else if(modify != null) {
						statsOutput.setText("Fighter " + modify + " not found!\n");
					}
				}
				
				private class ModWindowListener implements WindowListener {
					public void windowOpened(WindowEvent e) {
					}

					public void windowClosing(WindowEvent e) {
						openedModPanel = false;
					}

					public void windowClosed(WindowEvent e) {
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
				
				private class ApplyActionListener implements ActionListener {
					public void actionPerformed(ActionEvent e) {
						//check for validity -- there can't be more wins than battles
						for(int at = 0; at < 15; at += 2) {
							if(newValues[at] > newValues[at + 1]) {
								JOptionPane.showMessageDialog(modFrame, "There cannot be more wins than battles.",
										"Smash Character Picker", JOptionPane.ERROR_MESSAGE);
								return;
							}
						}
						
						stats.remove(oldName);
						stats.put(newName, newValues);
						JOptionPane.showMessageDialog(modFrame, "Stats updated. You can close the mod window now.",
								"Smash Character Picker", JOptionPane.INFORMATION_MESSAGE);
					}
				}
				
				private class RemoveActionListener implements ActionListener {
					public void actionPerformed(ActionEvent e) {
						if(JOptionPane.showConfirmDialog(modFrame, "Are you sure you want to remove " + oldName + " from the system?", 
								"Smash Character Picker", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)
								== JOptionPane.YES_OPTION) {
							stats.remove(oldName);
							JOptionPane.showMessageDialog(modFrame, oldName + " removed. You can close the mod window now.",
									"Smash Character Picker", JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
				
				private class ResetAllActionListener implements ActionListener {
					public void actionPerformed(ActionEvent e) {
						newName = oldName;
						double[] oldStats = stats.get(oldName);
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
						
						if(stats.containsKey(toChange)) {
							JOptionPane.showMessageDialog(modFrame, "There's already a character called " + toChange + ". "
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
				
				private class BattlesChangeListener implements ChangeListener {
					public void stateChanged(ChangeEvent e) {
						newValues[2 * (playerSelected - 1) + 1] = (double) battles.getValue();
					}
				}
				
				private class WinsChangeListener implements ChangeListener {
					public void stateChanged(ChangeEvent e) {
						newValues[2 * (playerSelected - 1)] = (double) wins.getValue();
					}
				}
				
				private class PlayerChangeListener implements ChangeListener {
					public void stateChanged(ChangeEvent e) {
						playerSelected = (int) player.getValue();
						wins.setValue(newValues[2 * (playerSelected - 1)]);
						battles.setValue(newValues[2 * (playerSelected - 1) + 1]);
					}
				}
			}
			
			private class SortButtonActionListener implements ActionListener {
				public void actionPerformed(ActionEvent e) {
					statsOutput.setText("");
					ComparableArray[] data = new ComparableArray[stats.size()];
					switch(selectedOption) {
						case 0:
							statsOutput.append("Sorted by overall win rate:\n");
							data = getTotalStats(3);
							break;
						case 1:
							statsOutput.append("Sorted by Player 1's win rate:\n");
							data = getPlayerStats(0);
							break;
						case 2:
							statsOutput.append("Sorted by Player 2's win rate:\n");
							data = getPlayerStats(1);
							break;
						case 3:
							statsOutput.append("Sorted by Player 3's win rate:\n");
							data = getPlayerStats(2);
							break;
						case 4:
							statsOutput.append("Sorted by Player 4's win rate:\n");
							data = getPlayerStats(3);
							break;
						case 5:
							statsOutput.append("Sorted by Player 5's win rate:\n");
							data = getPlayerStats(4);
							break;
						case 6:
							statsOutput.append("Sorted by Player 6's win rate:\n");
							data = getPlayerStats(5);
							break;
						case 7:
							statsOutput.append("Sorted by Player 7's win rate:\n");
							data = getPlayerStats(6);
							break;
						case 8:
							statsOutput.append("Sorted by Player 8's win rate:\n");
							data = getPlayerStats(7);
							break;
						case 9:
							statsOutput.append("Sorted by total battles:\n");
							data = getTotalStats(2);
							Arrays.sort(data);
							data = reverse(data);
							int at = 1;
							for(ComparableArray arrAt: data) {
								statsOutput.append(at + ". " + arrAt.getName() + " - " + arrAt.getBattles() + " battles\n");
								at++;
							}
							return;
						default:
							statsOutput.append("Error, unrecognized sort ID.\n");
							return;
					}
					
					Arrays.sort(data);
					data = reverse(data);
					int at = 1;
					for(ComparableArray arrAt: data) {
						statsOutput.append(at + ". " + arrAt + "\n");
						at++;
					}
				}
				
				private ComparableArray[] getTotalStats(int column) {
					ComparableArray[] data = new ComparableArray[stats.size()];
					int indexAt = 0;
					for(String fighter: stats.keySet()) {
						double totalWins = 0;
						double totalBattles = 0;
						double[] fighterStats = stats.get(fighter);
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
					ComparableArray[] data = new ComparableArray[stats.size()];
					int at = 0;
					for(String fighter: stats.keySet()) {
						double[] fighterStats = stats.get(fighter);
						data[at] = new ComparableArray(fighter, fighterStats[2 * player], fighterStats[2 * player + 1], 3);
						at++;
					}
					
					return data;
				}
				
				private ComparableArray[] reverse(ComparableArray[] start) {
					ComparableArray[] retArr = new ComparableArray[start.length];
					int j = start.length;
					for(int at = 0; at < start.length; at++) {
						retArr[j - 1] = start[at];
						j--;
					}
					
					return retArr;
				}
			}
			
			private class SortOptionsActionListener implements ActionListener {
				public void actionPerformed(ActionEvent e) {
					selectedOption = sortOptions.getSelectedIndex();
				}		
			}
			
			private class LookupButtonActionListener implements ActionListener {
				public void actionPerformed(ActionEvent e) {
					statsOutput.setText("");
					String lookup = toLookUp.getText();
					if(stats.containsKey(lookup)) {
						statsOutput.append("Stats for " + lookup + ":\n");
						double[] fighterStats = stats.get(lookup);
						double totalWins = 0;
						double totalFights = 0;
						for(int at = 0; at < 8; at++) {
							totalWins += fighterStats[2 * at];
							totalFights += fighterStats[2 * at + 1];
							statsOutput.append("Player " + (at + 1) + " W%: " + printDouble((fighterStats[2 * at] / fighterStats[2 * at + 1]) * 100) + "% (" + (int)fighterStats[2 * at] + "/" + (int)fighterStats[2 * at + 1] + ")\n");
						}
						statsOutput.append("Overall W%: " + printDouble((totalWins / totalFights) * 100) + "% (" + (int)totalWins + "/" + (int)totalFights + ")\n");
					}
					else if(lookup != null) {
						statsOutput.append("Fighter " + lookup + " not found!\n");
					}
				}
			}
		}
		
		private class PickWinnerActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				if(numBattles == battleWhenLastPressed) {
					stats.get(gotten.get(lastSelectedWinner - 1))[2 * (lastSelectedWinner - 1)]--;
					stats.get(gotten.get(selectedWinner - 1))[2 * (selectedWinner - 1)]++;
					lastSelectedWinner = selectedWinner;
					GenerateButtonActionListener gbal = new GenerateButtonActionListener();
					gbal.updateStatsScreen();
				}
				else if(numBattles == 0) {
					JOptionPane.showMessageDialog(frame, "There has to be a battle before there can be a winner.",
							"Smash Character Picker", JOptionPane.WARNING_MESSAGE);
					return;
				}
				else {
					battleWhenLastPressed = numBattles;
					lastSelectedWinner = selectedWinner;
					for(int at = 0; at < numPlayers; at++) {
						if((at + 1) == selectedWinner) {
							stats.get(gotten.get(at))[2 * at]++;
							stats.get(gotten.get(at))[2 * at + 1]++;
						}
						else {
							stats.get(gotten.get(at))[2 * at + 1]++;
						}
					}
					GenerateButtonActionListener gbal = new GenerateButtonActionListener();
					gbal.updateStatsScreen();
				}
			}
		}
		
		private class WinnerSpinnerChangeListener implements ChangeListener {
			public void stateChanged(ChangeEvent e) {
				selectedWinner = (int) winnerSpinner.getValue();
			}
		}
		
		private class StatsWindowListener implements WindowListener {

			public void windowOpened(WindowEvent e) {
			}

			public void windowClosing(WindowEvent e) {
				openedStatsPanel = false;
			}

			public void windowClosed(WindowEvent e) {
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
	
	//package visibility, allowing ComparableArray to use this method
	static String printDouble(double num) {
		if(num >= 0) {
			return new BigDecimal(String.valueOf(num)).setScale(2, RoundingMode.FLOOR).toString();
		}
		else if(num < 0) {
			return new BigDecimal(String.valueOf(num)).setScale(2, RoundingMode.CEILING).toString();
		}
		else {
			return "NaN";
		}
	}
	
	private class PickerWindowListener implements WindowListener {

		public void windowOpened(WindowEvent e) {
		}

		public void windowClosing(WindowEvent e) {
			if(needToSaveStats) {
				try {
					FileOutputStream fos = new FileOutputStream(statsFile);
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(stats);
					oos.close();
					fos.close();
				} catch (FileNotFoundException e1) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + e1);
					debug.append("[" + hour + ":" + min + ":" + sec + "]: " + e1 + "\n");
					JOptionPane.showMessageDialog(frame, "FileNotFoundException in saving stats file.",
							"Smash Character Picker", JOptionPane.ERROR_MESSAGE);
				} catch (IOException e1) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + e1);
					debug.append("[" + hour + ":" + min + ":" + sec + "]: " + e1 + "\n");
					JOptionPane.showMessageDialog(frame, "IOException in saving stats file.",
							"Smash Character Picker", JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		public void windowClosed(WindowEvent e) {
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
	
	private class SwitchManager {
		private int numSelected;
		private ArrayList<Integer> selected;
		
		private SwitchManager() {
			numSelected = 0;
			selected = new ArrayList<Integer>();
		}
		
		private class Player1BoxActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				if(numSelected == 2 && player1Box.isSelected()) {
					JOptionPane.showMessageDialog(frame, "Only select 2 players.",
							"Smash Character Picker", JOptionPane.WARNING_MESSAGE);
					player1Box.setSelected(false);
				}
				else {
					if(selected.contains(1)) {
						selected.remove(Integer.valueOf(1));
						numSelected--;
					}
					else {
						selected.add(1);
						numSelected++;
					}
				}
			}
		}
		
		private class Player2BoxActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				if(numSelected == 2 && player2Box.isSelected()) {
					JOptionPane.showMessageDialog(frame, "Only select 2 players.",
							"Smash Character Picker", JOptionPane.WARNING_MESSAGE);
					player2Box.setSelected(false);
				}
				else {
					if(selected.contains(2)) {
						selected.remove(Integer.valueOf(2));
						numSelected--;
					}
					else {
						selected.add(2);
						numSelected++;
					}
				}
			}
		}
		
		private class Player3BoxActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				if(numPlayers < 3) {
					JOptionPane.showMessageDialog(frame, "There are not that "
							+ "many players present.", "Smash Character Picker",
							JOptionPane.WARNING_MESSAGE);
					player3Box.setSelected(false);
				}
				else if(numSelected == 2 && player3Box.isSelected()) {
					JOptionPane.showMessageDialog(frame, "Only select 2 players.",
							"Smash Character Picker", JOptionPane.WARNING_MESSAGE);
					player3Box.setSelected(false);
				}
				else {
					if(selected.contains(3)) {
						selected.remove(Integer.valueOf(3));
						numSelected--;
					}
					else {
						selected.add(3);
						numSelected++;
					}
				}
			}
		}
		
		private class Player4BoxActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				if(numPlayers < 4) {
					JOptionPane.showMessageDialog(frame, "There are not that "
							+ "many players present.", "Smash Character Picker",
							JOptionPane.WARNING_MESSAGE);
					player4Box.setSelected(false);
				}
				else if(numSelected == 2 && player4Box.isSelected()) {
					JOptionPane.showMessageDialog(frame, "Only select 2 players.",
							"Smash Character Picker", JOptionPane.WARNING_MESSAGE);
					player4Box.setSelected(false);
				}
				else {
					if(selected.contains(4)) {
						selected.remove(Integer.valueOf(4));
						numSelected--;
					}
					else {
						selected.add(4);
						numSelected++;
					}
				}
			}
		}
		
		private class Player5BoxActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				if(numPlayers < 5) {
					JOptionPane.showMessageDialog(frame, "There are not that "
							+ "many players present.", "Smash Character Picker",
							JOptionPane.WARNING_MESSAGE);
					player5Box.setSelected(false);
				}
				else if(numSelected == 2 && player5Box.isSelected()) {
					JOptionPane.showMessageDialog(frame, "Only select 2 players.",
							"Smash Character Picker", JOptionPane.WARNING_MESSAGE);
					player5Box.setSelected(false);
				}
				else {
					if(selected.contains(5)) {
						selected.remove(Integer.valueOf(5));
						numSelected--;
					}
					else {
						selected.add(5);
						numSelected++;
					}
				}
			}
		}
		
		private class Player6BoxActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				if(numPlayers < 6) {
					JOptionPane.showMessageDialog(frame, "There are not that "
							+ "many players present.", "Smash Character Picker",
							JOptionPane.WARNING_MESSAGE);
					player6Box.setSelected(false);
				}
				else if(numSelected == 2 && player6Box.isSelected()) {
					JOptionPane.showMessageDialog(frame, "Only select 2 players.",
							"Smash Character Picker", JOptionPane.WARNING_MESSAGE);
					player6Box.setSelected(false);
				}
				else {
					if(selected.contains(6)) {
						selected.remove(Integer.valueOf(6));
						numSelected--;
					}
					else {
						selected.add(6);
						numSelected++;
					}
				}
			}
		}
		
		private class Player7BoxActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				if(numPlayers < 7) {
					JOptionPane.showMessageDialog(frame, "There are not that "
							+ "many players present.", "Smash Character Picker",
							JOptionPane.WARNING_MESSAGE);
					player7Box.setSelected(false);
				}
				else if(numSelected == 2 && player7Box.isSelected()) {
					JOptionPane.showMessageDialog(frame, "Only select 2 players.",
							"Smash Character Picker", JOptionPane.WARNING_MESSAGE);
					player7Box.setSelected(false);
				}
				else {
					if(selected.contains(7)) {
						selected.remove(Integer.valueOf(7));
						numSelected--;
					}
					else {
						selected.add(7);
						numSelected++;
					}
				}
			}
		}
		
		private class Player8BoxActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				if(numPlayers < 8) {
					JOptionPane.showMessageDialog(frame, "There are not that "
							+ "many players present.", "Smash Character Picker",
							JOptionPane.WARNING_MESSAGE);
					player8Box.setSelected(false);
				}
				else if(numSelected == 2 && player8Box.isSelected()) {
					JOptionPane.showMessageDialog(frame, "Only select 2 players.",
							"Smash Character Picker", JOptionPane.WARNING_MESSAGE);
					player8Box.setSelected(false);
				}
				else {
					if(selected.contains(8)) {
						selected.remove(Integer.valueOf(8));
						numSelected--;
					}
					else {
						selected.add(8);
						numSelected++;
					}
				}
			}
		}
		
		private class SwitchButtonActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				int p1 = selected.get(0) - 1;
				int p2 = selected.get(1) - 1;
				String char1 = gotten.get(p1);
				String char2 = gotten.get(p2);
				int tier1 = individualCannotGet[p1].getAndRemoveLastTier();
				int tier2 = individualCannotGet[p2].getAndRemoveLastTier();
				
				individualCannotGet[p1].add(char2, tier2);
				individualCannotGet[p2].add(char1, tier1);
				
				gotten.remove(char1);
				gotten.remove(char2);
				if(p2 > p1) {
					gotten.add(p1, char2);
					gotten.add(p2, char1);
				}
				else {
					gotten.add(p2, char1);
					gotten.add(p1, char2);
				}
				
				//refresh the screen
				results.setText("");
				results.setText("Battle #" + numBattles + ":\n");
				for(int at = 0; at < numPlayers; at++) {
					results.append("Player " + (at + 1) + " got " + gotten.get(at) + ", " + tierToString(individualCannotGet[at].getLastTier()) + ".\n");
				}
				GenerateButtonActionListener gbal = new GenerateButtonActionListener();
				gbal.updateStatsScreen();
				
				player1Box.setSelected(false);
				player2Box.setSelected(false);
				player3Box.setSelected(false);
				player4Box.setSelected(false);
				player5Box.setSelected(false);
				player6Box.setSelected(false);
				player7Box.setSelected(false);
				player8Box.setSelected(false);
				numSelected = 0;
				selected.clear();
			}
			
			private String tierToString(int tier) {
				switch(tier) {
					case 0:
						return "Upper Double S tier";
					case 1:
						return "Double S tier";
					case 2:
						return "Lower Double S tier";
					case 3:
						return "Upper S tier";
					case 4:
						return "Mid S tier";
					case 5:
						return "Lower S tier";
					case 6:
						return "Upper A tier";
					case 7:
						return "Mid A tier";
					case 8:
						return "Lower A tier";
					case 9:
						return "Upper B tier";
					case 10:
						return "Mid B tier";
					case 11:
						return "Lower B tier";
					case 12:
						return "Upper C tier";
					case 13:
						return "Mid C tier";
					case 14:
						return "Lower C tier";
					case 15:
						return "Upper D tier";
					case 16:
						return "Mid D tier";
					case 17:
						return "Lower D tier";
					case 18:
						return "Upper E tier";
					case 19:
						return "Mid E tier";
					case 20:
						return "Lower E tier";
					case 21:
						return "Upper F tier";
					case 22:
						return "Mid F tier";
					case 23:
						return "Lower F tier";
					default:
						return "Invalid tier";
				}
			}
		}
	}
	
	private class SkipButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			skipping = true;
			gbal.actionPerformed(null);
		}
		
	}
	
	private class DebugButtonActionListener implements ActionListener {
		private JFrame debugFrame;
		private JPanel debugPanel;
		
		public void actionPerformed(ActionEvent e) {
			if(openedDebugPanel) {
				return;
			}
			
			debugFrame = new JFrame("Debug");
			debugFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			debugFrame.setSize(835, 200);
			debugFrame.setResizable(false);
			
			debugFrame.setLocation(frame.getX(), (int) (frame.getY() + 2 * debugFrame.getHeight() + 30));
			debugFrame.addWindowListener(new DebugWindowListener());
			
			debugPanel = new JPanel();
			debugPanel.setLayout(new BorderLayout());
			debugPanel.add(debug, BorderLayout.LINE_START);
			JScrollPane scrollPane = new JScrollPane(debugPanel);
			scrollPane.getVerticalScrollBar().setUnitIncrement(16);
			debugFrame.add(scrollPane);
			
			debug.append("[DEBUG]: You've opened the debug panel.\n");
			
			debugFrame.setVisible(true);
			openedDebugPanel = true;
		}
		
		private class DebugWindowListener implements WindowListener {
			public void windowOpened(WindowEvent e) {
			}

			public void windowClosing(WindowEvent e) {
				openedDebugPanel = false;
				debug.append("[DEBUG]: You've closed the debug panel.\n");
			}

			public void windowClosed(WindowEvent e) {
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
	
	private class SoundBoardButtonActionListener implements ActionListener {
		private JFrame soundboardFrame;
		private JPanel soundboardPanel;
		
		private JButton doItAgainButton;
		private JButton neutralAerialButton;
		private JButton trashManButton;
		private JButton pikachuBustedButton;
		private JButton bruhSoundEffectButton;
		private JButton angerousNowButton;
		private JButton theWorstButton;
		private JButton excuseMeButton;
		private JButton bscuseMeButton;
		private JButton ghoulButton;
		private JButton whatButton;
		private JButton thatMakesMeFeelAngryButton;
		private JButton daringTodayButton;
		private JButton vsauceButton;
		private JButton despiseHimButton;
		private JButton whatDuhHeckButton;
		private JButton cheaterButton;
		private JButton ohMyGodButton;
		private JButton doingStringsButton;
		private JButton churlishButton;
		private JButton chicanerousButton;
		
		public void actionPerformed(ActionEvent e) {
			
			//do nothing if a sound panel has already been opened
			if(openedSoundPanel) {
				return;
			}
			
			//initialize frame
			soundboardFrame = new JFrame("Soundboard");
			soundboardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			soundboardFrame.setSize(325, 435);
			soundboardFrame.setResizable(false);
			
			//soundboard now always goes on left side, because stats goes on right side
			int xPos = frame.getX() - soundboardFrame.getWidth();
			
			if(xPos < 0) {
				xPos = frame.getX() + frame.getWidth();
			}
			
			soundboardFrame.setLocation(frame.getX() - soundboardFrame.getWidth(), frame.getY());
			soundboardFrame.addWindowListener(new SoundboardWindowListener());
			
			//Set look and feel
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException e4) {
				int hour = ZonedDateTime.now().getHour();
				int min = ZonedDateTime.now().getMinute();
				int sec = ZonedDateTime.now().getSecond();
				System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + e4);
				debug.append("[" + hour + ":" + min + ":" + sec + "]: " + e4 + "\n");
			} catch (InstantiationException e4) {
				int hour = ZonedDateTime.now().getHour();
				int min = ZonedDateTime.now().getMinute();
				int sec = ZonedDateTime.now().getSecond();
				System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + e4);
				debug.append("[" + hour + ":" + min + ":" + sec + "]: " + e4 + "\n");
			} catch (IllegalAccessException e4) {
				int hour = ZonedDateTime.now().getHour();
				int min = ZonedDateTime.now().getMinute();
				int sec = ZonedDateTime.now().getSecond();
				System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + e4);
				debug.append("[" + hour + ":" + min + ":" + sec + "]: " + e4 + "\n");
			} catch (UnsupportedLookAndFeelException e4) {
				int hour = ZonedDateTime.now().getHour();
				int min = ZonedDateTime.now().getMinute();
				int sec = ZonedDateTime.now().getSecond();
				System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + e4);
				debug.append("[" + hour + ":" + min + ":" + sec + "]: " + e4 + "\n");
			}
			
			//do the rest of it
			soundboardPanel = new JPanel();
			soundboardPanel.setLayout(new GridLayout(0, 3));
			
			doItAgainButton = new JButton("Do it again");
			doItAgainButton.addActionListener(new DoItAgainActionListener());
			neutralAerialButton = new JButton("Neutral aerial");
			neutralAerialButton.addActionListener(new NeutralAerialActionListener());
			trashManButton = new JButton("Trash man");
			trashManButton.addActionListener(new TrashManActionListener());
			pikachuBustedButton = new JButton("Pikachu busted");
			pikachuBustedButton.addActionListener(new PikachuBustedActionListener());
			bruhSoundEffectButton = new JButton("Bruh");
			bruhSoundEffectButton.addActionListener(new BruhSoundEffectActionListener());
			angerousNowButton = new JButton("<html><center>I am be<br>angerous now</center></html>");
			angerousNowButton.addActionListener(new AngerousNowActionListener());
			theWorstButton = new JButton("<html><center>This is... the<br>worst</center></html>");
			theWorstButton.addActionListener(new TheWorstActionListener());
			excuseMeButton = new JButton("<html><center>\"Excuse me!\"<br>- Arin</center></html>");
			excuseMeButton.addActionListener(new ExcuseMeActionListener());
			bscuseMeButton = new JButton("B'scuse me");
			bscuseMeButton.addActionListener(new BscuseMeActionListener());
			ghoulButton = new JButton("Ghoul");
			ghoulButton.addActionListener(new GhoulActionListener());
			whatButton = new JButton("What");
			whatButton.addActionListener(new WhatActionListener());
			thatMakesMeFeelAngryButton = new JButton("<html><center>That makes me<br>feel angry!</center></html>");
			thatMakesMeFeelAngryButton.addActionListener(new MakesMeFeelAngryActionListener());
			daringTodayButton = new JButton("<html><center>Daring today,<br>aren't we?</center></html>");
			daringTodayButton.addActionListener(new DaringTodayActionListener());
			vsauceButton = new JButton("<html><center>Vsauce<br>uhhowhaaa</center></html>");
			vsauceButton.addActionListener(new VsauceActionListener());
			despiseHimButton = new JButton("I despise him!");
			despiseHimButton.addActionListener(new DespiseHimActionListener());
			whatDuhHeckButton = new JButton("<html><center>What duh heck is up with dis game</center></html>");
			whatDuhHeckButton.addActionListener(new WhatDuhHeckActionListener());
			cheaterButton = new JButton("Cheater!");
			cheaterButton.addActionListener(new CheaterActionListener());
			ohMyGodButton = new JButton("Oh my GOD");
			ohMyGodButton.addActionListener(new OhMyGodActionListener());
			doingStringsButton = new JButton("<html>This dude's<br>doing strings</html>");
			doingStringsButton.addActionListener(new DoingStringsActionListener());
			churlishButton = new JButton("<html>Insubordinate<br>and churlish</html>");
			churlishButton.addActionListener(new ChurlishActionListener());
			chicanerousButton = new JButton("<html><center>Mischievous and deceitful, chicanerous and deplorable</center></html>");
			chicanerousButton.addActionListener(new ChicanerousActionListener());
			
			soundboardPanel.add(doItAgainButton);
			soundboardPanel.add(neutralAerialButton);
			soundboardPanel.add(trashManButton);
			soundboardPanel.add(pikachuBustedButton);
			soundboardPanel.add(bruhSoundEffectButton);
			soundboardPanel.add(angerousNowButton);
			soundboardPanel.add(theWorstButton);
			soundboardPanel.add(excuseMeButton);
			soundboardPanel.add(bscuseMeButton);
			soundboardPanel.add(whatDuhHeckButton);
			soundboardPanel.add(ghoulButton);
			soundboardPanel.add(whatButton);
			soundboardPanel.add(thatMakesMeFeelAngryButton);
			soundboardPanel.add(daringTodayButton);
			soundboardPanel.add(vsauceButton);
			soundboardPanel.add(despiseHimButton);
			soundboardPanel.add(cheaterButton);
			soundboardPanel.add(ohMyGodButton);
			soundboardPanel.add(doingStringsButton);
			soundboardPanel.add(churlishButton);
			soundboardPanel.add(chicanerousButton);
			
			soundboardFrame.getContentPane().add(soundboardPanel);
			
			soundboardFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/Icon.png")));
			
			soundboardFrame.setVisible(true);
			openedSoundPanel = true;
			
		}
		
		private class SoundboardWindowListener implements WindowListener {

			public void windowClosing(WindowEvent e) {
				openedSoundPanel = false;	
			}

			public void windowClosed(WindowEvent e) {
			}

			public void windowIconified(WindowEvent e) {
			}

			public void windowDeiconified(WindowEvent e) {
			}

			public void windowActivated(WindowEvent e) {
			}

			public void windowDeactivated(WindowEvent e) {
			}
			
			public void windowOpened(WindowEvent e) {
			}
		}
		
		private class ChurlishActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				try {
					InputStream is = getClass().getResourceAsStream("/sounds/churlish.wav");
					AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				} catch(Exception ex) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + ex);
					debug.append("[" + hour + ":" + min + ":" + sec + "]: " + ex + "\n");
				}
			}
		}
		
		private class ChicanerousActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				try {
					InputStream is = getClass().getResourceAsStream("/sounds/chicanerous.wav");
					AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				} catch(Exception ex) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + ex);
					debug.append("[" + hour + ":" + min + ":" + sec + "]: " + ex + "\n");
				}
			}
		}
		
		private class DoingStringsActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				try {
					InputStream is = getClass().getResourceAsStream("/sounds/doingstrings.wav");
					AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				} catch(Exception ex) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + ex);
					debug.append("[" + hour + ":" + min + ":" + sec + "]: " + ex + "\n");
				}
			}
		}
		
		private class OhMyGodActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				try {
					InputStream is = getClass().getResourceAsStream("/sounds/ohmygod.wav");
					AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				} catch(Exception ex) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + ex);
					debug.append("[" + hour + ":" + min + ":" + sec + "]: " + ex + "\n");
				}
			}
		}
		
		private class CheaterActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				try {
					int cheatNum = ThreadLocalRandom.current().nextInt(1, 6);
					InputStream is;
					switch(cheatNum) {
						case 1:
							is = getClass().getResourceAsStream("/sounds/cheat1.wav");
							break;
						case 2:
							is = getClass().getResourceAsStream("/sounds/cheat2.wav");
							break;
						case 3:
							is = getClass().getResourceAsStream("/sounds/cheat3.wav");
							break;
						case 4:
							is = getClass().getResourceAsStream("/sounds/cheat4.wav");
							break;
						default:
							is = getClass().getResourceAsStream("/sounds/cheat5.wav");						
					}
					AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				} catch(Exception ex) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + ex);
					debug.append("[" + hour + ":" + min + ":" + sec + "]: " + ex + "\n");
				}
			}
		}
		
		private class DespiseHimActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				try {
					InputStream is = getClass().getResourceAsStream("/sounds/despisehim.wav");
					AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				} catch(Exception ex) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + ex);
					debug.append("[" + hour + ":" + min + ":" + sec + "]: " + ex + "\n");
				}
			}
		}
		
		private class WhatDuhHeckActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				try {
					InputStream is = getClass().getResourceAsStream("/sounds/whatduhheck.wav");
					AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				} catch(Exception ex) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + ex);
					debug.append("[" + hour + ":" + min + ":" + sec + "]: " + ex + "\n");
				}
			}
		}
		
		private class VsauceActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				try {
					InputStream is = getClass().getResourceAsStream("/sounds/pancake.wav");
					AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				} catch(Exception ex) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + ex);
					debug.append("[" + hour + ":" + min + ":" + sec + "]: " + ex + "\n");
				}
			}
		}
		
		private class DaringTodayActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				try {
					InputStream is = getClass().getResourceAsStream("/sounds/daringtoday.wav");
					AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				} catch(Exception ex) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + ex);
					debug.append("[" + hour + ":" + min + ":" + sec + "]: " + ex + "\n");
				}
			}
		}
		
		private class MakesMeFeelAngryActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				try {
					InputStream is = getClass().getResourceAsStream("/sounds/makesmefeelangry.wav");
					AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				} catch(Exception ex) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + ex);
					debug.append("[" + hour + ":" + min + ":" + sec + "]: " + ex + "\n");
				}
			}
		}
		
		private class AngerousNowActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				try {
					InputStream is = getClass().getResourceAsStream("/sounds/angerousnow.wav");
					AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				} catch(Exception ex) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + ex);
					debug.append("[" + hour + ":" + min + ":" + sec + "]: " + ex + "\n");
				}
			}
		}
		
		private class TheWorstActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				try {
					InputStream is = getClass().getResourceAsStream("/sounds/thisistheworst.wav");
					AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				} catch(Exception ex) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + ex);
					debug.append("[" + hour + ":" + min + ":" + sec + "]: " + ex + "\n");
				}
			}
		}
		
		private class ExcuseMeActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				try {
					InputStream is = getClass().getResourceAsStream("/sounds/excuseme.wav");
					AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				} catch(Exception ex) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + ex);
					debug.append("[" + hour + ":" + min + ":" + sec + "]: " + ex + "\n");
				}
			}
		}
		
		private class BscuseMeActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				try {
					InputStream is = getClass().getResourceAsStream("/sounds/bscuseme.wav");
					AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				} catch(Exception ex) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + ex);
					debug.append("[" + hour + ":" + min + ":" + sec + "]: " + ex + "\n");
				}
			}
		}
		
		private class GhoulActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				try {
					int ghoulNum = ThreadLocalRandom.current().nextInt(1, 5);
					InputStream is;
					switch(ghoulNum) {
						case 1:
							is = getClass().getResourceAsStream("/sounds/ghoul1.wav");
							break;
						case 2:
							is = getClass().getResourceAsStream("/sounds/ghoul2.wav");
							break;
						case 3:
							is = getClass().getResourceAsStream("/sounds/ghoul3.wav");
							break;
						case 4:
							is = getClass().getResourceAsStream("/sounds/ghoul4.wav");
							break;
						default:
							is = getClass().getResourceAsStream("/sounds/ghoul1.wav");						
					}
					AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				} catch(Exception ex) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + ex);
					debug.append("[" + hour + ":" + min + ":" + sec + "]: " + ex + "\n");
				}
			}
		}
		
		private class WhatActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				try {
					InputStream is = getClass().getResourceAsStream("/sounds/what.wav");
					AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				} catch(Exception ex) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + ex);
					debug.append("[" + hour + ":" + min + ":" + sec + "]: " + ex + "\n");
				}
			}
		}
		
		private class DoItAgainActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				try {
					InputStream is = getClass().getResourceAsStream("/sounds/doitagain.wav");
					AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				} catch(Exception ex) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + ex);
					debug.append("[" + hour + ":" + min + ":" + sec + "]: " + ex + "\n");
				}
			}
		}
		
		private class NeutralAerialActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				try {
					InputStream is = getClass().getResourceAsStream("/sounds/neutralaerial.wav");
					AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				} catch(Exception ex) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + ex);
					debug.append("[" + hour + ":" + min + ":" + sec + "]: " + ex + "\n");
				}
			}
		}
		
		public class TrashManActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				try {
					InputStream is = getClass().getResourceAsStream("/sounds/trashman.wav");
					AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				} catch(Exception ex) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + ex);
					debug.append("[" + hour + ":" + min + ":" + sec + "]: " + ex + "\n");
				}
			}
		}
		
		public class PikachuBustedActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				try {
					InputStream is = getClass().getResourceAsStream("/sounds/pikachubusted.wav");
					AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				} catch(Exception ex) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + ex);
					debug.append("[" + hour + ":" + min + ":" + sec + "]: " + ex + "\n");
				}
			}
		}
		
		public class BruhSoundEffectActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {		
				try {
					InputStream is = getClass().getResourceAsStream("/sounds/bruh.wav");
					AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				} catch(Exception ex) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + ex);
					debug.append("[" + hour + ":" + min + ":" + sec + "]: " + ex + "\n");
				}
			}
		}
	}
	
	private class GenerateButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			//synchronize entire generation process in hopes of fixing program
			//hangups, which i'm just assuming are caused by threading, because
			//of how swing works... it wasn't the case but i'm keeping it anyway
			//because... because of how swing works, or at least how i think it
			//works, with the eventqueue thread and whatnot
			synchronized(key) {
				if(!fileLoaded) {
					JOptionPane.showMessageDialog(frame, "You must load a tier "
							+ "list first!", "Smash Character Picker", 
							JOptionPane.ERROR_MESSAGE);
					skipping = false;
					return;
				}
				//clear the results area
				results.setText("");
				//show number of battles if not skipping
				if(!skipping || numBattles == 0) {
					numBattles++;
				}
				results.append("Battle #" + numBattles + ":\n");
				
				int playerTiers[] = getPlayerTiers();
				
				gotten.clear();
				for(int player = 1; player <= numPlayers; player++) {
					int tier = playerTiers[player - 1];
					String got = "";
					ArrayList<String> validCharacters = new ArrayList<String>();
					
					//set up an array list of valid characters from the appropriate
					//tier from which this player's character will be chosen
					//basically, remove excluded characters, ones from the cannot
					//get queue, and ones already gotten
					for(String currentlyAt: linesOfFile.get(tier)) {
						if(!linesOfFile.get(player + 23).contains(currentlyAt) &&
						   !cannotGet.contains(currentlyAt) &&
						   !gotten.contains(currentlyAt) &&
						   !individualCannotGet[player - 1].contains(currentlyAt)) {
							validCharacters.add(currentlyAt);
						}
					}
					
					//if there are no valid characters, call the function again
					if(validCharacters.size() == 0) {
						if(!skipping) {
							numBattles--;
						}
						actionPerformed(null);
						return;
					}
					
					got = validCharacters.get(ThreadLocalRandom.current().nextInt(0, validCharacters.size()));
					gotten.add(got);
					
					results.append("Player " + player + " got " + got + ", " + tierToString(tier) + ".\n");
				}
				
				//remove from cannot get queue first
				for(int at = 0; at < numPlayers; at++) {
					if(cannotGet.size() > (cannotGetSize * numPlayers)) {
						cannotGet.removeFirst();
					}
				}
				
				debug.append("===BATTLE #" + numBattles + "===\n");
				//then add to queue
				for(int playerAt = 0; playerAt < gotten.size(); playerAt++) {
					int tier = playerTiers[playerAt];
					
					if(skipping) {
						individualCannotGet[playerAt].removeLast();
					}
					
					if(tier < 3 && SSAllowedInCannotGetBuffer) {
						cannotGet.add(gotten.get(playerAt), tier);
					}
					else if(tier >= 3 && tier <= 5 && SAllowedInCannotGetBuffer) {
						cannotGet.add(gotten.get(playerAt), tier);
					}
					else if(tier >= 6) {
						cannotGet.add(gotten.get(playerAt), tier);
					}
					
					//if the gotten character is a favorite, don't add it to the
					//cannot get for rest of session queue
					if(!linesOfFile.get(32 + playerAt).contains(gotten.get(playerAt))) {
						individualCannotGet[playerAt].add(gotten.get(playerAt), tier);
					}
					System.out.println("[DEBUG]: Player " + (playerAt + 1) + " cannot get " + individualCannotGet[playerAt]);
					debug.append("[DEBUG]: Player " + (playerAt + 1) + " cannot get " + individualCannotGet[playerAt] + "\n");
				}
				
				System.out.println("[DEBUG]: Nobody can get " + cannotGet);
				debug.append("[DEBUG]: Nobody can get " + cannotGet + "\n");
				
				//gonna use that as reasonable metric of whether or not the stats
				//window must be updated
				if(needToSaveStats) {
					updateStatsScreen();
				}
				
				skipping = false;
			}
		}
		
		public void updateStatsScreen() {
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
				statsOutput.append("P" + (at + 1) + " W%: " + 
							printDouble((playerBattlesWon / playerBattlesFought) * 100) + 
							"% (" + (int)playerBattlesWon + "/" + (int)playerBattlesFought + "). Total W%: " + 
							printDouble((totalBattlesWon / totalBattlesFought) * 100) + "% (" + 
							(int)totalBattlesWon + "/" + (int)totalBattlesFought + ").\n");
			}
		}
		
		private int[] getPlayerTiers() {
			int tierChance = ThreadLocalRandom.current().nextInt(0, 101);
			int tier = -1;
			int sum = 0;
			int[] playerTiers = new int[numPlayers];
			
			sum = tierChances[0];
			if(tierChance <= sum) {
				tier = 0;
			}
			sum += tierChances[1];
			if(tierChance <= sum && tier == -1) {
				tier = 1;
			}
			sum += tierChances[2];
			if(tierChance <= sum && tier == -1) {
				tier = 2;
			}
			sum += tierChances[3];
			if(tierChance <= sum && tier == -1) {
				tier = 3;
			}
			sum += tierChances[4];
			if(tierChance <= sum && tier == -1) {
				tier = 4;
			}
			sum += tierChances[5];
			if(tierChance <= sum && tier == -1) {
				tier = 5;
			}
			sum += tierChances[6];
			if(tierChance <= sum && tier == -1) {
				tier = 6;
			}
			sum += tierChances[7];
			if(tierChance <= sum && tier == -1) {
				tier = 7;
			}
			tier = convertToTierNum(tier);
			
			for(int at = 0; at < numPlayers; at++) {
				int adjust = tier + ThreadLocalRandom.current().nextInt(-2, 1);
				if(adjust < 0) {
					adjust = 0;
				}
				while(tierTurnedOff(adjust, tierChances)) {
					adjust++;
				}
				playerTiers[at] = adjust;
			}
			
			return playerTiers;
		}

		private boolean tierTurnedOff(int tier, int[] chances) {
			if((tier == 0 || tier == 1 || tier == 2) && chances[0] == 0) {
				return true;
			}
			else if((tier == 3 || tier == 4 || tier == 5) && chances[1] == 0) {
				return true;
			}
			else if((tier == 6 || tier == 7 || tier == 8) && chances[2] == 0) {
				return true;
			}
			else if((tier == 9 || tier == 10 || tier == 11) && chances[3] == 0) {
				return true;
			}
			else if((tier == 12 || tier == 13 || tier == 14) && chances[4] == 0) {
				return true;
			}
			else if((tier == 15 || tier == 16 || tier == 17) && chances[5] == 0) {
				return true;
			}
			else if((tier == 18 || tier == 19 || tier == 20) && chances[6] == 0) {
				return true;
			}
			else if((tier == 21 || tier == 22 || tier == 23) && chances[7] == 0) {
				return true;
			}
			else {
				return false;
			}
		}
		
		private String tierToString(int tier) {
			switch(tier) {
				case 0:
					return "Upper Double S tier";
				case 1:
					return "Double S tier";
				case 2:
					return "Lower Double S tier";
				case 3:
					return "Upper S tier";
				case 4:
					return "Mid S tier";
				case 5:
					return "Lower S tier";
				case 6:
					return "Upper A tier";
				case 7:
					return "Mid A tier";
				case 8:
					return "Lower A tier";
				case 9:
					return "Upper B tier";
				case 10:
					return "Mid B tier";
				case 11:
					return "Lower B tier";
				case 12:
					return "Upper C tier";
				case 13:
					return "Mid C tier";
				case 14:
					return "Lower C tier";
				case 15:
					return "Upper D tier";
				case 16:
					return "Mid D tier";
				case 17:
					return "Lower D tier";
				case 18:
					return "Upper E tier";
				case 19:
					return "Mid E tier";
				case 20:
					return "Lower E tier";
				case 21:
					return "Upper F tier";
				case 22:
					return "Mid F tier";
				case 23:
					return "Lower F tier";
				default:
					return "Invalid tier";
			}
		}
		
		private int convertToTierNum(int tier) {
			int change = ThreadLocalRandom.current().nextInt(-1, 2);
			
			if(tier == 0) {
				return 1 + change;
			}
			else if(tier == 1) {
				return 4 + change;
			}
			else if(tier == 2) {
				return 7 + change;
			}
			else if(tier == 3) {
				return 10 + change;
			}
			else if(tier == 4) {
				return 13 + change;
			}
			else if(tier == 5) {
				return 16 + change;
			}
			else if(tier == 6) {
				return 19 + change;
			}
			else {
				return 22 + change;
			}
		}
	}
	
	private class LoadButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(fileLoaded) {
				if(JOptionPane.showConfirmDialog(frame, "You already have a file "
						+ "loaded.\nLoad another one?", "Smash Character Picker",
						JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
					return;
				}
				else {
					linesOfFile.clear();
					for(int at = 0; at < 39; at++) {
						linesOfFile.add(new ArrayList<String>());

					}
				}
			}
			
			JFileChooser fileChooser = new JFileChooser(".");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Documents (*.txt)", "txt");
			fileChooser.setFileFilter(filter);
			int r = fileChooser.showOpenDialog(frame);
			
			if (r == JFileChooser.APPROVE_OPTION) {
				readFile(fileChooser.getSelectedFile());
			}
		}
		
		private void readFile(File tierListFile) {
			try {
				BufferedReader in = new BufferedReader(new FileReader(tierListFile));
				
				//file structure is as follows:
				//upper double S 	index 0
				//mid double S		index 1
				//lower double S	index 2
				//upper S			index 3
				//mid S				index 4
				//lower S			index 5
				//upper A			index 6
				//mid A				index 7
				//lower A			index 8
				//upper B			index 9
				//mid B				index 10
				//lower B			index 11
				//upper C			index 12
				//mid C				index 13
				//lower C			index 14
				//upper D			index 15
				//mid D				index 16
				//lower D			index 17
				//upper E			index 18
				//mid E				index 19
				//lower E			index 20
				//upper F			index 21
				//mid F				index 22
				//lower F			index 23
				//p1exc				index 24
				//p2exc				index 25
				//p3exc				index 26
				//p4exc				index 27
				//p5exc				index 28
				//p6exc				index 29
				//p7exc				index 30
				//p8exc				index 31
				//p1fav				index 32
				//p2fav				index 33
				//p3fav				index 34
				//p4fav				index 35
				//p5fav				index 36
				//p6fav				index 37
				//p7fav				index 38
				//p8fav				index 39
				
				//read first line
				String lineAt = in.readLine();
				//continue reading all lines as long as they exist
				while(lineAt != null) {
					//scroll through the chars in the read line, if one is an equals
					//then check for which tier it is
					String next = "";
					boolean foundEqual = false;
					for(int at = 0; at < lineAt.length(); at++) {
						if(lineAt.charAt(at) == '=') {
							//remove space before equals sign and check name
							foundEqual = true;
							next = next.substring(0, next.length() - 1);
							next = next.toLowerCase();
							switch(next) {
								case "upper double s":
									readLine(0, at, lineAt);
									break;
								case "mid double s":
									readLine(1, at, lineAt);
									break;
								case "lower double s":
									readLine(2, at, lineAt);
									break;
								case "upper s":
									readLine(3, at, lineAt);
									break;
								case "mid s":
									readLine(4, at, lineAt);
									break;
								case "lower s":
									readLine(5, at, lineAt);
									break;
								case "upper a":
									readLine(6, at, lineAt);
									break;
								case "mid a":
									readLine(7, at, lineAt);
									break;
								case "lower a":
									readLine(8, at, lineAt);
									break;
								case "upper b":
									readLine(9, at, lineAt);
									break;
								case "mid b":
									readLine(10, at, lineAt);
									break;
								case "lower b":
									readLine(11, at, lineAt);
									break;
								case "upper c":
									readLine(12, at, lineAt);
									break;
								case "mid c":
									readLine(13, at, lineAt);
									break;
								case "lower c":
									readLine(14, at, lineAt);
									break;
								case "upper d":
									readLine(15, at, lineAt);
									break;
								case "mid d":
									readLine(16, at, lineAt);
									break;
								case "lower d":
									readLine(17, at, lineAt);
									break;
								case "upper e":
									readLine(18, at, lineAt);
									break;
								case "mid e":
									readLine(19, at, lineAt);
									break;
								case "lower e":
									readLine(20, at, lineAt);
									break;
								case "upper f":
									readLine(21, at, lineAt);
									break;
								case "mid f":
									readLine(22, at, lineAt);
									break;
								case "lower f":
									readLine(23, at, lineAt);
									break;
								case "p1 exclude":
									readLine(24, at, lineAt);
									break;
								case "p2 exclude":
									readLine(25, at, lineAt);
									break;
								case "p3 exclude":
									readLine(26, at, lineAt);
									break;
								case "p4 exclude":
									readLine(27, at, lineAt);
									break;
								case "p5 exclude":
									readLine(28, at, lineAt);
									break;
								case "p6 exclude":
									readLine(29, at, lineAt);
									break;
								case "p7 exclude":
									readLine(30, at, lineAt);
									break;
								case "p8 exclude":
									readLine(31, at, lineAt);
									break;
								case "p1 favorite":
									readLine(32, at, lineAt);
									break;
								case "p2 favorite":
									readLine(33, at, lineAt);
									break;
								case "p3 favorite":
									readLine(34, at, lineAt);
									break;
								case "p4 favorite":
									readLine(35, at, lineAt);
									break;
								case "p5 favorite":
									readLine(36, at, lineAt);
									break;
								case "p6 favorite":
									readLine(37, at, lineAt);
									break;
								case "p7 favorite":
									readLine(38, at, lineAt);
									break;
								case "p8 favorite":
									readLine(39, at, lineAt);
									break;
								case "tier chances":
									readSetting(1, at, lineAt);
									break;
								case "cannot get size":
									readSetting(2, at, lineAt);
									break;
								case "allow ss in cannot get":
									readSetting(3, at, lineAt);
									break;
								case "allow s in cannot get":
									readSetting(4, at, lineAt);
									break;
								case "players":
									readSetting(5, at, lineAt);
									break;
								default:
									in.close();
									throw new IOException(next);
							}
						}
						else {
							next += lineAt.charAt(at);
						}
					}
					//if any lines are found that aren't valid, stop reading
					//file and throw an error
					if(!foundEqual && !next.equals("")) {
						in.close();
						throw new IOException();
					}
					lineAt = in.readLine();
				}
				in.close();
			} catch (FileNotFoundException e) {
				int hour = ZonedDateTime.now().getHour();
				int min = ZonedDateTime.now().getMinute();
				int sec = ZonedDateTime.now().getSecond();
				System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + e);
				debug.append("[" + hour + ":" + min + ":" + sec + "]: " + e + "\n");
				results.append("File " + tierListFile.getName() + " not found!\n");
				fileLoaded = false;
				return;
			} catch(IOException ioe) {
				int hour = ZonedDateTime.now().getHour();
				int min = ZonedDateTime.now().getMinute();
				int sec = ZonedDateTime.now().getSecond();
				System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + ioe);
				debug.append("[" + hour + ":" + min + ":" + sec + "]: " + ioe + "\n");
				results.append("IOException in reading " + tierListFile.getName() + ".\n"
						+ "This means it is not a valid tier list file.\n" +
						"Please load a valid tier list file.\n");
				fileLoaded = false;
				return;
			}
			
			//printing out debug info in case anything ever goes wrong
			GenerateButtonActionListener gbal = new GenerateButtonActionListener();
			System.out.println("[DEBUG]: The following data has been loaded as the current tier list:");
			debug.append("[DEBUG]: The following data has been loaded as the current tier list:\n");
			for(int at = 0; at < 24; at++) {
				String tierAt = gbal.tierToString(at);
				if(tierAt.equals("Upper Double S tier") || tierAt.equals("Lower Double S tier")) {
					System.out.println("         " + gbal.tierToString(at) + ":\t" + linesOfFile.get(at));
					debug.append("         " + gbal.tierToString(at) + ":\t" + linesOfFile.get(at) + "\n");
				}
				else {
					System.out.println("         " + gbal.tierToString(at) + ":\t\t" + linesOfFile.get(at));
					debug.append("         " + gbal.tierToString(at) + ":\t\t" + linesOfFile.get(at) + "\n");
				}
			}
			for(int at = 24; at < 32; at++) {
				System.out.println("         Player " + (at - 23) + " exclude:\t" + linesOfFile.get(at));
				debug.append("         Player " + (at - 23) + " exclude:\t" + linesOfFile.get(at) + "\n");
			}
			for(int at = 32; at < 40; at++) {
				System.out.println("         Player " + (at - 31) + " favorites:\t" + linesOfFile.get(at));
				debug.append("         Player " + (at - 31) + " favorites:\t" + linesOfFile.get(at) + "\n");
			}
			
			fileLoaded = true;
			results.append("Loaded file: ");
			results.append(tierListFile.getName() + "\n");
		}
		
		private void readSetting(int id, int startAt, String line) {
			//settings id's
			//1 = tier chances (comma-separated list; if they're valid, applied automatically)
			//2 = cannot get size (integer between 0 and 15)
			//3 = allow SS in cannot get buffer (true or false, 1 or 0)
			//4 = allow S in cannot get buffer (true or false, 1 or 0)
			//5 = number of players (integer between 2 and 8)
			String toRead = line.substring(startAt + 2);
			
			if(id == 2) {
				int newCannotGetSize = -1;
				
				try {
					newCannotGetSize = Integer.parseInt(toRead);
				} catch(NumberFormatException e) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec +"]: "
							+ "New 'cannot get size' of " + toRead + " is not "
							+ "valid.");
					debug.append("[" + hour + ":" + min + ":" + sec +"]: "
							+ "New 'cannot get size' of " + toRead + " is not "
							+ "valid.\n");
					return;
				}
				
				if(newCannotGetSize >= 0 && newCannotGetSize <= 15) {
					cannotGetSizeSpinner.setValue(newCannotGetSize);
					cannotGetSize = newCannotGetSize;
				}
				else {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec +"]: "
							+ "New 'cannot get size' of " + toRead + "is not " +
							"valid.");
					debug.append("[" + hour + ":" + min + ":" + sec +"]: "
							+ "New 'cannot get size' of " + toRead + "is not " +
							"valid.\n");
				}
			}
			else if(id == 3) {
				boolean newSSAllowedInCannotGet = false;
				
				try {
					if(toRead.equals("true")) {
						newSSAllowedInCannotGet = true;
					}
					else if(toRead.equals("false")) {
						newSSAllowedInCannotGet = false;
					}
					else if(Integer.parseInt(toRead) == 1) {
						newSSAllowedInCannotGet = true;
					}
					else if(Integer.parseInt(toRead) == 0) {
						newSSAllowedInCannotGet = false;
					}
					else {
						int hour = ZonedDateTime.now().getHour();
						int min = ZonedDateTime.now().getMinute();
						int sec = ZonedDateTime.now().getSecond();
						System.err.println("[" + hour + ":" + min + ":" + sec +"]: "
								+ "New 'ss allowed in cannot get' value of " +
								toRead + " is not valid.");
						debug.append("[" + hour + ":" + min + ":" + sec +"]: "
								+ "New 'ss allowed in cannot get' value of " +
								toRead + " is not valid.\n");
						return;
					}
					
					SSAllowedInCannotGetBuffer = newSSAllowedInCannotGet;
					allowSSInCannotGet.setSelected(newSSAllowedInCannotGet);
				} catch(NumberFormatException e) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec +"]: "
							+ "New 'ss allowed in cannot get' value of " +
							toRead + " is not valid.");
					debug.append("[" + hour + ":" + min + ":" + sec +"]: "
							+ "New 'ss allowed in cannot get' value of " +
							toRead + " is not valid.\n");
				}
			}
			else if(id == 4) {
				boolean newSAllowedInCannotGet = false;
				
				try {
					if(toRead.equals("true")) {
						newSAllowedInCannotGet = true;
					}
					else if(toRead.equals("false")) {
						newSAllowedInCannotGet = false;
					}
					else if(Integer.parseInt(toRead) == 1) {
						newSAllowedInCannotGet = true;
					}
					else if(Integer.parseInt(toRead) == 0) {
						newSAllowedInCannotGet = false;
					}
					else {
						int hour = ZonedDateTime.now().getHour();
						int min = ZonedDateTime.now().getMinute();
						int sec = ZonedDateTime.now().getSecond();
						System.err.println("[" + hour + ":" + min + ":" + sec +"]: "
								+ "New 's allowed in cannot get' value of " +
								toRead + " is not valid.");
						debug.append("[" + hour + ":" + min + ":" + sec +"]: "
								+ "New 's allowed in cannot get' value of " +
								toRead + " is not valid.\n");
						return;
					}
					
					SAllowedInCannotGetBuffer = newSAllowedInCannotGet;
					allowSInCannotGet.setSelected(newSAllowedInCannotGet);
				} catch(NumberFormatException e) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec +"]: "
							+ "New 's allowed in cannot get' value of " +
							toRead + " is not valid.");
					debug.append("[" + hour + ":" + min + ":" + sec +"]: "
							+ "New 's allowed in cannot get' value of " +
							toRead + " is not valid.\n");
				}
			}
			else if(id == 5) {
				int newNumPlayers = -1;
				
				try {
					newNumPlayers = Integer.parseInt(toRead);
				} catch(NumberFormatException e) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec +"]: "
							+ "New 'number of players' value of " + toRead +
							" is not valid.");
					debug.append("[" + hour + ":" + min + ":" + sec +"]: "
							+ "New 'number of players' value of " + toRead +
							" is not valid.\n");
					return;
				}
				
				if(newNumPlayers >= 2 && newNumPlayers <= 8) {
					numPlayersSpinner.setValue(newNumPlayers);
					numPlayers = newNumPlayers;
				}
				else {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec +"]: "
							+ "New 'number of players' value of " + toRead +
							" is not valid.");
					debug.append("[" + hour + ":" + min + ":" + sec +"]: "
							+ "New 'number of players' value of " + toRead +
							" is not valid.\n");
				}
			}
			else if(id == 1) {
				String next = "";
				int[] fileTierChances = new int[8];
				int numAt = 0;
				
				try {
					for(int at = 0; at < toRead.length(); at++) {
						if(toRead.charAt(at) == ',') {
							fileTierChances[numAt] = Integer.parseInt(next);
							next = "";
							numAt++;
							at++;
						}
						else {
							next += toRead.charAt(at);
						}
					}
					fileTierChances[numAt] = Integer.parseInt(next);
				} catch(NumberFormatException e) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec +"]: "
							+ next + " is not a valid tier chance.");
					debug.append("[" + hour + ":" + min + ":" + sec +"]: "
							+ next + " is not a valid tier chance.\n");
					return;
				}
				
				for(int at = 0; at < 8; at++) {
					newTierChances[at] = fileTierChances[at];
				}
				SSTierSpinner.setValue(newTierChances[0]);
				STierSpinner.setValue(newTierChances[1]);
				ATierSpinner.setValue(newTierChances[2]);
				BTierSpinner.setValue(newTierChances[3]);
				CTierSpinner.setValue(newTierChances[4]);
				DTierSpinner.setValue(newTierChances[5]);
				ETierSpinner.setValue(newTierChances[6]);
				FTierSpinner.setValue(newTierChances[7]);
				
				applyButton.getActionListeners()[0].actionPerformed(null);
				
			}
			//this is literally impossible
			else {
				int hour = ZonedDateTime.now().getHour();
				int min = ZonedDateTime.now().getMinute();
				int sec = ZonedDateTime.now().getSecond();
				System.err.println("[" + hour + ":" + min + ":" + sec +"]: "
						+ id + " is not a valid setting id!");
				debug.append("[" + hour + ":" + min + ":" + sec +"]: "
						+ id + " is not a valid setting id!\n");
			}
		}
		
		private void readLine(int index, int startAt, String line) {
			startAt += 2;
			
			String next = "";
			ArrayList<String> currentLine = new ArrayList<String>();
			for(int at = startAt; at < line.length(); at++) {
				if(line.charAt(at) == ',') {
					currentLine.add(next);
					next = "";
					at++;
				}
				else {
					next += line.charAt(at);
				}
			}
			currentLine.add(next);
			linesOfFile.add(index, currentLine);
		}
	}
	
	private class ApplyButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int total = 0;
			for(int at = 0; at < 8; at++) {
				total += newTierChances[at];
			}
			
			if(total == 100) {
				for(int x = 0; x < 8; x++) {
					tierChances[x] = newTierChances[x];
				}
				JOptionPane.showMessageDialog(frame, "Custom chances"
						+ " are valid and have been applied.", "Smash "
						+ "Character Picker",
						JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				JOptionPane.showMessageDialog(frame, "Your custom "
						+ "chances are invalid. They must add up to 100.\n"
						+ "They currently add up to " + total + ".",
						"Smash Character Picker",
						JOptionPane.ERROR_MESSAGE);
				SSTierSpinner.setValue(tierChances[0]);
				STierSpinner.setValue(tierChances[1]);
				ATierSpinner.setValue(tierChances[2]);
				BTierSpinner.setValue(tierChances[3]);
				CTierSpinner.setValue(tierChances[4]);
				DTierSpinner.setValue(tierChances[5]);
				ETierSpinner.setValue(tierChances[6]);
				FTierSpinner.setValue(tierChances[7]);
			}
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
	
	private class NumPlayersChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			numPlayers = (int) numPlayersSpinner.getValue();
		}
	}
	
	private class ChangeListenerSS implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			newTierChances[0] = (int) SSTierSpinner.getValue();
		}
	}
	
	private class ChangeListenerS implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			newTierChances[1] = (int) STierSpinner.getValue();
		}
	}
	
	private class ChangeListenerA implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			newTierChances[2] = (int) ATierSpinner.getValue();
		}
	}
	
	private class ChangeListenerB implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			newTierChances[3] = (int) BTierSpinner.getValue();
		}
	}
	
	private class ChangeListenerC implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			newTierChances[4] = (int) CTierSpinner.getValue();
		}
	}
	
	private class ChangeListenerD implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			newTierChances[5] = (int) DTierSpinner.getValue();
		}
	}
	
	private class ChangeListenerE implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			newTierChances[6] = (int) ETierSpinner.getValue();
		}
	}
	
	private class ChangeListenerF implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			newTierChances[7] = (int) FTierSpinner.getValue();
		}
	}
	
	private class CannotGetSizeChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			cannotGetSize = (int) cannotGetSizeSpinner.getValue();
		}
	}

}
