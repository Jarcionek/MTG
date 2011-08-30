package mtg;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Jaroslaw Pawlak
 */
public class Deck implements Serializable {
    private transient File directory;
    private ArrayList<String> names;
    private ArrayList<Integer> amounts;

    public Deck(String path) {
        directory = new File(path);
        names = new ArrayList<String>(15);
        amounts = new ArrayList<Integer>(15);
    }

    public File getDirectory() {
        return directory;
    }

    public boolean isCardInDeck(String name) {
        return names.contains(name);
    }

    /**
     * @param name card's name
     * @return how many cards of given name are in the deck
     */
    public int getCardInstances(String name) {
        return amounts.get(names.indexOf(name));
    }

    public boolean addCard(String name, int amount) {
//        check(name);
        if (!isCardInDeck(name)) {
            if (isBasicLand(name) || amount <= 4) {
                names.add(name);
                amounts.add(amount);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void setCardAmount(String name, int amount) {
        if (!isCardInDeck(name)) {
            addCard(name, amount);
        } else {
            amounts.set(names.indexOf(name), amount);
        }
    }

    /**
     * @return total number of cards
     */
    public int getDeckSize() {
        int r = 0;
        for (int i = 0; i < amounts.size(); i++) {
            r += amounts.get(i);
        }
        return r;
    }

    public int getArraySize() {
        return names.size();
    }

    public String getArrayNames(int i) {
        return names.get(i);
    }

    public int getArrayAmounts(int i) {
        return amounts.get(i);
    }

    private boolean isBasicLand(String name) {
        return name.toLowerCase().equals("plains")
                || name.toLowerCase().equals("island")
                || name.toLowerCase().equals("swamp")
                || name.toLowerCase().equals("mountain")
                || name.toLowerCase().equals("forest");
    }

    private void check(String name) {
        for (File e : directory.listFiles()) {
            if (name.equals(e.getName().substring(0, e.getName().lastIndexOf(".")))) {
                return;
            }
        }
        throw new IllegalArgumentException("Image not found: " + name);
    }
}
