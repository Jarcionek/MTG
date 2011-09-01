package mtg;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;

/**
 * @author Jaroslaw Pawlak
 */
public class DeckCreator extends JFrame {
    private DeckCreator() {}

    public DeckCreator(final JFrame parent) {
        super(Main.TITLE + " Deck Creator");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                parent.setVisible(true);
            }
        });
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JTree cards = new JTree(new CardTreeNode(Main.CARDS.listFiles()));
        cards.setRootVisible(false);
        JScrollPane cardsScrollPane = new JScrollPane(cards);

        this.setContentPane(cardsScrollPane);

        parent.setVisible(false);
        this.setVisible(true);
    }

}
