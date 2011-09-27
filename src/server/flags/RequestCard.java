package server.flags;

/**
 *
 * @author Jaroslaw Pawlak
 */
public class RequestCard extends Action {
    public String name;

    public RequestCard(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + name + ")";
    }

}
