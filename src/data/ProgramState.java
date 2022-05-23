package data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import ui.MainWindow;
import util.Util;

/**
 * The <code>ProgramState</code> is essentially an enormous data structure
 * responsible for keeping track of various pieces of data relating to the state
 * of the program. In particular, any data that multiple classes may require
 * access to is being stored in this class.
 * 
 * All of the fields for this class are public; I feel there's no reason to
 * create getters and setters for all these variables when being able to access
 * them directly would make things easier.
 * 
 * @author Jordan Knapp
 */
public class ProgramState {
	
	public boolean fileLoaded;
	public ArrayList<ArrayList<String>> linesOfFile;
	
	public int numBattles;
	public int numPlayers;
	public CannotGetQueue cannotGet;
	public CannotGetQueue[] individualCannotGet;
	public ArrayList<String> gotten;
	
	//settings variables
	public int[] tierChances;
	public int[] newTierChances;
	public boolean allowSInCannotGetBuffer;
	public boolean allowSSInCannotGetBuffer;
	public int cannotGetSize;
	public int bumpChances[];
	public int newBumpChances[];
	
	public boolean needToSaveStats;
	public File statsFile;
	public HashMap<String, double[]> stats;
	
	public boolean skipping;
	
	//ui booleans to prevent opening the same window when it's already open
	public boolean openedDebug;
	public boolean openedLookup;
	public boolean openedModify;
	public boolean openedAdvancedSettings;
	
	private MainWindow parent;
	
	/**
	 * Initialize the state of the program. Values are set to these defaults:
	 * <ul>
	 * 	<li><code>fileLoaded</code> = <code>false</code></li>
	 * 	<li><code>linesOfFile</code> will be an <code>ArrayList</code> with
	 * 		39 empty <code>ArrayList</code>s inside it.
	 * 	<li><code>numBattles</code> = 0</li>
	 * 	<li><code>numPlayers</code> = 2</li>
	 * 	<li>Tier Chances:
	 * 		<ul>
	 * 			<li>SS: 10%</li>
	 * 			<li>S:	20%</li>
	 * 			<li>A:	25%</li>
	 * 			<li>B:	25%</li>
	 * 			<li>C:	20%</li>
	 * 			<li>D:	 0%</li>
	 * 			<li>E:	 0%</li>
	 * 			<li>F:	 0%</li>
	 * 		</ul></li>
	 * 	<li>Bump chances:
	 * 		<ul>
	 * 			<li>Up 2: 25%</li>
	 * 			<li>Up 1: 25%</li>
	 * 			<li>Up 0: 50%</li>
	 * 		</ul></li>
	 * 	<li><code>cannotGetSize</code> = 5</li>
	 * 	<li><code>allowSInCannotGet</code> = <code>false</code></li>
	 * 	<li><code>allowSSInCannotGet</code> = <code>false</code></li>
	 * 	<li><code>needToSaveStats</code> = <code>false</code></li>
	 * 	<li><code>statsFile</code> is not initialized.</li>
	 * 	<li><code>stats</code> is initialized to an empty <code>HashMap</code></li>
	 * 	<li><code>skipping</code> = <code>false</code></li>
	 * 	<li><code>openedDebug</code> = <code>false</code></li>
	 * 	<li><code>openedLookup</code> = <code>false</code></li>
	 * 	<li><code>openedModify</code> = <code>false</code></li>
	 * </ul>
	 */
	public ProgramState(MainWindow parent) {
		fileLoaded = false;
		linesOfFile = new ArrayList<ArrayList<String>>();
		for(int at = 0; at < 39; at++) {
			linesOfFile.add(new ArrayList<String>());
		}
		
		numBattles = 0;
		numPlayers = 2;
		
		cannotGet = new CannotGetQueue();
		
		individualCannotGet = new CannotGetQueue[8];
		for(int at = 0; at < 8; at++) {
			individualCannotGet[at] = new CannotGetQueue();
		}
		
		gotten = new ArrayList<String>();
		
		tierChances = new int[8];
		tierChances[0] = 10;
		tierChances[1] = 20;
		tierChances[2] = 25;
		tierChances[3] = 25;
		tierChances[4] = 20;
		tierChances[5] = 0;
		tierChances[6] = 0;
		tierChances[7] = 0;
		
		newTierChances = new int[8];
		newTierChances[0] = 10;
		newTierChances[1] = 20;
		newTierChances[2] = 25;
		newTierChances[3] = 25;
		newTierChances[4] = 20;
		newTierChances[5] = 0;
		newTierChances[6] = 0;
		newTierChances[7] = 0;
		
		bumpChances = new int[3];
		bumpChances[0] = 50;
		bumpChances[1] = 25;
		bumpChances[2] = 25;
		
		newBumpChances = new int[3];
		newBumpChances[0] = 50;
		newBumpChances[1] = 25;
		newBumpChances[2] = 25;
		
		cannotGetSize = 5;
		
		allowSInCannotGetBuffer = false;
		allowSSInCannotGetBuffer = false;
		
		needToSaveStats = false;
		stats = new HashMap<String, double[]>();
		
		skipping = false;
		
		openedDebug = false;
		openedLookup = false;
		openedModify = false;
		
		this.parent = parent;
	}
	
	/**
	 * This method confirms that the values stored in the
	 * <code>newTierChances</code> instance variable are valid, and then
	 * transfers those values into the <code>tierChances</code> instance
	 * variable, thus setting the new chances for each tier.
	 * 
	 * @return	<code>true</code> if the chances are valid, <code>false</code>
	 * 			if they are not.
	 */
	public boolean applyTierChances() {
		int totalTier = 0;
		for(int at = 0; at < 8; at++) {
			totalTier += newTierChances[at];
		}
		
		int totalBump = 0;
		for(int at = 0; at < 3; at++) {
			totalBump += newBumpChances[at];
		}
		
		if(totalTier == 100 && totalBump == 100) {
			for(int at = 0; at < 8; at++) {
				tierChances[at] = newTierChances[at];
			}
			
			for(int at = 0; at < 3; at++) {
				bumpChances[at] = newBumpChances[at];
			}
			
			JOptionPane.showMessageDialog(null, "Custom chances"
					+ " are valid and have been applied.", "Smash "
					+ "Character Picker",
					JOptionPane.INFORMATION_MESSAGE);
			
			Util.log("You've updated the tier chances:");
			Util.log("SS Tier: " + tierChances[0] + "%");
			Util.log("S Tier: " + tierChances[1] + "%");
			Util.log("A Tier: " + tierChances[2] + "%");
			Util.log("B Tier: " + tierChances[3] + "%");
			Util.log("C Tier: " + tierChances[4] + "%");
			Util.log("D Tier: " + tierChances[5] + "%");
			Util.log("E Tier: " + tierChances[6] + "%");
			Util.log("F Tier: " + tierChances[7] + "%");
			Util.log("Bump 0: " + bumpChances[0] + "%");
			Util.log("Bump 1: " + bumpChances[1] + "%");
			Util.log("Bump 2: " + bumpChances[2] + "%");
			
			parent.updateTierChances();
			
			return true;
		}
		else if(totalTier == 100) {
			JOptionPane.showMessageDialog(null, "Your custom "
					+ "bump chances are invalid. They must add up to 100.\n"
					+ "They currently add up to " + totalBump + ".",
					"Smash Character Picker",
					JOptionPane.ERROR_MESSAGE);
			
			for(int at = 0; at < 3; at++) {
				newBumpChances[at] = bumpChances[at];
			}
			parent.updateTierChances();
			
			return false;
		}
		else if(totalBump == 100) {
			JOptionPane.showMessageDialog(null, "Your custom "
					+ "chances are invalid. They must add up to 100.\n"
					+ "They currently add up to " + totalTier + ".",
					"Smash Character Picker",
					JOptionPane.ERROR_MESSAGE);
			
			for(int at = 0; at < 8; at++) {
				newTierChances[at] = tierChances[at];
			}
			parent.updateTierChances();
			
			return false;
		}
		else {
			JOptionPane.showMessageDialog(null, "Your custom tier and bump "
					+ "chances are invalid. They must add up to 100.\n"
					+ "The tier chances add up to " + totalTier + ", and the "
					+ "bump chances add up to " + totalBump + ".",
					"Smash Character Picker",
					JOptionPane.ERROR_MESSAGE);
			
			for(int at = 0; at < 8; at++) {
				newTierChances[at] = tierChances[at];
			}
			for(int at = 0; at < 3; at++) {
				newBumpChances[at] = bumpChances[at];
			}
			parent.updateTierChances();
			
			return false;
		}
	}

}
