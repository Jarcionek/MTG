package deckCreator;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import mtg.Card;
import mtg.Utilities;

/**
 * @author Jaroslaw Pawlak
 */
class Stats extends JPanel {
    private final GridBagConstraints cName = new GridBagConstraints();
    private final GridBagConstraints cMinus = new GridBagConstraints();
    private final GridBagConstraints cPlus = new GridBagConstraints();
    private final GridBagConstraints cEnlarge = new GridBagConstraints();

    private final DeckCreator parent;
    
    private final List<JLabel> labels;
    private final List<JButton> buttonsMinus;
    private final List<JButton> buttonsPlus;
    private final List<JButton> buttonsEnlarge;
    
    private final JLabel cardsTotalLabel;
    
    public Stats(DeckCreator deckCreator) {
        super(new GridBagLayout());
        
        this.parent = deckCreator;
        this.labels = new ArrayList<>();
        this.buttonsEnlarge = new ArrayList<>();
        this.buttonsMinus = new ArrayList<>();
        this.buttonsPlus = new ArrayList<>();
        
        cardsTotalLabel = new JLabel();
        cardsTotalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        cEnlarge.gridx = 0;
        cName.gridx = 1;
        cMinus.gridx = 2;
        cPlus.gridx = 3;
        cName.fill = GridBagConstraints.BOTH;
        cName.insets = new Insets(0, 2, 0, 2);
        
        refresh();
    }
    
    private void addEntry(final String name, int amount) {
        final JLabel nameLabel = new JLabel(name + ": " + amount);
        nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(nameLabel, cName);
        
        JButton enlarge = new JButton("?");
        enlarge.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Card(Utilities.findPath(name)).viewLarger();
            }
        });
        add(enlarge, cEnlarge);
        
        JButton minus = new JButton("-");
        minus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (parent.scv.removeCard(name)) {
                    modifyCard(name, -1);
                }
            }
        });
        add(minus, cMinus);
        
        JButton plus = new JButton("+");
        plus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (parent.lcv.addCard(name)) {
                    modifyCard(name, 1);
                }
            }
        });
        add(plus, cPlus);
        
        labels.add(nameLabel);
        buttonsEnlarge.add(enlarge);
        buttonsMinus.add(minus);
        buttonsPlus.add(plus);
        this.revalidate();
        this.repaint();
        
        int cardsTotal = Integer.parseInt(cardsTotalLabel.getText()
                .split(":")[1].substring(1));
        cardsTotalLabel.setText("Cards in deck: " + (cardsTotal + amount));
    }
    
    void modifyCard(String name, int amount) {
        boolean found = false;
        for (int i = 0; i < labels.size(); i++) {
            String cardName = labels.get(i).getText().split(":")[0];
            int cardAmount = Integer.parseInt(labels.get(i).getText()
                    .split(":")[1].substring(1));
            //we don't want to remove a card with 0 amount immediately from
            //stats, but in next modification, so it can be easily undone
            if (amount > 0) {
                if (cardName.equals(name)) {
                    labels.get(i).setText(name + ": " + (cardAmount + amount));
                    found = true;
                } else if (cardAmount == 0 && !Card.isBasicLand(cardName)) {
                    this.remove(labels.remove(i));
                    this.remove(buttonsEnlarge.remove(i));
                    this.remove(buttonsMinus.remove(i));
                    this.remove(buttonsPlus.remove(i));
                    this.validate();
                    this.repaint();
                }
            } else {
                if (cardAmount == 0 && !Card.isBasicLand(cardName)) {
                    this.remove(labels.remove(i));
                    this.remove(buttonsEnlarge.remove(i));
                    this.remove(buttonsMinus.remove(i));
                    this.remove(buttonsPlus.remove(i));
                    this.validate();
                    this.repaint();
                    i--;
                } else if (cardName.equals(name)) {
                    labels.get(i).setText(name + ": " + (cardAmount + amount));
                }
            }
        }
        if (amount > 0 && !found) {
            addEntry(name, amount);
        } else {
            int cardsTotal = Integer.parseInt(cardsTotalLabel.getText()
                    .split(":")[1].substring(1));
            cardsTotalLabel.setText("Cards in deck: " + (cardsTotal + amount));
        }
    }
    
    final void refresh() {
        labels.clear();
        buttonsEnlarge.clear();
        buttonsMinus.clear();
        buttonsPlus.clear();
        this.removeAll();
        
        cardsTotalLabel.setText("Cards in deck: 0");
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 4;
        c.insets = new Insets(0, 0, 10, 0);
        add(cardsTotalLabel, c);
        
        addEntry("Swamp", 0);
        addEntry("Plains", 0);
        addEntry("Mountain", 0);
        addEntry("Island", 0);
        addEntry("Forest", 0);
        
        c.gridy = 6;
        c.insets = new Insets(5, 0, 5, 0);
        add(new JSeparator(), c);
        
        for (int i = 0; i < parent.deck.getArraySize(); i++) {
            String name = parent.deck.getArrayNames(i);
            int amount = parent.deck.getArrayAmounts(i);
            if (Card.isBasicLand(name)) {
                modifyCard(name, amount);
            } else {
                addEntry(name, amount);
            }
        }
    }
    
}
