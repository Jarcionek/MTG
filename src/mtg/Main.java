package mtg;

import deckCreator.DeckCreator;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import server.Server;

/**
 * @author Jaroslaw Pawlak
 */
public class Main extends JFrame {
    public static final String TITLE = "MTG";
    public static final String VERSION = "beta 1.2";

    public static final File DIRECTORY
            = new File(System.getProperty("user.dir"), "MTG");
    public static final File CARDS = new File(DIRECTORY, "Cards");
    public static final File CARDS_DL = new File(CARDS, "Download");
    public static final File DECKS = new File(DIRECTORY, "Decks");
    public static final File DECKS_DL = new File(DECKS, "Download");
    
    private JButton minim;
    private JLabel versionLabel;
    private JLabel authorLabel;
    private JLabel wofcLabel1;
    private JLabel wofcLabel2;
    private JTextArea coming;
    private JButton deckCreator;
    private JButton exit;
    private JButton createGame;
    private JButton joinGame;
    private JButton settingsButton;
    private JButton helpButton;
    private JLabel background;
    
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
            Resources.saveExampleCards();
            Resources.saveExampleDecks();
        }
        CARDS.mkdirs();
        CARDS_DL.mkdirs();
        DECKS.mkdirs();
        
        Settings.load();
        
        new Main();
    }

    private Main() {
        super(TITLE);
        
        createComponents();
        this.setContentPane(createGUI());
        
        this.addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent e) {
                switch (Main.this.getExtendedState()) {
                    case JFrame.MAXIMIZED_BOTH:
                    case JFrame.MAXIMIZED_HORIZ:
                    case JFrame.MAXIMIZED_VERT:
                    Main.this.setExtendedState(JFrame.NORMAL);
                }
            }
        });
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });

        this.setUndecorated(true);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setSize(750, 450);
        this.setResizable(false);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((d.width - this.getSize().width) / 2,
                          (d.height - this.getSize().height) / 2);
        this.setVisible(true);
    }
    
    private void createComponents() {
        minim = new JButton("_");
        minim.setOpaque(false);
        minim.setFocusable(false);
        minim.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.this.setState(JFrame.ICONIFIED);
            }
        });
        
        versionLabel = new JLabel(VERSION);
        versionLabel.setForeground(Color.white);
        
        authorLabel = new JLabel("Made by Jaroslaw Pawlak");
        authorLabel.setForeground(Color.white);
        
        wofcLabel1 = new JLabel("All names and images are the");
        wofcLabel1.setForeground(Color.lightGray);
        
        wofcLabel2 = new JLabel("property of Wizards of the Coast");
        wofcLabel2.setForeground(Color.lightGray);
        
        coming = new JTextArea(
                "Coming up:\n" +
                "- more cards and decks\n" +
                "- putting counters on permanents\n" +
                "- music\n" +
                "- better graphics\n" +
                "- improved deck creator"
                );
        coming.setFocusable(false);
        coming.setEditable(false);
        coming.setOpaque(false);
        coming.setFont(new Font("Arial", Font.PLAIN, 12));
        coming.setForeground(Color.yellow);
        
        deckCreator = new JButton("Deck creator");
        deckCreator.setOpaque(false);
        deckCreator.setFocusable(false);
        deckCreator.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new DeckCreator(TITLE + ": Deck Creator", Main.this);
            }
        });
        
        exit = new JButton("Exit");
        exit.setOpaque(false);
        exit.setFocusable(false);
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });
        
        createGame = new JButton("Create game");
        createGame.setOpaque(false);
        createGame.setFocusable(false);
        createGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Server.getStatus() != Server.DEAD) {
                    int c = JOptionPane.showConfirmDialog(Main.this,
                            "Server is already running.\n"
                            + "Do you want to close it?", Main.TITLE,
                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (c == JOptionPane.YES_OPTION) {
                        Server.closeServer();
                    }
                } else {
                    ServerFrame.show(Main.this) ;
                }
            }
        });
        
        joinGame = new JButton("Join game");
        joinGame.setOpaque(false);
        joinGame.setFocusable(false);
        joinGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JoinGameFrame.show(Main.this);
            }
        });
        
        settingsButton = new JButton("Settings");
        settingsButton.setOpaque(false);
        settingsButton.setFocusable(false);
        settingsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Settings.openSettingsPanel(Main.this);
            }
        });
        
        helpButton = new JButton("Help");
        helpButton.setOpaque(false);
        helpButton.setFocusable(false);
        helpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Help.show(Main.this);
            }
        });
        
        background = new JLabel(new ImageIcon(
                Main.class.getResource("/resources/Background.jpg")));
    }
    
    private JPanel createGUI() {
        JPanel contentPane = new JPanel(null);
        
        minim.setBounds(750 - minim.getPreferredSize().width + 5, -5,
                minim.getPreferredSize().width, minim.getPreferredSize().height);
        contentPane.add(minim);
        
        versionLabel.setBounds(5, 5,
                versionLabel.getPreferredSize().width,
                versionLabel.getPreferredSize().height);
        contentPane.add(versionLabel);
        
        authorLabel.setBounds(750 - authorLabel.getPreferredSize().width
                - minim.getPreferredSize().width, 5,
                authorLabel.getPreferredSize().width,
                authorLabel.getPreferredSize().height);
        contentPane.add(authorLabel);
        
        wofcLabel1.setBounds(745 - wofcLabel1.getPreferredSize().width,
                authorLabel.getBounds().y + authorLabel.getPreferredSize().height + 2,
                wofcLabel1.getPreferredSize().width,
                wofcLabel1.getPreferredSize().height);
        contentPane.add(wofcLabel1);
        
        wofcLabel2.setBounds(745 - wofcLabel2.getPreferredSize().width,
                wofcLabel1.getBounds().y + wofcLabel1.getPreferredSize().height + 2,
                wofcLabel2.getPreferredSize().width,
                wofcLabel2.getPreferredSize().height);
        contentPane.add(wofcLabel2);
        
        coming.setBounds(15, 145, coming.getPreferredSize().width,
                coming.getPreferredSize().height);
        contentPane.add(coming);
        
        deckCreator.setBounds(40, 60,
                deckCreator.getPreferredSize().width,
                deckCreator.getPreferredSize().height);
        contentPane.add(deckCreator);
        
        exit.setBounds(60, 405,
                exit.getPreferredSize().width,
                exit.getPreferredSize().height);
        contentPane.add(exit);
        
        createGame.setBounds(325, 35,
                createGame.getPreferredSize().width,
                createGame.getPreferredSize().height);
        contentPane.add(createGame);
        
        joinGame.setBounds(625, 75,
                joinGame.getPreferredSize().width,
                joinGame.getPreferredSize().height);
        contentPane.add(joinGame);
        
        settingsButton.setBounds(640, 380,
                settingsButton.getPreferredSize().width,
                settingsButton.getPreferredSize().height);
        contentPane.add(settingsButton);
        
        helpButton.setBounds(
                settingsButton.getBounds().x + (settingsButton.getBounds().width
                - helpButton.getPreferredSize().width) / 2,
                settingsButton.getBounds().y - settingsButton.getBounds().height - 5,
                helpButton.getPreferredSize().width,
                helpButton.getPreferredSize().height);
        contentPane.add(helpButton);
        
        background.setBounds(0, 0, 750, 450);
        contentPane.add(background);
        
        return contentPane;
    }
    
    private void exit() {
        switch (Server.getStatus()) {
            case Server.PLAYERS_CONNECTED:
                if (JOptionPane.showConfirmDialog(Main.this,
                        "The server is running and the game is in progress.\n"
                        + "Do you want to exit anyway?", Main.TITLE,
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)
                        != JOptionPane.YES_OPTION) {
                    Server.closeServer();
                    return;
                }
                break;
            case Server.RUNNING:
                if (JOptionPane.showConfirmDialog(Main.this,
                        "The server is running, but\n"
                        + "the game has not yet started.\n"
                        + "Are you sure that you want to exit?", Main.TITLE,
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)
                        != JOptionPane.YES_OPTION) {
                    Server.closeServer();
                    return;
                }
                break;
        }
        System.exit(0);
    }
}

/** //TODO LIST
 * 
 * proof read help
 * 
 * bottom library
 * add +1/+1 and -1/-1 counters to cards
 * restart with specified number of cards
 * splash screen
 * 
 * add notes to cards?
 * 
 * ----- ----- ----- ----- ----- RECREATE GAME GUI ----- ----- ----- ----- -----
 * divide InSearcherListener into X listeners
 *          - reveal card from hand
 *          - put card on the bottom
 * music?
 * PlayerX taps CardY - change into PlayerX taps his CardY etc (also in MoveCard)
 * TABLE should have a fixed size while card's position should be recalculated
 *          while placing a card or sending an Action to server
 * ----- ----- ----- ----- ----- DECK CREATOR ----- ----- ----- ----- ----- ----
 * deck creator - cards can be easily added or removed from the deck by
 *          accident, especially while viewing larger cards
 * better basic lands management
 * statistics on the right
 * show all files instead of tree
 */

/** //FIXME LIST
 * card is shaking while being dragged in large zoom
 * table is shaking when dragged to the border of it
 * 
 * logger sometimes does not scroll correctly when changing health or poison
 * deck creator - small cards viewer still crashes occasionally while scrolling
 */