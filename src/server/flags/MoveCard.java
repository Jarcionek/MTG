package server.flags;

import mtg.Zone;

/**
 * @author Jaroslaw Pawlak
 *
 * This object denotes that a card has been moved between different game zones.
 */
public class MoveCard extends Action {
    public Zone source;
    public Zone destination;
    public int requestor;
    public String cardID;
    /**
     * it is only used by events sent by client
     */
    public boolean reveal;

    /**
     * @param reveal determines if card should be shown to other players than
     * requestor
     */
    public MoveCard(Zone source, Zone destination, int requestor,
            String cardID, boolean reveal) {
        this.source = source;
        this.destination = destination;
        this.requestor = requestor;
        this.cardID = cardID;
        this.reveal = reveal;
    }

    /**
     * reveal = true
     */
    public MoveCard(Zone source, Zone destination, int requestor, String cardID) {
        this(source, destination, requestor, cardID, true);
    }

}
