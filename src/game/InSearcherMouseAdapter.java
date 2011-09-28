package game;

import game.CardViewer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import mtg.Card;
import server.flags.MoveCard;

/**
 * <code>MouseListener</code> for {@link mtg.Card cards}
 * <code>CardViewers</code>. For <code>Cards</code> on a <code>Table</code>
 * use {@link OnTableMouseListener}.
 *
 * @author Jaroslaw Pawlak
 */
public class InSearcherMouseAdapter extends MouseAdapter {

    private boolean play = true;
    private boolean toHand = true;
    private boolean toLibrary = true;
    private boolean toGraveyard = true;
    private boolean toExiled = true;

    private Zone type;

    private InSearcherMouseAdapter() {}

    /**
     * Defines the type of searcher to add proper options to the card, e.g.
     * card in hand will not have "play" option or card in graveyard will
     * not be able to be put in graveyard.
     */
    public InSearcherMouseAdapter(Zone type) {
        this.type = type;
        switch (type) {
            case HAND: toHand = false; break;
            case LIBRARY: toLibrary = false; break;
            case GRAVEYARD: toGraveyard = false; break;
            case EXILED: toExiled = false; break;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        final Card source = (Card) e.getSource();

        if (type.equals(Zone.HAND)
                && e.getButton() == MouseEvent.BUTTON1
                && e.getClickCount() > 1) {
            Game.client.send(
                    new MoveCard(MoveCard.HAND, MoveCard.TABLE, -1, source.getID()));
        }
        
        if (e.getButton() == MouseEvent.BUTTON3) {
            if (e.getClickCount() == 1) {
//                JPopupMenu popupMenu = new JPopupMenu();
//
//                JMenuItem exile = new JMenuItem("exile");
//                exile.addActionListener(new ActionListener() {
//                    public void actionPerformed(ActionEvent e) {
//                    }
//                });
//
//                JMenuItem moveToHand = new JMenuItem("return to hand");
//                moveToHand.addActionListener(new ActionListener() {
//                    public void actionPerformed(ActionEvent e) {
//                    }
//                });
//
//                JMenuItem moveToGraveyard = new JMenuItem("destroy");
//                moveToGraveyard.addActionListener(new ActionListener() {
//                    public void actionPerformed(ActionEvent e) {
//                    }
//                });
//
//                JMenuItem moveToLibrary = new JMenuItem("put on top of library");
//                moveToLibrary.addActionListener(new ActionListener() {
//                    public void actionPerformed(ActionEvent e) {
//                    }
//                });
//
//                if (toHand) {
//                    popupMenu.add(moveToHand);
//                }
//                if (toGraveyard) {
//                    popupMenu.add(moveToGraveyard);
//                }
//                if (toLibrary) {
//                    popupMenu.add(moveToLibrary);
//                }
//                if (toExiled) {
//                    popupMenu.add(exile);
//                }
//
//                popupMenu.show(source, e.getX(), e.getY());
            } else {
                source.viewLarger();
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        Card source = (Card) e.getSource();
        CardViewer cv = (CardViewer) source.getParent();
        cv.showCards(source);
    }
    
}
