package server;

import java.util.TreeMap;
import mtg.Deck;
import server.flags.MoveCard;

/**
 * @author Jaroslaw Pawlak
 *
 * None of Game's method should send anything to clients, a proper actions
 * have to be sent explicitly depending on these methods return values.
 *
 * All of the methods must ensure that no illegal move is posibble, e.g.
 * if player's library is empty, player cannot draw a card so nothing has
 * to be sent to clients. Possibility of drawing a card from empty library
 * should be disabled at client side, but a server has to be error-safe.
 */
class Game {
    private Collection[] library;
    private Collection[] hand;
    private Collection[] graveyard;
    private Collection[] exiled;
    private Collection table;
    private int[] health;
    private int[] poison;

    private TreeMap<String, String> cardsList;

    private Game() {}

    Game(Deck[] decks) {
        library = new Collection[decks.length];
        hand = new Collection[decks.length];
        graveyard = new Collection[decks.length];
        exiled = new Collection[decks.length];
        table = new Collection();
        health = new int[decks.length];
        for (int i = 0; i < health.length; i++) {
            health[i] = 20;
        }
        poison = new int[decks.length];

        cardsList = new TreeMap<String, String>();

        //create and shuffle libraries
        //p - player, c - card, ci - cardInstance
        for (int p = 0; p < decks.length; p++) {
            library[p] = new Collection();
            hand[p] = new Collection();
            graveyard[p] = new Collection();
            exiled[p] = new Collection();
            for (int c = 0; c < decks[p].getArraySize(); c++) {
                for (int ci = 0; ci < decks[p].getArrayAmounts(c); ci++) {
                    library[p].addCard(new Card(decks[p].getArrayNames(c)));
                }
            }
            library[p].shuffle();
        }

        //assign IDs, add all cards to the cardsList
        char code = 'A';
        for (int i = 0; i < library.length; i++) {
            for (int j = 0; j < library[i].getSize(); j++) {
                String id = "" + code + j;
                String name = library[i].get(j).name;
                library[i].get(j).ID = id;
                cardsList.put(id, name);
            }
            code++;
        }
    }

    TreeMap<String, String> getAllCardsList() {
        return cardsList;
    }

////////////////////////////////////////////////////////////////////////////////

    /**
     * Modifies server's game and returns drawn card or does nothing and returns
     * null if library is empty.
     * @param player player who draws a card
     * @return top card of library or null if library is empty
     */
    synchronized Card libraryDraw(int player) {
        if (library[player].getSize() > 0) {
            Card c = library[player].removeLast();
            hand[player].addCard(c);
            return c;
        } else {
            return null;
        }
    }

    /**
     * Returns array of IDs of first <tt>amount</tt> top cards from
     * <tt>player</tt>'s library.
     * @param player player whom library is to be searched
     * @param amount the amount of cards to be returned
     * @return array of cards' IDs
     */
    synchronized String[] librarySearch(int player, int amount) {
        Card[] x = library[player].getLast(amount);
        String[] result = new String[x.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = x[i].ID;
        }
        return result;
    }

    /**
     * Returns the top card of player's library
     * @param player player
     * @return top card of player's library
     */
    synchronized Card libraryGetTop(int player) {
        return library[player].getLast();
    }

    /**
     * Moves the top card of player's library onto the table and returns that
     * card or null if a library is empty.
     * @param player player
     * @return top card or null if library is empty
     */
    synchronized Card libraryPlayTop(int player) {
        if (library[player].getSize() == 0) {
            return null;
        } else {
            Card c = library[player].removeLast();
            table.addCard(c);
            return c;
        }
    }

    /**
     * Shuffles library of requested player.
     * @param player player
     */
    synchronized void libraryShuffle(int player) {
        library[player].shuffle();
    }

    /**
     * Returns size of <tt>player</tt>'s library.
     * @param player player whose library size is to be returned
     * @return size of a library
     */
    synchronized int libraryGetSize(int player) {
        return library[player].getSize();
    }

                    ////////////////////////////////////////

    /**
     * Modifies server's game by moving requested card from hand to the table
     * if the hand really contained requested card.
     * @param player player who played a card
     * @param cardID card ID
     * @return true if hand contained requested card and was played, false
     * otherwise
     */
    synchronized boolean handPlay(int player, String cardID) {
        if (hand[player].contains(cardID)) {
            table.addCard(hand[player].removeCard(cardID));
            return true;
        } else {
            return false;
        }
    }

    /**
     * to graveyard
     */
    synchronized void handDestroy(int player, String cardID) {
//        if (hand[player].contains(cardID)) {
//            graveyard[player].addCard(hand[player].removeCard(cardID));
//            Server.sendToAll(
//                    new MoveCard(MoveCard.HAND, MoveCard.GRAVEYARD, player, cardID));
//        }
    }

    synchronized void handExile(int player, String cardID) {
//        if (hand[player].contains(cardID)) {
//            exiled[player].addCard(hand[player].removeCard(cardID));
//            Server.sendToAll(
//                    new MoveCard(MoveCard.HAND, MoveCard.EXILED, player, cardID));
//        }
    }

                    ////////////////////////////////////////


    /**
     * Moves requested card from the table to card's owner's hand.
     * @param cardID
     * @return true if card moved, false otherwise
     */
    synchronized boolean tableTake(String cardID) {
        if (table.contains(cardID)) {
            hand[cardID.charAt(0) - 'A'].addCard(table.removeCard(cardID));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Moves requested card from the table onto the top of card's
     * owner's library.
     * @param cardID card's ID
     * @return true if card has been moved, false otherwise
     */
    synchronized boolean tablePutOnTopOfLibrary(String cardID) {
        if (table.contains(cardID)) {
            library[cardID.charAt(0) - 'A'].addCard(table.removeCard(cardID));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Moves requested card from the table to its owner's graveyard.
     * @param cardID card's ID
     * @return true if card has been moved, false otherwise
     */
    synchronized boolean tableDestroy(String cardID) {
        if (table.contains(cardID)) {
            graveyard[cardID.charAt(0) - 'A'].addCard(table.removeCard(cardID));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Exiles requested card from the table to card's owner's exile zone.
     * @param cardID card's ID
     * @return true if card was on the table and has been moved, false otherwise
     */
    synchronized boolean tableExile(String cardID) {
        if (table.contains(cardID)) {
            int player = cardID.charAt(0) - 'A';
            exiled[player].addCard(table.removeCard(cardID));
            return true;
        } else {
            return false;
        }
    }

                    ////////////////////////////////////////

    /**
     * Returns an array of cards' IDs in a player's graveyard
     * @param player player
     * @return array of cards IDs
     */
    synchronized String[] graveyardView(int player) {
        Card[] x = graveyard[player].getLast(graveyard[player].getSize());
        String[] result = new String[x.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = x[i].ID;
        }
        return result;
    }

                    ////////////////////////////////////////

    synchronized String[] exiledView(int player) {
        Card[] x = exiled[player].getLast(exiled[player].getSize());
        String[] result = new String[x.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = x[i].ID;
        }
        return result;
    }

                    ////////////////////////////////////////

    synchronized void playerSetPoison(int target, int value) {

    }

    synchronized void playerSetHealth(int target, int value) {

    }

}
