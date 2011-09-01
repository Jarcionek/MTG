package mtg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
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

    public boolean addCard(String name, int amount, File path) {
        if (isBasicLand(name)) {
            if (isCardInDeck(name)) {
                return setCardAmount(name, amount);
            } else {
                names.add(name);
                amounts.add(amount);
                if (path == null) {
                    path = new File(Utilities.findPath(Main.CARDS, name));
                }
                paths.add(path);
                return true;
            }
        }

        if (isCardInDeck(name)) {
            if (amounts.get(names.indexOf(name)) < 4) {
                return setCardAmount(name, amount);
            } else {
                return false;
            }
        } else {
            names.add(name);
            amounts.add(amount);
            if (path == null) {
                path = new File(Utilities.findPath(Main.CARDS, name));
            }
            paths.add(path);
            return true;
        }
    }

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

    public boolean setCardAmount(String name, int amount) {
        if (amount == 0) {
            return removeCard(name);
        } else if (!isCardInDeck(name)) {
            addCard(name, amount, null);
            return true;
        } else {
            if (amounts.get(names.indexOf(name)) >= 4) {
                return false;
            } else {
                amounts.set(names.indexOf(name), amount);
                return true;
            }
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

    public File getArrayFiles(int i) {
        if (paths == null) {
            paths = new ArrayList<File>(getArraySize());
        }
        for (int j = paths.size(); j < getArraySize(); j++) {
            paths.add(j, null);
        }
        File t;
        if ((t = paths.get(i)) == null) {
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

    public static Deck load(File file) {
        try {
            Scanner in = new Scanner(file);
            Deck result = new Deck();
            while (in.hasNextLine()) {
                String[] t = in.nextLine().split(";");
                if (t.length == 3) {
                    result.addCard(t[0],
                            Integer.parseInt(t[1]),
                            new File(t[2]));
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
