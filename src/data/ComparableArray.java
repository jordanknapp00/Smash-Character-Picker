package data;

import util.Util;

/**
 * Despite its name, <code>ComparableArray</code> is not itself an array. It's
 * a class that implements <code>Comparable</code>, designed to be used in
 * an array to make sorting the fighters of a tier list easier. When creating
 * an instance of <code>ComparableArray</code>, either an integer or a
 * <code>CompareType</code> is passed in. This will determine what exactly
 * is being sorted on in the <code>compareTo()</code> method. The result is
 * that <code>Arrays.sort()</code> can be used to sort a list of
 * <code>Fighter</code> objects on a variety of fields.
 * <br><br>
 * The following fields can be sorted on:
 * <ul>
 * 	<li>Each fighter's overall winrate</li>
 * 	<li>The winrate of each player (1 through 8) as each fighter</li>
 * 	<li>The total number of battles each fighter has been in</li>
 * </ul>
 * 
 * @author Jordan Knapp
 */
public class ComparableArray implements Comparable<ComparableArray> {
	
	enum CompareType {
		FIGHTER_OVERALL_WINRATE,
		P1_WINRATE,
		P2_WINRATE,
		P3_WINRATE,
		P4_WINRATE,
		P5_WINRATE,
		P6_WINRATE,
		P7_WINRATE,
		P8_WINRATE,
		TOTAL_BATTLES
	};
	
	private CompareType compareType;
	private Fighter fighter;
	
	private int wins;
	private int battles;
	
	/**
	 * Creates a <code>ComparableArray</code> containing the given
	 * <code>Fighter</code>, as well as the <code>CompareType</code>
	 * represented by the given integer. The expected int values are based
	 * on the dropdown used in <code>MainWindow</code> to create
	 * <code>ComparableArray</code>s.
	 * <br><br>
	 * The value of <code>compareType</code> roughly corresponds to the index
	 * of the values in the <code>CompareType</code> enum, with one caveat.
	 * In <code>MainWindow</code>, the second entry in the dropdown (index 1)
	 * represents each player's overall winrate, irrespective of the fighters
	 * in the tier list. That functionality does not use <code>ComparableArray</code>.
	 * As such, if 0 is passed in as <code>compareType</code>, the first
	 * <code>CompareType</code>, <code><i><b>FIGHTER_OVERALL_WINRATE</b></i></code>,
	 * is used. Otherwise, the index of the <code>CompareType</code> used
	 * corresponds to <code>compareType</code> minus 1.
	 * <br><br>
	 * It is not expected that 1 will be passed into this constructor. If it
	 * is, however, sorting will be done by each fighter's overall winrate,
	 * as if 0 had been passed in.
	 * 
	 * @param fighter		The <code>Fighter</code> that is represented in
	 * 						this index of the array.
	 * @param compareType	The type of comparison to be used. See above for
	 * 						how it works.
	 */
	public ComparableArray(Fighter fighter, int compareType) {
		//basically, converting an index from the array defined in the sort
		//button's action listener to a value of CompareType. the first option
		//is fighter's overall winrate, but the second option is player's
		//overall winrate, which isn't handled here. so knowing that 1 will
		//never be passed in, any other value can be subtracted by 1 to get
		//the enum value
		this(fighter, compareType == 0 ? CompareType.FIGHTER_OVERALL_WINRATE : CompareType.values()[compareType - 1]);
	}
	
	/**
	 * Create a <code>ComparableArray</code> with the given fighter and
	 * <code>CompareType</code>.
	 * 
	 * @param fighter		The <code>Fighter</code> that is represented in
	 * 						this index of the array.
	 * @param compareType	The type of comparison to be used.
	 */
	public ComparableArray(Fighter fighter, CompareType compareType) {
		this.fighter = fighter;
		this.compareType = compareType;
		
		//below we'll create the logic for getting the wins and battles
		int playerToGet = -1;
		
		switch(compareType) {
		//for both total battles and fighter's overall winrate, we just want
		//the total wins and battles. for total battles, we'll only use that
		//value. otherwise, we'll calculate the winrate
		case TOTAL_BATTLES:
		case FIGHTER_OVERALL_WINRATE:
			wins = fighter.getTotalWins();
			battles = fighter.getTotalBattles();
			break;
		//for individual players, we'll just get the number here and use that
		//below to get the winrate
		case P1_WINRATE:
			playerToGet = 0;
			break;
		case P2_WINRATE:
			playerToGet = 1;
			break;
		case P3_WINRATE:
			playerToGet = 2;
			break;
		case P4_WINRATE:
			playerToGet = 3;
			break;
		case P5_WINRATE:
			playerToGet = 4;
			break;
		case P6_WINRATE:
			playerToGet = 5;
			break;
		case P7_WINRATE:
			playerToGet = 6;
			break;
		case P8_WINRATE:
			playerToGet = 7;
		}
		
		if(playerToGet != -1) {
			wins = fighter.getPlayerWins(playerToGet);
			battles = fighter.getPlayerBattles(playerToGet);
		}
	}
	
	/**
	 * A method needed to allow comparing <code>Fighter</code>'s names.
	 * 
	 * @return	The entry in the first column, the name of the character
	 * 			represented in this row.
	 */
	private String getName() {
		return fighter.getName();
	}
	
	/**
	 * A method needed to allow comparing <code>Fighter</code>'s number of
	 * battles.
	 * 
	 * @return	The entry in the third column, the number of battles this character
	 * 			has participated in.
	 */
	private int getBattles() {
		return battles;
	}
	
	/**
	 * A method needed to allow comparing <code>Fighter</code>'s winrates.
	 * 
	 * @return	The winrate for this character, or <code>wins</code>/<code>battles</code>.
	 */
	private double getWinrate() {
		return (float) wins / battles;
	}

	/**
	 * Compares this object with the given object. Exactly how they are
	 * compared depends on this instance of <code>ComparableArray</code>'s
	 * <code>compareType</code> field. Ties are broken in the following way:
	 * <br><br>
	 * <ul>
	 * 	<li>First, if comparing only by the total number of battles, ties
	 * 		are broken using alphabetical order.
	 * 	</li>
	 * 	<li>Otherwise, any fighter that has 0 battles is considered to be
	 * 		"less than" the other fighter. If both havve 0 battles, they are
	 * 		considered equal, no matter which field is being compared on.
	 * 	</li>
	 * 	<li>If both fighters have battles, but an equal winrate, the one
	 * 		with fewer battles is considered "less than" the other.
	 * 	</li>
	 * 	<li>If both fighters have equal winrates and battles, then they are
	 * 		sorted in alphabetical order
	 * 	</li>
	 */
	public int compareTo(ComparableArray o) {		
		//handle battles comparison first
		if(compareType == CompareType.TOTAL_BATTLES) {
			//if number of battles are equal, sort by name
			if(battles == o.getBattles()) {
				return -(fighter.getName().compareTo(o.getName()));
			}
			
			//otherwise, sort by number of battles. inverted because we want
			//the most battles to go at the top
			return -Integer.compare(battles, o.getBattles());
		}
		
		if(battles == 0 && o.getBattles() == 0) {
			return 0;
		}
		else if(battles == 0 && o.getBattles() != 0) {
			return 1;
		}
		else if(battles != 0 && o.getBattles() == 0) {
			return -1;
		}
		
		int doubComp = -Double.compare(getWinrate(), o.getWinrate());
		
		if(doubComp == 0) {
			int batComp = Integer.compare(battles, o.getBattles());
			
			if(batComp == 0) {
				return -(fighter.getName().compareTo(o.getName()));
			}
			
			return batComp;
		}
		
		return doubComp;
	}
	
	/**
	 * If sorting by total number of battles, a string is output containing
	 * the <code>Fighter</code>'s name and the number of battles. If sorting
	 * by anything else, the string will contain the winrate as well as
	 * ratio of wins to losses represented by a fraction.
	 */
	public String toString() {
		if(compareType == CompareType.TOTAL_BATTLES) {
			return fighter.getName() + " - " + battles + " battles";
		}
		
		return fighter.getName() + " - " + Util.printDouble(getWinrate() * 100) + "% (" + wins + "/" + battles + ")";
	}

}