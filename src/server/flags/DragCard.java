package server.flags;

/**
 * @author Jaroslaw Pawlak
 */
public class DragCard extends Action {
    public int requestor;
    public String ID;
    public int newxpos;
    public int newypos;

    public DragCard(int requestor, String ID, int newxpos, int newypos) {
        this.requestor = requestor;
        this.ID = ID;
        this.newxpos = newxpos;
        this.newypos = newypos;
    }

}
