package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import data.ProgramState;
import picker.BattleGenerator;
import picker.FileLoaderParser;
import util.Util;

/**
 * The <code>MainWindow</code> class is responsible for basically managing the
 * entire program. While <code>Driver</code> is the class that actually
 * launches the program, everything else is handled as an extension of
 * <code>MainWindow</code>.
 * <br><br>
 * This class essentially <i>is</i> the UI for the main window of the program,
 * which includes the results field, tier chance settings, switch options, and
 * other buttons for accessing other parts of the program. As such, those other
 * parts of the program are handled based on the <code>ActionListener</code>
 * classes in this program. For example, when you press the generate button,
 * the generate button's <code>ActionListener</code> will be responsible for
 * using the <code>BattleGenerator</code> class to generate a battle.
 * <br><br>
 * There is debate on the internet as to whether using multiple
 * <code>JFrame</code>s in a program is good or bad practice. Many seem to think
 * it's very bad practice. However, the reason I use separate frames for each
 * aspect of the program is that the program is very modular. You do not need to
 * use the soundboard, stats menu, debug menu, or even the advanced settings
 * menu to run the program. And there is no satisfactory way to allow them to
 * be opened and closed as needed without using separate <code>JFrame</code>s.
 * <code>CardLayout</code>s, <code>InternalWindow</code>s, and simply
 * dynamically resizing a single <code>JFrame</code> have not produced the
 * functionality that I want. As such, I choose to use separate
 * <code>JFrame</code> objects for each aspect of this program.
 * 
 * @author Jordan Knapp
 */
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
	private JButton lookupButton;
	private JButton reloadButton;
	
	private ProgramState state;
	private BattleGenerator battleGenerator;
	
	public MainWindow() {
		//begin by initializing the state of the program to its default state
		//as well as any other picker classes
		state = new ProgramState(this);
		battleGenerator = new BattleGenerator(state);
		
		//initializing the frame that holds everything together in the main
		//window
		frame = new JFrame("Smash Character Picker");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200, 450);
		//frame.setResizable(false);
		
		//we want to place the window 25 pixels to the left of the center
		frame.setLocationRelativeTo(null);
		frame.setLocation(frame.getX() - 25, frame.getY());
		
		//initialize TextAreas before setting look and feel
		results = new JTextArea();
		results.setEditable(false);
		results.setFont(results.getFont().deriveFont(18f));
	
		//initialize the debug now, as some errors may potentially occur during
		//the next part of the initialization
		Util.initDebug();
		
		//also initialize the stats text area before setting look and feel
		StatsOutput.initStats();
		
		//attempt to set look and feel, catching any errors
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			Util.error(e);
		} catch (InstantiationException e) {
			Util.error(e);
		} catch (IllegalAccessException e) {
			Util.error(e);
		} catch (UnsupportedLookAndFeelException e) {
			Util.error(e);
		}
		
		//GridBagConstraints object that will be used while constructing the ui
		GridBagConstraints gc = new GridBagConstraints();
		
		//initialize resultsPanel
		resultsPanel = new JPanel();
		resultsPanel.setBorder(BorderFactory.createTitledBorder("Results"));
		resultsPanel.setLayout(new BorderLayout());
		resultsPanel.add(results);
		resultsPanel.setPreferredSize(new Dimension(425, 260));
		
		//initialize switchPanel
		switchPanel = new JPanel();
		switchPanel.setBorder(BorderFactory.createTitledBorder("Switch"));
		switchPanel.setLayout(new BoxLayout(switchPanel, BoxLayout.Y_AXIS));
		switchPanel.setPreferredSize(new Dimension(50, 260));
		switchPanel.setToolTipText("Select 2 players and hit the switch button,"
				+ " and they will switch fighters.");
		
		//initialize SwitchManager and all its related components
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
		
		//add components to the switchPanel, including some boxes to make things
		//look nicer
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
		switchPanel.setEnabled(false);
		player1Box.setEnabled(false);
		player2Box.setEnabled(false);
		player3Box.setEnabled(false);
		player4Box.setEnabled(false);
		player5Box.setEnabled(false);
		player6Box.setEnabled(false);
		player7Box.setEnabled(false);
		player8Box.setEnabled(false);
		switchButton.setEnabled(false);
		
		//initialize bottomPanel
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridBagLayout());
		generateButton = new JButton("Generate");
		generateButton.addActionListener(new GenerateButtonActionListener());
		skipButton = new JButton("Skip");
		skipButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(state.numBattles != 0) {
					state.skipping = true;
					if(state.fileLoaded) {
						results.setText(battleGenerator.generateBattle());
					}
				}
			}
		});
		
		//attempt to set up the load button correctly
		loadButton = new JButton("Load");
		try {
			Image loadImage = ImageIO.read(getClass().getResource("/img/Open.png"));
			loadButton.setIcon(new ImageIcon(loadImage));
		} catch (IOException e) {
			Util.error(e);
		}
		
		//continue initializing the bottomPanel
		debugButton = new JButton("Debug");
		debugButton.addActionListener(new DebugButtonActionListener());
		loadButton.addActionListener(new LoadButtonActionListener());
		
		numPlayersLabel = new JLabel("Number of players: ");
		SpinnerNumberModel spinner = new SpinnerNumberModel(2, 2, 8, 1);
		numPlayersSpinner = new JSpinner(spinner);
		numPlayersSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				state.numPlayers = (int) numPlayersSpinner.getValue();
			}
		});
		
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
		
		//initialize tierChanceTopPanel
		tierChanceTopPanel = new JPanel();
		tierChanceTopPanel.setLayout(new GridBagLayout());
		tierChanceLabel1 = new JLabel("You can set custom chances for "
				+ "each tier.");
		tierChanceLabel2 = new JLabel("Remember to hit apply!");
		SpinnerNumberModel SSmod = new SpinnerNumberModel(state.tierChances[0], 0, 100, 1);
		SSTierSpinner = new JSpinner(SSmod);
		SSTierSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				state.newTierChances[0] = (int) SSTierSpinner.getValue();
			}
		});
		SSTierLabel = new JLabel("SS tier chance: ");
		SpinnerNumberModel Smod = new SpinnerNumberModel(state.tierChances[1], 0, 100, 1);
		STierSpinner = new JSpinner(Smod);
		STierSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				state.newTierChances[1] = (int) STierSpinner.getValue();
			}
		});
		STierLabel = new JLabel("S tier chance: ");
		SpinnerNumberModel Amod = new SpinnerNumberModel(state.tierChances[2], 0, 100, 1);
		ATierSpinner = new JSpinner(Amod);
		ATierSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				state.newTierChances[2] = (int) ATierSpinner.getValue();
			}
		});
		ATierLabel = new JLabel("A tier chance: ");
		SpinnerNumberModel Bmod = new SpinnerNumberModel(state.tierChances[3], 0, 100, 1);
		BTierSpinner = new JSpinner(Bmod);
		BTierSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				state.newTierChances[3] = (int) BTierSpinner.getValue();
			}
		});
		BTierLabel = new JLabel("B tier chance: ");
		SpinnerNumberModel Cmod = new SpinnerNumberModel(state.tierChances[4], 0, 100, 1);
		CTierSpinner = new JSpinner(Cmod);
		CTierSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				state.newTierChances[4] = (int) CTierSpinner.getValue();
			}
		});
		CTierLabel = new JLabel("C tier chance: ");
		SpinnerNumberModel Dmod = new SpinnerNumberModel(state.tierChances[5], 0, 100, 1);
		DTierSpinner = new JSpinner(Dmod);
		DTierSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				state.newTierChances[5] = (int) DTierSpinner.getValue();
			}
		});
		DTierLabel = new JLabel("D tier chance: ");
		SpinnerNumberModel Emod = new SpinnerNumberModel(state.tierChances[6], 0, 100, 1);
		ETierSpinner = new JSpinner(Emod);
		ETierSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				state.newTierChances[6] = (int) ETierSpinner.getValue();
			}
		});
		ETierLabel = new JLabel("E tier chance: ");
		SpinnerNumberModel Fmod = new SpinnerNumberModel(state.tierChances[7], 0, 100, 1);
		FTierSpinner = new JSpinner(Fmod);
		FTierSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				state.newTierChances[7] = (int) FTierSpinner.getValue();
			}
		});
		FTierLabel = new JLabel("F tier chance: ");
		
		//add components to tierChanceTopPanel
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
		
		//initialize tierChanceBottomPanel
		tierChanceBottomPanel = new JPanel();
		tierChanceBottomPanel.setLayout(new GridBagLayout());
		
		applyButton = new JButton("Apply tier chance settings");
		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				state.applyTierChances();
			}
		});
		applyButton.setToolTipText("Will ensure that the given values are "
				+ "valid, meaning they add up to 100.");
		bumpChanceLabel = new JLabel("Chances of bumping up tiers:");
		bump2 = new JLabel(" 2 tiers");
		bump1 = new JLabel("  1 tier");
		bump0 = new JLabel("Stay same");
		SpinnerNumberModel bumpMod0 = new SpinnerNumberModel(state.bumpChances[0], 0, 100, 1);
		bump0Spinner = new JSpinner(bumpMod0);
		bump0Spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				state.newBumpChances[0] = (int) bump0Spinner.getValue();
			}
		});
		SpinnerNumberModel bumpMod1 = new SpinnerNumberModel(state.bumpChances[1], 0, 100, 1);
		bump1Spinner = new JSpinner(bumpMod1);
		bump1Spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				state.newBumpChances[1] = (int) bump1Spinner.getValue();
			}
		});
		SpinnerNumberModel bumpMod2 = new SpinnerNumberModel(state.bumpChances[2], 0, 100, 1);
		bump2Spinner = new JSpinner(bumpMod2);
		bump2Spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				state.newBumpChances[2] = (int) bump2Spinner.getValue();
			}
		});
		
		//add components to tierChanceBottomPanel
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
		
		//initialize tierChancePanel
		tierChancePanel = new JPanel();
		tierChancePanel.setBorder(BorderFactory.createTitledBorder("Tier chance settings"));
		tierChancePanel.setLayout(new GridBagLayout());
		
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
		
		//initialize cannotGetPanel
		cannotGetPanel = new JPanel();
		cannotGetPanel.setBorder(BorderFactory.createTitledBorder("\"Cannot get\" buffer settings"));
		cannotGetPanel.setLayout(new GridBagLayout());
		SpinnerNumberModel cannotGetNumberModel = new SpinnerNumberModel(state.cannotGetSize, 0, 15, 1);
		cannotGetSizeSpinner = new JSpinner(cannotGetNumberModel);
		cannotGetSizeSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				state.cannotGetSize = (int) cannotGetSizeSpinner.getValue();
			}
		});
		cannotGetSizeLabel = new JLabel("Size of the \"Cannot get\" "
				+ "buffer: ");
		cannotGetSizeLabel.setToolTipText("<html>If this is set too high, the "
				+ "program could freeze, because there may be no valid "
				+ "fighters left for it to pick.<br>5 is the recommended size. "
				+ "Note that you have the option of whether or not S & SS "
				+ "tiers are allowed in this buffer.</html>");
		allowSSInCannotGet = new JCheckBox("Allow SS tiers in \"Cannot "
				+ "get\" buffer");
		allowSSInCannotGet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				state.allowSSInCannotGetBuffer = !state.allowSSInCannotGetBuffer;
			}
		});
		allowSSInCannotGet.setSelected(state.allowSSInCannotGetBuffer);
		allowSInCannotGet = new JCheckBox("Allow S tiers in \"Cannot "
				+ "get\" buffer");
		allowSInCannotGet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				state.allowSInCannotGetBuffer = !state.allowSInCannotGetBuffer;
			}
		});
		allowSInCannotGet.setSelected(state.allowSInCannotGetBuffer);
		
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
		
		//initialize upper stats panel
		statsTopPanel = new JPanel();
		statsTopPanel.setLayout(new BorderLayout());
		statsTopPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));
		statsTopPanel.add(StatsOutput.getTextArea(), BorderLayout.CENTER);
		
		//initialize lower stats panel
		statsBottomPanel = new JPanel();
		statsBottomPanel.setLayout(new GridBagLayout());
		playerLabel = new JLabel("Player: ");
		SpinnerNumberModel winnerSpinnerModel = new SpinnerNumberModel(1, 1, 8, 1);
		winnerSpinner = new JSpinner(winnerSpinnerModel);
		winnerSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				state.selectedWinner = (int) winnerSpinner.getValue();
			}
		});
		pickWinnerButton = new JButton("Select winner");
		pickWinnerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pickWinner();
			}
		});
		
		reloadButton = new JButton("⭯");
		reloadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StatsOutput.updateStatsScreen(state.numPlayers, state.stats, state.gotten);
			}
		});
		
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
		frame.add(resultsPanel, gc);
		gc.gridy = 1;
		gc.gridx = 0;
		gc.weightx = 1;
		gc.weighty = .15;
		gc.gridwidth = 3;
		frame.add(bottomPanel, gc);
		gc.gridy = 0;
		gc.gridx = 2;
		gc.gridwidth = 1;
		frame.add(switchPanel, gc);
		
		//Create icon
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/Icon.png")));
		
		frame.addWindowListener(new MainWindowListener());
		
		Util.log("Finished initializing MainWindow UI");
		
		frame.setVisible(true);
		
		//after initializing everything, see if there's a tier list file to load
		File tierListMaybe = new File("tier list.txt");
		if(tierListMaybe.exists()) {
			if(JOptionPane.showConfirmDialog(frame, "Found 'tier list.txt' file.\n"
						+ "Load it?", "Smash Character Picker",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				FileLoaderParser loader = new FileLoaderParser(this, state);
				loader.parseFile(tierListMaybe);
			}
		}
	}
	
	private class GenerateButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(!state.fileLoaded) {
				JOptionPane.showMessageDialog(frame, "You must load a tier "
						+ "list first!", "Smash Character Picker", 
						JOptionPane.ERROR_MESSAGE);
				state.skipping = false;
				return;
			}
			
			results.setText(battleGenerator.generateBattle());
			
			//the last thing we're gonna do is enable all the switch panel
			//stuff, now that it won't cause errors and whatnot.
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
	}
	
	private class LoadButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(state.fileLoaded) {
				if(JOptionPane.showConfirmDialog(frame, "You already have a file "
						+ "loaded.\nLoad another one?", "Smash Character Picker",
						JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
					return;
				}
				else {
					//if we are loading a new file, we first need to clear out
					//all information about the previous file
					state.linesOfFile.clear();
					for(int at = 0; at < 39; at++) {
						state.linesOfFile.add(new ArrayList<String>());
					}
				}
			}
			
			FileLoaderParser flp = new FileLoaderParser(MainWindow.this, state);
			flp.loadFile();
		}
	}
	
	private class DebugButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(!state.openedDebug) {
				new DebugWindow(MainWindow.this, state);
			}
		}
	}
	
	/**
	 * A class to handle the logic for switching players. This is part of the
	 * <code>MainWindow</code> class because switching logic is directly
	 * connected to the UI elements that make up the <code>SwitchPanel</code>,
	 * which is part of the <code>MainWindow</code> class.
	 * 
	 * @author Jordan Knapp
	 */
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
					JOptionPane.showMessageDialog(null, "Only select 2 players.",
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
					JOptionPane.showMessageDialog(null, "Only select 2 players.",
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
				if(state.numPlayers < 3) {
					JOptionPane.showMessageDialog(null, "There are not that "
							+ "many players present.", "Smash Character Picker",
							JOptionPane.WARNING_MESSAGE);
					player3Box.setSelected(false);
				}
				else if(numSelected == 2 && player3Box.isSelected()) {
					JOptionPane.showMessageDialog(null, "Only select 2 players.",
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
				if(state.numPlayers < 4) {
					JOptionPane.showMessageDialog(null, "There are not that "
							+ "many players present.", "Smash Character Picker",
							JOptionPane.WARNING_MESSAGE);
					player4Box.setSelected(false);
				}
				else if(numSelected == 2 && player4Box.isSelected()) {
					JOptionPane.showMessageDialog(null, "Only select 2 players.",
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
				if(state.numPlayers < 5) {
					JOptionPane.showMessageDialog(null, "There are not that "
							+ "many players present.", "Smash Character Picker",
							JOptionPane.WARNING_MESSAGE);
					player5Box.setSelected(false);
				}
				else if(numSelected == 2 && player5Box.isSelected()) {
					JOptionPane.showMessageDialog(null, "Only select 2 players.",
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
				if(state.numPlayers < 6) {
					JOptionPane.showMessageDialog(null, "There are not that "
							+ "many players present.", "Smash Character Picker",
							JOptionPane.WARNING_MESSAGE);
					player6Box.setSelected(false);
				}
				else if(numSelected == 2 && player6Box.isSelected()) {
					JOptionPane.showMessageDialog(null, "Only select 2 players.",
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
				if(state.numPlayers < 7) {
					JOptionPane.showMessageDialog(null, "There are not that "
							+ "many players present.", "Smash Character Picker",
							JOptionPane.WARNING_MESSAGE);
					player7Box.setSelected(false);
				}
				else if(numSelected == 2 && player7Box.isSelected()) {
					JOptionPane.showMessageDialog(null, "Only select 2 players.",
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
				if(state.numPlayers < 8) {
					JOptionPane.showMessageDialog(null, "There are not that "
							+ "many players present.", "Smash Character Picker",
							JOptionPane.WARNING_MESSAGE);
					player8Box.setSelected(false);
				}
				else if(numSelected == 2 && player8Box.isSelected()) {
					JOptionPane.showMessageDialog(null, "Only select 2 players.",
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
				String char1 = state.gotten.get(p1);
				String char2 = state.gotten.get(p2);
				int tier1 = state.individualCannotGet[p1].getAndRemoveLastTier();
				int tier2 = state.individualCannotGet[p2].getAndRemoveLastTier();
				
				Util.log("Swapping player " + (p1 + 1) + " (" + char1 + "),");
				Util.log("and player " + (p2 + 1) + " (" + char2 + ").");
				
				state.individualCannotGet[p1].add(char2, tier2);
				state.individualCannotGet[p2].add(char1, tier1);
				
				state.gotten.remove(char1);
				state.gotten.remove(char2);
				if(p2 > p1) {
					state.gotten.add(p1, char2);
					state.gotten.add(p2, char1);
				}
				else {
					state.gotten.add(p2, char1);
					state.gotten.add(p1, char2);
				}
				
				//refresh the screen
				results.setText("");
				results.setText("Battle #" + state.numBattles + ":\n");
				for(int at = 0; at < state.numPlayers; at++) {
					results.append("Player " + (at + 1) + " got "
							+ state.gotten.get(at) + ", "
							+ Util.tierToString(state.individualCannotGet[at].getLastTier())
							+ ".\n");
				}
				
				Util.log("Player " + (p1 + 1) + " now cannot get " + state.individualCannotGet[p1]);
				Util.log("Player " + (p2 + 1) + " now cannot get " + state.individualCannotGet[p2]);
				
				StatsOutput.updateStatsScreen(state.numPlayers, state.stats, state.gotten);
				
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
		}
	}
	
	/**
	 * A simple getter for the <code>MainWindow</code>'s <code>JFrame</code>'s
	 * x position.
	 * 
	 * @return	The x position of the <code>MainWindow</code>.
	 */
	public int getX() {
		return frame.getX();
	}
	
	/**
	 * A simple getter for the <code>MainWindow</code>'s <code>JFrame</code>'s
	 * y position.
	 * 
	 * @return	The y position of the <code>MainWindow</code>.
	 */
	public int getY() {
		return frame.getY();
	}
	
	/**
	 * A simple getter for the <code>MainWindow</code>'s <code>JFrame</code>'s
	 * width.
	 * 
	 * @return	The width of the <code>MainWindow</code>.
	 */
	public int getWidth() {
		return frame.getWidth();
	}
	
	/**
	 * A simple getter for the <code>MainWindow</code>'s <code>JFrame</code>'s
	 * height.
	 * 
	 * @return	The height of the <code>MainWindow</code>.
	 */
	public int getHeight() {
		return frame.getHeight();
	}
	
	/**
	 * A method that allows printed messages directly to the results field in
	 * the main window. Primarily used by the <code>FileLoaderParser</code> in
	 * the event of critical errors when loading files, because the user should
	 * be aware of those regardless of whether or not they have the debug
	 * window open.
	 * <br><br>
	 * Note that the results field will be cleared beforehand.
	 * 
	 * @param toPrint	The message to be printed. A newline will be appended
	 * 					to it.
	 */
	public void printToResult(String toPrint) {
		results.setText("");
		results.append(toPrint + "\n");
	}
	
	/**
	 * Method that updates the value in the cannot get spinner to whatever the
	 * current cannot get size is. Used by the <code>FileLoaderParser</code>
	 * when it parses this setting from a tier list file.
	 */
	public void updateCannotGetSpinner() {
		cannotGetSizeSpinner.setValue(state.cannotGetSize);
	}
	
	/**
	 * Method that updates the value in the checkbox representing whether SS
	 * tiers are allowed in the cannot get buffer. Used by the
	 * <code>FileLoaderParser</code> when it parses this setting from a tier
	 * list file.
	 */
	public void updateSSAllowedInCannotGet() {
		allowSSInCannotGet.setSelected(state.allowSSInCannotGetBuffer);
	}
	
	/**
	 * Method that updates the value in the checkbox representing whether S
	 * tiers are allowed in the cannot get buffer. Used by the
	 * <code>FileLoaderParser</code> when it parses this setting from a tier
	 * list file.
	 */
	public void updateSAllowedInCannotGet() {
		allowSInCannotGet.setSelected(state.allowSInCannotGetBuffer);
	}
	
	/**
	 * Method that updates the value in the spinner representing the number of
	 * players to whatever the number of players is. Used by the
	 * <code>FileLoaderParser</code> when it parses this setting from a tier
	 * list file.
	 */
	public void updateNumPlayersSpinner() {
		numPlayersSpinner.setValue(state.numPlayers);
	}
	
	/**
	 * Method that updates the values in the spinners representing the chances
	 * for each tier to whatever the current values are. Used by the
	 * <code>FileLoaderParser</code> when it parses this setting from a tier
	 * list file. Also used by the <code>ProgramState</code> when you update the
	 * tier chances.
	 */
	public void updateTierChances() {
		SSTierSpinner.setValue(state.newTierChances[0]);
		STierSpinner.setValue(state.newTierChances[1]);
		ATierSpinner.setValue(state.newTierChances[2]);
		BTierSpinner.setValue(state.newTierChances[3]);
		CTierSpinner.setValue(state.newTierChances[4]);
		DTierSpinner.setValue(state.newTierChances[5]);
		ETierSpinner.setValue(state.newTierChances[6]);
		FTierSpinner.setValue(state.newTierChances[7]);
		
		bump0Spinner.setValue(state.bumpChances[0]);
		bump1Spinner.setValue(state.bumpChances[1]);
		bump2Spinner.setValue(state.bumpChances[2]);
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
			StatsOutput.updateStatsScreen(state.numPlayers, state.stats, state.gotten);
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
			StatsOutput.updateStatsScreen(state.numPlayers, state.stats, state.gotten);
		}
	}
	
	private class MainWindowListener implements WindowListener {

		public void windowOpened(WindowEvent e) {

		}

		public void windowClosing(WindowEvent e) {
			if(state.needToSaveStats) {
				try {
					FileOutputStream fos = new FileOutputStream(state.statsFile);
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(state.stats);
					oos.close();
					fos.close();
				} catch (FileNotFoundException e1) {
					Util.error(e1);
				} catch (IOException e1) {
					Util.error(e1);
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

}
