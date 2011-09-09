package flags;

import java.io.Serializable;

/**
 *
 * @author Jaroslaw Pawlak
 */
public class Action implements Serializable {
    @Override
    public String toString() {
        return this.getClass().getName().substring(
                this.getClass().getName().indexOf(".") + 1);
    }
}
