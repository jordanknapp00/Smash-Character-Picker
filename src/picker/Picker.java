package picker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Picker {
	
	static String[] Stier = {"Ganondorf", "Roy", "Ness", "King Dedede", "Palutena", "Donkey Kong"};
	static String[] Atier = {"Pichu", "Bowser", "Ike", "Lucas", "Little Mac", "Zelda", "Snake", "R.O.B.", "King K. Rool"};
	static String[] Btier = {"Link", "Ridley", "Luigi", "Yoshi", "Simon", "Young Link", "Cloud", "Fox", "Kirby", "Pikachu", "Isabelle", "Peach", "Toon Link", "Inkling"};
	static String[] Ctier = {"Marth", "Zero Suit Samus", "Captain Falcon", "Falco", "Lucario", "Shulk", "Wario", "Greninja", "Mr. Game & Watch", "Jigglypuff", "Robin", "Pit", "Sheik"};
	
	static String[] playableLowTiers = {"Mario", "Wolf", "Incineroar"};
	
	static String[][] tierArr = {Stier, Atier, Btier, Ctier, playableLowTiers};
	
	static ArrayList<String> p1Exclude = new ArrayList<String>(Arrays.asList("Pit", "Sheik", "Sonic", "R.O.B.", "Greninja"));
	static ArrayList<String> p2Exclude = new ArrayList<String>(Arrays.asList("Isabelle", "Robin", "Snake", "Wario"));
	static ArrayList<String> p3Exclude = new ArrayList<String>(Arrays.asList("Jigglypuff", "Cloud", "Snake"));
	
	static Random rand = new Random();

	public static void main(String[] args) {
		int tierChance = rand.nextInt(100);
		int midTier;
		
		//C tier, 20%
		//B tier, 30%
		//A tier, 25%
		//S tier, 20%
		if(tierChance <= 25) {
			midTier = 3;
		}
		else if(tierChance <= 55) {
			midTier = 2;
		}
		else if(tierChance <= 80) {
			midTier = 1;
		}
		else {
			midTier = 0;
		}
		
		int p1Tier = midTier;
		int p2Tier = midTier;
		int p3Tier = midTier;
		if(rand.nextInt(100) >= 50) {
			int mod1;
			int mod2;
			int mod3;
			if(midTier == 1 || midTier == 2) {
				mod1 = rand.nextInt(2) - 1;
				mod2 = rand.nextInt(2) - 1;
				mod3 = rand.nextInt(2) - 1;
				p1Tier += mod1;
				p2Tier += mod2;
				p3Tier += mod3;
			}
			else if(midTier == 0) {
				mod1 = rand.nextInt(2);
				mod2 = rand.nextInt(2);
				mod3 = rand.nextInt(2);
				p1Tier += mod1;
				p2Tier += mod2;
				p3Tier += mod3;
			}
			else {
				mod1 = rand.nextInt(2);
				mod2 = rand.nextInt(2);
				mod3 = rand.nextInt(2);
				if(rand.nextInt(100) < 50) {
					p1Tier -= mod1;
					p2Tier -= mod2;
					p3Tier -= mod3;
				}
				else {
					p1Tier += mod1;
					p2Tier += mod2;
					p3Tier += mod3;
				}
			}
		}
		
		String p1Char = getChar(tierArr[p1Tier], p1Exclude);
		p2Exclude.add(p1Char);
		p3Exclude.add(p1Char);
		String p2Char = getChar(tierArr[p2Tier], p2Exclude);
		p3Exclude.add(p2Char);
		String p3Char = getChar(tierArr[p3Tier], p3Exclude);
		
		System.out.println("Player 1, you are " + p1Char + ", tier " + tierToChar(p1Tier));
		System.out.println("Player 2, you are " + p2Char + ", tier " + tierToChar(p2Tier));
		System.out.println("Player 3, you are " + p3Char + ", tier " + tierToChar(p3Tier));

	}
	
	private static String getChar(String[] tierList, ArrayList<String> exclude) {
		String character = "";
		while(character.equals("") || exclude.contains(character)) {
			int charIndex = rand.nextInt(tierList.length);
			character = tierList[charIndex];
		}
		
		return character;
	}
	
	private static String tierToChar(int tier) {
		if(tier == 0) {
			return "S";
		}
		else if(tier == 1) {
			return "A";
		}
		else if(tier == 2) {
			return "B";
		}
		else if(tier == 3) {
			return "C";
		}
		else {
			return "Lower";
		}
	}

}
