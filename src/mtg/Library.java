package mtg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Jaroslaw Pawlak
 */
public final class Library implements Serializable {
    private ArrayList<Card> cards;

    public Library() {
        cards = new ArrayList<>(15);
    };

    public Library(Deck deck) {
        cards = new ArrayList<>(deck.getDeckSize());

        for (int i = 0; i < deck.getArraySize(); i++) {
            String path = deck.getArrayFiles(i).getPath();
            for (int j = 0; j < deck.getArrayAmounts(i); j++) {
                cards.add(new Card(path));
            }
        }

        shuffle();
    }

    public int getSize() {
        return cards.size();
    }

    public void shuffle() {
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

    public Card draw() {
        if (cards.isEmpty()) {
            return null;
        } else {
            return cards.remove(cards.size() - 1);
        }
    }

    public void addCardOnTop(Card card) {
        cards.add(card);
    }

    public void addCardOnBottom(Card card) {
        cards.add(0, card);
    }
}
