package mtg;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Jaroslaw Pawlak
 */
public final class Library implements Serializable {
    private ArrayList<Card> cards;

    private Library() {};

    public Library(Deck deck) {
        cards = new ArrayList<Card>(deck.getDeckSize());

        for (int i = 0; i < deck.getArraySize(); i++) {
            String path = deck.getArrayFiles(i).getPath();
            for (int j = 0; j < deck.getArrayAmounts(i); j++) {
                cards.add(new Card(path));
            }
        }

        shuffle();
    }

    private String findPath(File directory, String name) {
        for (File e : directory.listFiles()) {
            if (e.isFile() && Utilities.getName(e).equals(name)) {
                return e.getPath();
            }
            if (e.isDirectory()) {
                String x = findPath(e, name);
                if (x != null) {
                    return x;
                }
            }
        }
        return null;
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

    public void tempprint() {
        for (Card e : cards) {
            System.out.println(e);
        }
    }
}
