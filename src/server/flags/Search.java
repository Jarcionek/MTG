package server.flags;

import mtg.Zone;

/**
 * @author Jaroslaw Pawlak
 */
public class Search extends Action {
    /**
     * Determines how many from the top of the zone are to be sent
     */
    public int cards;
    public Zone zone;
    public int requestor;
}
