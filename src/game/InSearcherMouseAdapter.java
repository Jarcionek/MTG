package game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import mtg.Card;
import mtg.Zone;
import server.flags.MoveCard;

/**
 * <code>MouseListener</code> for {@link mtg.Card cards}
 * <code>CardViewers</code>. For <code>Cards</code> on a <code>Table</code>
 * use {@link OnTableMouseListener}.
 *
 * @author Jaroslaw Pawlak
 */
public class InSearcherMouseAdapter extends MouseAdapter {

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

    Zone getType() {
        return type;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        final Card source = (Card) e.getSource();

        if (type.equals(Zone.HAND) //TODO remove it to avoid accidental click?
                && e.getButton() == MouseEvent.BUTTON1) {
            Game.client.send(
                    new MoveCard(Zone.HAND, Zone.TABLE, -1, source.getID()));
        }
        
        if (e.getButton() == MouseEvent.BUTTON3) {
            if (e.getClickCount() == 1) {
                JPopupMenu popupMenu = new JPopupMenu();

                JMenuItem play = new JMenuItem("play");
                play.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        send(Zone.TABLE, source.getID());
                    }
                });

                JMenuItem exile = new JMenuItem("exile");
                exile.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                });

                JMenuItem moveToHandHidden = new JMenuItem("take to hand");
                moveToHandHidden.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                });

                JMenuItem moveToHand = new JMenuItem("reveal and take to hand");
                moveToHand.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                });

                JMenuItem moveToGraveyard = new JMenuItem("put onto graveyard");
                moveToGraveyard.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                });

                JMenuItem moveToLibraryHidden = new JMenuItem("put on top of library");
                moveToLibraryHidden.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                });

                JMenuItem moveToLibrary = new JMenuItem("reveal and put on top of library");
                moveToLibrary.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                });

                popupMenu.add(play);
                if (toHand) {
                    popupMenu.add(moveToHand);
                    if (type == Zone.LIBRARY) {
                        popupMenu.add(moveToHandHidden);
                    }
                }
                if (toGraveyard) {
                    popupMenu.add(moveToGraveyard);
                }
                if (toLibrary) {
                    popupMenu.add(moveToLibrary);
                    if (type == Zone.HAND) {
                        popupMenu.add(moveToLibraryHidden);
                    }
                }
                if (toExiled) {
                    popupMenu.add(exile);
                }

                popupMenu.show(source, e.getX(), e.getY());
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

    private void send(Zone target, String cardID, boolean reveal) {
        Game.client.send(new MoveCard(type, target, -1, cardID, reveal));
    }

    private void send(Zone target, String cardID) {
        send(target, cardID, true);
    }
    
}
