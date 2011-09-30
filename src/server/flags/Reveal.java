package server.flags;

import mtg.Zone;

/**
 * @author Jaroslaw Pawlak
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
