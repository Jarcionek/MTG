package deckCreator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import mtg.Card;
import mtg.Debug;
import mtg.Utilities;

/**
 * JPanel displaying cards from the given directory containing hundreds
 * or thousands cards. Loads only currently displayed cards instead of
 * all of them.
 *
 * @author Jaroslaw Pawlak
 */
public class LargeCardsViewer extends JPanel {
    private DeckCreator parent;

    private File[] files;

    private int start = 0;
    private int cards;

    private JButton moreRight;
    private JButton right;
    private JButton left;
    private JButton moreLeft;
    private JPanel cardsPanel;

    public LargeCardsViewer(DeckCreator parent) {
        super(new BorderLayout());

        this.parent = parent;

        moreRight = new JButton(">>");
        moreRight.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                recalculateCards();
                start += cards;
                showCards();
            }
        });
        right = new JButton(">");
        right.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                recalculateCards();
                start++;
                showCards();
            }
        });
        left = new JButton("<");
        left.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                recalculateCards();
                start--;
                showCards();
            }
        });
        moreLeft = new JButton("<<");
        moreLeft.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                recalculateCards();
                start -= cards;
                showCards();
            }
        });
        cardsPanel = new JPanel(new GridLayout(1, 1));
        JPanel innerPane = new JPanel(new BorderLayout());
        innerPane.add(left, BorderLayout.WEST);
        innerPane.add(right, BorderLayout.EAST);
        innerPane.add(cardsPanel, BorderLayout.CENTER);

        this.add(moreLeft, BorderLayout.WEST);
        this.add(moreRight, BorderLayout.EAST);
        this.add(innerPane, BorderLayout.CENTER);

        this.setPreferredSize(new Dimension(this.getPreferredSize().width, Card.H));
        cardsPanel.setMinimumSize(new Dimension(Card.W, Card.H));

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int temp = cards;
                recalculateCards();
                if (temp != cards) {
                    showCards();
                }
            }
        });
    }

    private void showCards() {
        cardsPanel.removeAll();

        if (start < 0) {
            start = 0;
        } else if (start > files.length - cards) {
            start = files.length - cards;
        }

        for (int j = start; j < start + cards; j++) {
            final ViewableCard t = new ViewableCard(files[j]);
            t.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        try {
                            ViewableCard vc = (ViewableCard) t.clone();
                            if (parent.deck.addCard(vc.getCardName(), 1, vc.getImage())) {
                                parent.scv.addCard(vc);
                                JLabel t = LargeCardsViewer.this.parent.deckName;
                                if (!t.getText().endsWith("*")) {
                                    t.setText(t.getText() + "*");
                                }
                            }
                        } catch (CloneNotSupportedException ex) {
                            Debug.p("Should never happen: " + ex, Debug.E);
                        }
                    }
                }
            });
            cardsPanel.add(t);
        }

        if (cards >= files.length) {
            moreLeft.setEnabled(false);
            left.setEnabled(false);
            right.setEnabled(false);
            moreRight.setEnabled(false);
        } else if (start == 0) {
            moreLeft.setEnabled(false);
            left.setEnabled(false);
            right.setEnabled(true);
            moreRight.setEnabled(true);
        } else if (start + cards == files.length) {
            moreLeft.setEnabled(true);
            left.setEnabled(true);
            right.setEnabled(false);
            moreRight.setEnabled(false);
        } else {
            moreLeft.setEnabled(true);
            left.setEnabled(true);
            right.setEnabled(true);
            moreRight.setEnabled(true);
        }

        cardsPanel.validate();
        cardsPanel.repaint();
    }

    /**
     * Recalculates how many cards can be displayed.
     */
    private void recalculateCards() {
        cards = (this.getSize().width
                - moreRight.getSize().width
                - right.getSize().width
                - moreLeft.getSize().width
                - left.getSize().width) / Card.W;
        if (cards > files.length) {
            cards = files.length;
        }
    }

    /**
     * Sets a directory to show cards from, if directory is a valid file
     * then only this file is shown.
     * @param directory a directory containing cards
     */
    public void setDirectory(File directory) {
        if (!directory.isDirectory()) {
            files = new File[] {directory};
        } else {
            int i = 0;
            for (File e : directory.listFiles()) {
                if (e.getName().contains(".")
                        && Utilities.getExtension(e)
                        .toLowerCase().equals("jpg")) {
                    i++;
                }
            }
            files = new File[i];
            i = 0;
            for (File e : directory.listFiles()) {
                if (e.getName().contains(".")
                        && Utilities.getExtension(e)
                        .toLowerCase().equals("jpg")) {
                    files[i++] = e;
                }
            }
        }

        start = 0;
        recalculateCards();
        showCards();
    }
}
