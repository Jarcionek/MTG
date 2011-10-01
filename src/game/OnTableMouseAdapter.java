package game;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import mtg.Card;
import mtg.Zone;
import server.flags.DragCard;
import server.flags.MoveCard;
import server.flags.TapCard;

/**
 * @author Jaroslaw Pawlak
 */
public class OnTableMouseAdapter extends MouseAdapter {

    private Card tempCard;
    private int tempX;
    private int tempY;
    private Point cardPosition;

    @Override
    public void mouseEntered(MouseEvent e) {
        Card source = (Card) e.getSource();
        JPanel table = (JPanel) source.getParent();
        table.remove(source);
        table.add(source, 0);
        source.repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        final Card source = (Card) e.getSource();
        if (e.getButton() == MouseEvent.BUTTON1) {
            tempCard = source;
            tempX = e.getX();
            tempY = e.getY();
            cardPosition = source.getCardPosition();
            if (e.getClickCount() == 2) {
                if (source.isTapped()) {
                    source.untap();
                    Game.client.send(new TapCard(-1, source.getID(), false));
                } else {
                    source.tap();
                    Game.client.send(new TapCard(-1, source.getID(), true));
                }
            }
        } else {
            tempCard = null;
        }
        if (e.getButton() == MouseEvent.BUTTON3) {
            if (e.getClickCount() == 1) {
                JPopupMenu popupMenu = new JPopupMenu();

                JMenuItem exile = new JMenuItem("exile");
                exile.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        
                    }
                });

                JMenuItem moveToHand = new JMenuItem("return to hand");
                moveToHand.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Game.client.send(new MoveCard(
                                Zone.TABLE, Zone.HAND, -1, source.getID()));
                    }
                });

                JMenuItem moveToGraveyard = new JMenuItem("destroy");
                moveToGraveyard.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Game.client.send(new MoveCard(Zone.TABLE,
                                Zone.GRAVEYARD, -1, source.getID()));
                    }
                });

                JMenuItem moveToLibrary = new JMenuItem("put on top of library");
                moveToLibrary.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Game.client.send(new MoveCard(
                                Zone.TABLE, Zone.TOP_LIBRARY, -1, source.getID()));
                    }
                });

                JMenuItem enlarge = new JMenuItem("view large");
                enlarge.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        source.viewLarger();
                    }
                });

                popupMenu.add(moveToGraveyard);
                popupMenu.add(moveToHand);
                popupMenu.add(moveToLibrary);
                popupMenu.add(exile);
                popupMenu.add(enlarge);

                popupMenu.show(source, e.getX(), e.getY());
            } else {
                source.viewLarger();
            }
        }
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        if (tempCard == null) {
            return;
        }
        Card source = ((Card) e.getSource());
        Point currentPos = source.getCardPosition();
        if (Math.abs(currentPos.x - cardPosition.x) > Table.mistakeMargin
                || Math.abs(currentPos.y - cardPosition.y) > Table.mistakeMargin) {
            Game.client.send(new DragCard(-1, source.getID(), currentPos.x, currentPos.y));
        } else {
            source.setCardPosition(cardPosition.x, cardPosition.y);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (tempCard == null) {
            return;
        }
        JPanel table = (JPanel) ((Card) e.getSource()).getParent();
        int newx = tempCard.getXpos() - tempX + e.getX();
        int newy = tempCard.getYpos() - tempY + e.getY();
        int margin = Card.H / 2;
        if (newx < margin) {
            newx = margin;
        } else if (newx > table.getWidth() - margin) {
            newx = table.getWidth() - margin;
        }
        if (newy < margin) {
            newy = margin;
        } else if (newy > table.getHeight() - margin) {
            newy = table.getHeight() - margin;
        }
        tempCard.setCardPosition(newx, newy);
    }
    
}
