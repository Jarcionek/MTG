package game;

import java.awt.Dimension;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import mtg.Card;
import mtg.Deck;
import mtg.Library;

/**
 * @author Jaroslaw Pawlak
 */
public class Table extends JScrollPane {
    /**
     * If card has been moved along both axes by not more than this value,
     * movement event will not be sent to the server and in the client's table
     * that card will be moved to the original position
     */
    public static final int mistakeMargin = 20;


    /*
     * each player should be able to put cards in 15 columns and 5 rows + some
     * margin + some place on the middle of a table for a stack
     */

    /**
     * Intended gaps between cards
     */
    private static final int gap = 40;
    public static final Dimension TWO_PLAYERS
            = new Dimension((15 * Card.W + 14 * gap) * 2 + 2 * Card.H,
            (5 * Card.H + 4 * gap) * 2 + 2 * Card.H);
    //TODO other sizes
    
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
        OnTableMouseListener t = new OnTableMouseListener();
        card.addMouseListener(t);
        card.addMouseMotionListener(t);
        card.setCardPosition(table.getPreferredSize().width / 2,
                table.getPreferredSize().height / 2);
        table.add(card, 0);
        card.repaint();
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



    public static void main(String[] ars) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {}

        JFrame x = new JFrame();

//        Table y = new Table(new Dimension(800, 600));
        Table y = new Table(TWO_PLAYERS);


        x.setContentPane(y);
        x.setSize(400, 300);
        x.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        x.setVisible(true);
        y.centerView();

        Deck deck = new Deck();
        deck.addCard("Ezuri's Archers", 4);
        deck.addCard("Joraga Treespeaker", 1);
        deck.addCard("Joraga Warcaller", 1);
        deck.addCard("Scattershot Archer", 2);
        deck.addCard("Twinblade Slasher", 2);
        deck.addCard("Bramblewood Paragon", 1);
        Library library = new Library(deck);

        y.addCard(library.draw());
        y.addCard(library.draw());
        y.addCard(library.draw());
    }
}
