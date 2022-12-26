package data;

/**
 * The <code>Fighter</code> class allows us to keep track of the fighters in
 * a tier list. Before v12, we simply kept lists of <code>String</code>s to
 * track fighters, but given that a fighter has both a name and a tier, it
 * makes more sense to have an object for it.
 * 
 * @author Jordan Knapp
 *
 */
public class Fighter {
	
	private String name;
	private int tier;
	
	public Fighter(String name, int tier) {
		this.name = name;
		this.tier = tier;
	}

}
