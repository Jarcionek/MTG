package server.flags;

/**
 * @author Jaroslaw Pawlak
 * 
 * Denotes that the player shuffles all cards he or she owns into his or her
 * library and draws seven cards.
 */
public class Restart extends Action {
    public String[] IDs;
    public int deckSize;
    
    public Restart() {
        super(-1);
        IDs = new String[7];
    }
    
    @Override
    public String toString() {
        String x = "{";
        if (IDs != null) {
            for (String e : IDs) {
                x += e + ",";
            }
        }
        x += "}";
        x = x.replace(",}", "}");
        return super.toString() + ", IDs = " + x + ")";
    }
    
}
