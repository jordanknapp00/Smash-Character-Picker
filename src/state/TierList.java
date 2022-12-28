package state;

import java.util.ArrayList;
import java.util.List;

import data.Fighter;
import util.Util;

/**
 * The <code>TierList</code> class is responsible for managing the tier list.
 * It has a private <code>List</code> of <code>List</code>s of
 * <code>Fighter</code>s. The data can be accessed with various static
 * methods.
 * 
 * @author Jordan Knapp
 *
 */
public final class TierList {

	private static List<List<Fighter>> tierList;
	
	/**
	 * Private constructor to prevent instantiating the <code>TierList</code>
	 * class.
	 */
	private TierList() {
		throw new UnsupportedOperationException("Cannot instantiate TierList class");
	}
	
	/**
	 * Initialize the <code>TierList</code> class, creating the internal
	 * <code>List</code>s that data is added to.
	 */
	public static void init() {
		tierList = new ArrayList<List<Fighter>>();
		
		for(int tierAt = 0; tierAt < 24; tierAt++) {
			tierList.add(new ArrayList<Fighter>());
		}
	}
	
	public static void addFighter(String name, int tier) {
		Fighter newFighter = new Fighter(name, tier);
		tierList.get(tier).add(newFighter);
	}
	
	public static List<Fighter> getTier(int tier) {
		return tierList.get(tier);
	}
	
	public static int tierSize(int tier) {
		return tierList.get(tier).size();
	}
	
	@Override
	public String toString() {
		StringBuilder retString = new StringBuilder("Tier List:\n");
		
		for(int tierAt = 0; tierAt < 24; tierAt++) {
			retString.append("Tier " + Util.tierToString(tierAt) + ": ");
			List<Fighter> at = tierList.get(tierAt);
			
			for(int fighterAt = 0; fighterAt < at.size() - 1; fighterAt++) {
				retString.append(at.get(fighterAt).toString() + ", ");
			}
			retString.append(at.get(at.size() - 1).toString() + "\n");
		}
		
		return retString.toString();
	}
}
