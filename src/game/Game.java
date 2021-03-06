package game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import mtg.Card;
import mtg.Main;
import mtg.Utilities;
import mtg.Zone;
import server.flags.CreateToken;
import server.flags.Message;
import server.flags.RandomValue;
import server.flags.RandomCard;
import server.flags.Restart;
import server.flags.UntapAll;

/**
 * @author Jaroslaw Pawlak
 */
class Game extends JFrame {
    static Client client;

    private Table table;
    private CardViewer hand;
    private PlayerInfo[] playersInfo;
    private CurrentPlayerLibrary playerLibrary;
    private Logger logger;
    private JTextField chat;
    
    private JButton exit;
    private JButton restart;
    private JButton coin;
    private JButton die;
    private JButton token;
    private JButton rand;
    private JButton untapAll;

    private static TreeMap<String, String> list;

    private Game() {}

    Game(int players, final Client client) {
        super(Main.TITLE_LONG);
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
        table = new Table();

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
                if (JOptionPane.showConfirmDialog(Game.this,
                        "Are you sure that you want\nto leave the game?",
                        Main.TITLE_SHORT, JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE)
                        == JOptionPane.YES_OPTION) {
                    client.closeClient();
                    Game.this.dispose();
                }
            }
        });
        
        restart = new JButton("Restart");
        restart.setFocusable(false);
        restart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(Game.this,
                        "Are you sure that you want to shuffle all your cards\n"
                        + "into your library and draw seven cards?\n"
                        + "This will also destroy all your tokens and reset "
                        + "your health.",
                        Main.TITLE_MED, JOptionPane.YES_NO_OPTION,
                        JOptionPane.PLAIN_MESSAGE)
                        == JOptionPane.YES_OPTION) {
                    client.send(new Restart());
                }
            }
        });
        
        coin = new JButton("Toss a coin");
        coin.setFocusable(false);
        coin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.send(new RandomValue(RandomValue.COIN));
            }
        });
        
        die = new JButton("Roll a die");
        die.setFocusable(false);
        die.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.send(new RandomValue(RandomValue.DIE));
            }
        });
        
        chat = new JTextField();
        chat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (chat.getText() != null && !chat.getText().equals("")) {
                    client.send(new Message(chat.getText()));
                    chat.setText("");
                }
            }
        });
        
        token = new JButton("Create token");
        token.setFocusable(false);
        token.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CreateToken ct = TokenCreator.show(Game.this);
                if (ct != null) {
                    client.send(ct);
                }
            }
        });
        
        rand = new JButton("<html>Choose random card<br>from your hand</html>");
        rand.setFocusable(false);
        rand.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.send(new RandomCard());
            }
        });
        
        untapAll = new JButton("Untap all");
        untapAll.setFocusable(false);
        untapAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.send(new UntapAll());
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

        JPanel loggerAndChat = new JPanel(new BorderLayout(0, 0));
        loggerAndChat.add(logger, BorderLayout.CENTER);
        loggerAndChat.add(chat, BorderLayout.SOUTH);
        
        JSplitPane right = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                playersInfoScrollPane, loggerAndChat);

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
        c.insets = new Insets(0, outside, between, outside);
        leftInnerPanel.add(untapAll, c);
        leftInnerPanel.add(token, c);
        leftInnerPanel.add(rand, c);
        leftInnerPanel.add(coin, c);
        leftInnerPanel.add(die, c);
        leftInnerPanel.add(restart, c);
        c.insets = new Insets(0, outside, outside, outside);
        leftInnerPanel.add(exit, c);
        
        JPanel leftOuterPanel = new JPanel(new BorderLayout());
        leftOuterPanel.add(leftInnerPanel, BorderLayout.CENTER);
        
        
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

///////////////////////////////////// INIT /////////////////////////////////////

    void addPlayer(String name) {
        for (int i = 0; i < playersInfo.length; i++) {
            if (playersInfo[i].nameLabel.getText().equals("...waiting...")) {
                playersInfo[i].nameLabel.setText(name);
                return;
            }
        }
    }

    void setPlayerLibrarySize(String playerName, int size) {
        for (int i = 0; i < playersInfo.length; i++) {
            if (playersInfo[i].nameLabel.getText().equals(playerName)) {
                playersInfo[i].handSizeValue.setText("0");
                playersInfo[i].healthPointsValue.setText("20");
                playersInfo[i].librarySizeValue.setText("" + size);
                playersInfo[i].poisonCountersValue.setText("0");
            }
        }
    }

    void setCardsList(TreeMap<String, String> list) {
        Game.list = list;
    }

///////////////////////////////// GUI MUTATORS /////////////////////////////////
    
    void createToken(CreateToken ct) {
        table.addCard(new Token(ct));
    }

    void cardAddToHand(String ID) {
        Card c = new Card(Utilities.findPath(list.get(ID)), ID);
        hand.addCard(c);
        hand.showCards(c);
    }

    void cardRemoveFromHand(String ID) {
        hand.removeCard(new Card(null, ID));
        hand.showCards(null);
    }
    
    void cardDiscardEntireHand() {
        hand.removeAllCards();
        hand.showCards(null);
    }

    void cardAddToTable(String ID) {
        table.addCard(new TCard(Utilities.findPath(list.get(ID)), ID));
    }

    void cardRemoveFromTable(String ID) {
        table.removeCard(ID);
    }

    void cardDragOnTable(String ID, int newx, int newy) {
        table.dragCard(ID, newx, newy);
    }

    void cardTap(String ID, boolean tapped) {
        table.tapCard(ID, tapped);
    }
    
    void cardUntapAll(int player) {
        table.untapAll(player);
    }

    void changeLibrarySize(int player, int by) {
        JLabel t = playersInfo[player].librarySizeValue;
        t.setText("" + (Integer.parseInt(t.getText()) + by));
    }

    void changeHandSize(int player, int by) {
        JLabel t = playersInfo[player].handSizeValue;
        t.setText("" + (Integer.parseInt(t.getText()) + by));
    }

    /**
     * Sets health of given player to given value and returns old value.
     * @param player player
     * @param newValue new health
     * @return old health
     */
    int playerSetHealth(int player, int newValue) {
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
    int playerSetPoison(int player, int newValue) {
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
    
    void restart(int player, int deckSize) {
        playersInfo[player].healthPointsValue.setText("20");
        playersInfo[player].librarySizeValue.setText("" + (deckSize - 7));
        playersInfo[player].poisonCountersValue.setText("0");
        playersInfo[player].handSizeValue.setText("7");
        table.removeCards(player);
    }

/////////////////////////// LOGGING AND INFORMATION ////////////////////////////

    void log(String first, String second, Color color) {
        logger.log(first == null? "" : first, second, color);
    }

    void log(String cardID, boolean onTable, String text, Color color) {
        logger.log(cardID, onTable, text, color);
    }

    String getPlayerName(int i) {
        return playersInfo[i].nameLabel.getText();
    }

    static String getCardName(String cardID) {
        String r = list.get(cardID);
        if (r == null) {
            return "token";
        } else {
            return r;
        }
    }
}
