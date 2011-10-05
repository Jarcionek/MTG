package server.flags;

import mtg.Zone;

/**
 * @author Jaroslaw Pawlak
 *
 * This object denotes that a card has been moved between different game zones.
 * <p>
 * <code>source</code>, <code>destination</code>, <code>requestor</code> and
 * <code>cardID</code> are obligatory fields and each MoveCard object
 * must specify those. <code>reveal</code> determines if a card has to be shown
 * to other players - because table, graveyard and exiled zones are public,
 * it matters only when moving a card between player's library and hand, in
 * other cases this field is ignored.
 * <p>
 * Card moved to graveyard, hand, exiled or library from anywhere is always
 * moved into owner's zone. If player requests to move a card from graveyard
 * or exiled, owner can be calculated from card's ID.
 */
public class MoveCard extends Action {
    public Zone source;
    public Zone destination;
    public String cardID;
    /**
     * It is only used by events sent by client if moving a card between
     * his library and hand to determine if a card should be revealed to other
     * players.
     */
    public boolean reveal;

    /**
     * @param reveal determines if card should be shown to other players than
     * requestor
     */
    public MoveCard(Zone source, Zone destination, int requestor,
            String cardID, boolean reveal) {
        super(requestor);
        this.source = source;
        this.destination = destination;
        this.cardID = cardID;
        this.reveal = reveal;
    }

    /**
     * reveal = true
     * @see #MoveCard(Zone, Zone, int, String, boolean)
     */
    public MoveCard(Zone source, Zone destination, int requestor, String cardID) {
        this(source, destination, requestor, cardID, true);
    }

    @Override
    public String toString() {
        return super.toString() + ", source = " + source + ", destination = "
                + destination + ", cardID = " + cardID + ")";
    }

}
