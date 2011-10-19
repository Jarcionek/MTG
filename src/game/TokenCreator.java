package game;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import mtg.Main;
import server.flags.CreateToken;

/**
 * @author Jaroslaw Pawlak
 */
public class TokenCreator {
    private static JCheckBox red;
    private static JCheckBox blue;
    private static JCheckBox white;
    private static JCheckBox black;
    private static JCheckBox green;
    private static JLabel nameLabel;
    private static JTextField nameField;
    private static JLabel typeLabel;
    private static JTextField typeField;
    private static JLabel descLabel;
    private static JTextArea descArea;
    private static JTextField stats;
    private static JCheckBox creature;    
    
    private TokenCreator() {}
    
    static CreateToken show(JFrame parent) {
        JPanel contentPane = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(2, 2, 2, 2);
        
        if (red == null) { // init
            
            red = new JCheckBox("Red");
            blue = new JCheckBox("Blue");
            white = new JCheckBox("White");
            black = new JCheckBox("Black");
            green = new JCheckBox("Green");
            nameLabel = new JLabel("Name:");
            nameField = new JTextField();
            typeLabel = new JLabel("Type:");
            typeField = new JTextField();
            
            descLabel = new JLabel("Description:");
            descLabel.setHorizontalAlignment(SwingConstants.CENTER);
                
            descArea = new JTextArea();
            descArea.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (descArea.getLineCount() == 5
                            && e.getKeyCode() == KeyEvent.VK_ENTER) {
                        e.consume();
                    }
                }
            });
            descArea.setBorder(nameField.getBorder());
            descArea.setFont(new Font("Arial", Font.PLAIN, 15));
            descArea.setRows(5);
            descArea.setLineWrap(true);
                
            stats = new JTextField("1/1");
            creature = new JCheckBox("Creature", true);
            creature.setHorizontalAlignment(SwingConstants.RIGHT);
            creature.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    stats.setEnabled(creature.isSelected());
                }
            });
        }

        
        c.gridy = 0;
        c.gridwidth = 1;
        contentPane.add(red, c);
        contentPane.add(blue, c);
        contentPane.add(white, c);
        contentPane.add(black, c);
        contentPane.add(green, c);
        
        c.gridy += 1;
        c.gridwidth = 1;
        contentPane.add(nameLabel, c);
        c.gridwidth = 4;
        contentPane.add(nameField, c);
        
        c.gridy += 1;
        c.gridwidth = 1;
        contentPane.add(typeLabel, c);
        c.gridwidth = 4;
        contentPane.add(typeField, c);
        
        c.gridy += 1;
        c.gridwidth = 5;
        contentPane.add(descLabel, c);
        c.gridy += 1;
        contentPane.add(descArea, c);
        
        c.gridy += 1;
        c.gridx = 2;
        c.gridwidth = 2;
        contentPane.add(creature, c);
        c.gridwidth = 1;
        c.gridx = 4;
        contentPane.add(stats, c);
        
        int choice = JOptionPane.showOptionDialog(parent, contentPane,
                Main.TITLE_MED, JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE, null,
                new String[] {"Create", "Cancel"}, 0);
        if (choice == 0) {
            int atk = 0;
            int def = 0;
            try {
                atk = Integer.parseInt(stats.getText().split("/")[0]);
            } catch (Exception ex) {}
            try {
                def = Integer.parseInt(stats.getText().split("/")[1]);
            } catch (Exception ex) {}
            return new CreateToken(red.isSelected(), blue.isSelected(),
                    white.isSelected(), black.isSelected(), green.isSelected(),
                    nameField.getText(), typeField.getText(),
                    descArea.getText(), creature.isSelected(), atk, def);
        }
        return null;
    }
}
