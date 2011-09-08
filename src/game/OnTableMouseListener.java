package game;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import mtg.Card;

/**
 * @author Jaroslaw Pawlak
 */
public class OnTableMouseListener extends MouseAdapter {

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
            if (e.getClickCount() >= 2) {
                if (source.isTapped()) {
                    source.untap(); //TODO client
                } else {
                    source.tap(); //TODO client
                }
            }
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            source.viewLarger();
//            JPopupMenu popupMenu = new JPopupMenu();
//            JMenuItem tapper = new JMenuItem(source.isTapped()? "untap" : "tap");
//            tapper.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent e) {
//                    if (source.isTapped()) {
//                        source.untap();
//                    } else {
//                        source.tap();
//                    }
//                }
//            });
//            JMenuItem viewerLarger = new JMenuItem("view");
//            viewerLarger.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent e) {
//                    c.viewLarger();
//                }
//            });
//            JMenuItem exile = new JMenuItem("exile");
//            exile.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent e) {
//                    contentPane.remove(c);
//                    contentPane.repaint();
//                }
//            });
//            JMenuItem play = new JMenuItem("return to hand");
//            play.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent e) {
//                    OldCardsViewer.addCard(c);
//                    contentPane.remove(c);
//                    contentPane.repaint();
//                }
//            });
//            popupMenu.add(tapper);
//            popupMenu.add(play);
//            popupMenu.add(viewerLarger);
//            popupMenu.add(exile);
//            popupMenu.show(source, e.getX(), e.getY());
        }
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        Card source = ((Card) e.getSource());
        Point currentPos = source.getCardPosition();
        if (Math.abs(currentPos.x - cardPosition.x) > Table.mistakeMargin
                || Math.abs(currentPos.y - cardPosition.y) > Table.mistakeMargin) {
            //TODO client - card moved
        } else {
            source.setCardPosition(cardPosition.x, cardPosition.y);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
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
