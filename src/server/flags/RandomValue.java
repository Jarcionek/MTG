package server.flags;

/**
 * @author Jaroslaw Pawlak
 * 
 * Random value returned by server
 */
public class RandomValue extends Action {
    public static final int COIN = 2;
    public static final int DIE = 6;
    
    public int value;
    public int max;
    
    public RandomValue(int max) {
        super(-1);
        this.max = max;
    }

    @Override
    public String toString() {
        return super.toString() + ", value = " + value + ", max = " + max + ")";
    }
    
}
