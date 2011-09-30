package server.flags;

import mtg.Zone;

/**
 * @author Jaroslaw Pawlak
 *
 * This object detones that a player has revelaed a card to all other players
 * from specified zone.
 */
public class Reveal extends Action {
    public Zone source;
    public int requstor;
    public String cardID;

    public Reveal(Zone source, int requstor, String cardID) {
        this.source = source;
        this.requstor = requstor;
        this.cardID = cardID;
    }
}
