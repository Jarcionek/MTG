package server;

import java.util.ArrayList;
import java.util.Random;
import mtg.Debug;

/**
 * @author Jaroslaw Pawlak
 */
class Collection {
    private ArrayList<Card> cards;

    Collection() {
        cards = new ArrayList<>();
    }

    void addCard(Card card) {
        if (card != null) {
            cards.add(card);
        }
    }

    Card removeCard(String ID) {
        cards.remove(new Card(null, ID));
        if (ID.charAt(1) == 'X') {
            return null;
        } else {
            return new Card(null, ID);
        }
    }

    Card get(int i) {
        return cards.get(i);
    }

    int getSize() {
        return cards.size();
    }

    /**
     * Returns the last card in the collection or null if a collection is empty.
     * @return the last card in the collection or null if a collection is empty
     */
    Card getLast() {
        if (cards.isEmpty()) {
            return null;
        } else {
            return cards.get(cards.size() - 1);
        }
    }

    /**
     * Returns <code>amount</code> cards the most recently added to the
     * collection. If amount = -1 then all cards are returned. For other
     * negative amounts and 0 - empty array is returned. If amount
     * is greater than a collection's size, all cards are returned.
     * @param amount the number of cards to be returned
     * @return array containing <code>amount</code> cards from the top of
     * a collection
     */
    Card[] getLast(int amount) {
        if (amount == -1 || amount >= cards.size()) {
            Card[] r = new Card[cards.size()];
            return cards.toArray(r);
        } else if (amount == 0 || amount < -1) {
            return new Card[0];
        } else {
            Card[] x = new Card[amount];
            for (int i = 0; i < amount; i++) {
                try {
                    x[i] = (Card) cards.get(cards.size() - 1 - i).clone();
                } catch (CloneNotSupportedException ex) {
                    //this should never happen
                    Debug.p(ex + " while cloning card", Debug.E);
                }
            }
            return x;
        }
    }

    Card removeLast() {
        return cards.remove(cards.size() - 1);
    }

    boolean contains(String cardID) {
        return cards.contains(new Card(null, cardID));
    }

    void shuffle() {
        Random r = new Random();
        int n;
        Card t;
        for (int i = 0; i < cards.size(); i++) {
            n = r.nextInt(cards.size());
            t = cards.get(i);
            cards.set(i, cards.get(n));
            cards.set(n, t);
        }
    }
    
    /**
     * Moves all cards owned by requested player from <code>this</code>
     * collection to <code>c</code> collection.
     * @param c target collection
     * @param player player
     */
    void transferCardsTo(Collection c, int player) {
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).ID.charAt(1) == 'X') { //token
                cards.remove(i--);
            } else if (cards.get(i).ID.charAt(0) == player + 'A') {
                c.addCard(cards.remove(i--));
            }
        }
    }
}
