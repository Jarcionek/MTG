package game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import mtg.Card;
import mtg.Utilities;
import mtg.Zone;

/**
 * @author Jaroslaw Pawlak
 */
public class CardViewer extends JPanel {
    private static JPopupMenu mostRecentPopupMenu;
    private static CardViewer mostRecentCardViewer;
    private static JLabel mostRecentCardViewerLabel;

    private ArrayList<Card> cards;
    private InSearcherMouseAdapter listener;

    private CardViewer() {}

    /**
     * @param listener listener to be added to the cards, all other listeners
     * will be removed
     */
    public CardViewer(InSearcherMouseAdapter listener) {
        super(null);
        this.listener = listener;
        this.cards = new ArrayList<Card>(60);
        this.setPreferredSize(new Dimension(Card.W * 2, Card.H));
    }

    /**
     * Adds a card to the card viewer. This method does not repaint the
     * container. To refresh the display use {@link showCards(Card)}.
     * If listener's type is different than Zone.HAND then card is added at the
     * end, otherwise cards are sorted.
     * @param card a card to be added
     */
    public void addCard(Card card) {
        if (listener.getType() == Zone.HAND) {

            /* Add a non-basic land card in a proper lixicographical position
             * or before the first found basic land. Basic lands are always added
             * at the end.
             */
            boolean done = false;
            if (!Card.isBasicLand(card.getCardName())) {
                for (int i = 0; i < cards.size(); i++) {
                    if (card.compareTo(cards.get(i)) <= 0
                            || Card.isBasicLand(cards.get(i).getCardName())) {
                        cards.add(i, card);
                        done = true;
                        break;
                    }
                }
            }
            if (!done) {
                cards.add(card);
            }

        } else {
            cards.add(card);
        }
        
        for (MouseListener e : card.getMouseListeners()) {
            card.removeMouseListener(e);
        }
        for (MouseMotionListener e : card.getMouseMotionListeners()) {
            card.removeMouseMotionListener(e);
        }
        card.addMouseListener(listener);
    }

    public void removeCard(Card card) {
        cards.remove(card);
    }

    /**
     * Repaints the container with the chosen card fully visible.
     * Other cards, depending on container's sizes, may be hidden be the
     * <code>topCard</code>. If topCard is null then the first card is chosen.
     * @param topCard card to be fully visible
     */
    public void showCards(Card topCard) {
        int top = 0;
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i) == topCard) {
                top = i;
            }
        }

        this.removeAll();

        if (cards.size() * Card.W < this.getSize().width) {
            int start = this.getSize().width - (cards.size() - 1) * Card.W;
            start /= 2;
            for (int i = 0; i < cards.size(); i++) {
                cards.get(i).setCardPosition(start + i * Card.W, Card.H / 2);
                this.add(cards.get(i));
            }
        } else if (cards.size() != 1) { // see: division by zero
            for (int i = top; i < cards.size(); i++) {
                cards.get(i).setCardPosition(
                        Card.W / 2 + i * (this.getSize().width - Card.W)
                                         / (cards.size() - 1),
                        Card.H / 2);
                this.add(cards.get(i));
            }
            for (int i = top - 1; i >= 0; i--) {
                cards.get(i).setCardPosition(
                        Card.W / 2 + i * (this.getSize().width - Card.W)
                                         / (cards.size() - 1),
                        Card.H / 2);
                this.add(cards.get(i));
            }
        } else {
            cards.get(0).setCardPosition(Card.W / 2, Card.H / 2);
            this.add(cards.get(0));
        }
        this.repaint();
    }

    public static void createViewerInFrame(String[] cardsID, Zone zone,
            Dimension gameSize, String info) {
        final JFrame frame = new JFrame();
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        mostRecentCardViewer = new CardViewer(new InSearcherMouseAdapter(zone));
        frame.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (!Card.isAnyCardEnlarged() &&
                        (mostRecentPopupMenu == null
                        || !mostRecentPopupMenu.isShowing())) {
                    frame.dispose();
                }
            }
        });

        int width = cardsID.length * Card.W > gameSize.width - Card.W * 2?
                gameSize.width - Card.W * 2 : cardsID.length * Card.W;
        mostRecentCardViewer.setPreferredSize(new Dimension(width, Card.H));

        mostRecentCardViewerLabel = new JLabel(info);
        mostRecentCardViewerLabel.setHorizontalAlignment(JLabel.CENTER);
        mostRecentCardViewerLabel.setFont(new Font("Arial", Font.PLAIN, 24));

        JPanel contentPane = new JPanel(new BorderLayout(3, 3));
        contentPane.add(mostRecentCardViewerLabel, BorderLayout.NORTH);
        contentPane.add(mostRecentCardViewer, BorderLayout.CENTER);
        contentPane.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));

        frame.setContentPane(contentPane);
        frame.pack();
        frame.setLocation((gameSize.width - frame.getSize().width) / 2,
                (gameSize.height - frame.getSize().height) / 2);

        for (String id : cardsID) {
            mostRecentCardViewer.addCard(new Card(Utilities.findPath(Game.getCardName(id)), id));
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mostRecentCardViewer.showCards(null);
            }
        });

        frame.setVisible(true);
    }

    public static void removeCardFromCurrentlyOpenCardViewer(String cardID) {
        Card previous;
        for (Card e : mostRecentCardViewer.cards) {
            previous = e;
            if (e.getID().equals(cardID)) {
                mostRecentCardViewer.cards.remove(e);
                mostRecentCardViewer.showCards(previous);
                String x = mostRecentCardViewerLabel.getText();
                if (x.contains(" cards)")) {
                    int value = Integer.parseInt(
                            x.substring(x.lastIndexOf("(") + 1,
                            x.lastIndexOf(" ")));
                    mostRecentCardViewerLabel.setText(
                            x.substring(0, x.lastIndexOf("(") + 1)
                            + (value - 1) + " cards)");
                }
                break;
            }
        }
    }

    public static void setPopupMenu(JPopupMenu popupMenu) {
        mostRecentPopupMenu = popupMenu;
    }

    public static void moveCardToFront(Card card) {
        mostRecentCardViewer.cards.remove(card);
        mostRecentCardViewer.cards.add(0, card);
        mostRecentCardViewer.showCards(null);
    }
}
