package deckCreator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JPanel;
import mtg.Card;

/**
 * JPanel displaying cards from the given directory containing hundreds
 * or thousands cards. Loads only currently displayed cards instead of
 * all of them.
 *
 * @author Jaroslaw Pawlak
 */
public class LargeCardsViewer extends JPanel {
    private File[] files;

    private int start = 0;
    private int cards;

    private JButton moreRight;
    private JButton right;
    private JButton left;
    private JButton moreLeft;
    private JPanel cardsPanel;

    public LargeCardsViewer() {
        super(new BorderLayout());

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
            //TODO add mouse listener here with adding to the deck
            cardsPanel.add(new ViewableCard(files[j]));
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

    public void setDirectory(File directory) {
        if (!directory.isDirectory()) {
            files = new File[] {directory};
        } else {
            int i = 0;
            for (String e : directory.list()) {
                if (e.contains(".")
                        && e.substring(e.lastIndexOf("."))
                        .toLowerCase().equals(".jpg")) {
                    i++;
                }
            }
            files = new File[i];
            i = 0;
            for (File e : directory.listFiles()) {
                if (e.getName().contains(".")
                        && e.getName().substring(e.getName().lastIndexOf("."))
                        .toLowerCase().equals(".jpg")) {
                    files[i++] = e;
                }
            }
        }

        start = 0;
        recalculateCards();
        showCards();
    }
}
