package game;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import mtg.Card;
import mtg.Utilities;

/**
 * @author Jaroslaw Pawlak
 */
public class Logger extends JPanel {
    private GridBagConstraints f;
    private GridBagConstraints s;

    private Table table;

    private JPanel content;
    private JScrollPane jsp;

    public Logger(Table table) {
        super(new GridLayout(1, 1));

        this.table = table;

        content = new JPanel(new GridBagLayout());

        f = new GridBagConstraints();
        f.insets = new Insets(0, 2, 0, 5);
        f.gridx = 0;

        s = new GridBagConstraints();
        s.insets = new Insets(2, 0, 3, 2);
        s.gridx = 1;
        s.fill = GridBagConstraints.BOTH;

        content.add(new JLabel("Internal IP:"), f);
        content.add(createTextField(Utilities.getInternalIP()), s);
        content.add(new JLabel("External IP:"), f);
        content.add(createTextField(Utilities.getExternalIP()), s);

        jsp = new JScrollPane(content);
        this.add(jsp);
    }

    public void log(String first, String second) {
        reduceSize();
        content.add(new JLabel(first), f);
        content.add(createTextField(second), s);
        content.revalidate();
        content.repaint();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                jsp.getVerticalScrollBar().setValue(
                        jsp.getVerticalScrollBar().getMaximum());
            }
        });
    }

    public void log(final String cardID, final boolean onTable, String text) {
        reduceSize();
        JButton button = new JButton("show");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!onTable || !table.scrollToCard(cardID)) {
                    new Card(Utilities.findPath(Game.getCardName(cardID)))
                            .viewLarger();
                }
            }
        });
        content.add(button, f);
        content.add(createTextField(text), s);
        content.revalidate();
        content.repaint();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                jsp.getVerticalScrollBar().setValue(
                        jsp.getVerticalScrollBar().getMaximum());
            }
        });
    }

    private void reduceSize() {
        if (content.getComponentCount() > 200) {
            content.remove(0);
            content.remove(0);
        }
    }

    private static JTextField createTextField(String text) {
        JTextField tf = new JTextField(text);
        tf.setEditable(false);
        tf.setBorder(null);
        tf.setFont(new Font("Arial", Font.PLAIN, 12));
        return tf;
    }
}
