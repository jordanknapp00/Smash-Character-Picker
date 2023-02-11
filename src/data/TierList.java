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
			//first things first, if the line is blank or it starts with '#',
			//skip it
			if(lineAt.equals("") || lineAt.charAt(0) == '#') {
				lineAt = in.readLine();
				continue;
			}
			
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
			
			//if we didn't find an equals sign at this point, the line is invalid
			if(!foundEqual) {
				in.close();
				
				//TODO: look into custom exception types.
				//instead of an IOException for everything, maybe have a
				//TierListParseException interface, with subclasses that
				//make it easier to understand exactly what went wrong.
				//then i don't have to spend so much time with error messages
				//and stuff, either. a CannotGetSizeException only needs to
				//be given the value that is invalid
				
				throw new IOException("Invalid line: " + next);
			}
			
			//remove space before equals sign and make sure it's all lowercase
			next = next.substring(0, next.length() - 2);
			next = next.toLowerCase();
			int posInLine = next.length() + 1;
			
			//if we're parsing exclusion lists, get the second character of
			//the string, that's going to be the player number
			if(next.contains("exclude")) {
				readExclude(Character.getNumericValue(next.charAt(1)) - 1, posInLine, lineAt);
			}
			//same thing for favorite lists
			else if(next.contains("favorite")) {
				readFavorite(Character.getNumericValue(next.charAt(1)), posInLine, lineAt);
			}
			//if this is a valid tier, process it
			else if(Util.stringToTier(next) != -1) {
				readTier(Util.stringToTier(next), posInLine, lineAt);
			}
			//otherwise, we can assume that we're reading settings. we want
			//to do all that in this method, because we have these settings
			//variables and we want to return a Settings object
			else {
				//some basic setup used for all settings
				String toRead = lineAt.substring(posInLine + 2).toLowerCase();
				String[] currentLine = toRead.split(",\\s*");
				String errMessage;
				
				//check for each setting. basically, for each settings, we're
				//setting up an error message to be thrown in case the input
				//is invalid, then we parse the given value, make sure it's
				//valid, and then we're good to go
				if(next.equals("cannot get size")) {
					errMessage = toRead + " is not a valid value for " +
							"\"Cannot Get Size\" setting. Please provide " +
							"an integer between 0 and " + Util.CANNOT_GET_MAX + ".";
					
					try {
						cannotGetSize = Integer.parseInt(toRead);
					} catch(NumberFormatException e) {
						in.close();
						throw new IOException(errMessage);
					}
					
					if(cannotGetSize < 0 || cannotGetSize > Util.CANNOT_GET_MAX) {
						in.close();
						throw new IOException(errMessage);
					}
				}
				else if(next.equals("allow ss in cannot get")) {
					errMessage = toRead + " is not a valid value for " +
							"\"SS allowed in Cannot Get\" setting. Please " +
							"use \"true\"/\"false\" or 0\1.";
					
					try {
						if(toRead.equals("true") || Integer.parseInt(toRead) == 1) {
							allowSSInCannotGet = true;
						}
						else if(toRead.equals("false") || Integer.parseInt(toRead) == 0) {
							allowSSInCannotGet = false;
						}
						else {
							in.close();
							throw new IOException(errMessage);
						}
					} catch(NumberFormatException e) {
						in.close();
						throw new IOException(errMessage);
					}
				}
				else if(next.equals("allow s in cannot get")) {
					errMessage = toRead + " is not a valid value for " +
							"\"S allowed in Cannot Get\" setting. Please " +
							"use \"true\"/\"false\" or 0\1.";
					
					try {
						if(toRead.equals("true") || Integer.parseInt(toRead) == 1) {
							allowSInCannotGet = true;
						}
						else if(toRead.equals("false") || Integer.parseInt(toRead) == 0) {
							allowSInCannotGet = false;
						}
						else {
							in.close();
							throw new IOException(errMessage);
						}
					} catch(NumberFormatException e) {
						in.close();
						throw new IOException(errMessage);
					}
				}
				else if(next.equals("players")) {
					errMessage = toRead + " is not a valid value for " +
							"\"Number of Players\" setting. Please provide " +
							"an integer between 2 and 8.";
					
					try {
						numPlayers = Integer.parseInt(toRead);
					} catch(NumberFormatException e) {
						in.close();
						throw new IOException(errMessage);
					}
					
					if(numPlayers < 2 || numPlayers > 8) {
						in.close();
						throw new IOException(errMessage);
					}
				}
				else if(next.equals("tier chances")) {
					errMessage = "Custom tier chances are not valid.";
					
					if(currentLine.length != 8) {
						in.close();
						throw new IOException(errMessage + " Please provide " +
								"exactly 8 values, comma-separated");
					}
					
					int sum = 0;
					
					try {
						for(int at = 0; at < 8; at++) {
							tierChances[at] = Integer.parseInt(currentLine[at]);
							sum += tierChances[at];
						}
					} catch(NumberFormatException e) {
						in.close();
						throw new IOException(errMessage + " One of the " +
								"values is not a number.");
					}
					
					if(sum != 100) {
						in.close();
						throw new IOException(errMessage + " The values " +
								"must add up to 100.");
					}
				}
				else if(next.equals("bump chances")) {
					errMessage = "Custom bump chances are not valid.";
					
					if(currentLine.length != 3) {
						in.close();
						throw new IOException(errMessage + " Please provide " +
								"exactly 3 values, comma-separated");
					}
					
					int sum = 0;
					
					try {
						for(int at = 0; at < 3; at++) {
							bumpChances[at] = Integer.parseInt(currentLine[at]);
							sum += bumpChances[at];
						}
					} catch(NumberFormatException e) {
						in.close();
						throw new IOException(errMessage + " One of the " +
								"values is not a number.");
					}
					
					if(sum != 100) {
						in.close();
						throw new IOException(errMessage + " The values " +
								"must add up to 100.");
					}
				}
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
	
	public Matchup generateBattle(Settings settings) {
		//first, generate the list of valid fighters for each player
		ArrayList<List<Fighter>> playerValidCharacters = new ArrayList<List<Fighter>>();
		for(int playerAt = 0; playerAt < settings.getNumPlayers(); playerAt++) {
			playerValidCharacters.add(getValidCharacters(playerAt));
			
			if(playerValidCharacters.get(playerAt).size() == 0) {
				//TODO: custom exception for this
				return null;
			}
		}
	}
	
	private List<Fighter> getValidCharacters(int player) {
		return null;
	}
}
