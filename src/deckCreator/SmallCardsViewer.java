package deckCreator;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import mtg.Card;

/**
 * JComponent displaying cards with better graphics effects but requiring
 * preloading all images - it may have about few hundreds cards limit
 * depending on JVM settings.
 *
 * @author Jaroslaw Pawlak
 */
public class SmallCardsViewer extends JScrollPane {
    private Thread t;

    private JPanel panel;
    private List<ViewableCard> cards;

    private DeckCreator parent;

    public SmallCardsViewer(DeckCreator parent) {
        super(JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        this.parent = parent;

        t = new Thread() {
            @Override
            public void run() {
                JScrollBar b = SmallCardsViewer.this.getHorizontalScrollBar();
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {}
                    Point p;
                    if ((p = SmallCardsViewer.this.getMousePosition()) != null) {
                        int x = p.x;
                        int w = SmallCardsViewer.this.getSize().width;
                        int i = 0;
                        if (x < w / 12) {
                            i = -5;
                        } else if (x < w / 6 ) {
                            i = -4;
                        } else if (x < w / 3) {
                            i = -3;
                        } else if (x > 11 * w / 6) {
                            i = 5;
                        } else if (x > 5 * w / 6) {
                            i = 4;
                        } else if (x > 2 * w / 3) {
                            i = 3;
                        }
//                        try {
                            b.setValue(b.getValue() + Card.W * i / 15);
//                        } catch (NullPointerException ex) {}
//TODO
/* throws exceptions when scrolling and using JSplitPane at the same time
 * the best would be to do nothing if any mouse button is pressed
 * it sometimes crashes even if nothing special happened
 * I don't think it's a good idea to mix a Swing and own threads...
 */
                    }
                }
            }
        };

        cards = new ArrayList<ViewableCard>();
        panel = new JPanel(new GridLayout(1, 0));
        panel.setPreferredSize(new Dimension(0, Card.H));

        this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        this.setViewportView(panel);
        t.start();
    }

    public void addCard(ViewableCard card) {
        boolean done = false;
        if (!Card.isBasicLand(card.getCardName())) {
            for (int i = 0; i < cards.size(); i++) {
                if (card.compareTo(cards.get(i)) <= 0
                        || Card.isBasicLand(cards.get(i).getCardName())) {
                    cards.add(i, card);
                    panel.add(card, i);
                    done = true;
                    break;
                }
            }
        }

        if (!done) {
            cards.add(card);
            panel.add(card);
        }
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ViewableCard source = (ViewableCard) e.getSource();
                if (e.getButton() == MouseEvent.BUTTON1
                        && parent.deck.removeCard(source.getCardName(), 1)) {
                    cards.remove(source);
                    panel.remove(source);
                    panel.setPreferredSize(
                            new Dimension(Card.W * cards.size(), Card.H));
                    panel.validate();
                    panel.repaint();
                    SmallCardsViewer.this.validate();
                    SmallCardsViewer.this.repaint();
                    JLabel t = SmallCardsViewer.this.parent.deckName;
                    if (!t.getText().endsWith("*")) {
                        t.setText(t.getText() + "*");
                    }
                }
            }
        });
        panel.setPreferredSize(new Dimension(Card.W * cards.size(), Card.H));
        panel.validate();
        panel.repaint();
        this.validate();
        this.repaint();
    }

    /**
     * Interrupts the thread responsible for scrolling. Invoke it before
     * disposure of a frame containing this SmallCardsViewer.
     */
    public void close() {
        if (t != null) {
            t.interrupt();
        }
    }

    public void refresh() {
        cards.clear();
        panel.removeAll();
        panel.setPreferredSize(new Dimension(0, Card.H));
        for (int name = 0; name < parent.deck.getArraySize(); name++) {
            for (int amount = 0; amount < parent.deck.getArrayAmounts(name); amount++) {
                addCard(new ViewableCard(parent.deck.getArrayFiles(name)));
            }
        }
    }

}
