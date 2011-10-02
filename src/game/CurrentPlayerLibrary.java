package game;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import mtg.Zone;
import server.flags.MoveCard;
import server.flags.Reveal;
import server.flags.Search;
import server.flags.Shuffle;

/**
 * @author Jaroslaw Pawlak
 */
public class CurrentPlayerLibrary extends JPanel {

    private JLabel title;
    private JButton drawButton;
    private JButton searchButton;
    private JButton revealTopButton;
    private JButton playTopButton;
    private JButton shuffleButton;

    public CurrentPlayerLibrary() {
        super();
        createComponents();
        createGUI();
    }

    private void createComponents() {
        title = new JLabel("Your library");
        title.setHorizontalAlignment(JLabel.CENTER);

        drawButton = new JButton("Draw");
        drawButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Game.client.send(new MoveCard(
                        Zone.TOP_LIBRARY, Zone.HAND, -1, null, false));
            }
        });
        drawButton.setFocusable(false);

        searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Object x = JOptionPane.showInputDialog(
                        null,
                        "How many cards from the top\n" +
                        "of your library you would like to see?",
                        "Search your library",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null, "all");

                if (x == null) {
                    return;
                }
                
                int value = 0;
                if (((String) x).equalsIgnoreCase("all")) {
                    value = -1;
                } else {
                    try {
                        value = Integer.parseInt((String) x);
                    } catch (NumberFormatException ex) {
                        return;
                    }
                }

                Game.client.send(new Search(value, Zone.LIBRARY, -1));
            }
        });
        searchButton.setFocusable(false);

        revealTopButton = new JButton("Reveal top");
        revealTopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Game.client.send(new Reveal(Zone.TOP_LIBRARY, -1, null));
            }
        });
        revealTopButton.setFocusable(false);

        playTopButton = new JButton("Play top");
        playTopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Game.client.send(new MoveCard(
                        Zone.TOP_LIBRARY, Zone.TABLE, -1, null, true));
            }
        });
        playTopButton.setFocusable(false);

        shuffleButton = new JButton("Shuffle");
        shuffleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Game.client.send(new Shuffle(-1));
            }
        });
        shuffleButton.setFocusable(false);
    }

    private void createGUI() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        int outside = 2;
        int between = 3;
        int b = 5;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;

        c.insets = new Insets(outside, outside, between, outside);
        title.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.RAISED),
                BorderFactory.createEmptyBorder(b, b, b, b)));
        this.add(title, c);

        c.insets = new Insets(0, outside, outside, between);
        this.add(drawButton, c);
        this.add(searchButton, c);
        this.add(revealTopButton, c);
        this.add(playTopButton, c);
        c.insets = new Insets(0, outside, outside, outside);
        this.add(shuffleButton, c);
    }
}
