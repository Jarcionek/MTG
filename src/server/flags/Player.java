package server.flags;

/**
 * @author Jaroslaw Pawlak
 *
 * Used to change either player's health or the number of his poison counters.
 */
public class Player extends Action {
    public static final int HEALTH = 0;
    public static final int POISON = 1;

    public int target;
    public int newValue;
    public int poisonOrHealth;

    public Player(int target, int newValue, int poisonOrHealth) {
        super(-1);
        this.target = target;
        this.newValue = newValue;
        this.poisonOrHealth = poisonOrHealth;
    }

    @Override
    public String toString() {
        return super.toString() + ", target = "
                + target + ", newValue = " + newValue + ", poisonOrHealth = "
                + poisonOrHealth + ")";
    }
    
}
