package data;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

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
	 * Constructs a new <code>TierList</code> using the specified file.
	 * 
	 * @param fileName	The tier list file to load data from.
	 */
	public TierList(String fileName) {
		//start by initializing everything to default values. this way we
		//can just override stuff that's in the file
		this();
	}

}
