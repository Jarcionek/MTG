package flags;

import mtg.Deck;

/**
 *
 * @author Jaroslaw Pawlak
 */
public class CheckDeck extends Action {
    public Deck deck;

    public CheckDeck(Deck deck) {
        this.deck = deck;
    }

    @Override
    public String toString() {
        return this.getClass().getName();
    }

}
