package server.flags;

/**
 * @author Jaroslaw Pawlak
 *
 * Used to change either player's health or the number of his poison counters.
 */
public class Player extends Action {
    public static final int HEALTH = 0;
    public static final int POISON = 1;

    public int requestor;
    public int target;
    public int newValue;
    public int poisonOrHealth;

    public Player(int requestor, int target, int newValue, int poisonOrHealth) {
        this.requestor = requestor;
        this.target = target;
        this.newValue = newValue;
        this.poisonOrHealth = poisonOrHealth;
    }
}
