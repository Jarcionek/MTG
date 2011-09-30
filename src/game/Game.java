package game;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import mtg.Card;
import mtg.Main;
import mtg.Utilities;
import server.Client;

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

    TreeMap<String, String> list;

    private Game() {}

    public Game(int players, Client client) {
        super(Main.TITLE);
        Game.client = client;

        createGUIComponents(players);
        createGUILayout();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
            playersInfo[i] = new PlayerInfo("...waiting...");
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

//        JPanel contentPane = new JPanel(new BorderLayout());
//        contentPane.add(center, BorderLayout.CENTER);
//        contentPane.add(right, BorderLayout.EAST);
        right.setMinimumSize(right.getPreferredSize());
        center.setMinimumSize(new Dimension(Card.W*2, Card.H));
        JSplitPane contentPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, center, right);
        contentPane.setDividerLocation(
                Toolkit.getDefaultToolkit().getScreenSize().width
                - right.getPreferredSize().width);

        this.setContentPane(contentPane);
    }

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
        this.list = list;
    }

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
        
    }

    public void cardDragOnTable(String ID, int newx, int newy) {
        table.dragCard(ID, newx, newy);
    }

    public void cardTap(String ID, boolean tapped) {
        table.tapCard(ID, tapped);
    }



    public void log(String text) {
        logger.log(text);
//        String temp = text.substring(text.lastIndexOf(" ") + 1);
//        System.out.println(temp); //TODO remove
//        logger.log(list.get(temp));
    }


    public String getCardName(String cardID) {
        return list.get(cardID);
    }

    public String getPlayerName(int i) {
//        System.out.println("//TODO player " + i + " is " + playersInfo[i].nameLabel.getText());
        return playersInfo[i].nameLabel.getText();
    }
}
