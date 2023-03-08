package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import exception.BooleanSettingParseException;
import exception.BooleanSettingParseException.BooleanSetting;
import exception.IntegerSettingParseException;
import exception.IntegerSettingParseException.IntegerSetting;
import exception.ListSettingParseException;
import exception.ListSettingParseException.ListSetting;
import exception.NoValidFightersException;
import exception.TierListParseException;
import util.Util;

/**
 * The <code>TierList</code> class holds all the data about the current
 * tier list loaded in the program. This includes the fighters in each tier,
 * as well as players' exclusion and favorites list.
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
 */
public class TierList {
	
	/**
	 * The number of tiers -- we use tiers SS through F, each subdivided 
	 * into three subtiers, so there are 24 total.
	 */
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
	
	private int[] numBattlesPerPlayer;
	
	private HashMap<String, double[]> stats;
	
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
		
		numBattlesPerPlayer = new int[] {0, 0, 0, 0, 0, 0, 0, 0};
	}
	
	/**
	 * Construct a <code>TierList</code> using data from the given file.
	 * 
	 * @param file	The <code>File</code> from which to load data.
	 * 
	 * @throws FileNotFoundException	Thrown if the file given does not exist.
	 * @throws IOException				Thrown if there are errors while
	 * 									loading the file, such as duplicate
	 * 									fighters.
	 * @throws TierListParseException	Thrown if there are any issues
	 * 									parsing the tier list, such as
	 * 									invalid settings values.
	 * @throws ClassNotFoundException	Thrown if the stats file loaded does
	 * 									not contain a valid <code>HashMap</code>
	 * 									object.
	 */
	public Settings loadFile(File file) throws FileNotFoundException, IOException, TierListParseException, ClassNotFoundException {
		//settings variables that will be used to instantiate the Settings
		//object returned by this method, initialized with default values
		int numPlayers = 2;
		int[] tierChances = {10, 20, 25, 25, 20, 0, 0, 0};
		int[] bumpChances = {50, 25, 25};
		int cannotGetSize = 10;
		boolean allowSInCannotGet = false;
		boolean allowSSInCannotGet = true;
		
		//try to load stats file first
		stats = loadStats();
		
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
		int lineNumber = 0;
		while(lineAt != null) {
			//increment current line number
			lineNumber++;
			
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
			
			//if we didn't find an equals sign at this point, we can skip the line
			if(!foundEqual) {
				lineAt = in.readLine();
				continue;
			}
			
			//remove space before equals sign and make sure it's all lowercase
			next = next.substring(0, next.length() - 2);
			next = next.toLowerCase();
			int posInLine = next.length() + 1;
			
			//if we're parsing exclusion lists, get the second character of
			//the string, that's going to be the player number. be sure to
			//subtract 1 to get the proper index
			if(next.contains("exclude")) {
				readExclude(Character.getNumericValue(next.charAt(1)) - 1, posInLine, lineAt);
			}
			//same thing for favorite lists
			else if(next.contains("favorite")) {
				readFavorite(Character.getNumericValue(next.charAt(1)) - 1, posInLine, lineAt);
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
				
				//check for each setting. basically, for each settings, we're
				//setting up an error message to be thrown in case the input
				//is invalid, then we parse the given value, make sure it's
				//valid, and then we're good to go
				if(next.equals("cannot get size")) {
					try {
						cannotGetSize = Integer.parseInt(toRead);
					} catch(NumberFormatException e) {
						in.close();
						throw new IntegerSettingParseException(toRead, lineNumber,
								0, Util.CANNOT_GET_MAX, IntegerSetting.CANNOT_GET_SIZE, e);
					}
					
					if(cannotGetSize < 0 || cannotGetSize > Util.CANNOT_GET_MAX) {
						in.close();
						throw new IntegerSettingParseException(toRead, lineNumber,
								0, Util.CANNOT_GET_MAX, IntegerSetting.CANNOT_GET_SIZE);
					}
				}
				else if(next.equals("allow ss in cannot get")) {
					try {
						if(toRead.equals("true")) {
							allowSSInCannotGet = true;
						}
						else if(toRead.equals("false")) {
							allowSSInCannotGet = false;
						}
						else if(Integer.parseInt(toRead) == 1) {
							allowSSInCannotGet = true;
						}
						else if(Integer.parseInt(toRead) == 0) {
							allowSSInCannotGet = false;
						}
						else {
							in.close();
							throw new BooleanSettingParseException(toRead, lineNumber,
									BooleanSetting.ALLOW_SS);
						}
					} catch(NumberFormatException e) {
						in.close();
						throw new BooleanSettingParseException(toRead, lineNumber,
								BooleanSetting.ALLOW_SS, e);
					}
				}
				else if(next.equals("allow s in cannot get")) {					
					try {
						if(toRead.equals("true")) {
							allowSInCannotGet = true;
						}
						else if(toRead.equals("false")) {
							allowSInCannotGet = false;
						}
						else if(Integer.parseInt(toRead) == 1) {
							allowSInCannotGet = true;
						}
						else if(Integer.parseInt(toRead) == 0) {
							allowSInCannotGet = false;
						}
						else {
							in.close();
							throw new BooleanSettingParseException(toRead, lineNumber,
									BooleanSetting.ALLOW_S);
						}
					} catch(NumberFormatException e) {
						in.close();
						throw new BooleanSettingParseException(toRead, lineNumber,
								BooleanSetting.ALLOW_S, e);
					}
				}
				else if(next.equals("players")) {					
					try {
						numPlayers = Integer.parseInt(toRead);
					} catch(NumberFormatException e) {
						in.close();
						throw new IntegerSettingParseException(toRead, lineNumber,
								0, 8, IntegerSetting.NUM_PLAYERS, e);
					}
					
					if(numPlayers < 2 || numPlayers > 8) {
						in.close();
						throw new IntegerSettingParseException(toRead, lineNumber,
								0, 8, IntegerSetting.NUM_PLAYERS);
					}
				}
				else if(next.equals("tier chances")) {				
					if(currentLine.length != 8) {
						in.close();
						throw new ListSettingParseException(Arrays.toString(currentLine),
								lineNumber, 8, ListSetting.TIER_CHANCES);
					}
					
					try {
						for(int at = 0; at < 8; at++) {
							tierChances[at] = Integer.parseInt(currentLine[at]);
							
							if(tierChances[at] < 0 || tierChances[at] > 100) {
								in.close();
								throw new ListSettingParseException(Arrays.toString(currentLine),
										lineNumber, 8, ListSetting.TIER_CHANCES);
							}
						}
					} catch(NumberFormatException e) {
						in.close();
						throw new ListSettingParseException(Arrays.toString(currentLine),
								lineNumber, 8, ListSetting.TIER_CHANCES, e);
					}
				}
				else if(next.equals("bump chances")) {					
					if(currentLine.length != 3) {
						in.close();
						throw new ListSettingParseException(Arrays.toString(currentLine),
								lineNumber, 8, ListSetting.BUMP_CHANCES);
					}
					
					try {
						for(int at = 0; at < 3; at++) {
							bumpChances[at] = Integer.parseInt(currentLine[at]);
							
							if(tierChances[at] < 0 || tierChances[at] > 100) {
								in.close();
								throw new ListSettingParseException(Arrays.toString(currentLine),
										lineNumber, 8, ListSetting.TIER_CHANCES);
							}
						}
					} catch(NumberFormatException e) {
						in.close();
						throw new ListSettingParseException(Arrays.toString(currentLine),
								lineNumber, 8, ListSetting.BUMP_CHANCES, e);
					}
				}
			}
			
			lineAt = in.readLine();
		}
		
		in.close();
		
		//we want to fill out the number of battles per player. we keep it in
		//an array as part of the TierList class so it doesn't need to be
		//recalculated every time a battle is generated
		for(List<Fighter> tierAt: tierList) {
			for(Fighter fighterAt: tierAt) {
				for(int playerAt = 0; playerAt < 8; playerAt++) {
					numBattlesPerPlayer[playerAt] += fighterAt.getPlayerBattles(playerAt);
				}
			}
		}
		
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
		//converts the comma-separated line into an array of strings. first
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
			
			//if we have no stats file loaded or this fighter isn't present
			//in the stats data, create a fighter with no stats data
			Fighter newFighter;
			if(stats == null || !stats.containsKey(fighterAt)) {
				newFighter = new Fighter(fighterAt, tier);
			}
			else {
				newFighter = new Fighter(fighterAt, tier, stats.get(fighterAt));
			}
			
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
	 * 
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
	 * 
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
	 * Attemps to load a file called "smash stats.sel" and store its data in
	 * a <code>HashMap</code> object that maps <code>String</code>s to an
	 * array of doubles.
	 * 
	 * @return	The <code>HashMap</code> containing loaded stats data.
	 * 			<code>null</code> will be returned if the file is not found.
	 * 
	 * @throws IOException				Thrown if there are errors loading
	 * 									the file.
	 * @throws ClassNotFoundException	Thrown if the file does not contain
	 * 									a valid <code>HashMap</code> file.
	 */
	@SuppressWarnings("unchecked")
	private HashMap<String, double[]> loadStats() throws IOException, ClassNotFoundException {
		File statsFile = new File("smash stats.sel");
		
		//do nothing if the file doesn't exist
		if(!statsFile.exists()) {
			return null;
		}
		
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(statsFile));
		HashMap<String, double[]> stats = (HashMap<String, double[]>) ois.readObject();
		ois.close();
		
		return stats;
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
	
	/**
	 * Returns a <code>String</code> representation of the tier list. Each
	 * tier is printed, followed by each player's exclusion and favorites
	 * lists.
	 */
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
	 * Generates a matchup using the given settings data. A <code>Matchup</code>
	 * containing the fighters for each player will be returned.
	 * 
	 * @param settings	The <code>Settings</code> object containing the
	 * 					settings to use when generating this battle.
	 * @param skipping	Whether or not this battle is being generated as a
	 * 					result of skipping the previous one. Will result in
	 * 					the last battle's results being removed from the
	 * 					"Cannot Get" system if true.
	 * @return			A <code>Matchup</code> representing the generated
	 * 					battle.
	 * 
	 * @throws NoValidFightersException	Thrown if a player does not have any
	 * 									valid fighters, meaning a battle
	 * 									cannot be generated.
	 */
	public Matchup generateBattle(Settings settings, boolean skipping) throws NoValidFightersException {
		//initialize an empty matchup
		Matchup matchup = new Matchup(settings.getNumPlayers());
		
		//first, generate the list of valid fighters for each player
		ArrayList<List<Fighter>> playerValidCharacters = new ArrayList<List<Fighter>>();
		for(int playerAt = 0; playerAt < settings.getNumPlayers(); playerAt++) {
			playerValidCharacters.add(getValidCharacters(playerAt, settings));
			
			if(playerValidCharacters.get(playerAt).size() == 0) {
				throw new NoValidFightersException(playerAt, false);
			}
		}
		
		//pick a player at random, and then pick a fighter at random from
		//that player. also get the tier and add it to the matchup
		int playerToPick = ThreadLocalRandom.current().nextInt(0, settings.getNumPlayers());
		int numFightersForPlayer = playerValidCharacters.get(playerToPick).size();
		Fighter chosen = playerValidCharacters.get(playerToPick).get(ThreadLocalRandom.current().nextInt(0, numFightersForPlayer));
		int tier = chosen.getTier();
		matchup.addFighter(playerToPick, chosen);
		
		Util.log("Picked a fighter at random from player " + (playerToPick + 1));
		Util.log("That player has " + numFightersForPlayer + " in their valid set.");
		Util.log("Chose " + chosen + ", so the tier is " + Util.tierToString(tier));
		
		//okay, so it's still entirely possible to choose a fighter from a
		//tier that has no valid fighters for a player. in that case, i
		//think we just return null and try again, honestly.
		for(int playerAt = 0; playerAt < settings.getNumPlayers(); playerAt++) {
			if(playerAt == playerToPick) {
				continue;
			}
			
			//basically, we're going to include all fighters in the possible
			//range of tiers. this means we'll need to apply some weighting
			//based on the bump chances
			int tier2 = tier - 1;
			int tier3 = tier - 2;
			
			//if we're at the upper end of the tier list, bump down instead
			//of bumping up. or bump up and down if it's mid double s
			if(tier == 0) {
				tier2 = 1;
				tier3 = 2;
			}
			else if(tier == 1) {
				tier2 = 0;
				tier3 = 2;
			}
			
			ArrayList<Fighter> inTierOptions = new ArrayList<Fighter>();
			int countTier1 = 0;
			int countTier2 = 0;
			int countTier3 = 0;
			
			for(Fighter fighterAt: playerValidCharacters.get(playerAt)) {
				//skip if it's already been gotten by another player
				if(matchup.contains(fighterAt)) {
					continue;
				}
				
				int tierOfChar = fighterAt.getTier();
				
				//we need to re-include some kind of weighting for the
				//number of times a player has gotten a fighter. do nothing
				//if we'd be dividing by zero
				int timesToAdd = tierOfChar;
				if(numBattlesPerPlayer[playerAt] != 0) {
					timesToAdd *= 1 - (fighterAt.getPlayerBattles(playerAt) / numBattlesPerPlayer[playerAt]);
				}
				
				//and now weight based on bump chances. this is also where
				//we skip any fighter that's not in the tier range
				if(tierOfChar == tier) {
					timesToAdd += settings.getBumpChance(0);
					countTier1 += timesToAdd;
				}
				else if(tierOfChar == tier2) {
					timesToAdd += settings.getBumpChance(1);
					countTier2 += timesToAdd;
				}
				else if(tierOfChar == tier3) {
					timesToAdd += settings.getBumpChance(2);
					countTier3 += timesToAdd;
				}
				else {
					timesToAdd = 0;
				}
				
				for(int at = 0; at < timesToAdd; at++) {
					inTierOptions.add(fighterAt);
				}
			}
			
			if(inTierOptions.size() == 0) {
				throw new NoValidFightersException(playerAt, true);
			}
			
			Util.log("Player " + (playerAt + 1) + " has " + inTierOptions.size() +
					" options within tier range.");
			Util.log("  Of them, " + countTier1 + " are original tier, " +
					countTier2 + " bump once, and " + countTier3 + " bump twice.");
			
			numFightersForPlayer = inTierOptions.size();
			chosen = inTierOptions.get(ThreadLocalRandom.current().nextInt(0, numFightersForPlayer));
			matchup.addFighter(playerAt, chosen);
		}
		
		Util.log("===== Successfully generated battle! =====");
		
		//remove from cannot get queue first
		Util.log("The max size of the cannot get buffer is " + settings.getCannotGetSize());
		Util.log("There are " + cannotGet.size() + " fighters in it, and " +
				settings.getNumPlayers() + " players.");
		
		if(cannotGet.size() >= (settings.getCannotGetSize() * settings.getNumPlayers())) {
			Util.log("Removing from cannot get...");
			
			for(int at = 0; at < settings.getNumPlayers(); at++) {
				cannotGet.poll();
			}
		}
		
		//then add to queue
		for(int playerAt = 0; playerAt < settings.getNumPlayers(); playerAt++) {
			Fighter fighterAt = matchup.getFighter(playerAt);
			tier = fighterAt.getTier();
			
			if(skipping) {
				individualCannotGet.get(playerAt).poll();
			}
			
			//assume that we will add, and change to false based on whether
			//the fighter is S or SS tier and those tiers are not allowed in
			boolean add = true;
			if(tier < 3 && !settings.ssAllowedInCannotGet()) {
				add = false;
			}
			else if(tier >= 3 && tier <= 5 && !settings.sAllowedInCannotGet()) {
				add = false;
			}
			
			if(add) {
				cannotGet.add(fighterAt);
			}
			
			//if the gotten character is a favorite, don't add it to the
			//individual cannot get
			if(!favoriteList.get(playerAt).contains(fighterAt)) {
				individualCannotGet.get(playerAt).add(fighterAt);
			}
			
			Util.log("Player " + (playerAt + 1) + " cannot get " + individualCannotGet.get(playerAt));
		}
		
		Util.log("Nobody can get " + cannotGet);
		
		return matchup;
	}
	
	/**
	 * Gets the set of valid fighters for the given player, weighted by the
	 * chance of getting that tier and the number of times the player has
	 * already gotten that fighter. The number of times the player has gotten
	 * a particular fighter is retrieved, then divided by the total number of
	 * battles they have participated in. That value is then multiplied by
	 * the chance of getting the tier of that fighter. So if Link is in a
	 * tier that has a 25% chance of being gotten, and player 1 has gotten
	 * link in 1 out of the 10 battles he's participated in, then Link will
	 * appear in the list 22 times.
	 * 
	 * @param player	The player whose valid fighter set is being generated.
	 * @param settings	The <code>Settings</code> object being used to
	 * 					generate this battle. Needed to get the tier chances,
	 * 					which is used to weight the likelihood of getting a
	 * 					particular fighter.
	 * @return			A <code>List</code> containing the fighters that this
	 * 					player can get, weighted by the chance of getting that
	 * 					fighter's tier and the number of times the player has
	 * 					already gotten that fighter.
	 */
	private List<Fighter> getValidCharacters(int player, Settings settings) {
		ArrayList<Fighter> initialValidChars = new ArrayList<Fighter>();
		ArrayList<Fighter> finalValidChars = new ArrayList<Fighter>();
		
		//loop through all the fighters
		for(int tierAt = 0; tierAt < NUM_TIERS; tierAt++) {
			//ignore any tier that's turned off
			if(settings.getTierChance(Util.subTierToTier(tierAt)) == 0) {
				continue;
			}
			
			for(Fighter fighterAt: tierList.get(tierAt)) {
				//if the cannot get queue, individual cannot get queue, and
				//the player's exclusion list don't contain this fighter, add it
				if(!cannotGet.contains(fighterAt) &&
						!individualCannotGet.get(player).contains(fighterAt) &&
						!exclusionList.get(player).contains(fighterAt)) {
					initialValidChars.add(fighterAt);
				}
			}
		}
		
		Util.log("Found " + initialValidChars.size() + " fighters for player " + (player + 1));
		
		//now is where the fun happens. we want to essentially create a
		//multiplier for each fighter based on some conditions.
		for(Fighter fighterAt: initialValidChars) {
			//start with the chance of getting the tier of that fighter
			int toAppear = settings.getTierChance(Util.subTierToTier(fighterAt.getTier()));
			
			//multiply toAppear by the ratio of times this player has gotten
			//this fighter to the total battles they've participated in.
			//well, inverse of the ratio. if a player has gotten Link in 10
			//of 100 battles, multiply ratio by .9 -- if we multiply by .1,
			//then getting the fighter more actually increases chances.
			//of course, we do nothing if we'd be dividing by zero
			if(numBattlesPerPlayer[player] != 0) {
				double ratio = 1 - ((double) fighterAt.getPlayerBattles(player) / numBattlesPerPlayer[player]);
				toAppear *= ratio;
			}
			
			//if it's below 0, normalize to 1
			if(toAppear <= 0) {
				toAppear = 1;
			}
			
			//then add it that number of times. we use a new arraylist here
			//because otherwise we'd be modifying the arraylist while
			//iterating over it
			for(int at = 0; at < toAppear; at++) {
				finalValidChars.add(fighterAt);
			}
		}
		
		return finalValidChars;
	}
	
	/**
	 * Swaps the specified fighters and players in the cannot get queues.
	 * 
	 * @param player1	The first player to swap.
	 * @param fighter1	The fighter that that the first given player got.
	 * @param player2	The second player to swap.
	 * @param fighter2	The fighter that the second given player got.
	 */
	public void swapFighters(int player1, Fighter fighter1, int player2, Fighter fighter2) {
		//remove the fighters if they're present. they may not be because
		//of favorites lists and all that
		individualCannotGet.get(player1).remove(fighter1);
		individualCannotGet.get(player2).remove(fighter2);
		
		//and add those fighters to the other player's cannot get, as long as
		//they aren't in that player's favorites. also don't add if they're
		//already there, which is allowed
		if(!favoriteList.get(player1).contains(fighter2) && !individualCannotGet.get(player1).contains(fighter2)) {
			individualCannotGet.get(player1).add(fighter2);
		}
		
		if(!favoriteList.get(player2).contains(fighter1) && !individualCannotGet.get(player2).contains(fighter1)) {
			individualCannotGet.get(player2).add(fighter1);
		}
		
		Util.log("Swapped player " + (player1 + 1) + " and " + (player2 + 1) + ".");
		Util.log("Now, player " + (player1 + 1) + " cannot get " + individualCannotGet.get(player1));
		Util.log("And player " + (player2 + 1) + " cannot get " + individualCannotGet.get(player2));
	}
	
	/**
	 * @return	A <code>HashMap</code> object containing the stats data, so
	 * 			it can be saved in an <code>.sel</code> file.
	 */
	public HashMap<String, double[]> getStatsMap() {
		HashMap<String, double[]> retMap = new HashMap<String, double[]>();
		
		for(int at = 0; at < NUM_TIERS; at++) {
			for(Fighter fighterAt: tierList.get(at)) {
				double[] stats = new double[16];
				
				for(int playerAt = 0; playerAt < 8; playerAt++) {
					stats[playerAt * 2] = fighterAt.getPlayerWins(playerAt);
					stats[playerAt * 2 + 1] = fighterAt.getPlayerBattles(playerAt);
				}
				
				retMap.put(fighterAt.getName(), stats);
			}
		}
		
		return retMap;
	}
	
	/**
	 * Gets the results of a lookup, stored in an array of
	 * <code>ComparableArray</code>s. The array is sorted based on the
	 * lookup type.
	 * 
	 * @param lookupType	The type of lookup being done. Based off the
	 * 						dropdown in <code>MainWindow</code>.
	 * @return				An array of <code>ComparableArray</code> objects,
	 * 						one for each fighter in the tier list. The array
	 * 						is sorted based on the lookup type.
	 */
	public ComparableArray[] getLookupResults(int lookupType) {
		ComparableArray[] results = new ComparableArray[numFighters()];
		
		int indexAt = 0;
		for(int at = 0; at < NUM_TIERS; at++) {
			for(Fighter fighterAt: tierList.get(at)) {
				results[indexAt] = new ComparableArray(fighterAt, lookupType);
				indexAt++;
			}
		}
		
		Arrays.sort(results);
		return results;
	}
	
	/**
	 * Gets the data for the second lookup type, each player's winrate.
	 * Each player's total number of wins and battles across all fighters
	 * are added up.
	 * 
	 * @return	A <code>String</code> containing the data described above.
	 */
	public String getPlayerWinrateString() {
		StringBuffer retString = new StringBuffer(175);
		
		int[] playerBattles = new int[8];
		int[] playerWins = new int[8];
		
		for(int tierAt = 0; tierAt < NUM_TIERS; tierAt++) {
			for(Fighter fighterAt: tierList.get(tierAt)) {
				for(int playerAt = 0; playerAt < 8; playerAt++) {
					playerBattles[playerAt] += fighterAt.getPlayerBattles(playerAt);
					playerWins[playerAt] += fighterAt.getPlayerWins(playerAt);
				}
			}
		}
		
		retString.append("Players' Overall Winrate:\n");
		
		for(int playerAt = 0; playerAt < 8; playerAt++) {
			retString.append("P" + (playerAt + 1) + " W% - " +
					Util.printDouble(((float) playerWins[playerAt] / playerBattles[playerAt]) * 100) + "% (" +
					playerWins[playerAt] + "/" + playerBattles[playerAt] + ")\n");
		}
		
		return retString.toString();
	}
}
