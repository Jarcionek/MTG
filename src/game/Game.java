package game;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import mtg.Card;
import mtg.Client;
import mtg.Deck;
import mtg.Library;
import mtg.Main;

/**
 * @author Jaroslaw Pawlak
 */
public class Game extends JFrame {
    private JFrame parentFrame;

    Client client;

    private Table table;
    private CardViewer hand;
    private PlayerInfo[] playersInfo;
    private CurrentPlayerLibrary playerLibrary;
    private Logger logger;


    private Game() {}

    /* //TODO client must receive number of players from the server with their
     * names and optionally table's size. Also shuffled library is required.
     *
     */
    public Game(JFrame parentFrame, String[] playersNames) {
        super(Main.TITLE);
        this.parentFrame = parentFrame;
        this.parentFrame.setVisible(false);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Game.this.parentFrame.setVisible(true);
            }
        });

        createGUIComponents(playersNames);
        createGUILayout();

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setUndecorated(true);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setVisible(true);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                table.centerView();
            }
        });
    }

    private void createGUIComponents(String[] playersNames) {
        if (playersNames.length == 2) {
            table = new Table(Table.TWO_PLAYERS);
        } else if (playersNames.length <= 4) {
            table = new Table(Table.FOUR_PLAYERS);
        } else if (playersNames.length <= 6) {
            table = new Table(Table.SIX_PLAYERS);
        } else {
            table = new Table(Table.EIGHT_PLAYERS);
        }

        hand = new CardViewer(new InSearcherMouseListener(Zone.HAND));

        playersInfo = new PlayerInfo[playersNames.length];
        for (int i = 0; i < playersInfo.length; i++) {
            playersInfo[i] = new PlayerInfo(playersNames[i]);
        }

        playerLibrary = new CurrentPlayerLibrary();

        logger = new Logger();
    }

    private void createGUILayout() {
        JPanel playersInfoPanel
                = new JPanel(new GridLayout(playersInfo.length, 1));
        for (PlayerInfo e : playersInfo) {
            playersInfoPanel.add(e);
        }

        JScrollPane playersInfoScrollPane = new JScrollPane(
                playersInfoPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //some place for scroll bar
        playersInfoScrollPane.setPreferredSize(new Dimension(
                playersInfoScrollPane.getPreferredSize().width + 20,
                playersInfoScrollPane.getPreferredSize().height));

        JScrollPane loggerScrollPane = new JScrollPane(logger,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JSplitPane right = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                playersInfoScrollPane, loggerScrollPane);

        JPanel handPanel = new JPanel(new GridLayout(1, 1));
        handPanel.add(hand);
        handPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(handPanel, BorderLayout.CENTER);
        bottom.add(playerLibrary, BorderLayout.EAST);

        JPanel center = new JPanel(new BorderLayout());
        center.add(table, BorderLayout.CENTER);
        center.add(bottom, BorderLayout.SOUTH);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(center, BorderLayout.CENTER);
        contentPane.add(right, BorderLayout.EAST);
        this.setContentPane(contentPane);
    }


    public static void main(String[] as) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        Game x = new Game(new JFrame(), new String[] {"Jarcionek", "Kenoicraj"});

        Deck deck = new Deck();
        deck.addCard("Ezuri's Archers", 4);
        deck.addCard("Joraga Treespeaker", 1);
        deck.addCard("Joraga Warcaller", 1);
        deck.addCard("Scattershot Archer", 2);
        deck.addCard("Twinblade Slasher", 2);
        deck.addCard("Bramblewood Paragon", 1);
        Library library = new Library(deck);

        Card c;
        while ((c = library.draw()) != null) {
            x.hand.addCard(c);
        }

        x.hand.showCards(null);
    }
}
