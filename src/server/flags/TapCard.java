package server.flags;

/**
 * @author Jaroslaw Pawlak
 * 
 * Used to inform that a card has been tapped or untapped.
 */
public class TapCard extends Action {
    public String ID;
    public boolean tapped;

    public TapCard(String ID, boolean tapped) {
        super(-1);
        this.ID = ID;
        this.tapped = tapped;
    }

    @Override
    public String toString() {
        return super.toString() + ", ID = " + ID + ", tapped = " + tapped + ")";
    }

}
