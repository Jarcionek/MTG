package game;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import mtg.Zone;
import server.flags.Search;

/**
 * @author Jaroslaw Pawlak
 */
public class PlayerInfo extends JPanel {

    private int playerID;

    /**
     * name of a player
     */
    JLabel nameLabel;
    private JLabel handSizeLabel;
    JLabel handSizeValue;
    private JLabel healthPointsLabel;
    JLabel healthPointsValue;
    private JLabel librarySizeLabel;
    JLabel librarySizeValue;
    private JLabel poisonCountersLabel;
    JLabel poisonCountersValue;
    private JButton viewGraveyardButton;
    private JButton viewExiledButton;
    private JButton poisonButton;
    private JButton healthButton;

    public PlayerInfo(int playerID, String playerName) {
        super();
        this.playerID = playerID;
        createComponents(playerName);
        createGUI();
    }

    private void createComponents(String playerName) {
        JLabel[] jlabels = {
            nameLabel = new JLabel(playerName),
            handSizeLabel = new JLabel("Hand size"),
            healthPointsLabel = new JLabel("Health points"),
            librarySizeLabel = new JLabel("Library size"),
            poisonCountersLabel = new JLabel("Poison counters"),
            handSizeValue = new JLabel("..."),
            healthPointsValue = new JLabel("..."),
            librarySizeValue = new JLabel("..."),
            poisonCountersValue = new JLabel("..."),
        };
        for (JLabel e : jlabels) {
            e.setHorizontalAlignment(JLabel.CENTER);
        }
        
        viewGraveyardButton = new JButton("Graveyard");
        viewGraveyardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Game.client.send(
                        new Search(-1, null, Zone.GRAVEYARD, -1, playerID));
            }
        });
        viewGraveyardButton.setFocusable(false);
        
        viewExiledButton = new JButton("Exiled");
        viewExiledButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        viewExiledButton.setFocusable(false);

        poisonButton = new JButton("Poison");
        poisonButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        poisonButton.setFocusable(false);

        healthButton = new JButton("Health");
        healthButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        healthButton.setFocusable(false);
    }

    private void createGUI() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        int outside = 2;
        int between = 3;
        int b = 5;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridwidth = 2;
        c.gridy = 0;
        c.insets = new Insets(outside, outside, between, outside);
        nameLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.RAISED),
                BorderFactory.createEmptyBorder(b, b, b, b)));
        this.add(nameLabel, c);

        c.gridwidth = 1;
        c.gridy += 1;
        c.insets = new Insets(0, outside, between, between);
        JPanel health = new JPanel(new GridLayout(2, 1));
        health.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.RAISED),
                BorderFactory.createEmptyBorder(b, b, b, b)));
        health.add(healthPointsLabel);
        health.add(healthPointsValue);
        this.add(health, c);

        c.insets = new Insets(0, 0, between, outside);
        JPanel library = new JPanel(new GridLayout(2, 1));
        library.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.RAISED),
                BorderFactory.createEmptyBorder(b, b, b, b)));
        library.add(librarySizeLabel);
        library.add(librarySizeValue);
        this.add(library, c);

        c.gridy += 1;
        c.insets = new Insets(0, outside, between, between);
        JPanel poison = new JPanel(new GridLayout(2, 1));
        poison.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.RAISED),
                BorderFactory.createEmptyBorder(b, b, b, b)));
        poison.add(poisonCountersLabel);
        poison.add(poisonCountersValue);
        this.add(poison, c);

        c.insets = new Insets(0, 0, between, outside);
        JPanel hand = new JPanel(new GridLayout(2, 1));
        hand.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.RAISED),
                BorderFactory.createEmptyBorder(b, b, b, b)));
        hand.add(handSizeLabel);
        hand.add(handSizeValue);
        this.add(hand, c);

        c.gridy += 1;
        c.insets = new Insets(0, outside, between, between);
        this.add(viewGraveyardButton, c);
        c.insets = new Insets(0, 0, between, outside);
        this.add(viewExiledButton, c);

        c.gridy += 1;
        c.insets = new Insets(0, outside, outside, between);
        this.add(poisonButton, c);
        c.insets = new Insets(0, 0, outside, outside);
        this.add(healthButton, c);
    }
}
