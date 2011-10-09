package server.flags;

/**
 * @author Jaroslaw Pawlak
 * 
 * For choosing a random card from hand.
 */
public class RandomCard extends Action {
    public String cardID;
    
    public RandomCard() {
        super(-1);
    }

    @Override
    public String toString() {
        return super.toString() + ", cardID = " + cardID + ")";
    }
    
}
