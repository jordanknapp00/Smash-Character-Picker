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
	
	public BattleGenerator(ProgramState state, StatsManager statsManager) {
		this.state = state;
		this.statsManager = statsManager;
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
		
		//okay, first we're going to make a dictionary that maps each fighter
		//to its tier, that way when we pick a fighter, we know what tier it
		//is. i'm fairly certain at this point that a Fighter class would
		//make this whole process a lot simpler. perhaps i'll just get this
		//working first, then work on refactoring the whole program around
		//some better data structures. object-oriented programming is cool,
		//i really shouldn't be dealing with strings like this.
		HashMap<String, Integer> tierDict = new HashMap<String, Integer>();
		for(int tier = 0; tier < 24; tier++) {
			for(String charAt: state.linesOfFile.get(tier)) {
				tierDict.put(charAt, tier);
			}
		}
		
		Util.log("Created tier dictionary");
		
		ArrayList<ArrayList<String>> playerValidCharacters = new ArrayList<ArrayList<String>>();
		for(int playerAt = 0; playerAt < state.numPlayers; playerAt++) {
			playerValidCharacters.add(getValidCharacters(playerAt, tierDict));
			
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
				int timesToAdd = 0;
				
				//alright, we're going to weight things even more to account
				//for the bump chances, why the hell not?
				if(tierOfChar == tier) {
					timesToAdd = state.bumpChances[0];
					countTier1 += timesToAdd;
				}
				else if(tierOfChar == tier2) {
					timesToAdd = state.bumpChances[1];
					countTier2 += timesToAdd;
				}
				else if(tierOfChar == tier3) {
					timesToAdd = state.bumpChances[2];
					countTier3 += timesToAdd;
				}
				
				//multiplying each set of characters by a factor of 10 or
				//even more is going to throw off the balancing due to the
				//number of times a player has gotten a fighter. so we want
				//to add in some more adjustment based on that number
				timesToAdd -= 3 * state.stats.get(charAt)[playerAt * 2  + 1];
				
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
	
	private ArrayList<String> getValidCharacters(int player, HashMap<String, Integer> tierDict) {
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
						!tierTurnedOff(tierDict.get(charAt))) {
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
	
//	/**
//	 * @return	A <code>String</code> containing the battle. The string is
//	 * 			formatted to be output directly to the <code>MainWindow</code>'s
//	 * 			<code>results TextArea</code>.
//	 */
//	public String generateBattle() {
//		//we want to keep track of exactly how long it took to generate this
//		//battle, for the fun of it.
//		startGen = System.currentTimeMillis();
//		
//		//the actual battle is generated in a helper method, because that method
//		//may be required to call itself recursively. everything that may need
//		//to be repeated in a separate method makes things easier, including
//		//keeping track of the time it took to generate the battle. anything
//		//that should not be repeated it kept outside of the helper method.
//		String battle = "";
//		try {
//			battle = generateBattleHelper(0);
//		} catch(Exception e) {
//			Util.error(e);
//			battle = "Battle generation failed! See debug log.";
//		}
//		
//		Util.log("Nobody can get " + state.cannotGet);
//		
//		Util.log("= Final result for battle #" + state.numBattles + ": =");
//		//print out the results of the battle just because it's helpful to know
//		for(int playerAt = 0; playerAt < state.gotten.size(); playerAt++) {
//			Util.log("Player " + (playerAt + 1) + " got " + state.gotten.get(playerAt));
//		}
//		
//		statsManager.updateStatsScreen();
//		
//		state.skipping = false;
//		
//		endGen = System.currentTimeMillis();
//		double delta = endGen - startGen;
//		Util.log("Generation of this battle took " + delta + "ms.");
//		
//		return battle;
//	}
	
	private String generateBattleHelper(int depth) throws Exception {
		//if this is our 25th try, then give up and decide that there must not
		//be any valid battles left.
		if(depth == 25) {
			throw new Exception("No valid battles found after 25 tries");
		}
		
		//otherwise, continue with battle generation normally
		String battle = "";
		
		if(!state.skipping || state.numBattles == 0) {
			state.numBattles++;
		}
		
		battle += "Battle #" + state.numBattles + ":\n";
		Util.log("=== BATTLE #" + state.numBattles + ": ===");
		
		int[] playerTiers = getPlayerTiers();
		
		state.gotten.clear();
		for(int player = 1; player <= state.numPlayers; player++) {
			int tier = playerTiers[player - 1];
			String got = "";
			ArrayList<String> validCharacters = new ArrayList<String>();
			
			//set up an array list of valid characters from the appropriate
			//tier from which this player's character will be chosen
			//basically, remove excluded characters, ones from the cannot
			//get queue, and ones already gotten
			for(String currentlyAt: state.linesOfFile.get(tier)) {
				if(!state.linesOfFile.get(player + 23).contains(currentlyAt) &&
				   !state.cannotGet.contains(currentlyAt) &&
				   !state.gotten.contains(currentlyAt) &&
				   !state.individualCannotGet[player - 1].contains(currentlyAt)) {
					validCharacters.add(currentlyAt);
				}
			}
			
			//if there are no valid characters, call the function again
			if(validCharacters.size() == 0) {
				if(!state.skipping) {
					state.numBattles--;
				}
				
				Util.error("There were no valid characters, retrying...");
				return generateBattleHelper(depth + 1);
			}
			
			got = validCharacters.get(ThreadLocalRandom.current().nextInt(0, validCharacters.size()));
			state.gotten.add(got);
			
			battle += "Player " + player + " got " + got + ", " + Util.tierToString(tier) + ".\n";
		}
		
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
			int tier = playerTiers[playerAt];
			
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
		
		return battle;
	}
	
	private int[] getPlayerTiers() {
		int tierChance = ThreadLocalRandom.current().nextInt(0, 101);
		int tier = -1;
		int sum = 0;
		int[] playerTiers = new int[state.numPlayers];
		
		sum = state.tierChances[0];
		if(tierChance <= sum) {
			tier = 0;
		}
		sum += state.tierChances[1];
		if(tierChance <= sum && tier == -1) {
			tier = 1;
		}
		sum += state.tierChances[2];
		if(tierChance <= sum && tier == -1) {
			tier = 2;
		}
		sum += state.tierChances[3];
		if(tierChance <= sum && tier == -1) {
			tier = 3;
		}
		sum += state.tierChances[4];
		if(tierChance <= sum && tier == -1) {
			tier = 4;
		}
		sum += state.tierChances[5];
		if(tierChance <= sum && tier == -1) {
			tier = 5;
		}
		sum += state.tierChances[6];
		if(tierChance <= sum && tier == -1) {
			tier = 6;
		}
		sum += state.tierChances[7];
		if(tierChance <= sum && tier == -1) {
			tier = 7;
		}
		tier = convertToTierNum(tier);
		
		Util.log("The starting tier is " + Util.tierToString(tier));
		
		for(int at = 0; at < state.numPlayers; at++) {
			int adjustVal = ThreadLocalRandom.current().nextInt(0, 100);
			int adjust = 1;
			
			sum = state.bumpChances[0];
			if(adjustVal <= sum) {
				adjust = 0;
			}
			sum += state.bumpChances[1];
			if(adjustVal <= sum && adjust == 1) {
				adjust = -1;
			}
			sum += state.bumpChances[2];
			if(adjustVal <= sum && adjust == 1) {
				adjust = -2;
			}
			
			int oldadj = adjust;
			adjust = tier + adjust;
			
			if(adjust < 0) {
				adjust = 0;
			}
			while(tierTurnedOff(adjust)) {
				adjust++;
			}
			if(adjust != tier) {
				Util.log("Player " + (at + 1) + " has been adjusted (adjval "
						+ oldadj + "), getting " + Util.tierToString(adjust));
			}
			
			playerTiers[at] = adjust;
		}
		
		return playerTiers;
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
	 * This method takes in a tier generated in the
	 * <code>getPlayerTiers()</code> method, which is a value between 0 and 7,
	 * and converts it into a proper tier number that can be used by the rest of
	 * the program to index into the <code>linesOfFile</code> array.
	 * <br><br>
	 * Since the <code>getPlayerTiers()</code> method only picks a basic tier,
	 * i.e. SS, S, A, B, etc., this tier will also randomly select a sub-tier.
	 * The given tier value has equal chance of being incremented by one,
	 * decremented by one, or staying the same, thus choosing between the upper,
	 * lower, or mid sub-tiers.
	 * 
	 * @param tier	The base tier to convert.
	 * @return		The given tier, converted into the appropriate index and
	 * 				with a sub-tier randomly chosen.
	 */
	private int convertToTierNum(int tier) {
		int change = ThreadLocalRandom.current().nextInt(-1, 2);
		
		if(tier == 0) {
			return 1 + change;
		}
		else if(tier == 1) {
			return 4 + change;
		}
		else if(tier == 2) {
			return 7 + change;
		}
		else if(tier == 3) {
			return 10 + change;
		}
		else if(tier == 4) {
			return 13 + change;
		}
		else if(tier == 5) {
			return 16 + change;
		}
		else if(tier == 6) {
			return 19 + change;
		}
		else {
			return 22 + change;
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
