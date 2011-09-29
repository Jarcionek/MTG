package server;

import java.util.TreeMap;
import mtg.Deck;
import server.flags.MoveCard;

/**
 * @author Jaroslaw Pawlak
 */
class Game {
    Collection[] library;
    Collection[] hand;
    Collection[] graveyard;
    Collection[] exiled;
    Collection table;
    int[] health;
    int[] poison;

    TreeMap<String, String> cardsList;

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



    synchronized void libraryDraw(int player) {
        if (library[player].getSize() > 0) {
            Card c = library[player].removeLast();
            hand[player].addCard(c);

            MoveCard mc = new MoveCard(
                    MoveCard.LIBRARY, MoveCard.HAND, player, null);
            for (int i = 0; i < library.length; i++) {
                if (i == player) {
                    mc.cardID = c.ID;
                    Server.send(player, mc);
                    mc.cardID = null;
                } else {
                    Server.send(player, mc);
                }
            }
        }
    }

    synchronized void librarySearch(int player) {

    }

    synchronized void libraryRevealTop(int player) {

    }

    synchronized void libraryPlayTop(int player) {

    }

    synchronized void libraryShuffle(int player) {

    }

    synchronized void handPlay(int player, String cardID) {
        if (hand[player].contains(cardID)) {
            table.addCard(hand[player].removeCard(cardID));
            Server.sendToAll(
                    new MoveCard(MoveCard.HAND, MoveCard.TABLE, player, cardID));
        }
    }

    /**
     * to graveyard
     */
    synchronized void handDestroy(int player, String cardID) {
        if (hand[player].contains(cardID)) {
            graveyard[player].addCard(hand[player].removeCard(cardID));
            Server.sendToAll(
                    new MoveCard(MoveCard.HAND, MoveCard.GRAVEYARD, player, cardID));
        }
    }

    synchronized void handExile(int player, String cardID) {
        if (hand[player].contains(cardID)) {
            exiled[player].addCard(hand[player].removeCard(cardID));
            Server.sendToAll(
                    new MoveCard(MoveCard.HAND, MoveCard.EXILED, player, cardID));
        }
    }

    /**
     * to hand
     */
    synchronized void tableTake(String cardID) {
        if (table.contains(cardID)) {
            int player = cardID.charAt(0) - 'A';
            hand[player].addCard(table.removeCard(cardID));
            Server.sendToAll(
                    new MoveCard(MoveCard.TABLE, MoveCard.HAND, player, cardID));
        }
    }

    /**
     * to graveyard
     */
    synchronized void tableDestroy(String cardID) {
        if (table.contains(cardID)) {
            int player = cardID.charAt(0) - 'A';
            graveyard[player].addCard(table.removeCard(cardID));
            //TODO
        }
    }

    synchronized void tableExile(String cardID) {
        if (table.contains(cardID)) {
            int player = cardID.charAt(0) - 'A';
            exiled[player].addCard(table.removeCard(cardID));
            //TODO
        }
    }

    synchronized void playerViewGraveyard(int requestor, int target) {

    }

    synchronized void playerViewExiled(int requestor, int target) {

    }

    synchronized void playerSetPoison(int requstor, int target, int value) {

    }

    synchronized void playerSetHealth(int requstor, int target, int value) {

    }

}
