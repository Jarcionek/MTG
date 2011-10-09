package game;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import mtg.Zone;
import server.flags.DragCard;
import server.flags.MoveCard;
import server.flags.TapCard;

/**
 * @author Jaroslaw Pawlak
 */
public class OnTableMouseAdapter extends MouseAdapter {

    private TCard tempCard;
    private int tempX;
    private int tempY;
    private Point cardPosition;

    @Override
    public void mouseEntered(MouseEvent e) {
        TCard source = (TCard) e.getSource();
        JComponent table = (JComponent) source.getParent();
        table.remove(source);
        table.add(source, 0);
        source.repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        final TCard source = (TCard) e.getSource();
        if (e.getButton() == MouseEvent.BUTTON1) {
            tempCard = source;
            tempX = e.getX();
            tempY = e.getY();
            cardPosition = source.getCardPosition();
            if (e.getClickCount() == 2) {
                if (source.isTapped()) {
                    source.untap();
                    Game.client.send(new TapCard(source.getID(), false));
                } else {
                    source.tap();
                    Game.client.send(new TapCard(source.getID(), true));
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
                        Game.client.send(new MoveCard(Zone.TABLE, Zone.EXILED,
                                -1, source.getID()));
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

                popupMenu.add(moveToGraveyard);
                popupMenu.add(moveToHand);
                popupMenu.add(moveToLibrary);
                popupMenu.add(exile);

                popupMenu.show(source, e.getX(), e.getY());
            }
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        if (tempCard == null) {
            return;
        }
        TCard source = ((TCard) e.getSource());
        Point currentPos = source.getCardPosition();
        if (cardPosition.x != currentPos.x || cardPosition.y != currentPos.y) {
            Game.client.send(new DragCard(source.getID(),
                    currentPos.x,
                    currentPos.y));
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (tempCard == null) {
            return;
        }
        int newx = tempCard.getXpos() - tempX + e.getX();
        int newy = tempCard.getYpos() - tempY + e.getY();
        int margin = TCard.H() / 2;
        if (newx < margin) {
            newx = margin;
        } else if (newx > Table.SIZE.width - margin) {
            newx = Table.SIZE.width - margin;
        }
        if (newy < margin) {
            newy = margin;
        } else if (newy > Table.SIZE.height - margin) {
            newy = Table.SIZE.height - margin;
        }
        tempCard.setCardPosition(newx, newy);
    }
    
}
