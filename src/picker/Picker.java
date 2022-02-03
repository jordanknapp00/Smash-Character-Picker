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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

//TODO: add favorite player system

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
	
	//left panel components:
	
	//tier chance panel components
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
	
	//settings variables
	private boolean usingCustomChances;
	private int[] standardTierChances;
	private int[] customTierChances;
	private boolean SSAllowedInCannotGetBuffer;
	private boolean SAllowedInCannotGetBuffer;
	private boolean customVerified;
	private int cannotGetSize;
	private boolean openedSoundPanel;
	
	//key for synchronization, hopefully prevent crashes
	private static Object key = new Object();
	
	public Picker() {
		//initialize main frame
		frame = new JFrame("Smash Character Picker");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(760, 435);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		
		//Initialize TextAreas before setting look and feel
		results = new JTextArea();
		results.setEditable(false);
		results.setFont(results.getFont().deriveFont(18f));
		
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
		for(int at = 0; at < 37; at++) {
			linesOfFile.add(new ArrayList<String>());
		}
		cannotGet = new CannotGetQueue();
		numPlayers = 2;
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
		SAllowedInCannotGetBuffer = false;
		customVerified = true;
		openedSoundPanel = false;
		individualCannotGet = new CannotGetQueue[8];
		for(int at = 0; at < 8; at++) {
			individualCannotGet[at] = new CannotGetQueue();
		}
		skipping = false;
		gotten = new ArrayList<String>();
		
		//initialize right panel
		rightPanel = new JPanel();
		rightPanel.setBorder(BorderFactory.createTitledBorder("Results"));
		GridBagConstraints gc = new GridBagConstraints();
		rightPanel.setLayout(new BorderLayout());
		rightPanel.add(results);
		rightPanel.setPreferredSize(new Dimension(350, 260));
		
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
		gc.weightx = .22;
		gc.gridx = 1;
		bottomPanel.add(soundBoardButton, gc);
		gc.weightx = 1;
		gc.gridx = 2;
		bottomPanel.add(generateButton, gc);
		gc.weightx = .1;
		gc.gridx = 3;
		bottomPanel.add(skipButton, gc);
		gc.gridx = 4;
		gc.weightx = .03;
		gc.fill = GridBagConstraints.NONE;
		bottomPanel.add(numPlayersLabel, gc);
		gc.gridx = 5;
		gc.weightx = .05;
		bottomPanel.add(numPlayersSpinner, gc);
		
		//customChanceRules panel
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
				+ "valid, meaning they add up to 100.");
		tierChancesPanel = new JPanel();
		tierChancesPanel.setBorder(BorderFactory.createTitledBorder("Tier chance settings"));
		tierChancesPanel.setLayout(new GridBagLayout());
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = 2;
		tierChancesPanel.add(tierChanceLabel, gc);
		gc.gridy = 1;
		gc.anchor = GridBagConstraints.CENTER;
		tierChancesPanel.add(useCustomChances, gc);
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
		gc.weightx = .50;
		gc.weighty = .85;
		gc.anchor = GridBagConstraints.CENTER;
		gc.fill = GridBagConstraints.BOTH;
		frame.add(leftPanel, gc);
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
		
		//Create icon
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/Icon.png")));
				
		//Finally make it visible
		frame.setVisible(true);
		
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
						return "Double S tier";
					case 1:
						return "Upper S tier";
					case 2:
						return "Mid S tier";
					case 3:
						return "Lower S tier";
					case 4:
						return "Upper A tier";
					case 5:
						return "Mid A tier";
					case 6:
						return "Lower A tier";
					case 7:
						return "Upper B tier";
					case 8:
						return "Mid B tier";
					case 9:
						return "Lower B tier";
					case 10:
						return "Upper C tier";
					case 11:
						return "Mid C tier";
					case 12:
						return "Lower C tier";
					case 13:
						return "Upper D tier";
					case 14:
						return "Mid D tier";
					case 15:
						return "Lower D tier";
					case 16:
						return "Upper E tier";
					case 17:
						return "Mid E tier";
					case 18:
						return "Lower E tier";
					case 19:
						return "Upper F tier";
					case 20:
						return "Mid F tier";
					case 21:
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
		private JButton excuseMeWhatButton;
		private JButton bscuseMeButton;
		private JButton ghoulButton;
		private JButton whatButton;
		private JButton ohNoButton;
		private JButton thatMakesMeFeelAngryButton;
		private JButton daringTodayButton;
		private JButton vsauceButton;
		private JButton okMomButton;
		private JButton despiseHimButton;
		private JButton whatDuhHeckButton;
		private JButton cheaterButton;
		private JButton ohMyGodButton;
		private JButton doingStringsButton;
		
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
			
			//determine where soundboard should go, either left or right side
			int xPos = frame.getX() - soundboardFrame.getWidth();
			
			if(xPos < 0) {
				xPos = frame.getX() + frame.getWidth();
			}
			
			soundboardFrame.setLocation(xPos, frame.getY());
			soundboardFrame.addWindowListener(new SoundboardWindowListener());
			
			//Set look and feel
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException e4) {
				int hour = ZonedDateTime.now().getHour();
				int min = ZonedDateTime.now().getMinute();
				int sec = ZonedDateTime.now().getSecond();
				System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + e4);
			} catch (InstantiationException e4) {
				int hour = ZonedDateTime.now().getHour();
				int min = ZonedDateTime.now().getMinute();
				int sec = ZonedDateTime.now().getSecond();
				System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + e4);
			} catch (IllegalAccessException e4) {
				int hour = ZonedDateTime.now().getHour();
				int min = ZonedDateTime.now().getMinute();
				int sec = ZonedDateTime.now().getSecond();
				System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + e4);
			} catch (UnsupportedLookAndFeelException e4) {
				int hour = ZonedDateTime.now().getHour();
				int min = ZonedDateTime.now().getMinute();
				int sec = ZonedDateTime.now().getSecond();
				System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + e4);
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
			bscuseMeButton = new JButton("B'scuse me");
			bscuseMeButton.addActionListener(new BscuseMeActionListener());
			excuseMeWhatButton = new JButton("<html><center>Excuse me<br>whaaaat</center></html>");
			excuseMeWhatButton.addActionListener(new ExcuseMeWhatActionListener());
			ghoulButton = new JButton("Ghoul");
			ghoulButton.addActionListener(new GhoulActionListener());
			whatButton = new JButton("What");
			whatButton.addActionListener(new WhatActionListener());
			ohNoButton = new JButton("Ohh nooo");
			ohNoButton.addActionListener(new OhNoActionListener());
			thatMakesMeFeelAngryButton = new JButton("<html><center>That makes me<br>feel angry!</center></html>");
			thatMakesMeFeelAngryButton.addActionListener(new MakesMeFeelAngryActionListener());
			daringTodayButton = new JButton("<html><center>Daring today,<br>aren't we?</center></html>");
			daringTodayButton.addActionListener(new DaringTodayActionListener());
			vsauceButton = new JButton("<html><center>Vsauce<br>uhhowhaaa</center></html>");
			vsauceButton.addActionListener(new VsauceActionListener());
			okMomButton = new JButton("Okay mom");
			okMomButton.addActionListener(new OkMomActionListener());
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
			
			soundboardPanel.add(doItAgainButton);
			soundboardPanel.add(neutralAerialButton);
			soundboardPanel.add(trashManButton);
			soundboardPanel.add(pikachuBustedButton);
			soundboardPanel.add(bruhSoundEffectButton);
			soundboardPanel.add(angerousNowButton);
			soundboardPanel.add(theWorstButton);
			soundboardPanel.add(bscuseMeButton);
			soundboardPanel.add(whatDuhHeckButton);
			soundboardPanel.add(excuseMeWhatButton);
			soundboardPanel.add(ghoulButton);
			soundboardPanel.add(whatButton);
			soundboardPanel.add(ohNoButton);
			soundboardPanel.add(thatMakesMeFeelAngryButton);
			soundboardPanel.add(daringTodayButton);
			soundboardPanel.add(vsauceButton);
			soundboardPanel.add(okMomButton);
			soundboardPanel.add(despiseHimButton);
			soundboardPanel.add(cheaterButton);
			soundboardPanel.add(ohMyGodButton);
			soundboardPanel.add(doingStringsButton);
			
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
				}
			}
		}
		
		private class OkMomActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				try {
					InputStream is = getClass().getResourceAsStream("/sounds/okmom.wav");
					AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				} catch(Exception ex) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + ex);
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
				}
			}
		}
		
		private class OhNoActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				try {
					InputStream is = getClass().getResourceAsStream("/sounds/ohno.wav");
					AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				} catch(Exception ex) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + ex);
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
				}
			}
		}
		
		private class ExcuseMeWhatActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				try {
					InputStream is = getClass().getResourceAsStream("/sounds/excusemewhat.wav");
					AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				} catch(Exception ex) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + ex);
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
				
				int[] playerTiers = new int[numPlayers];
				if(usingCustomChances) {
					playerTiers = getPlayerTiers(customTierChances);
				}
				else {
					playerTiers = getPlayerTiers(standardTierChances);
				}
				
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
						if(!linesOfFile.get(player + 21).contains(currentlyAt) &&
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
				
				//then add to queue
				for(int playerAt = 0; playerAt < gotten.size(); playerAt++) {
					int tier = playerTiers[playerAt];
					
					if(skipping) {
						individualCannotGet[playerAt].removeLast();
					}
					
					if(tier == 0 && SSAllowedInCannotGetBuffer) {
						cannotGet.add(gotten.get(playerAt), tier);
					}
					else if(tier > 0 && tier < 4 && SAllowedInCannotGetBuffer) {
						cannotGet.add(gotten.get(playerAt), tier);
					}
					else if(tier >= 4) {
						cannotGet.add(gotten.get(playerAt), tier);
					}
					
					//if the gotten character is a favorite, don't add it to the
					//cannot get for rest of session queue
					if(!linesOfFile.get(30 + playerAt).contains(gotten.get(playerAt))) {
						individualCannotGet[playerAt].add(gotten.get(playerAt), tier);
					}
					System.out.println("[DEBUG]: Player " + (playerAt + 1) + " cannot get " + individualCannotGet[playerAt]);
				}
				
				System.out.println("[DEBUG]: Nobody can get " + cannotGet);
				
				skipping = false;
			}
		}
		
		private int[] getPlayerTiers(int[] chances) {
			int tierChance = ThreadLocalRandom.current().nextInt(0, 101);
			int tier = -1;
			int sum = 0;
			int[] playerTiers = new int[numPlayers];
			
			sum = chances[0];
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
			tier = convertToTierNum(tier);
			
			for(int at = 0; at < numPlayers; at++) {
				int adjust = tier + ThreadLocalRandom.current().nextInt(-2, 1);
				if(adjust < 0) {
					adjust = 0;
				}
				while(tierTurnedOff(adjust, chances)) {
					adjust++;
				}
				playerTiers[at] = adjust;
			}
			
			return playerTiers;
		}

		private boolean tierTurnedOff(int tier, int[] chances) {
			if(tier == 0 && chances[0] == 0) {
				return true;
			}
			else if((tier == 1 || tier == 2 || tier == 3) && chances[1] == 0) {
				return true;
			}
			else if((tier == 4 || tier == 5 || tier == 6) && chances[2] == 0) {
				return true;
			}
			else if((tier == 7 || tier == 8 || tier == 9) && chances[3] == 0) {
				return true;
			}
			else if((tier == 10 || tier == 11 || tier == 12) && chances[4] == 0) {
				return true;
			}
			else if((tier == 13 || tier == 14 || tier == 15) && chances[5] == 0) {
				return true;
			}
			else if((tier == 16 || tier == 17 || tier == 18) && chances[6] == 0) {
				return true;
			}
			else if((tier == 19 || tier == 20 || tier == 21) && chances[7] == 0) {
				return true;
			}
			else {
				return false;
			}
		}
		
		private String tierToString(int tier) {
			switch(tier) {
				case 0:
					return "Double S tier";
				case 1:
					return "Upper S tier";
				case 2:
					return "Mid S tier";
				case 3:
					return "Lower S tier";
				case 4:
					return "Upper A tier";
				case 5:
					return "Mid A tier";
				case 6:
					return "Lower A tier";
				case 7:
					return "Upper B tier";
				case 8:
					return "Mid B tier";
				case 9:
					return "Lower B tier";
				case 10:
					return "Upper C tier";
				case 11:
					return "Mid C tier";
				case 12:
					return "Lower C tier";
				case 13:
					return "Upper D tier";
				case 14:
					return "Mid D tier";
				case 15:
					return "Lower D tier";
				case 16:
					return "Upper E tier";
				case 17:
					return "Mid E tier";
				case 18:
					return "Lower E tier";
				case 19:
					return "Upper F tier";
				case 20:
					return "Mid F tier";
				case 21:
					return "Lower F tier";
				default:
					return "Invalid tier";
			}
		}
		
		private int convertToTierNum(int tier) {
			int change = ThreadLocalRandom.current().nextInt(-1, 2);
			
			if(tier == 0) {
				return 0;
			}
			else if(tier == 1) {
				return 2 + change;
			}
			else if(tier == 2) {
				return 5 + change;
			}
			else if(tier == 3) {
				return 8 + change;
			}
			else if(tier == 4) {
				return 11 + change;
			}
			else if(tier == 5) {
				return 14 + change;
			}
			else if(tier == 6) {
				return 17 + change;
			}
			else {
				return 20 + change;
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
					for(int at = 0; at < 29; at++) {
						linesOfFile.add(new ArrayList<String>());

					}
				}
			}
			
			JFileChooser fileChooser = new JFileChooser(".");
			FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt files", "txt");
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
				//double S 	index 0
				//upper S	index 1
				//mid S		index 2
				//lower S	index 3
				//upper A	index 4
				//mid A		index 5
				//lower A	index 6
				//upper B	index 7
				//mid B		index 8
				//lower B	index 9
				//upper C	index 10
				//mid C		index 11
				//lower C	index 12
				//upper D	index 13
				//mid D		index 14
				//lower D	index 15
				//upper E	index 16
				//mid E		index 17
				//lower E	index 18
				//upper F	index 19
				//mid F		index 20
				//lower F	index 21
				//p1exc		index 22
				//p2exc		index 23
				//p3exc		index 24
				//p4exc		index 25
				//p5exc		index 26
				//p6exc		index 27
				//p7exc		index 28
				//p8exc		index 29
				//p1fav		index 30
				//p2fav		index 31
				//p3fav		index 32
				//p4fav		index 33
				//p5fav		index 34
				//p6fav		index 35
				//p7fav		index 36
				//p8fav		index 37
				
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
								case "double s":
									readLine(0, at, lineAt);
									break;
								case "upper s":
									readLine(1, at, lineAt);
									break;
								case "mid s":
									readLine(2, at, lineAt);
									break;
								case "lower s":
									readLine(3, at, lineAt);
									break;
								case "upper a":
									readLine(4, at, lineAt);
									break;
								case "mid a":
									readLine(5, at, lineAt);
									break;
								case "lower a":
									readLine(6, at, lineAt);
									break;
								case "upper b":
									readLine(7, at, lineAt);
									break;
								case "mid b":
									readLine(8, at, lineAt);
									break;
								case "lower b":
									readLine(9, at, lineAt);
									break;
								case "upper c":
									readLine(10, at, lineAt);
									break;
								case "mid c":
									readLine(11, at, lineAt);
									break;
								case "lower c":
									readLine(12, at, lineAt);
									break;
								case "upper d":
									readLine(13, at, lineAt);
									break;
								case "mid d":
									readLine(14, at, lineAt);
									break;
								case "lower d":
									readLine(15, at, lineAt);
									break;
								case "upper e":
									readLine(16, at, lineAt);
									break;
								case "mid e":
									readLine(17, at, lineAt);
									break;
								case "lower e":
									readLine(18, at, lineAt);
									break;
								case "upper f":
									readLine(19, at, lineAt);
									break;
								case "mid f":
									readLine(20, at, lineAt);
									break;
								case "lower f":
									readLine(21, at, lineAt);
									break;
								case "p1 exclude":
									readLine(22, at, lineAt);
									break;
								case "p2 exclude":
									readLine(23, at, lineAt);
									break;
								case "p3 exclude":
									readLine(24, at, lineAt);
									break;
								case "p4 exclude":
									readLine(25, at, lineAt);
									break;
								case "p5 exclude":
									readLine(26, at, lineAt);
									break;
								case "p6 exclude":
									readLine(27, at, lineAt);
									break;
								case "p7 exclude":
									readLine(28, at, lineAt);
									break;
								case "p8 exclude":
									readLine(29, at, lineAt);
									break;
								case "p1 favorite":
									readLine(30, at, lineAt);
									break;
								case "p2 favorite":
									readLine(31, at, lineAt);
									break;
								case "p3 favorite":
									readLine(32, at, lineAt);
									break;
								case "p4 favorite":
									readLine(33, at, lineAt);
									break;
								case "p5 favorite":
									readLine(34, at, lineAt);
									break;
								case "p6 favorite":
									readLine(35, at, lineAt);
									break;
								case "p7 favorite":
									readLine(36, at, lineAt);
									break;
								case "p8 favorite":
									readLine(37, at, lineAt);
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
				results.append("File " + tierListFile.getName() + " not found!\n");
				fileLoaded = false;
				return;
			} catch(IOException ioe) {
				int hour = ZonedDateTime.now().getHour();
				int min = ZonedDateTime.now().getMinute();
				int sec = ZonedDateTime.now().getSecond();
				System.err.println("[" + hour + ":" + min + ":" + sec + "]: " + ioe);
				results.append("IOException in reading " + tierListFile.getName() + ".\n"
						+ "This means it is not a valid tier list file.\n" +
						"Please load a valid tier list file.\n");
				fileLoaded = false;
				return;
			}
			
			//printing out debug info in case anything ever goes wrong
			GenerateButtonActionListener gbal = new GenerateButtonActionListener();
			System.out.println("[DEBUG]: The following data has been loaded as the current tier list:");
			for(int at = 0; at < 22; at++) {
				System.out.println("         " + gbal.tierToString(at) + ":\t\t" + linesOfFile.get(at));
			}
			for(int at = 22; at < 30; at++) {
				System.out.println("         Player " + (at - 21) + " exclude:\t" + linesOfFile.get(at));
			}
			for(int at = 30; at < 38; at++) {
				System.out.println("         Player " + (at - 29) + " favorites:\t" + linesOfFile.get(at));
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
				}
			}
			else if(id == 1) {
				String next = "";
				int[] newTierChances = new int[8];
				int numAt = 0;
				
				try {
					for(int at = 0; at < toRead.length(); at++) {
						if(toRead.charAt(at) == ',') {
							newTierChances[numAt] = Integer.parseInt(next);
							next = "";
							numAt++;
							at++;
						}
						else {
							next += toRead.charAt(at);
						}
					}
					newTierChances[numAt] = Integer.parseInt(next);
				} catch(NumberFormatException e) {
					int hour = ZonedDateTime.now().getHour();
					int min = ZonedDateTime.now().getMinute();
					int sec = ZonedDateTime.now().getSecond();
					System.err.println("[" + hour + ":" + min + ":" + sec +"]: "
							+ next + " is not a valid tier chance.");
					return;
				}
				
				for(int at = 0; at < 8; at++) {
					customTierChances[at] = newTierChances[at];
				}
				SSTierSpinner.setValue(customTierChances[0]);
				STierSpinner.setValue(customTierChances[1]);
				ATierSpinner.setValue(customTierChances[2]);
				BTierSpinner.setValue(customTierChances[3]);
				CTierSpinner.setValue(customTierChances[4]);
				DTierSpinner.setValue(customTierChances[5]);
				ETierSpinner.setValue(customTierChances[6]);
				FTierSpinner.setValue(customTierChances[7]);
				
				applyButton.getActionListeners()[0].actionPerformed(null);
				
			}
			//this is literally impossible
			else {
				int hour = ZonedDateTime.now().getHour();
				int min = ZonedDateTime.now().getMinute();
				int sec = ZonedDateTime.now().getSecond();
				System.err.println("[" + hour + ":" + min + ":" + sec +"]: "
						+ id + " is not a valid setting id!");
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
				total += customTierChances[at];
			}
			
			if(total == 100) {
				customVerified = true;
				useCustomChances.setEnabled(true);
				usingCustomChances = true;
				useCustomChances.setSelected(true);
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
			}
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
	
	private class CannotGetSizeChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			cannotGetSize = (int) cannotGetSizeSpinner.getValue();
		}
	}

}
