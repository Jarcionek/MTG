package server.flags;

import mtg.Deck;

/**
 *
 * @author Jaroslaw Pawlak
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
