package Routing;

/**
 * @author stephan
 */
public class RoutingEntry {
	public final String clientIdentifier;
	public String nextHopIdentifier;
	public int linkCost;

	public RoutingEntry(String ci, String ni, int lc){
		this.clientIdentifier = ci;
		this.nextHopIdentifier = ni;
		this.linkCost = lc;
	}

	public String toString(){
		return "| "  + clientIdentifier + " | " + nextHopIdentifier + " | " + linkCost + " |";
	}
}
