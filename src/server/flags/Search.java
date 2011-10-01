package server.flags;

import mtg.Zone;

/**
 * @author Jaroslaw Pawlak
 */
public class Search extends Action {
    /**
     * Determines how many from the top of the zone are to be sent
     */
    public int amount;
    public String[] cardsIDs;
    public Zone zone;
    public int requestor;

    public Search(int cards, String[] cardsID, Zone zone, int requestor) {
        this.amount = cards;
        this.cardsIDs = cardsID;
        this.zone = zone;
        this.requestor = requestor;
    }

    public Search(int cards, Zone zone, int requestor) {
        this(cards, null, zone, requestor);
    }

    @Override
    public String toString() {
        String x = "{";
        if (cardsIDs != null) {
            for (String e : cardsIDs) {
                x += e + ",";
            }
        }
        x += "}";
        x = x.replace(",}", "}");
        return super.toString() + "(amount=" + amount + ",cardsIDs=" + x
                + ",zone=" + zone + ",requestor=" + requestor;
    }

}
