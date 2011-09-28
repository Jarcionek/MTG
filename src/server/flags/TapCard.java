package server.flags;

/**
 * @author Jaroslaw Pawlak
 */
public class TapCard extends Action {
    public int requestor;
    public String ID;
    public boolean tapped;

    public TapCard(int requestor, String ID, boolean tapped) {
        this.requestor = requestor;
        this.ID = ID;
        this.tapped = tapped;
    }

}
