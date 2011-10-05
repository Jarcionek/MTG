package server.flags;

/**
 * @author Jaroslaw Pawlak
 *
 * This object may be sent by both client or server and requests the other
 * side to send a requested card.
 */
public class RequestCard extends Action {
    public String name;

    public RequestCard(String name) {
        super(-1);
        this.name = name;
    }

    @Override
    public String toString() {
        return super.toString() + ", name = " + name + ")";
    }

}
