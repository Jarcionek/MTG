package server.flags;

/**
 * @author Jaroslaw Pawlak
 *
 * This object denotes a card dragging event and contains new coordinates
 * of this card.
 */
public class DragCard extends Action {
    public String ID;
    public int newxpos;
    public int newypos;

    public DragCard(String ID, int newxpos, int newypos) {
        super(-1);
        this.ID = ID;
        this.newxpos = newxpos;
        this.newypos = newypos;
    }

    @Override
    public String toString() {
        return super.toString() + ", ID = " + ID + ", newxpos = " + newxpos
                + ", newypos = " + newypos + ")";
    }

}
