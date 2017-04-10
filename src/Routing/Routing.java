package Routing;

/**
 * @author stephan
 */

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Routing {

	public static final int FREEZE_TIME = 3000;
	public static final int MAX_HOPS = 10;
	public static final int INFINITY = -1;

	private ConcurrentHashMap<String, RoutingEntry> routingTable;
	private ConcurrentHashMap<String, Long> frozenDestinations;
    public final String clientName;


	public Routing(String name) {
		this.clientName = name;
		this.routingTable = new ConcurrentHashMap<>();
		this.frozenDestinations = new ConcurrentHashMap<>();
		this.routingTable.put(clientName, new RoutingEntry(clientName, clientName, 0));
	}

    // Method to manually add routingtables, for testing
	public void addRoute(RoutingEntry e, String sender) {
		List<RoutingEntry> list = new LinkedList<>();
		list.add(e);
		addRoutes(list, sender);
	}

    // update routingtable with incoming routes from neighbour
	public void addRoutes(List<RoutingEntry> paths, String sender) {
		ListIterator<RoutingEntry> pathit = paths.listIterator();
		while (pathit.hasNext()) {
			RoutingEntry e = pathit.next();
            //checks if node no longer connected
			if (!e.nextHopIdentifier.equals(clientName)) {
				if (e.linkCost < 0 && !isFrozen(e.clientIdentifier)) {
					frozenDestinations.put(e.clientIdentifier, System.currentTimeMillis());
					Iterator<Map.Entry<String, RoutingEntry>> routit = routingTable.entrySet().iterator();
					while (routit.hasNext()) {
						Map.Entry<String, RoutingEntry> entry = routit.next();
						if (entry.getValue().clientIdentifier.equals(e.clientIdentifier) && entry.getValue().nextHopIdentifier.equals(sender)) {
							entry.getValue().linkCost = INFINITY;
						}
					}
				}
                //updates routingtable if it's a shorter route
				else if (!isFrozen(e.clientIdentifier) && e.linkCost < MAX_HOPS && e.linkCost >= 0) {
					if (!routingTable.containsKey(e.clientIdentifier)) {
						routingTable.put(e.clientIdentifier, new RoutingEntry(e.clientIdentifier, sender, e.linkCost + 1));
					} else if (routingTable.containsKey(e.clientIdentifier) &&
							(routingTable.get(e.clientIdentifier).linkCost > e.linkCost + 1
									|| (routingTable.get(e.clientIdentifier).linkCost == INFINITY))) {
						RoutingEntry re = routingTable.get(e.clientIdentifier);
						re.nextHopIdentifier = sender;
						re.linkCost = e.linkCost + 1;
					}
				}
			}
		}
	}

    //Print the current routingtable
	public Map<String, RoutingEntry> getRoutingTable() {
		return this.routingTable;
	}


    //remove a field from the routingtable
	public void removeField(String destination) {
		if (routingTable.containsKey(destination)) {
			routingTable.get(destination).linkCost = INFINITY;
			frozenDestinations.put(destination, System.currentTimeMillis());

			Iterator<Map.Entry<String, RoutingEntry>> routit = routingTable.entrySet().iterator();
			while (routit.hasNext()) {
				Map.Entry<String, RoutingEntry> route = routit.next();
				if (route.getValue().nextHopIdentifier.equals(destination)) {
					route.getValue().linkCost = INFINITY;
				}
			}
		}
	}

	public String getNextHop(String destination) {
		if (routeExists(destination)) {
			return routingTable.get(destination).nextHopIdentifier;
		}
		return null;
	}

	public boolean isFrozen(String destination) {
		return frozenDestinations.containsKey(destination) && frozenDestinations.get(destination) > System.currentTimeMillis() + FREEZE_TIME;
	}

	public boolean routeExists(String r) {
		return routingTable.containsKey(r) && routingTable.get(r).linkCost >= 0;
	}

	public Set<String> getAllAvailableDestinations() {
		return this.routingTable.entrySet().stream()
				.filter(entry -> entry.getValue().linkCost != INFINITY)
				.map(Map.Entry::getKey).collect(Collectors.toSet());
	}

    //testing purposes
	public List<RoutingEntry> getEntryList() {
		return routingTable.entrySet().stream()
				.map(e -> new RoutingEntry(
						e.getValue().clientIdentifier,
						e.getValue().nextHopIdentifier,
						e.getValue().linkCost))
				.collect(Collectors.toCollection(LinkedList::new));
	}


	public String toString() {
		String s = "ROUTING TABLE OF " + clientName + " \n";
		s += "| Destination | nexthop | linkcost |\n";
		for (Map.Entry<String, RoutingEntry> e : routingTable.entrySet()) {
			s += e.getValue() + "\n";
		}
		s += "----------------\n";
		return s;
	}
}
