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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import server.Client;
import server.ServerFrame;

/**
 * @author Jaroslaw Pawlak
 */
public class Main extends JFrame {

    public static final File DIRECTORY
            = new File(System.getProperty("user.dir"), "MTG");
    public static final File CARDS = new File(DIRECTORY, "Cards");
    public static final File CARDS_DL = new File(CARDS, "Download");
    public static final File DECKS = new File(DIRECTORY, "Decks");
    public static final File DECKS_DL = new File(DECKS, "Download");
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
        CARDS.mkdirs();
        CARDS_DL.mkdirs();
        DECKS.mkdirs();

        new Main();
    }

    private Main() {
        super(TITLE);

        JPanel contentPane = new JPanel(null);

        JButton deckCreator = new JButton("Deck creator");
        deckCreator.setOpaque(false);
        deckCreator.setFocusable(false);
        deckCreator.setBounds(40, 60,
                deckCreator.getPreferredSize().width,
                deckCreator.getPreferredSize().height);
        deckCreator.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new DeckCreator(TITLE + ": Deck Creator", Main.this);
            }
        });
        contentPane.add(deckCreator);

        JButton exit = new JButton("Exit");
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

        JButton createGame = new JButton("Create game");
        createGame.setOpaque(false);
        createGame.setFocusable(false);
        createGame.setBounds(325, 35,
                createGame.getPreferredSize().width,
                createGame.getPreferredSize().height);
        createGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ServerFrame(Main.this);
            }
        });
        contentPane.add(createGame);

        JButton joinGame = new JButton("Join game");
        joinGame.setOpaque(false);
        joinGame.setFocusable(false);
        joinGame.setBounds(625, 75,
                joinGame.getPreferredSize().width,
                joinGame.getPreferredSize().height);
        joinGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //TODO temp
                JFileChooser jfc = new JFileChooser(DECKS);
                jfc.setMultiSelectionEnabled(false);
                jfc.showOpenDialog(Main.this);
                File f = jfc.getSelectedFile();
                Deck deck = Deck.load(f);
                String ip = JOptionPane.showInputDialog(Main.this, "IP:", "localhost");
                int port = Integer.parseInt(
                        JOptionPane.showInputDialog(Main.this, "port:", "56789"));
                try {
                    new Client("Jarek", ip, port, deck);
                    Main.this.setVisible(false);
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        contentPane.add(joinGame);

        JLabel background = new JLabel(new ImageIcon(
                Main.class.getResource("/resources/Background.jpg")));
        background.setBounds(0, 0, 750, 450);
        contentPane.add(background);

        this.addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent e) {
                Main.this.setExtendedState(JFrame.NORMAL);
            }
        });

        this.setContentPane(contentPane);
        this.setUndecorated(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(750, 450);
        this.setResizable(true);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((d.width - this.getSize().width) / 2,
                          (d.height - this.getSize().height) / 2);
        this.setVisible(true);
    }

    private static void saveExampleCards() {
        String [] wieldingSteelCards = {};
        String [] realmOfIllusionCards = {};
        String [] strengthOfStoneCards = {};
        String [] guardiansOfTheWoodCards = {};
        String [] ancientDepthsCards = {};
        String [] dragonsRoarCards = {};
        String[] blooHungerCards = {
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
        String [] machinationsCards = {};
        String [] unquenchableCards = {};
        String [] apexPredatorsCards = {};

        File examples = new File(CARDS, "Example");

        File wieldingSteel = new File(examples, "Wielding Steel");
        File realmOfIllusion = new File(examples, "Realm of Illusion");
        File strengthOfStone = new File(examples, "Strength of Stone");
        File guardiansOfTheWood = new File(examples, "Guardians of the Wood");
        File ancientDepths = new File(examples, "Ancient Depths");
        File dragonsRoar = new File(examples, "Dragon's Roar");
        File bloodHunger = new File(examples, "Blood Hunger"); //TODO
        File machinations = new File(examples, "Machinations");
        File unquenchableFire = new File(examples, "Unquenchable Fire");
        File apexPredators = new File(examples, "Apex Predators");

        for (String e : blooHungerCards) {
            save("/resources/cards/" + e, new File(bloodHunger, e));
        }


        //TODO --- remove
        String[] lands = {
            "Forest.jpg",
            "Island.jpg",
            "Mountain.jpg",
            "Plains.jpg",
        };
        for (String e : lands) {
            save("/resources/cards/" + e, new File(examples, e));
        }
        //TODO --- remove
    }

    private static void saveExampleDecks() {
        String[] decks = {"Blood Hunger.txt"}; //TODO

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

/** //TODO LIST
 * return random number from server (coin, die, specified borders)
 * choose a card at random from your hand
 * add +1/+1 and -1/-1 counters to cards
 * add notes to cards?
 */

/** //FIXME
 * !!! downloaded deck has incorrect name !!!
 * linux could not load a deck: IllegalArgumentException: Unicode
 * UI problems on some computers (use java one)
 */
