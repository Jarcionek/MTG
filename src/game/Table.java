package game;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import mtg.Card;

/**
 * @author Jaroslaw Pawlak
 */
public class Table extends JScrollPane {
    /**
     * If card has been moved along both axes by not more than this value,
     * movement event will not be sent to the server and in the client's table
     * that card will be moved to the original position
     */
    public static final int mistakeMargin = Card.W / 8;


    /*
     * each player should be able to put cards in 15 columns and 5 rows + some
     * margin + some place on the middle of a table for a stack
     */

    /**
     * Intended gaps between cards
     */
    private static final int gap = Card.W / 4;
    public static final Dimension TWO_PLAYERS
            = new Dimension((15 * Card.W + 14 * gap) * 2 + 2 * Card.H,
            (5 * Card.H + 4 * gap) * 2 + 2 * Card.H);
    public static final Dimension FOUR_PLAYERS
            = TWO_PLAYERS; //TODO
    public static final Dimension SIX_PLAYERS
            = TWO_PLAYERS; //TODO
    public static final Dimension EIGHT_PLAYERS
            = TWO_PLAYERS; //TODO
    
    private JPanel table;

    public Table(final Dimension size) {
        super();
        this.table = new JPanel(null);
        this.table.setPreferredSize(size);
        this.setViewportView(this.table);
        this.removeAll();

        TableDragListener tdl = new TableDragListener();
        this.table.addMouseListener(tdl);
        this.table.addMouseMotionListener(tdl);
        this.table.setAutoscrolls(true);

        this.setWheelScrollingEnabled(false);
    }

    /**
     * Scrolls to the centre of a table. Invoke it when a frame is displayed,
     * otherwise it may not work.
     */
    public void centerView() {
        int h = this.getHorizontalScrollBar().getMaximum()
                - this.getHorizontalScrollBar().getVisibleAmount();
        int v = this.getVerticalScrollBar().getMaximum()
                - this.getVerticalScrollBar().getVisibleAmount();
        
        h /= 2;
        v /= 2;

        this.getHorizontalScrollBar().setValue(h);
        this.getVerticalScrollBar().setValue(v);

    }

    /**
     * Adds a cards at the top layer in the middle of a table. Repaints
     * the container.
     * @param card card to be played
     */
    public void addCard(Card card) {
        for (MouseListener e : card.getMouseListeners()) {
            card.removeMouseListener(e);
        }
        for (MouseMotionListener e : card.getMouseMotionListeners()) {
            card.removeMouseMotionListener(e);
        }

        OnTableMouseAdapter t = new OnTableMouseAdapter();
        card.addMouseListener(t);
        card.addMouseMotionListener(t);
        card.setCardPosition(table.getPreferredSize().width / 2,
                table.getPreferredSize().height / 2);
        table.add(card, 0);
        card.repaint();
    }

    public void dragCard(String ID, int newx, int newy) {
        for (Object e : table.getComponents()) {
            if (!e.getClass().equals(Card.class)) {
                continue;
            }
            if (((Card) e).getID().equals(ID)) {
                ((Card) e).setCardPosition(newx, newy);
                break;
            }
        }
    }

    public void tapCard(String ID, boolean tapped) {
        for (Object e : table.getComponents()) {
            if (!e.getClass().equals(Card.class)) {
                continue;
            }
            if (((Card) e).getID().equals(ID)) {
                if (tapped) {
                    ((Card) e).tap();
                } else {
                    ((Card) e).untap();
                }
                break;
            }
        }
    }

    /**
     * Scrolls table to the given card or returns false and does nothing
     * if card is not on the table.
     * @param ID Card ID
     * @return true if scrolled to the card or false if card not on the table
     */
    public boolean scrollToCard(String ID) {
        for (Component c : table.getComponents()) {
            if (c.getClass().equals(Card.class)) {
                final Card card = (Card) c;
                if (card.getID().equals(ID)) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            Table.this.getHorizontalScrollBar().setValue(
                                    card.getBounds().x + Card.W / 2
                                    - Table.this.getWidth() / 2);
                            Table.this.getVerticalScrollBar().setValue(
                                    card.getBounds().y + Card.H / 2
                                    - Table.this.getHeight() / 2);
                        }
                    });
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Removes all cards from the table.
     */
    @Override
    public final void removeAll() {
        table.removeAll();
        JLabel centre = new JLabel();
        centre.setSize(Card.W, Card.H);
        centre.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createBevelBorder(BevelBorder.RAISED),
                    BorderFactory.createBevelBorder(BevelBorder.LOWERED)
                ));
        centre.setBounds((table.getPreferredSize().width - Card.W) / 2 - 4,
                (table.getPreferredSize().height - Card.H) / 2 - 4,
                Card.W + 8,
                Card.H + 8);
        table.add(centre);
    }
}
