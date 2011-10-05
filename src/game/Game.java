package game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.util.TreeMap;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import mtg.Card;
import mtg.Main;
import mtg.Utilities;
import mtg.Zone;

/**
 * @author Jaroslaw Pawlak
 */
public class Game extends JFrame {
    static Client client;

    private Table table;
    private CardViewer hand;
    private PlayerInfo[] playersInfo;
    private CurrentPlayerLibrary playerLibrary;
    private Logger logger;
    
    private JButton exit;

    private static TreeMap<String, String> list;

    private Game() {}

    public Game(int players, final Client client) {
        super(Main.TITLE);
        Game.client = client;

        createGUIComponents(players);
        createGUILayout();

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                client.closeClient();
            }
        });
        this.setUndecorated(true);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setVisible(true);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                table.centerView();
            }
        });
        
        client.start();
    }

    private void createGUIComponents(int players) {
        if (players == 2) {
            table = new Table(Table.TWO_PLAYERS);
        } else if (players <= 4) {
            table = new Table(Table.FOUR_PLAYERS);
        } else if (players <= 6) {
            table = new Table(Table.SIX_PLAYERS);
        } else {
            table = new Table(Table.EIGHT_PLAYERS);
        }

        hand = new CardViewer(new InSearcherMouseAdapter(Zone.HAND));

        playersInfo = new PlayerInfo[players];
        for (int i = 0; i < playersInfo.length; i++) {
            playersInfo[i] = new PlayerInfo(i, "...waiting...");
        }

        playerLibrary = new CurrentPlayerLibrary();

        logger = new Logger(table);
        
        exit = new JButton("Leave game");
        exit.setFocusable(false);
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.closeClient();
                Game.this.dispose();
            }
        });
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

        JSplitPane right = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                playersInfoScrollPane, logger);

        JPanel handPanel = new JPanel(new GridLayout(1, 1));
        handPanel.add(hand);
        handPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

        
        JPanel leftInnerPanel = new JPanel(new GridBagLayout()); //TODO move it somewhere else
        int outside = 2;
        int between = 3;
        int b = 5;
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.BOTH;
        JLabel title = new JLabel("Menu");
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.RAISED),
                BorderFactory.createEmptyBorder(b, b, b, b)));
        c.insets = new Insets(outside, outside, between, outside);
        leftInnerPanel.add(title, c);
        c.insets = new Insets(0, outside, outside, outside);
        leftInnerPanel.add(exit, c);
        
        JPanel leftOuterPanel = new JPanel(new BorderLayout());
        leftOuterPanel.add(leftInnerPanel, BorderLayout.NORTH);
        
        
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(leftOuterPanel, BorderLayout.WEST);
        bottom.add(handPanel, BorderLayout.CENTER);
        bottom.add(playerLibrary, BorderLayout.EAST);

        JPanel center = new JPanel(new BorderLayout());
        center.add(table, BorderLayout.CENTER);
        center.add(bottom, BorderLayout.SOUTH);

        right.setMinimumSize(right.getPreferredSize());
        center.setMinimumSize(new Dimension(Card.W*2, Card.H));
        JSplitPane contentPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, center, right);
        contentPane.setDividerLocation(
                Toolkit.getDefaultToolkit().getScreenSize().width
                - right.getPreferredSize().width);

        this.setContentPane(contentPane);
    }

    Table getTable() {
        return table;
    }

////////////////////////////////////////////////////////////////////////////////

    public void addPlayer(String name) {
        for (int i = 0; i < playersInfo.length; i++) {
            if (playersInfo[i].nameLabel.getText().equals("...waiting...")) {
                playersInfo[i].nameLabel.setText(name);
                return;
            }
        }
    }

    public void setPlayerLibrarySize(String playerName, int size) {
        for (int i = 0; i < playersInfo.length; i++) {
            if (playersInfo[i].nameLabel.getText().equals(playerName)) {
                playersInfo[i].handSizeValue.setText("0");
                playersInfo[i].healthPointsValue.setText("20");
                playersInfo[i].librarySizeValue.setText("" + size);
                playersInfo[i].poisonCountersValue.setText("0");
            }
        }
    }

    public void setCardsList(TreeMap<String, String> list) {
        Game.list = list;
    }

////////////////////////////////////////////////////////////////////////////////

    public void cardAddToHand(String ID) {
        Card c = new Card(Utilities.findPath(list.get(ID)), ID);
        hand.addCard(c);
        hand.showCards(c);
    }

    public void cardRemoveFromHand(String ID) {
        hand.removeCard(new Card(Utilities.findPath(list.get(ID)), ID));
        hand.showCards(null);
    }

    public void cardAddToTable(String ID) {
        table.addCard(new Card(Utilities.findPath(list.get(ID)), ID));
    }

    public void cardRemoveFromTable(String ID) {
        table.removeCard(ID);
    }

    public void cardDragOnTable(String ID, int newx, int newy) {
        table.dragCard(ID, newx, newy);
    }

    public void cardTap(String ID, boolean tapped) {
        table.tapCard(ID, tapped);
    }

    public void changeLibrarySize(int player, int by) {
        JLabel t = playersInfo[player].librarySizeValue;
        t.setText("" + (Integer.parseInt(t.getText()) + by));
    }

    public void changeHandSize(int player, int by) {
        JLabel t = playersInfo[player].handSizeValue;
        t.setText("" + (Integer.parseInt(t.getText()) + by));
    }

    /**
     * Sets health of given player to given value and returns old value.
     * @param player player
     * @param newValue new health
     * @return old health
     */
    public int playerSetHealth(int player, int newValue) {
        int t = Integer.parseInt(playersInfo[player].healthPointsValue.getText());
        playersInfo[player].healthPointsValue.setText("" + newValue);
        return t;
    }

    /**
     * Sets posion counters of given player to given value and returns old value.
     * @param player player
     * @param newValue new amount of poison counters
     * @return old amount of poison counters
     */
    public int playerSetPoison(int player, int newValue) {
        int t = Integer.parseInt(playersInfo[player].poisonCountersValue.getText());
        playersInfo[player].poisonCountersValue.setText("" + newValue);
        return t;
    }
    
    void kill(int player) {
        playersInfo[player].nameLabel.setText("(dead) "
                + playersInfo[player].nameLabel.getText());
        playersInfo[player].handSizeValue.setText("0");
        playersInfo[player].healthPointsValue.setText("0");
        playersInfo[player].librarySizeValue.setText("0");
        playersInfo[player].poisonCountersValue.setText("0");
        table.removeCards(player);
    }

////////////////////////////////////////////////////////////////////////////////

    public void log(String first, String second, Color color) {
        logger.log(first, second, color);
    }

    public void log(String cardID, boolean onTable, String text, Color color) {
        logger.log(cardID, onTable, text, color);
    }

    public static String getCardName(String cardID) {
        return list.get(cardID);
    }

    public String getPlayerName(int i) {
        return playersInfo[i].nameLabel.getText();
    }
}
