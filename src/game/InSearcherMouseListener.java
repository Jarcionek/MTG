package game;

import game.CardViewer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import mtg.Card;

/**
 * <code>MouseListener</code> for {@link mtg.Card cards}
 * <code>CardViewers</code>. For <code>Cards</code> on a <code>Table</code>
 * use {@link OnTableMouseListener}.
 *
 * @author Jaroslaw Pawlak
 */
public class InSearcherMouseListener extends MouseAdapter {

    private boolean play = true;
    private boolean toHand = true;
    private boolean toLibrary = true;
    private boolean toGraveyard = true;
    private boolean toExiled = true;

    private InSearcherMouseListener() {}

    /**
     * Defines the type of searcher to add proper options to the card, e.g.
     * card in hand will not have "play" option or card in graveyard will
     * not be able to be put in graveyard.
     */
    public InSearcherMouseListener(Zone type) {
        switch (type) {
            case HAND: toHand = false; break;
            case LIBRARY: toLibrary = false; break;
            case GRAVEYARD: toGraveyard = false; break;
            case EXILED: toExiled = false; break;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Card source = (Card) e.getSource();
        if (e.getButton() == MouseEvent.BUTTON3) {
            source.viewLarger();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        Card source = (Card) e.getSource();
        CardViewer cv = (CardViewer) source.getParent();
        cv.showCards(source);
    }
    
}
