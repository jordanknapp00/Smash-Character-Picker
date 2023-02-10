package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import util.Util;

/**
 * The <code>TierList</code> class holds all the data about the current
 * tier list loaded in the program. This includes the fighters in each tier,
 * players' exclusion and favorites list, and all the settings. Basically,
 * everything you'd find in a tier list file.
 * <br><br>
 * The <code>TierList</code> class can either be initialized with default
 * values, or a tier list file name can be passed into the constructor, and
 * the <code>TierList</code> will be initialized with the data in that file.
 * <br><br>
 * Additionally, the <code>TierList</code> is responsible for generating
 * battles. It will internally keep track of the "Cannot Get" system and
 * use the settings data it contains to generate a battle.
 * 
 * @author Jordan Knapp
 *
 */
public class TierList {
	
	private static final int NUM_TIERS = 24;
	
	//Tier list data, including a variety of ways to access the tier list
	//itself, either by tier or by name. Names are stored in a set to
	//ensure uniqueness. There is also a HashMap, which maps lowercase names
	//to the fighter object. this should make it easy to locate a fighter
	//by name.
	
	private List<List<Fighter>> tierList;
	private Set<String> fighterNames;
	private HashMap<String, Fighter> lowercaseNames;
	
	private List<Set<Fighter>> exclusionList;
	private List<Set<Fighter>> favoriteList;
	
	private Queue<Fighter> cannotGet;
	private List<Queue<Fighter>> individualCannotGet;
	
	/**
	 * Constructs an empty <code>TierList</code> with the following default
	 * parameters:
	 * <ul>
	 * 	<li>The tier list itself will be empty. The internal data structures
	 * 		will consist of a <code>List</code> containing <b><i><code>NUM_TIERS</code></b></i>
	 * 		empty <code>List</code>s of <code>Fighter</code>s, while the
	 * 		<code>fighterNames</code> set and <code>lowercaseNames</code>
	 * 		map will also be empty.</li>
	 *	<li>The exclusion and favorites lists will be initialized with empty
	 *		lists for all 8 players.</li>
	 *	<li>The global cannot get queue will be empty, while the individual
	 *		cannot get queue will be initialized to empty for all 8 players.</li>
	 * </ul>
	 */
	public TierList() {
		tierList = new ArrayList<List<Fighter>>();
		fighterNames = new HashSet<String>();
		lowercaseNames = new HashMap<String, Fighter>();
		
		exclusionList = new ArrayList<Set<Fighter>>();
		favoriteList = new ArrayList<Set<Fighter>>();
		
		cannotGet = new ArrayDeque<Fighter>();
		individualCannotGet = new ArrayList<Queue<Fighter>>();
		
		//initialize a tier array for every tier
		for(int at = 0; at < NUM_TIERS; at++) {
			tierList.add(new ArrayList<Fighter>());
		}
		
		//initialize an exclusion/favorite set for every player, and an
		//entry in the individual cannot get
		for(int at = 0; at < 8; at++) {
			exclusionList.add(new HashSet<Fighter>());
			favoriteList.add(new HashSet<Fighter>());
			
			individualCannotGet.add(new ArrayDeque<Fighter>());
		}
	}
	
	/**
	 * Construct a <code>TierList</code> using data from the given file.
	 * 
	 * @param file	The <code>File</code> from which to load data.
	 * @throws FileNotFoundException	Thrown if the file given does not exist.
	 * @throws IOException				Thrown if the data in the file is
	 * 									invalid in any way.
	 */
	public Settings loadFile(File file) throws FileNotFoundException, IOException {
		//settings variables that will be used to instantiate the Settings
		//object returned by this method, initialized with default values
		int numPlayers = 2;
		int[] tierChances = {10, 20, 25, 25, 20, 0, 0, 0};
		int[] bumpChances = {50, 25, 25};
		int cannotGetSize = 10;
		boolean allowSInCannotGet = false;
		boolean allowSSInCannotGet = true;
		
		//file structure is as follows:
		//
		//upper double s	0
		//mid double s		1
		//lower double s	2
		//upper s			3
		//mid s				4
		//lower s			5
		//upper a			6
		//mid a				7
		//lower a			8
		//upper b			9
		//mid b				10
		//lower b			11
		//upper c			12
		//mid c				13
		//lower c			14
		//upper d			15
		//mid d				16
		//lower d			17
		//upper e			18
		//mid e				19
		//lower e			20
		//upper f			21
		//mid f				22
		//lower f			23
		
		BufferedReader in = new BufferedReader(new FileReader(file));
		
		//read the first line and keep reading while lines exist
		String lineAt = in.readLine();
		while(lineAt != null) {
			//scroll through the chars in the read line. if one is an equals,
			//then check for which tier/setting it is
			String next = "";
			boolean foundEqual = false;
			for(char at: lineAt.toCharArray()) {
				next += at;
				
				if(at == '=') {
					foundEqual = true;
					break;
				}
			}
			
			//remove space before equals sign and check name
			next = next.substring(0, next.length() - 1);
			next = next.toLowerCase();
			int posInLine = next.length();
			
			switch(next) {
			case "upper double s":
				readTier(0, posInLine, lineAt);
				break;
			case "mid double s":
				readTier(1, posInLine, lineAt);
				break;
			case "lower double s":
				readTier(2, posInLine, lineAt);
				break;
			case "upper s":
				readTier(3, posInLine, lineAt);
				break;
			case "mid s":
				readTier(4, posInLine, lineAt);
				break;
			case "lower s":
				readTier(5, posInLine, lineAt);
				break;
			case "upper a":
				readTier(6, posInLine, lineAt);
				break;
			case "mid a":
				readTier(7, posInLine, lineAt);
				break;
			case "lower a":
				readTier(8, posInLine, lineAt);
				break;
			case "upper b":
				readTier(9, posInLine, lineAt);
				break;
			case "mid b":
				readTier(10, posInLine, lineAt);
				break;
			case "lower b":
				readTier(11, posInLine, lineAt);
				break;
			case "upper c":
				readTier(12, posInLine, lineAt);
				break;
			case "mid c":
				readTier(13, posInLine, lineAt);
				break;
			case "lower c":
				readTier(14, posInLine, lineAt);
				break;
			case "upper d":
				readTier(15, posInLine, lineAt);
				break;
			case "mid d":
				readTier(16, posInLine, lineAt);
				break;
			case "lower d":
				readTier(17, posInLine, lineAt);
				break;
			case "upper e":
				readTier(18, posInLine, lineAt);
				break;
			case "mid e":
				readTier(19, posInLine, lineAt);
				break;
			case "lower e":
				readTier(20, posInLine, lineAt);
				break;
			case "upper f":
				readTier(21, posInLine, lineAt);
				break;
			case "mid f":
				readTier(22, posInLine, lineAt);
				break;
			case "lower f":
				readTier(23, posInLine, lineAt);
				break;
			case "p1 exclude":
				readExclude(0, posInLine, lineAt);
				break;
			case "p2 exclude":
				readExclude(1, posInLine, lineAt);
				break;
			case "p3 exclude":
				readExclude(2, posInLine, lineAt);
				break;
			case "p4 exclude":
				readExclude(3, posInLine, lineAt);
				break;
			case "p5 exclude":
				readExclude(4, posInLine, lineAt);
				break;
			case "p6 exclude":
				readExclude(5, posInLine, lineAt);
				break;
			case "p7 exclude":
				readExclude(6, posInLine, lineAt);
				break;
			case "p8 exclude":
				readExclude(7, posInLine, lineAt);
				break;
			case "p1 favorite":
				readFavorite(0, posInLine, lineAt);
				break;
			case "p2 favorite":
				readFavorite(1, posInLine, lineAt);
				break;
			case "p3 favorite":
				readFavorite(2, posInLine, lineAt);
				break;
			case "p4 favorite":
				readFavorite(3, posInLine, lineAt);
				break;
			case "p5 favorite":
				readFavorite(4, posInLine, lineAt);
				break;
			case "p6 favorite":
				readFavorite(5, posInLine, lineAt);
				break;
			case "p7 favorite":
				readFavorite(6, posInLine, lineAt);
				break;
			case "p8 favorite":
				readFavorite(7, posInLine, lineAt);
				break;
			//TODO: instead of reading settings (which just calls a method that uses case), just handle each setting here
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
			case "bump chances":
				readSetting(6, at, lineAt);
				break;
			default:
				in.close();
				throw new IOException("Error on line " + next);
			}
			
			//TODO: not sure this is actually possible now
			//if any lines are found that aren't valid, stop reading file
			//and throw an error. unless the line is blank or it starts with
			//a #, which is a comment
			if(!foundEqual && !next.equals("") && !(next.charAt(0) == '#')) {
				in.close();
				throw new IOException("Invalid line: " + next);
			}
			
			lineAt = in.readLine();
		}
		
		in.close();
		
		return new Settings(numPlayers, tierChances, bumpChances,
				cannotGetSize, allowSInCannotGet, allowSSInCannotGet);
	}
	
	/**
	 * Reads the given line and creates <code>Fighter</code> objects to add
	 * to the specified tier in the tier list.
	 * 
	 * @param tier		The tier index being added to.
	 * @param startAt	The index at which data begins in <code>line</code>.
	 * 					The name of the tier (i.e. "Upper Double S = ") will
	 * 					be given before the relevant data, so this value
	 * 					allows us to skip that information.
	 * @param line		The full line read from the file.
	 */
	private void readTier(int tier, int startAt, String line) {
		//convers the comma-separated line into an array of strings. first
		//we get the substring from character index 2, to skip over the
		//equals and space. then we split on commas with optional spaces,
		//giving us an array
		String[] currentLine = line.substring(startAt + 2).split(",\\s*");
		
		//go through the fighters and add them. duplicates will be ignored
		//automatically by addFighter()
		for(String fighterAt: currentLine) {
			//skip blank ones
			if(fighterAt.equals("")) {
				continue;
			}
			
			Fighter newFighter = new Fighter(fighterAt, tier);
			addFighter(newFighter);
		}
	}
	
	/**
	 * Reads the given line and inserts the fighters into the specified
	 * player's exclusion list.
	 * 
	 * @param player		The player having their exclusion list set.
	 * @param startAt		The index at which data beings in <code>line</code>.
	 * 						See javadoc for <code>readTier()</code> for full
	 * 						explanation.
	 * @param line			The full line read from the file.
	 * @throws IOException	Thrown if a read fighter was not found in the
	 * 						tier list.
	 */
	private void readExclude(int player, int startAt, String line) throws IOException {
		String[] currentLine = line.substring(startAt + 2).split(",\\s*");
		
		for(String fighterAt: currentLine) {
			//skip blank ones
			if(fighterAt.equals("")) {
				continue;
			}
			
			Fighter toAdd = getFighter(fighterAt);
			
			if(toAdd == null) {
				throw new IOException("Fighter with name " + fighterAt +
						" was added to player " + (player + 1) + "'s " +
						"exclusion list, but no fighter with that name " +
						"was defined beforehand.");
			}
			
			exclusionList.get(player).add(toAdd);
		}
	}
	
	/**
	 * Reads the given line and inserts the fighters into the specified
	 * player's favorite list.
	 * 
	 * @param player		The player having their favorite list set.
	 * @param startAt		The index at which data beings in <code>line</code>.
	 * 						See javadoc for <code>readTier()</code> for full
	 * 						explanation.
	 * @param line			The full line read from the file.
	 * @throws IOException	Thrown if a read fighter was not found in the
	 * 						tier list.
	 */
	private void readFavorite(int player, int startAt, String line) throws IOException {
		String[] currentLine = line.substring(startAt + 2).split(",\\s*");
		
		for(String fighterAt: currentLine) {
			//skip blank ones
			if(fighterAt.equals("")) {
				continue;
			}
			
			Fighter toAdd = getFighter(fighterAt);
			
			if(toAdd == null) {
				throw new IOException("Fighter with name " + fighterAt +
						" was added to player " + (player + 1) + "'s " +
						"favorite list, but no fighter with that name " +
						"was defined beforehand.");
			}
			
			favoriteList.get(player).add(toAdd);
		}
	}
	
	/**
	 * Reads the specified setting id. Setting id's are as follows:<br><br>
	 * <ul>
	 * 	<li>1 = tier chances (comma-separated list)</li>
	 * 	<li>2 = cannot get size (integer between 0 and <code><b><i>CANNOT_GET_MAX</i></b></code>)</li>
	 * 	<li>3 = allow ss in cannot get (true or false, 1 or 0)</li>
	 * 	<li>4 = allow s in cannot get (true or false, 1 or 0)</li>
	 * 	<li>5 = number of players (integer between 2 and 8)</li>
	 * 	<li>6 = bump chances (comma-separated list)</li>
	 * </ul>
	 * @param id			The setting id being read.
	 * @param startAt		The index at which data beings in <code>line</code>.
	 * 						See javadoc for <code>readTier()</code> for full
	 * 						explanation.
	 * @param line			The full line read from the file.
	 * @throws IOException	Thrown if data is invalid in any way.
	 */
	private void readSetting(int id, int startAt, String line) throws IOException {	
		String toRead = line.substring(startAt + 2).toLowerCase();
		String[] currentLine = toRead.split(",\\s*");
		String errMessage;
		
		switch(id) {
		case 2:
			errMessage = toRead + " is not a valid value for \"Cannot Get Size\" " +
					"setting. Please provide an integer between 0 and " +
					CANNOT_GET_MAX + ".";
			
			try {
				int newCannotGetSize = Integer.parseInt(toRead);
				
				if(newCannotGetSize >= 0 && newCannotGetSize <= CANNOT_GET_MAX) {
					cannotGetSize = newCannotGetSize;
				}
				else {
					throw new IOException(errMessage);
				}
			} catch(NumberFormatException e) {
				throw new IOException(errMessage);
			}
			
			break;
		case 3:
		case 4:
			String tier;
			if(id == 3) {
				tier = "SS";
			}
			else {
				tier = "S";
			}
			
			errMessage = toRead + " is not a valid value for \" " + tier +
					" Allowed in Cannot Get\" setting. Please use \"true\" " +
					"or \"false\", or 0 or 1.";
			
			try {
				boolean newAllowedInCannotGet;
				
				if(toRead.equals("true") || Integer.parseInt(toRead) == 1) {
					newAllowedInCannotGet = true;
				}
				else if(toRead.equals("false") || Integer.parseInt(toRead) == 0) {
					newAllowedInCannotGet = false;
				}
				else {
					throw new IOException(errMessage);
				}
				
				if(id == 3) {
					allowSSInCannotGet = newAllowedInCannotGet;
				}
				else {
					allowSInCannotGet = newAllowedInCannotGet;
				}
			} catch(NumberFormatException e) {
				throw new IOException(errMessage);
			}
			
			break;
		case 5:
			errMessage = toRead + " is not a valid value for \"Number of " +
					"Players\" setting. Please provide a number between " +
					"2 and 8.";
			
			try {
				int newNumPlayers = Integer.parseInt(toRead);
				
				if(newNumPlayers >= 2 && newNumPlayers <= 8) {
					numPlayers = newNumPlayers;
				}
				else {
					throw new IOException(errMessage);
				}
			} catch(NumberFormatException e) {
				throw new IOException(errMessage);
			}
			
			break;
		case 1:
			if(currentLine.length != 8) {
				throw new IOException("Error processing custom tier chances. " +
						"Please provide exactly 8 values, comma-separated.");
			}
			
			int[] newTierChances = new int[8];
			int sum = 0;
			
			try {
				for(int at = 0; at < 8; at++) {
					newTierChances[at] = Integer.parseInt(currentLine[at]);
					sum += newTierChances[at];
				}
			} catch(NumberFormatException e) {
				throw new IOException("Error processing custom tier chances. " +
						"One of the values is not a number.");
			}
			
			if(sum != 100) {
				throw new IOException("Error processing custom tier chances. " +
						"The values do not add up to 100.");
			}
			
			for(int at = 0; at < 8; at++) {
				tierChances[at] = newTierChances[at];
			}
			
			break;
		case 6:
			if(currentLine.length != 3) {
				throw new IOException("Error processing custom bump chances. " +
						"Please provide exactly 3 values, comma-separated.");
			}
			
			int[] newBumpChances = new int[3];
			sum = 0;
			
			try {
				for(int at = 0; at < 3; at++) {
					newBumpChances[at] = Integer.parseInt(currentLine[at]);
					sum += newBumpChances[at];
				}
			} catch(NumberFormatException e) {
				throw new IOException("Error processing custom bump chances. " +
						"One of the values is not a number.");
			}
			
			if(sum != 100) {
				throw new IOException("Error processing custom bump chances. " +
						"The values do not add up to 100.");
			}
			
			for(int at = 0; at < 3; at++) {
				bumpChances[at] = newBumpChances[at];
			}
		}
	}
	
	/**
	 * Adds the specified <code>Fighter</code> to the tier list, if a fighter
	 * with the same name doesn't already exist in the tier list.
	 * 
	 * @param toAdd	The <code>Fighter</code> to add.
	 * @return		<code>true</code> if the fighter is added successfully,
	 * 				<code>false</code> if it is not (i.e. a fighter with the
	 * 				same name is already in the tier list).
	 */
	public boolean addFighter(Fighter toAdd) {
		if(contains(toAdd)) {
			return false;
		}
		
		tierList.get(toAdd.getTier()).add(toAdd);
		fighterNames.add(toAdd.getName());
		lowercaseNames.put(toAdd.getName().toLowerCase(), toAdd);
		
		return true;
	}
	
	/**
	 * @return	The number of fighters in the tier list.
	 */
	public int numFighters() {
		return fighterNames.size();
	}
	
	/**
	 * Determines if the tier list contains a particular <code>Fighter</code>.
	 * 
	 * @param toCheck	The <code>Fighter</code> to check.
	 * @return			<code>true</code> if the tier list contains that
	 * 					fighter, <code>false</code> if it does not.
	 */
	public boolean contains(Fighter toCheck) {
		return contains(toCheck.getName());
	}
	
	/**
	 * Determines if the tier list contains a fighter with a particular name.
	 * 
	 * @param nameToCheck	The name of a fighter to check.
	 * @return				<code>true</code> if the tier list has a
	 * 						fighter with that name, <code>false</code> if it
	 * 						does not.
	 */
	public boolean contains(String nameToCheck) {
		return fighterNames.contains(nameToCheck);
	}
	
	/**
	 * Determines the tier of the given <code>Fighter</code>.
	 * 
	 * @param toCheck	The <code>Fighter</code> to check.
	 * @return			The tier of the given fighter (from 0 to
	 * 					<code><b><i>NUM_TIERS</b></i> - 1</code>), or -1 if
	 * 					they are not present in the tier list.
	 */
	public int tierOf(Fighter toCheck) {
		return tierOf(toCheck.getName());
	}
	
	/**
	 * Determines which tier the fighter with the given name is in.
	 * 
	 * @param nameToCheck	The name of the fighter to check.
	 * @return				The tier of the given fighter (from 0 to
	 * 						<code><b><i>NUM_TIERS</b></i> - 1</code>), or -1
	 * 						if they are not present in the tier list.
	 */
	public int tierOf(String nameToCheck) {
		if(!contains(nameToCheck)) {
			return -1;
		}
		
		for(int tierAt = 0; tierAt < NUM_TIERS; tierAt++) {
			for(Fighter fighterAt: tierList.get(tierAt)) {
				if(fighterAt.getName().equals(nameToCheck)) {
					return tierAt;
				}
			}
		}
		
		return -1;
	}
	
	/**
	 * Returns the <code>Fighter</code> object with the given name.
	 * 
	 * @param name	The name of the fighter to return.
	 * @return		The <code>Fighter</code> object with the given name, or
	 * 				<code>null</code> if no fighter with that name is in the
	 * 				tier list.
	 */
	public Fighter getFighter(String name) {
		return lowercaseNames.get(name.toLowerCase());
	}
	
	@Override
	public String toString() {
		//allocate 1000 characters (will probably end up needing more, but
		//we'll stave off reallocating for a while)
		StringBuffer retString = new StringBuffer(1000);
		
		for(int at = 0; at < NUM_TIERS; at++) {
			String tierAt = Util.tierToString(at);
			
			if(at == 0 || at == 2) {
				retString.append(tierAt + ":\t" + tierList.get(at) + "\n");
			}
			else {
				retString.append(tierAt + ":\t\t" + tierList.get(at) + "\n");
			}
		}
		
		for(int at = 0; at < 8; at++) {
			retString.append("Player " + (at + 1) + " exclude:\t" + exclusionList.get(at) + "\n");
		}
		
		for(int at = 0; at < 8; at++) {
			retString.append("Player " + (at + 1) + " favorite:\t" + favoriteList.get(at) + "\n");
		}
		
		return retString.toString();
	}
	
	/**
	 * @return	The current max size of the "Cannot Get" queue.
	 */
	public int getCannotGetSize() {
		return cannotGetSize;
	}
	
	/**
	 * Sets the max size of the "Cannot Get" queue to the given value, as
	 * long as it's between 0 and <code><i><b>CANNOT_GET_MAX</i></b></code>.
	 * 
	 * @param newCannotGetSize	The new max size of the "Cannot Get" queue.
	 * @return					<code>true</code> if the operation was
	 * 							successful, <Code>false</code> if not.
	 */
	public boolean setCannotGetSize(int newCannotGetSize) {
		if(newCannotGetSize >= 0 && newCannotGetSize <= CANNOT_GET_MAX) {
			cannotGetSize = newCannotGetSize;
			return true;
		}
		
		return false;
	}
	
	/**
	 * @return	The current number of players.
	 */
	public int getNumPlayers() {
		return numPlayers;
	}
	
	/**
	 * Set the number of players to a given value.
	 * 
	 * @param newNumPlayers	The new number of players, between 2 and 8.
	 * @return				<code>true</code> if the operation was successful,
	 * 						<code>false</code> if it was not.
	 */
	public boolean setNumPlayers(int newNumPlayers) {
		if(newNumPlayers < 2 || newNumPlayers > 8) {
			return false;
		}
		
		numPlayers = newNumPlayers;
		return true;
	}
	
	/**
	 * @return	<code>true</code> if S tiers are allowed in the "Cannot Get"
	 * 			queue, <code>false</code> if they are not.
	 */
	public boolean getAllowSInCannotGet() {
		return allowSInCannotGet;
	}
	
	/**
	 * Updates the status of whether S tiers are allowed in the "Cannot Get"
	 * queue.
	 * 
	 * @param newAllowSInCannotGet	Whether or not S tiers are allowed in
	 * 								the "Cannot Get" queue.
	 */
	public void setAllowSInCannotGet(boolean newAllowSInCannotGet) {
		allowSInCannotGet = newAllowSInCannotGet;
	}
	
	/**
	 * @return	<code>true</code> if SS tiers are allowed in the "Cannot Get"
	 * 			queue, <code>false</code> if they are not.
	 */
	public boolean getAllowSSInCannotGet() {
		return allowSSInCannotGet;
	}
	
	/**
	 * Updates the status of whether SS tiers are allowed in the "Cannot Get"
	 * queue.
	 * 
	 * @param newAllowSSInCannotGet	Whether or not S tiers are allowed in the
	 * 								"Cannot Get" queue.
	 */
	public void setAllowSSInCannotGet(boolean newAllowSSInCannotGet) {
		allowSSInCannotGet = newAllowSSInCannotGet;
	}
	
	/**
	 * Get the percent chance of getting the given tier.
	 * 
	 * @param tier	The tier (from 0 to 8) to get the chance of.
	 * @return		The chance (from 0 to 100) of getting that tier, or -1
	 * 				if a tier outside the above ranges is given.
	 */
	public int getTierChance(int tier) {
		if(tier < 0 || tier >= 8) {
			return -1;
		}
		
		return tierChances[tier];
	}
	
	/**
	 * Get the percent chance of bumping the given number of tiers.
	 * 
	 * @param bumpAmount	The number of tiers to bump (0 to 2).
	 * @return				The chance (from 0 to 100) of bumping that amount,
	 * 						or -1 if the value given is outside the above range.
	 */
	public int getBumpChance(int bumpAmount) {
		if(bumpAmount < 0 || bumpAmount > 2) {
			return -1;
		}
		
		return bumpChances[bumpAmount];
	}
	
	/**
	 * Updates the tier and bump chances to new values, as long as those new
	 * values are valid. The sum of the tier and bump chances must each add
	 * up to 100.
	 * 
	 * @param newTierChances	An array consisting of 8 numbers, representing
	 * 							the percent chance of getting tiers from SS
	 * 							to FF. The sum of its contents should be 100.
	 * @param newBumpChances	An array consisting of 3 numbers, representing
	 * 							the percent chance of bumping 0, 1, or 2
	 * 							tiers upward. The sum of its content should
	 * 							be 100.
	 * @return					<code>true</code> if the new tier and bump
	 * 							chances are both valid and have been applied,
	 * 							<code>false</code> otherwise.
	 */
	public boolean updateTierBumpChances(int[] newTierChances, int[] newBumpChances) {
		//prevent index out of bounds errors, make sure we always have
		//exactly the sizes we're expecting
		if(newTierChances.length != 8 || newBumpChances.length != 3) {
			return false;
		}
		
		int tierSum = 0;
		for(int at = 0; at < 8; at++) {
			tierSum += newTierChances[at];
		}
		
		int bumpSum = 0;
		for(int at = 0; at < 3; at++) {
			bumpSum += newBumpChances[at];
		}
		
		//if both are correct, update the actual values and return true
		if(tierSum == 100 && bumpSum == 100) {
			for(int at = 0; at < 8; at++) {
				tierChances[at] = newTierChances[at];
			}
			
			for(int at = 0; at < 3; at++) {
				bumpChances[at] = newBumpChances[at];
			}
			
			return true;
		}
		//otherwise, return false
		else {
			return false;
		}
	}
}
