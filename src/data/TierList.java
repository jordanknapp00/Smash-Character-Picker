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
	
	//Settings stuff
	
	int numPlayers;
	
	private int[] tierChances;
	private int[] bumpChances;
	
	private boolean allowSInCannotGet;
	private boolean allowSSInCannotGet;
	
	//Cannot Get stuff
	
	private int cannotGetSize;
	
	private Queue<Fighter> cannotGet;
	private List<Queue<Fighter>> individualCannotGet;
	
	/**
	 * Constructs an empty <code>TierList</code> with the following default
	 * settings:
	 * <br><br>
	 * <ul>
	 * 	<li>The tier list itself will be empty. The internal data structures
	 * 		will consist of a <code>List</code> containing <b><i><code>NUM_TIERS</code></b></i>
	 * 		empty <code>List</code>s of <code>Fighter</code>s, while the
	 * 		<code>fighterNames</code> set and <code>lowercaseNames</code>
	 * 		map will also be empty.</li>
	 * 	<li>The number of players will be 2.</li>
	 *	<li>The exclusion and favorites lists will be initialized with empty
	 *		lists for all 8 players.</li>
	 *	<li>The global cannot get queue will be empty, while the individual
	 *		cannot get queue will be initialized to empty for all 8 players.</li>
	 *	<li>The tier chances will be the following:
	 *		<ul>
	 *			<li>SS Tier: 10%</li>
	 *			<li>S Tier:  20%</li>
	 *			<li>A Tier:  25%</li>
	 *			<li>B Tier:  25%</li>
	 *			<li>C Tier:  20%</li>
	 *			<li>D Tier:   0%</li>
	 *			<li>E Tier:   0%</li>
	 *			<li>F Tier:   0%</li>
	 *		</ul></li>
	 *	<li>The bump chances will be the following:
	 *		<ul>
	 *			<li>Stay the same tier: 50%</li>
	 *			<li>Bump up 1 tier:     25%</li>
	 *			<li>Bump up 2 tiers:    25%</li>
	 *		</ul></li>
	 *	<li>Both S and SS tiers will be allowed in the cannot get, and the
	 *		cannot get size will be set to 10.</li>
	 * </ul>
	 */
	public TierList() {
		tierList = new ArrayList<List<Fighter>>();
		fighterNames = new HashSet<String>();
		lowercaseNames = new HashMap<String, Fighter>();
		
		exclusionList = new ArrayList<Set<Fighter>>();
		favoriteList = new ArrayList<Set<Fighter>>();
		
		numPlayers = 2;
		
		cannotGetSize = 10;
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
		
		tierChances = new int[] {10, 20, 25, 25, 20, 0, 0, 0};
		bumpChances = new int[] {50, 25, 25};
		
		allowSInCannotGet = true;
		allowSSInCannotGet = true;
	}
	
	/**
	 * Construct a <code>TierList</code> using data from the given file.
	 * 
	 * @param file	The <code>File</code> from which to load data.
	 * @throws FileNotFoundException	Thrown if the file given does not exist.
	 * @throws IOException				Thrown if the data in the file is
	 * 									invalid in any way.
	 */
	public TierList(File file) throws FileNotFoundException, IOException {
		//start by initializing everything to default values. this way we
		//can just override stuff that's in the file
		this();
		
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
			for(int at = 0; at < lineAt.length(); at++) {
				if(lineAt.charAt(at) != '=') {
					next += lineAt.charAt(at);
				}
				else {
					//remove space before equals sign and check name
					foundEqual = true;
					next = next.substring(0, next.length() - 1);
					next = next.toLowerCase();
					
					switch(next) {
					case "upper double s":
						readTier(0, at, lineAt);
						break;
					case "mid double s":
						readTier(1, at, lineAt);
						break;
					case "lower double s":
						readTier(2, at, lineAt);
						break;
					case "upper s":
						readTier(3, at, lineAt);
						break;
					case "mid s":
						readTier(4, at, lineAt);
						break;
					case "lower s":
						readTier(5, at, lineAt);
						break;
					case "upper a":
						readTier(6, at, lineAt);
						break;
					case "mid a":
						readTier(7, at, lineAt);
						break;
					case "lower a":
						readTier(8, at, lineAt);
						break;
					case "upper b":
						readTier(9, at, lineAt);
						break;
					case "mid b":
						readTier(10, at, lineAt);
						break;
					case "lower b":
						readTier(11, at, lineAt);
						break;
					case "upper c":
						readTier(12, at, lineAt);
						break;
					case "mid c":
						readTier(13, at, lineAt);
						break;
					case "lower c":
						readTier(14, at, lineAt);
						break;
					case "upper d":
						readTier(15, at, lineAt);
						break;
					case "mid d":
						readTier(16, at, lineAt);
						break;
					case "lower d":
						readTier(17, at, lineAt);
						break;
					case "upper e":
						readTier(18, at, lineAt);
						break;
					case "mid e":
						readTier(19, at, lineAt);
						break;
					case "lower e":
						readTier(20, at, lineAt);
						break;
					case "upper f":
						readTier(21, at, lineAt);
						break;
					case "mid f":
						readTier(22, at, lineAt);
						break;
					case "lower f":
						readTier(23, at, lineAt);
						break;
					case "p1 exclude":
						readExclude(0, at, lineAt);
						break;
					case "p2 exclude":
						readExclude(1, at, lineAt);
						break;
					case "p3 exclude":
						readExclude(2, at, lineAt);
						break;
					case "p4 exclude":
						readExclude(3, at, lineAt);
						break;
					case "p5 exclude":
						readExclude(4, at, lineAt);
						break;
					case "p6 exclude":
						readExclude(5, at, lineAt);
						break;
					case "p7 exclude":
						readExclude(360, at, lineAt);
						break;
					case "p8 exclude":
						readExclude(7, at, lineAt);
						break;
					case "p1 favorite":
						readFavorite(0, at, lineAt);
						break;
					case "p2 favorite":
						readFavorite(1, at, lineAt);
						break;
					case "p3 favorite":
						readFavorite(2, at, lineAt);
						break;
					case "p4 favorite":
						readFavorite(3, at, lineAt);
						break;
					case "p5 favorite":
						readFavorite(4, at, lineAt);
						break;
					case "p6 favorite":
						readFavorite(5, at, lineAt);
						break;
					case "p7 favorite":
						readFavorite(6, at, lineAt);
						break;
					case "p8 favorite":
						readFavorite(7, at, lineAt);
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
					case "bump chances":
						readSetting(6, at, lineAt);
						break;
					default:
						in.close();
						throw new IOException("Error on line " + next);
					}
				}
			}
			
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
		//equals and space. then we split on commas, giving us an array
		String[] currentLine = line.substring(startAt + 2).split(",");
		
		//go through the fighters and add them. duplicates will be ignored
		//automatically by addFighter()
		for(String fighterAt: currentLine) {
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
		String[] currentLine = line.substring(startAt + 2).split(",");
		
		for(String fighterAt: currentLine) {
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
		String[] currentLine = line.substring(startAt + 2).split(",");
		
		for(String fighterAt: currentLine) {
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
	 * 	<li>2 = cannot get size (integer between 0 and 15)</li> //TODO: consider increasing?
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
		String[] currentLine = toRead.split(",");
		String errMessage;
		
		switch(id) {
		case 2:
			errMessage = toRead + " is not a valid value for \"Cannot Get Size\" " +
					"setting. Please provide an integer between 0 and 15.";
			
			try {
				int newCannotGetSize = Integer.parseInt(toRead);
				
				if(newCannotGetSize >= 0 && newCannotGetSize <= 15) {
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
		StringBuffer retString = new StringBuffer(500);
		
		for(int at = 0; at < NUM_TIERS; at++) {
			String tierAt = Util.tierToString(at);
			
			if(at == 0 || at == 2) {
				retString.append(tierAt + ":\t" + tierList.get(at));
			}
			else {
				retString.append(tierAt + ":\t\t" + tierList.get(at));
			}
		}
		
		return retString.toString();
	}
}
