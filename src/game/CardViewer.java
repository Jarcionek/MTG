package game;

import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import mtg.Card;
import mtg.Deck;
import mtg.Library;

/**
 * @author Jaroslaw Pawlak
 */
public class CardViewer extends JPanel {
    private ArrayList<Card> cards;
    private MouseListener listener;

    private CardViewer() {}

    /**
     * @param listener listener to be added to the cards, all other listeners
     * will be removed
     */
    public CardViewer(MouseListener listener) {
        super(null);
        this.listener = listener;
        this.cards = new ArrayList<Card>(60);
        this.setPreferredSize(new Dimension(Card.W * 2, Card.H));
    }

    /**
     * Adds a card to the card viewer. This method does not repaint the
     * container. To refresh the display use {@link showCards(Card)}.
     * @param card a card to be added
     */
    public void addCard(Card card) {
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
        
        for (MouseListener e : card.getMouseListeners()) {
            card.removeMouseListener(e);
        }
        for (MouseMotionListener e : card.getMouseMotionListeners()) {
            card.removeMouseMotionListener(e);
        }
        card.addMouseListener(listener);
    }

    /**
     * Repaints the container with the chosen card fully visible.
     * Other cards, depending on container's sizes, may be hidden be the
     * <code>topCard</code>
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
        } else {
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
        }
        this.repaint();
    }

    public static void main(String[] asf) {
        JFrame x = new JFrame();

        Deck deck = new Deck();
        deck.addCard("Ezuri's Archers", 4);
        deck.addCard("Joraga Treespeaker", 1);
        deck.addCard("Joraga Warcaller", 1);
        deck.addCard("Scattershot Archer", 2);
        deck.addCard("Twinblade Slasher", 2);
        deck.addCard("Bramblewood Paragon", 1);
        Library library = new Library(deck);

        CardViewer cont = new CardViewer(new InSearcherMouseListener(Zone.HAND));



        x.setContentPane(cont);
//        x.setSize(400, 300);
        x.pack();
        x.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        x.setVisible(true);

        
        Card c;
        while ((c = library.draw()) != null) {
            cont.addCard(c);
        }

        cont.showCards(null);
    }
}
