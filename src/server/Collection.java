package server;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Jaroslaw Pawlak
 */
class Collection {
    private ArrayList<Card> cards;

    Collection() {
        cards = new ArrayList<Card>();
    }

    void addCard(Card card) {
        cards.add(card);
    }

    Card removeCard(Card card) {
        int index = cards.indexOf(card);
        return cards.remove(index);
    }

    Card removeCard(String ID) {
        return removeCard(new Card(null, ID));
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
        if (cards.size() == 0) {
            return null;
        } else {
            return cards.get(cards.size() - 1);
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
}
