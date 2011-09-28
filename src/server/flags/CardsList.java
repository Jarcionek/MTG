package server.flags;

import java.util.TreeMap;

/**
 * @author Jaroslaw Pawlak
 */
public class CardsList extends Action {
    public TreeMap<String, String> list;

    public CardsList(TreeMap<String, String> list) {
        this.list = list;
    }
}
