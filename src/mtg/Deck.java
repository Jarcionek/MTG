package mtg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Jaroslaw Pawlak
 */
public class Deck implements Serializable {
    private ArrayList<String> names;
    private ArrayList<Integer> amounts;
    private transient ArrayList<File> paths;

    public Deck() {
        names = new ArrayList<String>(15);
        amounts = new ArrayList<Integer>(15);
        paths = new ArrayList<File>(15);
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
        return addCard(name, amount, null);
    }

    /**
     * Adds card to the deck.
     * @param name Card to be added
     * @param amount amount to be added
     * @param path optional file path
     * @return true if card has been added
     * @throws IllegalArgumentException if amount <= 0
     */
    public boolean addCard(String name, int amount, File path)
            throws IllegalArgumentException{
        if (amount <= 0) {
            throw new IllegalArgumentException("Adding " + amount + " cards");
        }

        if (checkCard(name) == 0) { //not in deck
            names.add(name);
            amounts.add(amount);
            paths.add(path);
            return true;
        }

        int index = names.indexOf(name);
        int previousValue = amounts.get(index);
        amounts.set(index, amounts.get(index) + amount);
        if (checkCard(name) == 3) {
            amounts.set(index, previousValue);
            return false;
        }
        return true;
    }

    /**
     * Removes all cards of given name
     * @param name card to be removed
     * @return true if a card was in a deck, false otherwise
     */
    public boolean removeCard(String name) {
        int t = names.indexOf(name);
        if (t != -1) {
            names.remove(t);
            amounts.remove(t);
            paths.remove(t);
            return true;
        }
        return false;
    }

    /**
     * Removes given amount of given cards from the deck
     * @param name card to be removed
     * @param amount amount to be removed
     * @return true if card's amount has been decreased, false otherwise
     */
    public boolean removeCard(String name, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Removing " + amount + " cards");
        }

        if (checkCard(name) == 0) { //not in deck
            return false;
        }

        int index = names.indexOf(name);
        amounts.set(index, amounts.get(index) - amount);
        switch (checkCard(name)) {
            case 1:
            case 2:
                removeCard(name);
        }
        return true;
    }
    
    /**
     * Returns:<p>
     * 0 - if card is not in a deck<p>
     * 1 - if card has negative amount<p>
     * 2 - if card is in a deck with amount equal 0<p>
     * 3 - if card is not a basic land type and its amount is greater than 4<p>
     * 4 - if card is a basic land type or its amount is smaller or equal 4
     */
    private int checkCard(String name) {
        int t = names.indexOf(name);
        if (t == -1) {
            return 0;
        }
        if (amounts.get(t) < 0) {
            return 1;
        }
        if (amounts.get(t) == 0) {
            return 2;
        }
        if (!isBasicLand(name) && amounts.get(t) > 4) {
            return 3;
        }
        return 4;
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

    public File getArrayFiles(int i) {
        if (paths == null) {
            paths = new ArrayList<File>(getArraySize());
        }
        for (int j = paths.size(); j < getArraySize(); j++) {
            paths.add(j, null);
        }
        File t;
        if ((t = paths.get(i)) == null || !t.exists()) {
            t = new File(Utilities.findPath(Main.CARDS, names.get(i)));
            paths.set(i, t);
        }
        return t;
    }

    private boolean isBasicLand(String name) {
        return name.toLowerCase().equals("plains")
                || name.toLowerCase().equals("island")
                || name.toLowerCase().equals("swamp")
                || name.toLowerCase().equals("mountain")
                || name.toLowerCase().equals("forest");
    }

    /**
     * Saves this deck to the text file given
     * @param file file to save a deck
     * @return true if save succeeded, false otherwise
     */
    public boolean save(File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            Writer bf = new BufferedWriter(new FileWriter(file));
            for (int i = 0; i < names.size(); i++) {
                bf.write(names.get(i) + ";"
                        + amounts.get(i) + ";"
                        + paths.get(i) + "\r\n");
            }
            bf.close();
        } catch (IOException ex) {
            Debug.p("Deck could not be saved to file "
                    + file + ": " + ex, Debug.E);
            return false;
        }
        return true;
    }

    /**
     * Loads a deck from text file given
     * @param file file to load a deck from
     * @return deck or null if loading failed
     */
    public static Deck load(File file) {
        try {
            Scanner in = new Scanner(file);
            Deck result = new Deck();
            String line;
            while (in.hasNextLine()) {
                String[] t = (line = in.nextLine()).split(";");
                try {
                    result.addCard(t[0], Integer.parseInt(t[1]), new File(t[2]));
                } catch (Exception ex) {
                    Debug.p("Ignored line while loading a deck: " + line, Debug.W);
                }
            }
            in.close();
            return result;
        } catch (Exception ex) {
            Debug.p("Deck could not be loaded from file "
                    + file + ": " + ex, Debug.E);
        }
        return null;
    }
}
