package server.flags;

import java.util.TreeMap;

/**
 * @author Jaroslaw Pawlak
 *
 * This object is to be sent by server to all the clients after players'
 * decks are shuffled and card IDs are assigned
 */
public class CardsList extends Action {
    public TreeMap<String, String> list;

    public CardsList(TreeMap<String, String> list) {
        this.list = list;
    }
}
