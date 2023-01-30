package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import data.Fighter;

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
	
	//various state variables
	//TODO: determine whether or not any of these values are actually necessary (can we just pass the direct UI element's value?)
	private int numPlayers;
	
	private int[] tierChances;
	private int[] newTierChances;
	private int[] bumpChances;
	private int[] newBumpChances;
	
	private int cannotGetSize;
	private Queue<Fighter> cannotGetQueue;
	
	private boolean sInCannotGet;
	private boolean ssInCannotGet;
	
	public MainWindow() {
		//before we initialize anything, initialize the state variables to
		//their default values
		numPlayers = 3;
		
		tierChances = new int[] {10, 20, 25, 25, 20, 0, 0, 0};
		bumpChances = new int[] {70, 15, 15};
		newTierChances = new int[] {10, 20, 25, 25, 20, 0, 0, 0};
		newBumpChances = new int[] {70, 15, 15};
		
		cannotGetSize = 10;
		cannotGetQueue = new ArrayDeque<Fighter>();
		
		sInCannotGet = true;
		ssInCannotGet = false;
		
		//initialize the frame and put it in the middle of the screen
		frame = new JFrame("Smash Character Picker");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200, 450);
		frame.setLocationRelativeTo(null);
		
		//initialize textareas before setting look and feel
		results = new JTextArea();
		results.setEditable(false);
		results.setFont(results.getFont().deriveFont(18f));
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(ClassNotFoundException e) {
			//TODO: handle these exceptions
		} catch(InstantiationException e) {
			
		} catch(IllegalAccessException e) {
			
		} catch(UnsupportedLookAndFeelException e) {
			
		}
		
		//will be used to set up a majority of the UI
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
		switchPanel.setToolTipText("Select 2 fighters and hit the switch button, " +
				"and they will switch fighters.");
		
		//TODO: initialize the SwitchManager, which should act as an
		//ActionListener too?
		
		player1Box = new JCheckBox("P1");
		player2Box = new JCheckBox("P2");
		player3Box = new JCheckBox("P3");
		player4Box = new JCheckBox("P4");
		player5Box = new JCheckBox("P5");
		player6Box = new JCheckBox("P6");
		player7Box = new JCheckBox("P7");
		player8Box = new JCheckBox("P8");
		
		switchButton = new JButton("Switch");
		
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
		
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridBagLayout());
		
		generateButton = new JButton("Generate");
		//TODO: add anonymous ActionListener that will call a BattleGenerator class
		
		skipButton = new JButton("Skip");
		//TODO: add ActionListener
		
		loadButton = new JButton("Load");
		try {
			Image loadImage = ImageIO.read(getClass().getResource("/img/Open.png"));
			loadButton.setIcon(new ImageIcon(loadImage));
		} catch(IOException e) {
			//TODO: handle this error
		}
		//TODO: add action listener
		
		debugButton = new JButton("Debug");
		//TODO: add action listener
		
		numPlayersLabel = new JLabel("Number of players: ");
		SpinnerNumberModel model = new SpinnerNumberModel(2, 2, 8, 1);
		numPlayersSpinner = new JSpinner(model);
		numPlayersSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				numPlayers = (int) numPlayersSpinner.getValue();
			}
		});
		
		
	}

}
