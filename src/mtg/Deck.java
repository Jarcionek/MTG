package mtg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Jaroslaw Pawlak
 */
public class Deck implements Serializable {
    private String deckName;
    private ArrayList<String> names;
    private ArrayList<Integer> amounts;
    private transient ArrayList<File> paths;

    public Deck() {
        deckName = null;
        names = new ArrayList<>(15);
        amounts = new ArrayList<>(15);
        paths = new ArrayList<>(15);
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
        checkPaths();
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
        checkPaths();
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
        if (!Card.isBasicLand(name) && amounts.get(t) > 4) {
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
        checkPaths();
        File t;
        if ((t = paths.get(i)) == null || !t.exists()) {
            String path = Utilities.findPath(names.get(i));
            if (path == null) {
                Debug.p("Card \"" + names.get(i) + "\" not found", Debug.CE);
            }
            t = new File(path);
            paths.set(i, t);
        }
        return t;
    }

    /**
     * Returns deck name - specified at save/load time
     * @return deck name
     */
    public String getName() {
        return deckName;
    }

    /**
     * Saves this deck to the text file given
     * @param file file to save a deck
     * @return true if save succeeded, false otherwise
     */
    public boolean save(File file) {
        checkPaths();
        file.getParentFile().mkdirs();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Debug.p("Deck file could not be created: "
                        + file + ": " + ex, Debug.E);
                return false;
            }
        }
        try (Writer bf = new OutputStreamWriter(
                new FileOutputStream(file), "Unicode")) {
            for (int i = 0; i < names.size(); i++) {
                bf.write(names.get(i) + ";"
                        + amounts.get(i) + ";"
                        + paths.get(i) + System.getProperty("line.separator"));
            }
        } catch (IOException ex) {
            Debug.p("Deck could not be saved to file "
                    + file + ": " + ex, Debug.E);
            return false;
        }
        deckName = Utilities.getName(file);
        return true;
    }

    /**
     * In case of accessing <code>paths</code> after serialization.
     * This method creates <code>paths</code> ArrayList (if missing)
     * and adds nulls if <code>paths'</code> size is smaller than
     * <code>names'</code> size.
     */
    private void checkPaths() {
        if (paths == null) {
            paths = new ArrayList<>(getArraySize());
        }
        for (int i = paths.size(); i < getArraySize(); i++) {
            paths.add(i, null);
        }
    }

    /**
     * Loads a deck from text file given
     * @param file file to load a deck from
     * @return deck or null if loading failed
     */
    public static Deck load(File file) {
        try (Scanner in = new Scanner(file, "Unicode")) {
            Deck result = new Deck();
            String line;
            while (in.hasNextLine()) {
                line = in.nextLine();
                String[] t = line.split(";");
                try {
                    // t[2] is not null, but "null"
                    result.addCard(t[0], Integer.parseInt(t[1]), new File(t[2]));
                } catch (Exception ex) {
                    Debug.p("Ignored line while loading a deck: " + line, Debug.W);
                }
            }
            result.deckName = Utilities.getName(file);
            return result;
        } catch (Exception ex) {
            Debug.p("Deck could not be loaded from file "
                    + file + ": " + ex, Debug.E);
        }
        return null;
    }
    
    public static void check(Deck deck) throws InvalidDeckException {
        if (deck.amounts.size() != deck.names.size()) {
            throw new InvalidDeckException("Number of amounts and names differ");
        }
        
        int total = 0;
        for (int i = 0; i < deck.amounts.size(); i++) {
            if (deck.amounts.get(i) > 4 && !Card.isBasicLand(deck.names.get(i))) {
                throw new InvalidDeckException("More than 4 instances of \""
                        + deck.names.get(i) + "\"");
            } else if (deck.amounts.get(i) < 0) {
                throw new InvalidDeckException("Negative number of instances "
                        + "of \"" + deck.names.get(i) + "\"");
            }
            total += deck.amounts.get(i);
        }
        
        if (total < 60) {
            throw new InvalidDeckException("Less than 60 cards in deck");
        }
    }
}
