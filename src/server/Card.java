package server;

/**
 * @author Jaroslaw Pawlak
 */
public class Card {
    String name;
    String ID;

    private Card() {}

    Card(String name) {
        this.name = name;
    }

    Card(String name, String ID) {
        this.name = name;
        this.ID = ID;
    }
    
    @Override
    public String toString() {
        return getClass().getName() + "(name = " + name + ", ID = " + ID + ")";
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null
                && obj.getClass().equals(Card.class)
                && ((Card) obj).ID.equals(this.ID);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.ID != null ? this.ID.hashCode() : 0);
        return hash;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new Card(name, ID);
    }

}
