package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import data.Matchup;
import data.Settings;
import data.TierList;
import util.Util;

public class MainWindow {
	
	//all Swing objects are fields of the MainWindow class
	
	private JFrame frame;
	
	private JPanel resultsPanel;
	private JPanel switchPanel;
	private JPanel bottomPanel;
	
	//leftPanel will encapsulate the tierChancePanel and cannotGetPanel
	private JPanel leftPanel;
	private JPanel cannotGetPanel;
	
	//tierChancePanel will encapsulate tierChanceTopPanel and tierChanceBottomPanel
	private JPanel tierChancePanel;
	private JPanel tierChanceTopPanel;
	private JPanel tierChanceBottomPanel;
	
	//statsPanel will encapsulate statsTopPanel and statsBottomPanel
	private JPanel statsPanel;
	private JPanel statsTopPanel;
	private JPanel statsBottomPanel;
	
	//resultsPanel only has one component, the results text area
	private JTextArea results;
	
	//bottomPanel components
	private JButton generateButton;
	private JSpinner numPlayersSpinner;
	private JLabel numPlayersLabel;
	private JButton loadButton;
	private JButton skipButton;
	private JButton debugButton;
	
	//switchPanel components
	private JCheckBox player1Box;
	private JCheckBox player2Box;
	private JCheckBox player3Box;
	private JCheckBox player4Box;
	private JCheckBox player5Box;
	private JCheckBox player6Box;
	private JCheckBox player7Box;
	private JCheckBox player8Box;
	private JButton switchButton;
	
	//tierChanceTopPanel components
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
	
	//tierChanceBottomPanel components
	private JLabel bumpChanceLabel;
	private JLabel bump1;
	private JLabel bump2;
	private JLabel bump0;
	private JSpinner bump1Spinner;
	private JSpinner bump2Spinner;
	private JSpinner bump0Spinner;
	private JButton applyButton;
	
	//cannot get panel components
	private JLabel cannotGetSizeLabel;
	private JSpinner cannotGetSizeSpinner;
	private JCheckBox allowSSInCannotGet;
	private JCheckBox allowSInCannotGet;
	
	//stats panel components
	private JLabel playerLabel;
	private JSpinner winnerSpinner;
	private JButton pickWinnerButton;
	private JButton searchButton;
	private JButton sortButton;
	private JButton modButton;
	private JButton reloadButton;
	
	//other variables	
	private TierList tierList;
	private boolean fileLoaded;
	private int numBattles;
	private List<Matchup> previousMatchups;
	private int[] switchVals;
	
	public MainWindow() throws Exception {	
		//initialize the frame and put it in the middle of the screen
		frame = new JFrame("Smash Character Picker");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200, 450);
		frame.setLocationRelativeTo(null);
		
		//initialize textareas before setting look and feel
		results = new JTextArea();
		results.setEditable(false);
		results.setFont(results.getFont().deriveFont(18f));
		
		Util.initDebug();
		
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		//will be used to set up a majority of the UI
		GridBagConstraints gc = new GridBagConstraints();
		
		//initialize resultsPanel
		resultsPanel = new JPanel(new BorderLayout());
		resultsPanel.setBorder(BorderFactory.createTitledBorder("Results"));
		resultsPanel.add(results);
		resultsPanel.setPreferredSize(new Dimension(425, 260));
		
		//initialize switchPanel
		switchPanel = new JPanel();
		switchPanel.setBorder(BorderFactory.createTitledBorder("Switch"));
		switchPanel.setLayout(new BoxLayout(switchPanel, BoxLayout.Y_AXIS));
		switchPanel.setToolTipText("Select 2 fighters and hit the switch button, " +
				"and they will switch fighters.");
		
		//TODO: initialize the SwitchManager, which should act as an
		//ActionListener too?
		
		player1Box = new JCheckBox("P1");
		player1Box.addActionListener(new SwitchActionListener(1));
		player2Box = new JCheckBox("P2");
		player2Box.addActionListener(new SwitchActionListener(2));
		player3Box = new JCheckBox("P3");
		player3Box.addActionListener(new SwitchActionListener(3));
		player4Box = new JCheckBox("P4");
		player4Box.addActionListener(new SwitchActionListener(4));
		player5Box = new JCheckBox("P5");
		player5Box.addActionListener(new SwitchActionListener(5));
		player6Box = new JCheckBox("P6");
		player6Box.addActionListener(new SwitchActionListener(6));
		player7Box = new JCheckBox("P7");
		player7Box.addActionListener(new SwitchActionListener(7));
		player8Box = new JCheckBox("P8");
		player8Box.addActionListener(new SwitchActionListener(8));
		
		switchButton = new JButton("Switch");
		switchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//make sure that we have 2 players selected
				if(switchVals[0] == 0 || switchVals[1] == 0) {
					//TODO: at some point, maybe change some of the message types
					JOptionPane.showMessageDialog(null, "Please select 2 players.",
							"Smash Character Picker", JOptionPane.ERROR_MESSAGE);
					
					return;
				}
				
				int p1 = switchVals[0];
				int p2 = switchVals[1];
				
				//if so, we're good to switch using the last matchup
				Matchup toSwap = previousMatchups.get(previousMatchups.size() - 1);
				tierList.swapFighters(p1, toSwap.getFighter(p1), p2, toSwap.getFighter(p2));
				toSwap.swapFighters(p1, p2);
				
				results.setText("Battle #" + numBattles + ":" + toSwap);
			}
		});
		
		//add components to switchPanel, including some boxes to make things
		//look a bit nicer
		switchPanel.add(Box.createRigidArea(new Dimension(0, 25)));
		switchPanel.add(player1Box);
		switchPanel.add(Box.createRigidArea(new Dimension(0, 3)));
		switchPanel.add(player2Box);
		switchPanel.add(Box.createRigidArea(new Dimension(0, 3)));
		switchPanel.add(player3Box);
		switchPanel.add(Box.createRigidArea(new Dimension(0, 3)));
		switchPanel.add(player4Box);
		switchPanel.add(Box.createRigidArea(new Dimension(0, 3)));
		switchPanel.add(player5Box);
		switchPanel.add(Box.createRigidArea(new Dimension(0, 3)));
		switchPanel.add(player6Box);
		switchPanel.add(Box.createRigidArea(new Dimension(0, 3)));
		switchPanel.add(player7Box);
		switchPanel.add(Box.createRigidArea(new Dimension(0, 3)));
		switchPanel.add(player8Box);
		switchPanel.add(Box.createRigidArea(new Dimension(0, 110)));
		switchPanel.add(switchButton);
		
		//disable literally everything in the switchPanel until a battle has
		//been generated. it can be buggy before then. besides, you literally
		//can't switch fighters when none have been picked yet.
		player1Box.setEnabled(false);
		player2Box.setEnabled(false);
		player3Box.setEnabled(false);
		player4Box.setEnabled(false);
		player5Box.setEnabled(false);
		player6Box.setEnabled(false);
		player7Box.setEnabled(false);
		player8Box.setEnabled(false);
		switchButton.setEnabled(false);
		
		//set up the bottom panel
		
		bottomPanel = new JPanel(new GridBagLayout());
		
		generateButton = new JButton("Generate");
		generateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!fileLoaded) {
					JOptionPane.showMessageDialog(null, "You must load a tier list first!",
							"Smash Character Picker", JOptionPane.ERROR_MESSAGE);
					
					return;
				}
				
				double startTime = System.currentTimeMillis();
				
				numBattles++;
				
				Util.log("========== BEGINNING GENERATION OF BATTLE " + numBattles + " ==========");
				
				Matchup result = null;
				Settings settings = getSettings();
				int tries = 0;
				while(result == null && !previousMatchups.contains(result) && tries < 100) {
					tries++;
					Util.log("======= Try " + tries + " =======");
					
					result = tierList.generateBattle(settings, false);
				}
				
				Util.log("========== End battle generation process ==========");
				
				String resultString;
				if(result == null) {
					resultString = "No valid battles found after 100 tries.";
				}
				else {
					resultString = "Battle #" + numBattles + ":\n" + result.toString();
				}
				
				results.setText(resultString);
				
				//finally, enable all the switch stuff if this was the first battle
				if(numBattles == 1) {
					switchPanel.setEnabled(true);
					player1Box.setEnabled(true);
					player2Box.setEnabled(true);
					player3Box.setEnabled(true);
					player4Box.setEnabled(true);
					player5Box.setEnabled(true);
					player6Box.setEnabled(true);
					player7Box.setEnabled(true);
					player8Box.setEnabled(true);
					switchButton.setEnabled(true);
				}
				
				double delta = System.currentTimeMillis() - startTime;
				Util.log("Generation of this battle took " + delta + "ms.");
			}
		});
		
		skipButton = new JButton("Skip");
		skipButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!fileLoaded) {
					JOptionPane.showMessageDialog(null, "You must load a tier list first!",
							"Smash Character Picker", JOptionPane.ERROR_MESSAGE);
					
					return;
				}
				
				if(numBattles == 0) {
					JOptionPane.showMessageDialog(null, "There must be a battle before " +
							"you can skip.", "Smash Character Picker",
							JOptionPane.ERROR_MESSAGE);
					
					return;
				}
				
				double startTime = System.currentTimeMillis();
				
				Util.log("========== RESULT FOR BATTLE " + numBattles + " SKIPPED, GENERATING AGAIN ==========");
				
				Matchup result = null;
				Settings settings = getSettings();
				int tries = 0;
				while(result == null && !previousMatchups.contains(result) && tries < 100) {
					tries++;
					Util.log("======= Try " + tries + " =======");
					
					result = tierList.generateBattle(settings, true);
				}
				
				Util.log("========== End battle generation process ==========");
				
				String resultString;
				if(result == null) {
					resultString = "No valid battles found after 100 tries.";
				}
				else {
					previousMatchups.add(result);
					resultString = "Battle #" + numBattles + ":\n" + result.toString();
				}
				
				results.setText(resultString);
				
				double delta = System.currentTimeMillis() - startTime;
				Util.log("Generation of this battle took " + delta + "ms.");
			}
		});
		
		loadButton = new JButton("Load");
		Image loadImage = ImageIO.read(getClass().getResource("/img/Open.png"));
		loadButton.setIcon(new ImageIcon(loadImage));
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(".");
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Documents (*.txt)", "txt");
				fileChooser.setFileFilter(filter);
				
				if(fileChooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) {
					return;
				}
				
				Settings settings = null;
				
				try {
					tierList = new TierList();
					settings = tierList.loadFile(fileChooser.getSelectedFile());
					fileLoaded = true;
				} catch(FileNotFoundException e1) {
					results.setText("File " + fileChooser.getSelectedFile().getName() +
							" not found!");
					Util.error(e1);
					
					tierList = null;
					fileLoaded = false;
				} catch(IOException e1) {
					results.setText("IOException when reading " +
							fileChooser.getSelectedFile().getName() + "!\n" +
							"See the debug log for details.");
					Util.error(e1);
					
					tierList = null;
					fileLoaded = false;
				}
				
				//if file was loaded, update the UI
				if(fileLoaded) {
					Util.log("The following data was loaded as the tier list:\n" + tierList.toString());
					
					cannotGetSizeSpinner.setValue(settings.getCannotGetSize());
					allowSSInCannotGet.setSelected(settings.ssAllowedInCannotGet());
					allowSInCannotGet.setSelected(settings.sAllowedInCannotGet());
					numPlayersSpinner.setValue(settings.getNumPlayers());
					
					SSTierSpinner.setValue(settings.getTierChance(0));
					STierSpinner.setValue(settings.getTierChance(1));
					ATierSpinner.setValue(settings.getTierChance(2));
					BTierSpinner.setValue(settings.getTierChance(3));
					CTierSpinner.setValue(settings.getTierChance(4));
					DTierSpinner.setValue(settings.getTierChance(5));
					ETierSpinner.setValue(settings.getTierChance(6));
					FTierSpinner.setValue(settings.getTierChance(7));
					
					bump0Spinner.setValue(settings.getBumpChance(0));
					bump1Spinner.setValue(settings.getBumpChance(1));
					bump2Spinner.setValue(settings.getBumpChance(2));
				}
			}
		});
		
		debugButton = new JButton("Debug");
		//TODO: add action listener
		
		numPlayersLabel = new JLabel("Number of players: ");
		SpinnerNumberModel model = new SpinnerNumberModel(2, 2, 8, 1);
		numPlayersSpinner = new JSpinner(model);
		
		//add components to the bottomPanel
		gc.weightx = .03;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.anchor = GridBagConstraints.CENTER;
		bottomPanel.add(loadButton, gc);
		gc.weightx = .77;
		gc.gridx = 1;
		bottomPanel.add(generateButton, gc);
		gc.weightx = .05;
		gc.gridx = 2;
		bottomPanel.add(skipButton, gc);
		gc.gridx = 3;
		bottomPanel.add(debugButton, gc);
		gc.gridx = 4;
		gc.weightx = .03;
		gc.fill = GridBagConstraints.NONE;
		bottomPanel.add(numPlayersLabel, gc);
		gc.gridx = 5;
		gc.weightx = .05;
		bottomPanel.add(numPlayersSpinner, gc);
		
		//set up tierChanceTopPanel
		
		tierChanceTopPanel = new JPanel(new GridBagLayout());
		
		tierChanceLabel1 = new JLabel("You can set custom chances for each " +
				"tier.");
		tierChanceLabel2 = new JLabel("Remember to hit apply!");
		
		//i've learned an unfortunate lesson, all the spinners need to have
		//different models. otherwise they all do exactly the same thing.
		//so fine. also, these default values should be the same as what the
		//default constructor for TierList does.
		model = new SpinnerNumberModel(10, 0, 100, 1);
		SSTierSpinner = new JSpinner(model);
		model = new SpinnerNumberModel(20, 0, 100, 1);
		STierSpinner = new JSpinner(model);
		model = new SpinnerNumberModel(25, 0, 100, 1);
		ATierSpinner = new JSpinner(model);
		model = new SpinnerNumberModel(25, 0, 100, 1);
		BTierSpinner = new JSpinner(model);
		model = new SpinnerNumberModel(20, 0, 100, 1);
		CTierSpinner = new JSpinner(model);
		model = new SpinnerNumberModel(0, 0, 100, 1);
		DTierSpinner = new JSpinner(model);
		model = new SpinnerNumberModel(0, 0, 100, 1);
		ETierSpinner = new JSpinner(model);
		model = new SpinnerNumberModel(0, 0, 100, 1);
		FTierSpinner = new JSpinner(model);
		
		//also create all the labels
		SSTierLabel = new JLabel("SS tier chance: ");
		STierLabel = new JLabel("S tier chance: ");
		ATierLabel = new JLabel("A tier chance: ");
		BTierLabel = new JLabel("B tier chance: ");
		CTierLabel = new JLabel("C tier chance: ");
		DTierLabel = new JLabel("D tier chance: ");
		ETierLabel = new JLabel("E tier chance: ");
		FTierLabel = new JLabel("F tier chance: ");
		
		//and then add to the panel
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = 2;
		tierChanceTopPanel.add(tierChanceLabel1, gc);
		gc.gridy = 1;
		tierChanceTopPanel.add(tierChanceLabel2, gc);
		gc.anchor = GridBagConstraints.CENTER;
		gc.anchor = GridBagConstraints.LINE_END;
		gc.gridwidth = 1;
		gc.gridy = 2;
		tierChanceTopPanel.add(SSTierLabel, gc);
		gc.gridy = 3;
		tierChanceTopPanel.add(STierLabel, gc);
		gc.gridy = 4;
		tierChanceTopPanel.add(ATierLabel, gc);
		gc.gridy = 5;
		tierChanceTopPanel.add(BTierLabel, gc);
		gc.gridy = 6;
		tierChanceTopPanel.add(CTierLabel, gc);
		gc.gridy = 7;
		tierChanceTopPanel.add(DTierLabel, gc);
		gc.gridy = 8;
		tierChanceTopPanel.add(ETierLabel, gc);
		gc.gridy = 9;
		tierChanceTopPanel.add(FTierLabel, gc);
		gc.gridx = 1;
		gc.gridy = 2;
		gc.anchor = GridBagConstraints.LINE_START;
		tierChanceTopPanel.add(SSTierSpinner, gc);
		gc.gridy = 3;
		tierChanceTopPanel.add(STierSpinner, gc);
		gc.gridy = 4;
		tierChanceTopPanel.add(ATierSpinner, gc);
		gc.gridy = 5;
		tierChanceTopPanel.add(BTierSpinner, gc);
		gc.gridy = 6;
		tierChanceTopPanel.add(CTierSpinner, gc);
		gc.gridy = 7;
		tierChanceTopPanel.add(DTierSpinner, gc);
		gc.gridy = 8;
		tierChanceTopPanel.add(ETierSpinner, gc);
		gc.gridy = 9;
		tierChanceTopPanel.add(FTierSpinner, gc);
		
		//set up tierChanceBottomPanel
		
		tierChanceBottomPanel = new JPanel(new GridBagLayout());
		
		applyButton = new JButton("Apply tier chance settings");
		
		bumpChanceLabel = new JLabel("Chances of bumping up tiers:");
		bump2 = new JLabel(" 2 tiers");
		bump1 = new JLabel("  1 tier");
		bump0 = new JLabel("Stay same");
		
		model = new SpinnerNumberModel(50, 0, 100, 1);
		bump0Spinner = new JSpinner(model);
		model = new SpinnerNumberModel(25, 0, 100, 1);
		bump1Spinner = new JSpinner(model);
		model = new SpinnerNumberModel(25, 0, 100, 1);
		bump2Spinner = new JSpinner(model);
		
		//add components to the panel
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = 3;
		gc.anchor = GridBagConstraints.CENTER;
		tierChanceBottomPanel.add(bumpChanceLabel, gc);
		gc.gridy = 1;
		gc.gridwidth = 1;
		gc.anchor = GridBagConstraints.LINE_START;
		tierChanceBottomPanel.add(bump0Spinner, gc);
		gc.gridx = 1;
		tierChanceBottomPanel.add(bump1Spinner, gc);
		gc.gridx = 2;
		tierChanceBottomPanel.add(bump2Spinner, gc);
		gc.gridx = 0;
		gc.gridy = 2;
		tierChanceBottomPanel.add(bump0, gc);
		gc.gridx = 1;
		tierChanceBottomPanel.add(bump1, gc);
		gc.gridx = 2;
		tierChanceBottomPanel.add(bump2, gc);
		gc.gridx = 0;
		gc.gridy = 3;
		gc.gridwidth = 3;
		gc.anchor = GridBagConstraints.CENTER;
		tierChanceBottomPanel.add(applyButton, gc);
		
		tierChancePanel = new JPanel(new GridBagLayout());
		tierChancePanel.setBorder(BorderFactory.createTitledBorder("Tier chance settings"));
		
		//add components to tierChancePanel
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = 1;
		gc.anchor = GridBagConstraints.CENTER;
		gc.weighty = .7;
		tierChancePanel.add(tierChanceTopPanel, gc);
		gc.gridy = 1;
		gc.weighty = .3;
		tierChancePanel.add(tierChanceBottomPanel, gc);
		tierChancePanel.setPreferredSize(new Dimension(240, 280));
		
		//set up cannotGetPanel
		
		cannotGetPanel = new JPanel(new GridBagLayout());
		cannotGetPanel.setBorder(BorderFactory.createTitledBorder("\"Cannot Get\" buffer settings"));
		
		//default cannot get size defined here
		model = new SpinnerNumberModel(10, 0, 15, 1);
		cannotGetSizeSpinner = new JSpinner(model);
		
		cannotGetSizeLabel = new JLabel("Size of the \"Cannot Get\" buffer: ");
		cannotGetSizeLabel.setToolTipText("<html>If this is set too high, " +
				"the program could freeze, because there may be no valid " +
				"fighters left for it to pick.<br>With all fighters being " +
				"included, it should be safe to go up to 15, assuming there " +
				"are 2 players.<br>Note that you have the option as to " +
				"whether S & SS tiers are allowed in the buffer.</html>");
		
		allowSSInCannotGet = new JCheckBox("Allow SS tiers in \"Cannot Get\" buffer");
		allowSInCannotGet = new JCheckBox("Allow S tiers in \"Cannot Get\" buffer");
		
		//add components to cannotGetPanel
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
		leftPanel.add(tierChancePanel, gc);
		gc.gridy = 1;
		leftPanel.add(cannotGetPanel, gc);
		
		//set up upper stats panel
		
		statsTopPanel = new JPanel(new BorderLayout());
		statsTopPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));
		//TODO: handle stats text area
		
		//set up lower stats panel
		statsBottomPanel = new JPanel(new GridBagLayout());
		
		playerLabel = new JLabel("Player: ");
		model = new SpinnerNumberModel(1, 1, 8, 1);
		winnerSpinner = new JSpinner(model);
		
		pickWinnerButton = new JButton("Select winner");
		//TODO: add action listener
		
		searchButton = new JButton("Search");
		//TODO: add action listener
		
		sortButton = new JButton("Sort");
		//TODO: add action listener
		
		modButton = new JButton("Mod");
		//TODO: add action listener
		
		reloadButton = new JButton("⭯");
		//TODO: add action listener
		
		statsPanel = new JPanel(new GridBagLayout());
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weighty = 1;
		gc.weightx = .1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		statsBottomPanel.add(playerLabel, gc);
		gc.gridx = 1;
		gc.weightx = .05;
		statsBottomPanel.add(winnerSpinner, gc);
		gc.gridx = 2;
		gc.weightx = .3;
		statsBottomPanel.add(Box.createRigidArea(new Dimension(3, 0)), gc);
		gc.gridx = 3;
		gc.weightx = .35;
		statsBottomPanel.add(pickWinnerButton, gc);
		gc.gridx = 4;
		gc.weightx = .25;
		statsBottomPanel.add(searchButton, gc);
		gc.gridx = 5;
		statsBottomPanel.add(sortButton, gc);
		gc.gridx = 6;
		statsBottomPanel.add(modButton, gc);
		gc.gridx = 7;
		statsBottomPanel.add(reloadButton, gc);
		
		//add top and bottom panel to main stats panel
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = .98;
		gc.fill = GridBagConstraints.BOTH;
		gc.anchor = GridBagConstraints.CENTER;
		statsPanel.setLayout(new GridBagLayout());
		JScrollPane scrollPane = new JScrollPane(statsTopPanel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		statsPanel.add(scrollPane, gc);
		gc.gridy = 1;
		gc.weighty = .02;
		statsPanel.add(statsBottomPanel, gc);
		
		//put it all together
		frame.getContentPane().setLayout(new GridBagLayout());
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = .2;
		gc.weighty = .85;
		gc.anchor = GridBagConstraints.CENTER;
		gc.fill = GridBagConstraints.BOTH;
		frame.add(leftPanel, gc);
		gc.weightx = .45;
		gc.gridx = 1;
		frame.add(resultsPanel, gc);
		gc.gridy = 1;
		gc.gridx = 0;
		gc.weightx = 1;
		gc.weighty = .15;
		gc.gridwidth = 3;
		frame.add(bottomPanel, gc);
		gc.gridy = 0;
		gc.gridx = 2;
		gc.weightx = .05;
		gc.gridwidth = 1;
		frame.add(switchPanel, gc);
		gc.gridx = 3;
		gc.weightx = .3;
		gc.gridheight = 2;
		frame.add(statsPanel, gc);
		
		//Create icon
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/Icon.png")));
		
		//TODO: add window listener for stuff like saving stats
		
		Util.log("Finished initializing MainWindow UI");
		
		fileLoaded = false;
		numBattles = 0;
		previousMatchups = new ArrayList<Matchup>();
		switchVals = new int[2];
		
		frame.setVisible(true);
		
		//TODO: attempt to load tier list
	}
	
	/**
	 * @return	A <code>Settings</code> object representing the current
	 * 			settings selected in the UI.
	 */
	private Settings getSettings() {
		int[] tierChances = new int[8];
		int[] bumpChances = new int[3];
		
		tierChances[0] = (int) SSTierSpinner.getValue();
		tierChances[1] = (int) STierSpinner.getValue();
		tierChances[2] = (int) ATierSpinner.getValue();
		tierChances[3] = (int) BTierSpinner.getValue();
		tierChances[4] = (int) CTierSpinner.getValue();
		tierChances[5] = (int) DTierSpinner.getValue();
		tierChances[6] = (int) ETierSpinner.getValue();
		tierChances[7] = (int) FTierSpinner.getValue();
		
		bumpChances[0] = (int) bump0Spinner.getValue();
		bumpChances[1] = (int) bump1Spinner.getValue();
		bumpChances[2] = (int) bump2Spinner.getValue();
		
		return new Settings((int) numPlayersSpinner.getValue(),
				tierChances, bumpChances,
				(int) cannotGetSizeSpinner.getValue(),
				allowSInCannotGet.isSelected(), allowSSInCannotGet.isSelected());
	}
	
	private class SwitchActionListener implements ActionListener {
		private int player;
		private int indexSet;
		
		public SwitchActionListener(int player) {
			this.player = player;
			indexSet = -1;
		}
		
		public void actionPerformed(ActionEvent e) {
			JCheckBox clicked = ((JCheckBox) e.getSource());
			
			//if we're unchecking, need to remove the current value from
			//switchVals
			if(!clicked.isSelected()) {
				switchVals[indexSet] = 0;
				indexSet = -1;
				return;
			}
			
			//make sure there are this many players present
			if(player > (int) numPlayersSpinner.getValue()) {
				JOptionPane.showMessageDialog(null, "There are not that many " +
						"players present.", "Smash Character Picker",
						JOptionPane.ERROR_MESSAGE);
				
				clicked.setSelected(false);
			}
			//then check how many are already selected based on what's set
			//in the switchVals array
			else if(switchVals[0] != 0 && switchVals[1] != 0) {
				JOptionPane.showMessageDialog(null, "You can select up to " +
						"two players.", "Smash Character Picker",
						JOptionPane.ERROR_MESSAGE);
				
				clicked.setSelected(false);
			}
			//otherwise, just need to determine whether we're setting index 0 or 1
			else if(switchVals[0] == 0) {
				switchVals[0] = player;
				indexSet = 0;
				
				Util.log("Selected player " + player + " in index " + indexSet + " to switch.");
			}
			else {
				switchVals[1] = player;
				indexSet = 1;
				
				Util.log("Selected player " + player + " in index " + indexSet + " to switch.");
			}
		}
	}
}
