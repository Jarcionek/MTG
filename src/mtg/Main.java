package mtg;

import deckCreator.DeckCreator;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * @author Jaroslaw Pawlak
 */
public class Main {
    public static final File DIRECTORY
            = new File(System.getProperty("user.dir"), "MTG");
    public static final File CARDS = new File(DIRECTORY, "Cards");
    public static final File DECKS = new File(DIRECTORY, "Decks");
    public static final String TITLE = "MTG";
    
    public static void main(String[] args)
            throws FileNotFoundException, IOException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            Debug.p("Look and feel could not be set: " + ex, Debug.E);
        }

        if (!DIRECTORY.exists()) {
            int choice = JOptionPane.showConfirmDialog(
                    null,
                    "MTG will be using \n" + DIRECTORY.getPath()
                    + "\nto save files. "
                    + "Do you want to continue?",
                    TITLE,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (choice != 0) {
                System.exit(0);
            }
            saveExampleCards();
            saveExampleDecks();
        }
        
        final JFrame frame = new JFrame(TITLE);
        JPanel contentPane = new JPanel(null);

        JButton deckCreator = new JButton("Deck creator");
        deckCreator.setFocusPainted(false);
        deckCreator.setOpaque(false);
        deckCreator.setFocusable(false);
        deckCreator.setBounds(40, 60,
                deckCreator.getPreferredSize().width,
                deckCreator.getPreferredSize().height);
        deckCreator.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new DeckCreator(TITLE + ": Deck Creator", frame, CARDS);
            }
        });
        contentPane.add(deckCreator);

        JButton exit = new JButton("Exit");
        exit.setFocusPainted(false);
        exit.setOpaque(false);
        exit.setFocusable(false);
        exit.setBounds(60, 405,
                exit.getPreferredSize().width,
                exit.getPreferredSize().height);
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        contentPane.add(exit);

        JLabel background = new JLabel(new ImageIcon(
                Main.class.getResource("/resources/Background.jpg")));
        background.setBounds(0, 0, 750, 450);
        contentPane.add(background);

        frame.addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent e) {
                frame.setExtendedState(JFrame.NORMAL);
            }
        });

        exit.revalidate();
//        contentPane.validate();

        frame.setContentPane(contentPane);
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(750, 450);
        frame.setResizable(true);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((d.width - frame.getSize().width) / 2,
                          (d.height - frame.getSize().height) / 2);
        frame.setVisible(true);

    }

    private static void saveExampleCards() {
        String[] cards = {
            "Barony Vampire.jpg",
            "Blade of the Bloodchief.jpg",
            "Bloodghast.jpg",
            "Bloodrage Vampire.jpg",
            "Captivating Vampire.jpg",
            "Child of Night.jpg",
            "Corrupt.jpg",
            "Demon's Horn.jpg",
            "Drana, Kalastria Bloodchief.jpg",
            "Duskhunter Bat.jpg",
            "Feast of Blood.jpg",
            "Gatekeeper of Malakir.jpg",
            "Mirri the Cursed.jpg",
            "Quag Vampires.jpg",
            "Repay in Kind.jpg",
            "Ruthless Cullblade.jpg",
            "Sangromancer.jpg",
            "Sengir Vampire.jpg",
            "Skeletal Vampire.jpg",
            "Spread the Sickness.jpg",
            "Stalking Bloodsucker.jpg",
            "Swamp.jpg",
            "Tormented Soul.jpg",
            "Urge to Feed.jpg",
            "Vampire Aristocrat.jpg",
            "Vampire Nighthawk.jpg",
            "Vampire Nocturnus.jpg",
            "Vampire Outcasts.jpg",
            "Vampire's Bite.jpg",
            "Vicious Hunger.jpg",
        };
        String[] lands = {
            "Forest.jpg",
            "Island.jpg",
            "Mountain.jpg",
            "Plains.jpg",
        };

        File examples = new File(CARDS, "Example");
        File bloodHunger = new File(examples, "Blood Hunger");

        for (String e : cards) {
            save("/resources/cards/" + e, new File(bloodHunger, e));
        }
        for (String e : lands) {
            save("/resources/cards/" + e, new File(examples, e));
        }
    }

    private static void saveExampleDecks() {
        String[] decks = {"Blood Hunger.txt"};

        for (String e : decks) {
            save("/resources/decks/" + e, new File(DECKS, e));
        }
    }

    private static void save(String resource, File file) {
        file.getParentFile().mkdirs();
        BufferedOutputStream bos = null;
            try {
                InputStream is = Main.class
                        .getResourceAsStream(resource);
                bos = new BufferedOutputStream(
                        new FileOutputStream(file));
                byte[] b = new byte[256];
                int read = -1;
                while ((read = is.read(b)) >= 0) {
                    bos.write(b, 0, read);
                }
                bos.close();
                is.close();
            } catch (IOException ex) {
                Debug.p("Resource " + resource + " could not be saved: "
                        + ex, Debug.E);
            } finally {
                try {
                    bos.close();
                } catch (IOException ex) {
                    Debug.p("Resource " + resource + " output stream could "
                            + "not be closed: " + ex, Debug.E);
                }
            }
    }
}