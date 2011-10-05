package server.flags;

import java.io.Serializable;

/**
 *
 * @author Jaroslaw Pawlak
 */
public class Action implements Serializable {
    public int requestor;

    public Action(int requestor) {
        this.requestor = requestor;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName().substring(
                this.getClass().getName().indexOf(".") + 1)
                + "(req = " + requestor;
    }
}
