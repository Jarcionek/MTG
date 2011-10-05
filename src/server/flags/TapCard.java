package server.flags;

/**
 * @author Jaroslaw Pawlak
 */
public class TapCard extends Action {
    public String ID;
    public boolean tapped;

    public TapCard(int requestor, String ID, boolean tapped) {
        super(requestor);
        this.ID = ID;
        this.tapped = tapped;
    }

    @Override
    public String toString() {
        return super.toString() + ", ID = " + ID + ", tapped = " + tapped + ")";
    }

}
