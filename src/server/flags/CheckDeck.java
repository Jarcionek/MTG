package server.flags;

import mtg.Deck;

/**
 * @author Jaroslaw Pawlak
 *
 * This object may be sent by both client or server - the other side should
 * check if it has all cards in a deck sent
 */
public class CheckDeck extends Action {
    public String owner;
    public Deck deck;

    public CheckDeck(Deck deck) {
        this(null, deck);
    }

    public CheckDeck(String owner, Deck deck) {
        this.owner = owner;
        this.deck = deck;
    }

}
