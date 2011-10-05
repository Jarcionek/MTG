package server.flags;

/**
 * @author Jaroslaw Pawlak
 *
 * Objects sent by clients to the server to confirm that they finished
 * downloading missing cards.
 */
public class Ready extends Action {

    public Ready() {
        super(-1);
    }

    @Override
    public String toString() {
        return super.toString() + ")";
    }
    
}
