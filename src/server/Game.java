package server;

import java.util.TreeMap;
import mtg.Debug;
import mtg.Deck;

/**
 * @author Jaroslaw Pawlak
 *
 * None of Game's method should send anything to clients, a proper actions
 * have to be sent explicitly depending on these methods return values.
 * <p>
 * All of the methods must ensure that no illegal move is posibble, e.g.
 * if player's library is empty, player cannot draw a card so nothing has
 * to be sent to clients. Possibility of drawing a card from empty library
 * should be also disabled at client side, but a server has to be error-safe.
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

    /**
     * Returns a list of cards' names and IDs assigned to them. ID assignment
     * is random and cannot be predicted, however consecutive cards in a library
     * have consecutive IDs, e.g. if a player has 60 cards in library, top card
     * of his library is X59, the next is X58 and so on, so it is recommended
     * to shuffle libraries before play.
     * @return list of cards and IDs assigned to them
     */
    TreeMap<String, String> getAllCardsList() {
        return cardsList;
    }

////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// LIBRARY ////////////////////////////////////
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
     * Moves the requested card of player's library onto the table and returns
     * true. If card is not in player's library returns false and does nothing.
     * @param player player
     * @param cardID cardID
     * @return true if card is in the library and has been moved,
     * false otherwise
     */
    synchronized boolean libraryPlay(int player, String cardID) {
        if (library[player].contains(cardID)) {
            table.addCard(library[player].removeCard(cardID));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Moves the requested card of player's library to his hand and returns
     * true. If card is not in player's library returns false and does nothing.
     * @param player player
     * @param cardID cardID
     * @return true if card is in the library and has been moved,
     * false otherwise
     */
    synchronized boolean libraryToHand(int player, String cardID) {
        if (library[player].contains(cardID)) {
            hand[player].addCard(library[player].removeCard(cardID));
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Moves the requested card of player's library onto the top of it and returns
     * true. If card is not in player's library returns false and does nothing.
     * @param player player
     * @param cardID cardID
     * @return true if card is in the library and has been moved,
     * false otherwise
     */
    synchronized boolean libraryToTop(int player, String cardID) {
        if (library[player].contains(cardID)) {
            library[player].addCard(library[player].removeCard(cardID));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Moves the requested card of player's library onto his graveyard and returns
     * true. If card is not in player's library returns false and does nothing.
     * @param player player
     * @param cardID cardID
     * @return true if card is in the library and has been moved,
     * false otherwise
     */
    synchronized boolean libraryDestroy(int player, String cardID) {
        if (library[player].contains(cardID)) {
            graveyard[player].addCard(library[player].removeCard(cardID));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Exiles the requested card of player's library and returns
     * true. If card is not in player's library returns false and does nothing.
     * @param player player
     * @param cardID cardID
     * @return true if card is in the library and has been moved,
     * false otherwise
     */
    synchronized boolean libraryExile(int player, String cardID) {
        if (library[player].contains(cardID)) {
            exiled[player].addCard(library[player].removeCard(cardID));
            return true;
        } else {
            return false;
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

////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////// HAND //////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

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
     * Moves requested card from requsted player's hand on top of his library.
     * Returns true if card has been moved or false if card was not in
     * player's hand.
     * @param player player
     * @param cardID cardID
     * @return true if card has been moved, false otherwise
     */
    synchronized boolean handToLibrary(int player, String cardID) {
        if (hand[player].contains(cardID)) {
            library[player].addCard(hand[player].removeCard(cardID));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Moves requested card from requsted player's hand to his graveyard.
     * Returns true if card has been moved or false if card was not in
     * player's hand.
     * @param player player
     * @param cardID cardID
     * @return true if card has been moved, false otherwise
     */
    synchronized boolean handDestroy(int player, String cardID) {
        if (hand[player].contains(cardID)) {
            graveyard[player].addCard(hand[player].removeCard(cardID));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Exiles requested card from requsted player's hand.
     * Returns true if card has been moved or false if card was not in
     * player's hand.
     * @param player player
     * @param cardID cardID
     * @return true if card has been moved, false otherwise
     */
    synchronized boolean handExile(int player, String cardID) {
        if (hand[player].contains(cardID)) {
            exiled[player].addCard(hand[player].removeCard(cardID));
            return true;
        } else {
            return false;
        }
    }

////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////// TABLE /////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////


    /**
     * Moves requested card from the table to card's owner's hand.
     * @param cardID
     * @return true if card moved, false otherwise
     */
    synchronized boolean tableTake(String cardID) {
        if (table.contains(cardID)) {
            int player = cardID.charAt(0) - 'A';
            if (player < 0 || player > library.length) {
                Debug.p("Received non-exisitng card's ID: " + cardID, Debug.W);
                return false;
            }
            hand[player].addCard(table.removeCard(cardID));
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
            int player = cardID.charAt(0) - 'A';
            if (player < 0 || player > library.length) {
                Debug.p("Received non-exisitng card's ID: " + cardID, Debug.W);
                return false;
            }
            library[player].addCard(table.removeCard(cardID));
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
            int player = cardID.charAt(0) - 'A';
            if (player < 0 || player > library.length) {
                Debug.p("Received non-exisitng card's ID: " + cardID, Debug.W);
                return false;
            }
            graveyard[player].addCard(table.removeCard(cardID));
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
            if (player < 0 || player > library.length) {
                Debug.p("Received non-exisitng card's ID: " + cardID, Debug.W);
                return false;
            }
            exiled[player].addCard(table.removeCard(cardID));
            return true;
        } else {
            return false;
        }
    }

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////// GRAVEYARD ///////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

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

    /**
     * Moves requested card from owner's graveyard onto the table.
     * Returns true if a card has been moved and false if a card was not in
     * the graveyard.
     * @param cardID cardID
     * @return true if card has been moved, false otherwise
     */
    synchronized boolean graveyardPlay(String cardID) {
        int player = cardID.charAt(0) - 'A';
        if (player < 0 || player > library.length) {
            Debug.p("Received non-exisitng card's ID: " + cardID, Debug.W);
            return false;
        } else if (graveyard[player].contains(cardID)) {
            table.addCard(graveyard[player].removeCard(cardID));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Moves requested card from owner's graveyard to his hand.
     * Returns true if a card has been moved and false if a card was not in
     * the graveyard.
     * @param cardID cardID
     * @return true if card has been moved, false otherwise
     */
    synchronized boolean graveyardToHand(String cardID) {
        int player = cardID.charAt(0) - 'A';
        if (player < 0 || player > library.length) {
            Debug.p("Received non-exisitng card's ID: " + cardID, Debug.W);
            return false;
        } else if (graveyard[player].contains(cardID)) {
            hand[player].addCard(graveyard[player].removeCard(cardID));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Moves requested card from owner's graveyard to his exiled zone.
     * Returns true if a card has been moved and false if a card was not in
     * the graveyard.
     * @param cardID cardID
     * @return true if card has been moved, false otherwise
     */
    synchronized boolean graveyardExile(String cardID) {
        int player = cardID.charAt(0) - 'A';
        if (player < 0 || player > library.length) {
            Debug.p("Received non-exisitng card's ID: " + cardID, Debug.W);
            return false;
        } else if (graveyard[player].contains(cardID)) {
            exiled[player].addCard(graveyard[player].removeCard(cardID));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Moves requested card from owner's graveyard onto the top of his library.
     * Returns true if a card has been moved and false if a card was not in
     * the graveyard.
     * @param cardID cardID
     * @return true if card has been moved, false otherwise
     */
    synchronized boolean graveyardToLibrary(String cardID) {
        int player = cardID.charAt(0) - 'A';
        if (player < 0 || player > library.length) {
            Debug.p("Received non-exisitng card's ID: " + cardID, Debug.W);
            return false;
        } else if (graveyard[player].contains(cardID)) {
            library[player].addCard(graveyard[player].removeCard(cardID));
            return true;
        } else {
            return false;
        }
    }

////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// EXILED /////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

    synchronized String[] exiledView(int player) {
        Card[] x = exiled[player].getLast(exiled[player].getSize());
        String[] result = new String[x.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = x[i].ID;
        }
        return result;
    }

    /**
     * Moves requested card from owner's exiled zone onto the table.
     * Returns true if a card has been moved and false if a card was not in 
     * exiled zone.
     * @param cardID cardID
     * @return true if card has been moved, false otherwise
     */
    synchronized boolean exiledPlay(String cardID) {
        int player = cardID.charAt(0) - 'A';
        if (player < 0 || player > library.length) {
            Debug.p("Received non-exisitng card's ID: " + cardID, Debug.W);
            return false;
        } else if (exiled[player].contains(cardID)) {
            table.addCard(exiled[player].removeCard(cardID));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Moves requested card from owner's exiled zone to his hand.
     * Returns true if a card has been moved and false if a card was not in
     * exiled zone.
     * @param cardID cardID
     * @return true if card has been moved, false otherwise
     */
    synchronized boolean exiledToHand(String cardID) {
        int player = cardID.charAt(0) - 'A';
        if (player < 0 || player > library.length) {
            Debug.p("Received non-exisitng card's ID: " + cardID, Debug.W);
            return false;
        } else if (exiled[player].contains(cardID)) {
            hand[player].addCard(exiled[player].removeCard(cardID));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Moves requested card from owner's exiled zone onto his graveyard.
     * Returns true if a card has been moved and false if a card was not in
     * exiled zone.
     * @param cardID cardID
     * @return true if card has been moved, false otherwise
     */
    synchronized boolean exiledToGraveyard(String cardID) {
        int player = cardID.charAt(0) - 'A';
        if (player < 0 || player > library.length) {
            Debug.p("Received non-exisitng card's ID: " + cardID, Debug.W);
            return false;
        } else if (exiled[player].contains(cardID)) {
            graveyard[player].addCard(exiled[player].removeCard(cardID));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Moves requested card from owner's exiled zone on the top of his library.
     * Returns true if a card has been moved and false if a card was not exiled.
     * @param cardID cardID
     * @return true if card has been moved, false otherwise
     */
    synchronized boolean exiledToLibrary(String cardID) {
        int player = cardID.charAt(0) - 'A';
        if (player < 0 || player > library.length) {
            Debug.p("Received non-exisitng card's ID: " + cardID, Debug.W);
            return false;
        } else if (exiled[player].contains(cardID)) {
            library[player].addCard(exiled[player].removeCard(cardID));
            return true;
        } else {
            return false;
        }
    }

////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// OTHER //////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

    synchronized void playerSetPoison(int target, int value) {
        poison[target] = value;
    }

    synchronized void playerSetHealth(int target, int value) {
        health[target] = value;
    }

}
