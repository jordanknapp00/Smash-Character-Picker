package picker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import data.ProgramState;
import util.Util;

/**
 * Class responsible for generating battles. It takes in a
 * <code>ProgramState</code> and uses that data to choose a valid matchup.
 * 
 * @author Jordan Knapp
 */
public class BattleGenerator {
	
	private ProgramState state;
	private StatsManager statsManager;
	
	private HashMap<String, Integer> tierDict;
	
	public BattleGenerator(ProgramState state, StatsManager statsManager) {
		this.state = state;
		this.statsManager = statsManager;
		
		//for now, we're using this dictionary to easily be able to figure
		//out the tier of a fighter. in the future, i want to be able to
		//have a Fighter class that makes managing all this stuff a lot easier.
		//java is object-oriented, why am i not using it?
		tierDict = new HashMap<String, Integer>();
	}
	
	/**
	 * @return	A <code>String</code> containing the battle. The string is
	 * 			formatted to be output directly to the <code>MainWindow</code>'s
	 *			<code>results TextArea</code>.
	 */
	public String generateBattle() {
		double startGen = System.currentTimeMillis();
		
		//instead of picking a tier and going from there, maybe start with the
		//set of all fighters that each character can get. then maybe weight the
		//chances of getting them based on the tier chances and the number of
		//times the player has gotten particular fighters?
		
		//for each player, generate a list of each fighter they could
		//potentially get. basically, every fighter except the ones on their
		//exclusion list, the ones in the cannot get queue, and the ones in
		//their individual cannot get. each fighter will then have a number
		//basically representing their chance of being chosen. that number
		//will be the percentage chance of getting that particular tier,
		//subtracted by the number of times that player has already gotten
		//that fighter.
		//
		//after we've generated the lists for each player, we choose a player
		//at random, and pick a fighter at random from the list. that will
		//determine the tier. other players will then have all fighters outside
		//of a plus or minus one tier range of that fighter removed. note that
		//the quantities of fighters in each list still remains based on the
		//chance of a tier and the number of times a player has gotten that
		//fighter. once the remaining players have had their lists narrowed
		//down to the appropriate tier, fighters are chosen at random from
		//those lists.
		
		//i guess i can't initialize the tierDict in the constructor like i
		//wanted to, because a tier list hasn't been loaded when this class
		//is initialized. i mean, duh! we still don't want to waste time
		//making the tier dict every time, so we'll do it here but only if
		//the tier dict is empty
		if(tierDict.size() == 0) {
			for(int tier = 0; tier < 24; tier++) {
				for(String charAt: state.linesOfFile.get(tier)) {
					tierDict.put(charAt, tier);
				}
			}
		}
		
		ArrayList<ArrayList<String>> playerValidCharacters = new ArrayList<ArrayList<String>>();
		for(int playerAt = 0; playerAt < state.numPlayers; playerAt++) {
			playerValidCharacters.add(getValidCharacters(playerAt));
			
			//go ahead and check if a particular player has no valid chars
			if(playerValidCharacters.get(playerAt).size() == 0) {
				Util.log("Player " + (playerAt + 1) + " has no valid characters!");
				return "";
			}
		}
		
		int playerToPick = ThreadLocalRandom.current().nextInt(0, state.numPlayers);
		int numFightersForPlayer = playerValidCharacters.get(playerToPick).size();
		
		//this is kind of stupid and annoying, but we need to initialize each
		//spot in the gotten arraylist to null, because we aren't simply
		//adding on to the end of it every time. apparently you can't add to
		//an index if you haven't assigned stuff before it
		state.gotten.clear();
		for(int playerAt = 0; playerAt < state.numPlayers; playerAt++) {
			state.gotten.add(null);
		}
		
		String chosenFighter = playerValidCharacters.get(playerToPick).get(ThreadLocalRandom.current().nextInt(0, numFightersForPlayer));
		state.gotten.set(playerToPick, chosenFighter);
		int tier = tierDict.get(chosenFighter);
		
		Util.log("Picking fighter at random from player " + (playerToPick + 1));
		Util.log("Player " + (playerToPick + 1) + " has " + numFightersForPlayer + " fighters in their valid set");
		Util.log("Chose " + chosenFighter + ", so the tier is " + Util.tierToString(tier));
		
		//okay, so it's still entirely possible to generate a tier that has
		//no valid characters for a player. whatever, we'll just return a
		//blank string in that case.
		for(int playerAt = 0; playerAt < state.numPlayers; playerAt++) {
			if(playerAt == playerToPick) {
				continue;
			}
			
			//instead of generating a tier, why not instead just include
			//all characters from the range of tiers that are available?
			//that would greatly reduce the chances of getting a tier without
			//any valid characters, right? let's try it. only problem is it
			//kind of invalidates the bump chances. maybe we need to apply
			//even more weighting according to the bump chances...
			int tier2 = tier - 1;
			int tier3 = tier - 2;
			if(tier == 0) {
				tier2 = 1;
				tier3 = 2;
			}
			else if(tier == 1) {
				tier2 = 0;
				tier3 = 2;
			}
			
			ArrayList<String> inTierOptions = new ArrayList<String>();
			int countTier1 = 0;
			int countTier2 = 0;
			int countTier3 = 0;
			
			for(String charAt: playerValidCharacters.get(playerAt)) {
				//skip fighters already chosen
				if(state.gotten.contains(charAt)) {
					continue;
				}
				
				int tierOfChar = tierDict.get(charAt);
				
				//multiplying each set of characters by a factor of 10 or
				//even more is going to throw off the balancing due to the
				//number of times a player has gotten a fighter. so we want
				//to add in some more adjustment based on that number
				int timesToAdd = (int) (-2 * state.stats.get(charAt)[playerAt * 2  + 1]);
				
				//alright, we're going to weight things even more to account
				//for the bump chances, why the hell not?
				if(tierOfChar == tier) {
					timesToAdd += state.bumpChances[0];
					countTier1 += timesToAdd;
				}
				else if(tierOfChar == tier2) {
					timesToAdd += state.bumpChances[1];
					countTier2 += timesToAdd;
				}
				else if(tierOfChar == tier3) {
					timesToAdd += state.bumpChances[2];
					countTier3 += timesToAdd;
				}
				
				for(int at = 0; at < timesToAdd; at++) {
					inTierOptions.add(charAt);
				}
			}
			
			if(inTierOptions.size() == 0) {
				Util.log("Player " + (playerAt + 1) + " has no valid options within tier range.");
				return "";
			}
			else {
				Util.log("Player " + (playerAt + 1) + " has " + inTierOptions.size() + " options within tier range");
				Util.log("  Of them, " + countTier1 + " are the original tier, " + countTier2 + " bump once, " + countTier3 + " bump twice.");
			}
			
			numFightersForPlayer = inTierOptions.size();
			chosenFighter = inTierOptions.get(ThreadLocalRandom.current().nextInt(0, numFightersForPlayer));
			state.gotten.set(playerAt, chosenFighter);
		}
		
		Util.log("===== Successfully generated battle! =====");
		
		String returnString = "Battle #" + state.numBattles + ":\n";
		for(int playerAt = 0; playerAt < state.numPlayers; playerAt++) {
			String fighterGot = state.gotten.get(playerAt);
			Util.log("Player " + (playerAt + 1) + " got " + fighterGot + ", tier " + Util.tierToString(tierDict.get(fighterGot)));
			
			returnString += "Player " + (playerAt + 1) + " got " + fighterGot + ", " + Util.tierToString(tierDict.get(fighterGot)) + "\n";
		}
		
		Util.log("=== Cannot get information: ===");
		
		//remove from cannot get queue first
		Util.log("The total size of the cannot get buffer is " + state.cannotGetSize);
		Util.log("There are " + state.cannotGet.size() + " fighters in the buffer, and " + state.numPlayers + " players.");
		if(state.cannotGet.size() >= (state.cannotGetSize * state.numPlayers)) {
			Util.log("Removing from cannot get...");
			for(int at = 0; at < state.numPlayers; at++) {
				state.cannotGet.removeFirst();
			}
		}
		
		//then add to queue
		for(int playerAt = 0; playerAt < state.gotten.size(); playerAt++) {
			tier = tierDict.get(state.gotten.get(playerAt));
			
			if(state.skipping) {
				state.individualCannotGet[playerAt].removeLast();
			}
			
			if(tier < 3 && state.allowSSInCannotGetBuffer) {
				state.cannotGet.add(state.gotten.get(playerAt), tier);
			}
			else if(tier >= 3 && tier <= 5 && state.allowSInCannotGetBuffer) {
				state.cannotGet.add(state.gotten.get(playerAt), tier);
			}
			else if(tier >= 6) {
				state.cannotGet.add(state.gotten.get(playerAt), tier);
			}
			
			//if the gotten character is a favorite, don't add it to the
			//cannot get for rest of session queue
			if(!state.linesOfFile.get(32 + playerAt).contains(state.gotten.get(playerAt))) {
				state.individualCannotGet[playerAt].add(state.gotten.get(playerAt), tier);
			}
			
			Util.log("Player " + (playerAt + 1) + " cannot get " + state.individualCannotGet[playerAt]);
		}
		
		Util.log("Nobody can get " + state.cannotGet);
		
		statsManager.updateStatsScreen();
		state.skipping = false;
		
		double delta = System.currentTimeMillis() - startGen;
		Util.log("Finished generating. Generation of this battle took " + delta + "ms.");
		
		return returnString;
	}
	
	private ArrayList<String> getValidCharacters(int player) {
		ArrayList<String> validChars = new ArrayList<String>();
		
		//loop through the lines of the file up to a certain point. each
		//line is a tier
		for(int tier = 0; tier < 24; tier++) {
			//for each character in this tier
			for(String charAt: state.linesOfFile.get(tier)) {
				//if the cannot get queue doesn't contain this character,
				//and this player's individual cannot get doesn't contain
				//this character, and the player's exclusion list doesn't
				//contain this character, add them to the set of valid chars
				if(!state.cannotGet.contains(charAt) &&
						!state.individualCannotGet[player].contains(charAt) &&
						!state.linesOfFile.get(player + 24).contains(charAt) &&
						!tierTurnedOff(tier)) {
					validChars.add(charAt);
				}
			}
		}
		
		Util.log("Found " + validChars.size() + " for player " + (player + 1));
		
		//now is where the fun happens. we want to determine the number of
		//times that each player should appear, and have them appear that
		//number of times in the arraylist
		int size = validChars.size();
		for(int at = 0; at < size; at++) {
			String charAt = validChars.get(at);
			
			//to determine the number of times the fighter should appear,
			//start with the tier chances for its tier
			int toAppear = state.tierChances[subtierToTier(tierDict.get(charAt))];
			
			//however, now we want to subtract by the number of times this
			//player has gotten this fighter... maybe times two to really
			//make a difference
			toAppear -= 2 * state.stats.get(charAt)[player * 2  + 1];
			
			if(toAppear <= 0) {
				toAppear = 1;
			}
			
			for(int at2 = 0; at2 < toAppear; at2++) {
				validChars.add(charAt);
			}
		}
		
		return validChars;
	}
	
	/**
	 * @param tier		The tier to check whether it's turned off.
	 * @return			<code>true</code> if the given tier is not currently
	 * 					active, meaning its percentage chance is zero.
	 * 					<code>false</code> if it <i>is</i> active.
	 */
	private boolean tierTurnedOff(int tier) {
		if((tier == 0 || tier == 1 || tier == 2) && state.tierChances[0] == 0) {
			return true;
		}
		else if((tier == 3 || tier == 4 || tier == 5) && state.tierChances[1] == 0) {
			return true;
		}
		else if((tier == 6 || tier == 7 || tier == 8) && state.tierChances[2] == 0) {
			return true;
		}
		else if((tier == 9 || tier == 10 || tier == 11) && state.tierChances[3] == 0) {
			return true;
		}
		else if((tier == 12 || tier == 13 || tier == 14) && state.tierChances[4] == 0) {
			return true;
		}
		else if((tier == 15 || tier == 16 || tier == 17) && state.tierChances[5] == 0) {
			return true;
		}
		else if((tier == 18 || tier == 19 || tier == 20) && state.tierChances[6] == 0) {
			return true;
		}
		else if((tier == 21 || tier == 22 || tier == 23) && state.tierChances[7] == 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * This method takes a subtier value (between 0 and 23) and converts it
	 * to a regular tier number (between 0 and 7) that can be use to index
	 * into the tier chances array. For example, if 7 (mid A tier) is given,
	 * 2 (A tier) will be returned.
	 * 
	 * @param tier	The tier to convert.
	 * @return		The given subtier converted to a higher-level tier.
	 */
	private int subtierToTier(int tier) {
		if((tier == 0 || tier == 1 || tier == 2)) {
			return 0;
		}
		else if((tier == 3 || tier == 4 || tier == 5)) {
			return 1;
		}
		else if((tier == 6 || tier == 7 || tier == 8)) {
			return 2;
		}
		else if((tier == 9 || tier == 10 || tier == 11)) {
			return 3;
		}
		else if((tier == 12 || tier == 13 || tier == 14)) {
			return 4;
		}
		else if((tier == 15 || tier == 16 || tier == 17)) {
			return 5;
		}
		else if((tier == 18 || tier == 19 || tier == 20)) {
			return 6;
		}
		else if((tier == 21 || tier == 22 || tier == 23)) {
			return 7;
		}
		else {
			return -1;
		}
	}

}
