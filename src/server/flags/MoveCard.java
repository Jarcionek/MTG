package server.flags;

/**
 * @author Jaroslaw Pawlak
 */
public class MoveCard extends Action {
    public static final int TABLE = 0;
    public static final int HAND = 1;
    public static final int GRAVEYARD = 2;
    public static final int EXILED = 3;
    public static final int LIBRARY = 4;
    public static final int TOP_LIBRARY = 5;

    public int source;
    public int destination;
    public int requestor;
    public String cardID;
    public boolean reveal;

    public MoveCard(int source, int destination, int requestor,
            String cardID, boolean reveal) {
        this.source = source;
        this.destination = destination;
        this.requestor = requestor;
        this.cardID = cardID;
        this.reveal = reveal;
    }

    public MoveCard(int source, int destination, int requestor, String cardID) {
        this(source, destination, requestor, cardID, true);
    }

}
