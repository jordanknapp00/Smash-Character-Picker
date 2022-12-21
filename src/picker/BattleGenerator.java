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
		
		double delta = System.currentTimeMillis() - startGen;
		Util.log("Generation of this battle took " + delta + "ms.");
		
		return "";
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
			while(tierTurnedOff(adjust, state.tierChances)) {
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
	 * @param chances	The current tier chances.
	 * @return			<code>true</code> if the given tier is not currently
	 * 					active, meaning its percentage chance is zero.
	 * 					<code>false</code> if it <i>is</i> active.
	 */
	private boolean tierTurnedOff(int tier, int[] chances) {
		if((tier == 0 || tier == 1 || tier == 2) && chances[0] == 0) {
			return true;
		}
		else if((tier == 3 || tier == 4 || tier == 5) && chances[1] == 0) {
			return true;
		}
		else if((tier == 6 || tier == 7 || tier == 8) && chances[2] == 0) {
			return true;
		}
		else if((tier == 9 || tier == 10 || tier == 11) && chances[3] == 0) {
			return true;
		}
		else if((tier == 12 || tier == 13 || tier == 14) && chances[4] == 0) {
			return true;
		}
		else if((tier == 15 || tier == 16 || tier == 17) && chances[5] == 0) {
			return true;
		}
		else if((tier == 18 || tier == 19 || tier == 20) && chances[6] == 0) {
			return true;
		}
		else if((tier == 21 || tier == 22 || tier == 23) && chances[7] == 0) {
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

}
