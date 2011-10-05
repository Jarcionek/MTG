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
    public int zoneOwner;

    public Search(int amount, String[] cardsIDs, Zone zone, int requestor, int zoneOwner) {
        super(requestor);
        this.amount = amount;
        this.cardsIDs = cardsIDs;
        this.zone = zone;
        this.zoneOwner = zoneOwner;
    }

    /**
     * zoneOwner = -1, cardsIDs = null
     */
    public Search(int cards, Zone zone, int requestor) {
        this(cards, null, zone, requestor, -1);
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
        return super.toString() + ", amount = " + amount + ", cardsIDs = " + x
                + ", zone = " + zone + ")";
    }

}
