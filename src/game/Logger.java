package game;

import java.awt.Color;
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
import javax.swing.SwingUtilities;
import mtg.Card;
import mtg.Utilities;

/**
 * @author Jaroslaw Pawlak
 */
public class Logger extends JPanel {
    public static final Color C_DRAG             = Color.black;
    public static final Color C_DRAW             = Color.blue;
    public static final Color C_TAP              = Color.gray;
    public static final Color C_CHANGE_HP        = Color.yellow.darker();
    public static final Color C_MOVE_DESTROY     = Color.red;
    public static final Color C_MOVE_EXILE       = Color.red;
    public static final Color C_MOVE_PLAY        = Color.green.darker().darker();
    public static final Color C_MOVE_TO_HAND     = Color.magenta;
    public static final Color C_MOVE_TO_LIBRARY  = Color.orange.darker();
    public static final Color C_SEARCH_LIBRARY   = Color.orange.darker();
    public static final Color C_SEARCH_GRAVEYARD = Color.black;
    public static final Color C_SEARCH_EXILED    = Color.black;
    public static final Color C_SHUFFLE          = Color.blue;
    public static final Color C_REVEAL           = Color.blue.darker().darker();
    public static final Color C_DISCONNECT       = Color.pink;
    public static final Color C_MESSAGE          = Color.black;
    public static final Color C_RANDOM           = Color.pink.darker();
    public static final Color C_RESTART          = Color.blue;

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
        content.add(createTextField(Utilities.getInternalIP(), Color.black), s);
        content.add(new JLabel("External IP:"), f);
        content.add(createTextField(Utilities.getExternalIP(), Color.black), s);

        jsp = new JScrollPane(content);
        this.add(jsp);
    }

    public void log(String first, String second, Color color) {
        reduceSize();
        content.add(new JLabel(first), f);
        content.add(createTextField(second, color), s);
        content.revalidate();
        content.repaint();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                jsp.getVerticalScrollBar().setValue(
                        jsp.getVerticalScrollBar().getMaximum());
            }
        });
    }

    public void log(final String cardID, final boolean onTable, String text,
            Color color) {
        reduceSize();
        if (cardID != null) {
            JButton button = new JButton("show");
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (!onTable || !table.scrollToCard(cardID)) {
                        new Card(Utilities.findPath(Game.getCardName(cardID)))
                                .viewLarger();
                    }
                }
            });
            button.setFocusable(false);
            content.add(button, f);
        } else {
            content.add(new JLabel(), f);
        }
        content.add(createTextField(text, color), s);
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

    private static JLabel createTextField(String text, Color color) {
        JLabel tf = new JLabel(text);
//        tf.setEditable(false);
        tf.setBorder(null);
        tf.setFont(new Font("Arial", Font.PLAIN, 12));
        tf.setForeground(color);
        return tf;
    }
}
