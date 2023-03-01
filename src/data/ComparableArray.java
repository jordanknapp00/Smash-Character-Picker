package data;

import util.Util;

//TODO: redo all the javadoc

/**
 * The <code>ComparableArray</code> is actually not an array... or is it? It's
 * an object meant to be used in an array to make it easy to sort the columns
 * of a 2D array. The <code>ComparableArray</code> represents one row of a
 * table, essentially. By implementing the <code>Comparable</code> interface,
 * it becomes possible to sort an array of <code>ComparableArray</code>s by the
 * values of a particular column.
 * 
 * @author Jordan
 *
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
	private double winrate;
	
	public ComparableArray(Fighter fighter, int compareType) {
		//basically, converting an index from the array defined in the sort
		//button's action listener to a value of CompareType. the first option
		//is fighter's overall winrate, but the second option is player's
		//overall winrate, which isn't handled here. so knowing that 1 will
		//never be passed in, any other value can be subtracted by 1 to get
		//the enum value
		this(fighter, compareType == 0 ? CompareType.FIGHTER_OVERALL_WINRATE : CompareType.values()[compareType - 1]);
	}
	
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
		
		if(battles != 0) {
			winrate = (double) wins / battles;
		}
		else {
			winrate = -1;
		}
	}
	
	/**
	 * @return	The entry in the first column, the name of the character
	 * 			represented in this row.
	 */
	private String getName() {
		return fighter.getName();
	}
	
	/**
	 * @return	The entry in the third column, the number of battles this character
	 * 			has participated in.
	 */
	private int getBattles() {
		return battles;
	}
	
	/**
	 * @return	The winrate for this character, or <code>wins</code>/<code>battles</code>.
	 */
	private double getWinrate() {
		return winrate;
	}

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
		else if(battles == 0 && !(o.getBattles() == 0)) {
			return -1;
		}
		else if(battles != 0 && o.getBattles() == 0) {
			return 1;
		}
		
		int doubComp = Double.compare(winrate, o.getWinrate());
		
		if(doubComp == 0) {
			int batComp = Integer.compare(battles, o.getBattles());
			
			if(batComp == 0) {
				return -(fighter.getName().compareTo(o.getName()));
			}
			
			return batComp;
		}
		
		return doubComp;
	}
	
	public String toString() {
		if(compareType == CompareType.TOTAL_BATTLES) {
			return fighter.getName() + " - " + battles + " battles";
		}
		
		return fighter.getName() + " - " + Util.printDouble(winrate * 100) + "% (" + wins + "/" + battles + ")";
	}

}