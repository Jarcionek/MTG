package server.flags;

/**
 * @author Jaroslaw Pawlak
 */
public class Shuffle extends Action {
    public Shuffle(int owner) {
        super(owner);
    }

    @Override
    public String toString() {
        return super.toString() + ")";
    }

}
