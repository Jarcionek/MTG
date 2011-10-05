package server.flags;

/**
 * @author Jaroslaw Pawlak
 * 
 * Informs a server that a client has been intentionally disconnected.
 */
public class Disconnect extends Action {
    public boolean intentional;

    public Disconnect(int player, boolean intentional) {
        super(player);
        this.intentional = intentional;
    }

    @Override
    public String toString() {
        return super.toString() + ", intentional = "
                + intentional + ")";
    }
    
}
