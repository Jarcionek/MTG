package server.flags;

import mtg.Zone;

/**
 * @author Jaroslaw Pawlak
 *
 * This object denotes that a player has revealed a card to all other players
 * from specified zone.
 */
public class Reveal extends Action {
    public Zone source;
    public String cardID;

    public Reveal(Zone source, String cardID) {
        super(-1);
        this.source = source;
        this.cardID = cardID;
    }

    @Override
    public String toString() {
        return super.toString() + ", source = " + source + ", cardID = " + cardID + ")";
    }
}
