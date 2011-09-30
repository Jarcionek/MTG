package server.flags;

import mtg.Zone;

/**
 * @author Jaroslaw Pawlak
 */
public class MoveCard extends Action {
    public Zone source;
    public Zone destination;
    public int requestor;
    public String cardID;
    public boolean reveal;

    public MoveCard(Zone source, Zone destination, int requestor,
            String cardID, boolean reveal) {
        this.source = source;
        this.destination = destination;
        this.requestor = requestor;
        this.cardID = cardID;
        this.reveal = reveal;
    }

    public MoveCard(Zone source, Zone destination, int requestor, String cardID) {
        this(source, destination, requestor, cardID, true);
    }

}
