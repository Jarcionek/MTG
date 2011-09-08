package game;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import mtg.Library;
import mtg.Main;

/**
 * @author Jaroslaw Pawlak
 */
public class Game extends JFrame {
    private JFrame parentFrame;

    private Table table;
    private Library library; //TODO to be removed
    private CardViewer hand;
    private CardViewer graveyard; //TODO remove it and let the client download
    private CardViewer exiled; //it from the server every time when neccessary

    private Game() {}

    /* //TODO client must receive number of players from the server with their
     * names and optionally table's size. Also shuffled library is required.
     *
     */
    public Game(JFrame parentFrame) {
        super(Main.TITLE);
        this.parentFrame = parentFrame;
        this.parentFrame.setVisible(false);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Game.this.parentFrame.setVisible(true);
            }
        });

        createGUIComponents();
        createGUILayout();

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setUndecorated(true);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setVisible(true);
    }

    public static void main(String[] as) {
        new Game(new JFrame());
    }

    private void createGUIComponents() {
        table = new Table(Table.TWO_PLAYERS);
        hand = new CardViewer(new InSearcherMouseListener(Zone.HAND));
    }

    private void createGUILayout() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
