package state;

public final class Settings {
	
	public static int numPlayers;
	
	public static int[] tierChances;
	public static int[] bumpChances;
	
	public static boolean allowSInCannotGet;
	public static boolean allowSSInCannotGet;
	
	public static int cannotGetSize;
	
	/**
	 * Initialize the settings to their default values:
	 * <ul>
	 * 	<li><code>numPlayers</code> = 2</li>
	 * 	<li>Tier Chances:
	 * 		<ul>
	 * 			<li>SS: 10%</li>
	 * 			<li>S:	20%</li>
	 * 			<li>A:	25%</li>
	 * 			<li>B:	25%</li>
	 * 			<li>C:	20%</li>
	 * 			<li>D:	 0%</li>
	 * 			<li>E:	 0%</li>
	 * 			<li>F:	 0%</li>
	 * 		</ul></li>
	 * 	<li>Bump chances:
	 * 		<ul>
	 * 			<li>Up 0: 50%</li>
	 * 			<li>Up 1: 25%</li>
	 * 			<li>Up 2: 25%</li>
	 * 		</ul></li>
	 * 	<li><code>allowSSInCannotGet</code> = <code>false</code></li>
	 * 	<li><code>allowSInCannotGet</code> = <code>false</code></li>
	 * 	<li><code>cannotGetSize</code> = 5</li>
	 * </ul>
	**/
	public static void init() {
		numPlayers = 2;
		
		tierChances = new int[] {10, 20, 25, 25, 20, 0, 0, 0};
		bumpChances = new int[] {50, 25, 25};
		
		allowSInCannotGet = false;
		allowSSInCannotGet = false;
		
		cannotGetSize = 5;
	}
}
