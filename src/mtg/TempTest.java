package mtg;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

/**
 * @author Jaroslaw Pawlak
 */
public class TempTest extends MouseAdapter {
    private static Thread t = new Thread() {
        @Override
        public void run() {
            JScrollBar b = scrollPane.getHorizontalScrollBar();
            while (!isInterrupted()) {
                b.setValue(b.getValue() + Card.W * pos / 1);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {}
            }
        }
    };

    private static int pos = 0;
    private static JScrollPane scrollPane = null;

    public static void main(String[] args) {
        Deck x = new Deck();
        x.addCard("Nin, the Pain Artist", 4);
        x.addCard("Curiosity", 4);
        x.addCard("Ivory Tower", 4);
        x.addCard("Insist", 4);
        x.addCard("Explore", 4);
        x.addCard("Quicken", 4);
        x.addCard("Reroute", 4);
        x.addCard("Plains", 250);
        x.addCard("Ezuri's Archers", 4);
        x.addCard("Joraga Treespeaker", 1);
        x.addCard("Joraga Warcaller", 1);
        x.addCard("Scattershot Archer", 2);
        x.addCard("Twinblade Slasher", 2);
        x.addCard("Bramblewood Paragon", 1);
        x.addCard("Elvish Vanguard", 1);
        x.addCard("Gaea's Herald", 1);
        x.addCard("Joiner Adept", 1);
        x.addCard("Pendelhaven Elder", 1);
        x.addCard("Tajaru Preserver", 1);
        x.addCard("Thornweald Archer", 2);
        x.addCard("Wellwisher", 2);
        x.addCard("Wirewood Herald", 1);
        x.addCard("Elvish Archdruid", 1);
        x.addCard("Elvish Champion", 1);
        x.addCard("Elvish Harbinger", 4);
        x.addCard("Ezuri, Renegade Leader", 1);
        x.addCard("Glissa, the Traitor", 1);
        x.addCard("Imperious Perfect", 1);
        x.addCard("Jagged-Scar Archers", 4);
        x.addCard("Lys Alana Bowmaster", 4);
        x.addCard("Rhys the Exiled", 1);
        x.addCard("Lys Alana Huntmaster", 1);
        x.addCard("Nullmage Shepherd", 1);
        x.addCard("Wirewood Channeler", 1);
        x.addCard("Ambush Commander", 1);
        x.addCard("Greatbow Doyen", 1);
        x.addCard("Kaysa", 1);
        x.addCard("Nath of the Gilt-Leaf", 1);
        x.addCard("Regal Force", 1);
        x.addCard("Gilt-Leaf Palace", 4);
        x.addCard("Golgari Rot Farm", 4);
        x.addCard("Oran-Rief, the Vastwood", 4);
        x.addCard("Reliquary Tower", 4);
        x.addCard("Asceticism", 2);
        x.addCard("Avoid Fate", 4);
        x.addCard("Collective Unconscious", 2);
        x.addCard("Darksteel Plate", 4);
        x.addCard("Elvish Promenade", 3);
        x.addCard("Konda's Banner", 1);
        x.addCard("Leyline of Lifeforce", 1);
        x.addCard("Leyline of Vitality", 1);
        x.addCard("Nissa Revane", 1);
        x.addCard("Praetor's Counsel", 1);
        x.addCard("Prowess of the Fair", 1);
        x.addCard("Tooth and Nail", 1);
        x.addCard("Windstorm", 1);
        x.addCard("Æther Web", 4);
        x.addCard("Silhana Starfletcher", 2);
        x.addCard("Spider Umbra", 4);
        x.addCard("Forest", 24);
        Library lib = new Library(x);

        JFrame frame = new JFrame("temp");
        JPanel panel = new JPanel(new GridLayout(1, 1));

        Card temp = null;
        while ((temp = lib.draw()) != null) {
            panel.add(temp);
        }

        scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        JLabel left = new JLabel("<");
        left.setPreferredSize(new Dimension(20, Card.H));
        left.setHorizontalAlignment(SwingConstants.CENTER);
        left.addMouseListener(new TempTest());
        JLabel right = new JLabel(">");
        right.setPreferredSize(new Dimension(20, Card.H));
        right.setHorizontalAlignment(SwingConstants.CENTER);
        right.addMouseListener(new TempTest());
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(left, BorderLayout.WEST);
        contentPane.add(right, BorderLayout.EAST);
        frame.setContentPane(contentPane);

        frame.setSize(Card.W * 5, Card.H);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((d.width - frame.getSize().width) / 2,
                          (d.height - frame.getSize().height) / 2);
        frame.setVisible(true);
        t.start();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        String text = ((JLabel) e.getSource()).getText();
        if (text.equals("<")) {
            pos = -1;
        } else if (text.equals(">")) {
            pos = 1;
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        pos = 0;
    }
}