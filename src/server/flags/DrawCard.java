package server.flags;

/**
 * @author Jaroslaw Pawlak
 */
public class DrawCard extends Action {
    public int requestor;
    public String cardID;

    public DrawCard(int requestor, String cardID) {
        this.requestor = requestor;
        this.cardID = cardID;
    }
}
