package mtg;

import deckCreator.DeckCreator;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import javax.swing.JTextArea;
import javax.swing.UIManager;
import server.ServerFrame;

/**
 * @author Jaroslaw Pawlak
 */
public class Main extends JFrame {
    public static final String VERSION = "beta 1.01";

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

        JLabel versionLabel = new JLabel(VERSION);
        versionLabel.setForeground(Color.white);
        versionLabel.setBounds(5, 5,
                versionLabel.getPreferredSize().width,
                versionLabel.getPreferredSize().height);
        contentPane.add(versionLabel);

        JLabel authorLabel = new JLabel("Made by Jaroslaw Pawlak");
        authorLabel.setForeground(Color.white);
        authorLabel.setBounds(745 - authorLabel.getPreferredSize().width, 5,
                authorLabel.getPreferredSize().width,
                authorLabel.getPreferredSize().height);
        contentPane.add(authorLabel);

        JLabel label1 = new JLabel("All names and images are the");
        label1.setForeground(Color.lightGray);
        label1.setBounds(745 - label1.getPreferredSize().width,
                authorLabel.getBounds().y + authorLabel.getPreferredSize().height + 2,
                label1.getPreferredSize().width,
                label1.getPreferredSize().height);
        contentPane.add(label1);

        JLabel label2 = new JLabel("property of Wizards of the Coast");
        label2.setForeground(Color.lightGray);
        label2.setBounds(745 - label2.getPreferredSize().width,
                label1.getBounds().y + label1.getPreferredSize().height + 2,
                label2.getPreferredSize().width,
                label2.getPreferredSize().height);
        contentPane.add(label2);

        JTextArea ta = new JTextArea(
                "Coming up:\n" +
                "- more cards and decks\n" +
                "- saving player name and active deck\n" +
                "- changing cards' sizes\n" +
                "- tossing a coin, rolling a die\n" +
                "- choosing a card at random\n" +
                "- putting counters on permanents\n" +
                "- music\n" +
                "- improved deck creator"
                );
        ta.setFocusable(false);
        ta.setEditable(false);
        ta.setOpaque(false);
        ta.setFont(new Font("Arial", Font.PLAIN, 12));
        ta.setForeground(Color.yellow);
        ta.setBounds(10, 140, ta.getPreferredSize().width, ta.getPreferredSize().height);
        contentPane.add(ta);

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
                String name = JOptionPane.showInputDialog("your name:");
                JFileChooser jfc = new JFileChooser(DECKS);
                jfc.setMultiSelectionEnabled(false);
                jfc.showOpenDialog(Main.this);
                File f = jfc.getSelectedFile();
                Deck deck = Deck.load(f);
                String ip = JOptionPane.showInputDialog(Main.this, "IP:", "localhost");
                int port = Integer.parseInt(
                        JOptionPane.showInputDialog(Main.this, "port:", "56789"));
                try {
                    new game.Client(name, ip, port, deck);
                    Main.this.setVisible(false);
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        contentPane.add(joinGame);

        //TODO temp
        JButton quickOnePlayerStart = new JButton("Quick single player game");
        quickOnePlayerStart.setOpaque(false);
        quickOnePlayerStart.setFocusable(false);
        quickOnePlayerStart.setBounds(580, 380,
                quickOnePlayerStart.getPreferredSize().width,
                quickOnePlayerStart.getPreferredSize().height);
        quickOnePlayerStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    server.Server.start(56789, 1);
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                Main.this.dispose();
                Deck d = Deck.load(new File(DECKS, "Blood Hunger.txt"));
                try {
                    new game.Client("Single_Player", "localhost", 56789, d);
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        contentPane.add(quickOnePlayerStart);

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
        String[] wieldingSteelCards = {
            "Brave the Elements.jpg",
            "Elite Vanguard.jpg",
            "Gideon's Lawkeeper.jpg",
            "Infiltration Lens.jpg",
            "Kitesail Apprentice.jpg",
            "Kor Duelist.jpg",
            "Trusty Machete.jpg",
            "Angel's Feather.jpg",
            "Glory Seeker.jpg",
            "Kitesail.jpg",
            "Kor Outfitter.jpg",
            "Puresteel Paladin.jpg",
            "Revoke Existence.jpg",
            "Stoneforge Mystic.jpg",
            "Sunspear Shikari.jpg",
            "Arrest.jpg",
            "Gideon's Avenger.jpg",
            "Kor Hookmaster.jpg",
            "Pennon Blade.jpg",
            "Strider Harness.jpg",
            "Sword of War and Peace.jpg",
            "Congregate.jpg",
            "Harmless Assault.jpg",
            "Baneslayer Angel.jpg",
            "Conqueror's Pledge.jpg",
            "Serra Angel.jpg",
            "Argentum Armor.jpg",
            "Captain of the Watch.jpg",
            "Archangel of Strife.jpg",
            "Plains.jpg",
        };
        String[] realmOfIllusionCards = {};
        String[] strengthOfStoneCards = {};
        String[] guardiansOfTheWoodCards = {
            "Elvish Eulogist.jpg",
            "Elvish Lyrist.jpg",
            "Ezuri's Archers.jpg",
            "Joraga Warcaller.jpg",
            "Might of the Masses.jpg",
            "Norwood Ranger.jpg",
            "Elvish Visionary.jpg",
            "Nissa's Chosen.jpg",
            "Plummet.jpg",
            "Sylvan Ranger.jpg",
            "Viridian Emissary.jpg",
            "Eyeblight's Ending.jpg",
            "Ezuri, Renegade Leader.jpg",
            "Imperious Perfect.jpg",
            "Jagged-Scar Archers.jpg",
            "Maelstrom Pulse.jpg",
            "Titania's Chosen.jpg",
            "Viridian Shaman.jpg",
            "Elvish Promenade.jpg",
            "Heedless One.jpg",
            "Lys Alana Huntmaster.jpg",
            "Wildheart Invoker.jpg",
            "Elven Riders.jpg",
            "Essence Drain.jpg",
            "Nath of the Gilt-Leaf.jpg",
            "Epic Proportions.jpg",
            "Forest.jpg",
            "Swamp.jpg",
        };
        String[] ancientDepthsCards = {};
        String[] dragonsRoarCards = {};
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
        String[] machinationsCards = {
            "Terramorphic Expanse.jpg",
            "Signal Pest.jpg",
            "Alpha Myr.jpg",
            "Etherium Sculptor.jpg",
            "Golem's Heart.jpg",
            "Go for the Throat.jpg",
            "Gust-Skimmer.jpg",
            "Hunger of the Nim.jpg",
            "Steel Overseer.jpg",
            "Tidehollow Strix.jpg",
            "Darksteel Plate.jpg",
            "Dead Reckoning.jpg",
            "Dispense Justice.jpg",
            "Etched Champion.jpg",
            "Master of Etherium.jpg",
            "Pilgrim's Eye.jpg",
            "Snapsail Glider.jpg",
            "Stoic Rebuttal.jpg",
            "Undermine.jpg",
            "Sanctum Gargoyle.jpg",
            "Seer's Sundial.jpg",
            "Shape Anew.jpg",
            "Sleep.jpg",
            "Mirrorworks.jpg",
            "Psychosis Crawler.jpg",
            "Stone Golem.jpg",
            "Venser's Journal.jpg",
            "Razorfield Rhino.jpg",
            "Wurmcoil Engine.jpg",
            "Magister Sphinx.jpg",
            "Razorfield Thresher.jpg",
            "Soulquake.jpg",
            "Darksteel Colossus.jpg",
            "Swamp.jpg",
            "Island.jpg",
            "Plains.jpg",
        };
        String[] unquenchableCards = {};
        String[] apexPredatorsCards = {};

        File examples = new File(CARDS, "Example");

        File wieldingSteel = new File(examples, "Wielding Steel");
        File realmOfIllusion = new File(examples, "Realm of Illusion");
        File strengthOfStone = new File(examples, "Strength of Stone");
        File guardiansOfTheWood = new File(examples, "Guardians of the Wood");
        File ancientDepths = new File(examples, "Ancient Depths");
        File dragonsRoar = new File(examples, "Dragon's Roar");
        File bloodHunger = new File(examples, "Blood Hunger");
        File machinations = new File(examples, "Machinations");
        File unquenchableFire = new File(examples, "Unquenchable Fire");
        File apexPredators = new File(examples, "Apex Predators");

        for (String e : wieldingSteelCards) {
            save("/resources/cards/" + e, new File(wieldingSteel, e));
        }
        for (String e : realmOfIllusionCards) {
            save("/resources/cards/" + e, new File(realmOfIllusion, e));
        }
        for (String e : strengthOfStoneCards) {
            save("/resources/cards/" + e, new File(strengthOfStone, e));
        }
        for (String e : guardiansOfTheWoodCards) {
            save("/resources/cards/" + e, new File(guardiansOfTheWood, e));
        }
        for (String e : ancientDepthsCards) {
            save("/resources/cards/" + e, new File(ancientDepths, e));
        }
        for (String e : dragonsRoarCards) {
            save("/resources/cards/" + e, new File(dragonsRoar, e));
        }
        for (String e : blooHungerCards) {
            save("/resources/cards/" + e, new File(bloodHunger, e));
        }
        for (String e : machinationsCards) {
            save("/resources/cards/" + e, new File(machinations, e));
        }
        for (String e : unquenchableCards) {
            save("/resources/cards/" + e, new File(unquenchableFire, e));
        }
        for (String e : apexPredatorsCards) {
            save("/resources/cards/" + e, new File(apexPredators, e));
        }
    }

    private static void saveExampleDecks() {
        String[] decks = {
            "Blood Hunger.txt",
            "Guardians of the Wood.txt",
            "Machinations.txt",
            "Wielding Steel.txt",
        }; //TODO

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
 * DECKS!
 * Settings 1: name - by default computer name
 * Settings 3: active deck (here?)
 * finish "create server" and "join to game" options
 * Settings 4: card size multiplier (add static in Card and multiply by it
 *          whenever card.W or H is used and for table size and cards positions)
 * player disconnected - move all his cards into exiled
 * return random number from server (coin, die, specified borders)
 * choose a card at random from your hand
 * add +1/+1 and -1/-1 counters to cards
 * add notes to cards?
 * chat?
 * game exit button
 * in ServerFrame add copy button which copies IP with port into the clipboard
 * Settings 2: music volume
 * PlayerX taps CardY - change into PlayerX taps his CardY etc
 *          move requestor field into Action class
 * finish deckCreator:
 *          - better basic lands managament
 *          - statistics on the right
 */

/** //FIXME
 * logger does not scroll correctly when changing health or poison
 * deck creator - small cards viewer still crashes occasionally while scrolling
 * deck creator - cards can be easily added or removed from the deck by
 *          accident, especially while viewing larger cards
 */

/** //FIXME problems at university computer
 * linux could not load a deck: IllegalArgumentException: Unicode
 * UI problems on some computers (use java one)
 */