package deckCreator;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;
import mtg.Deck;
import mtg.Main;
import mtg.Utilities;

/**
 * @author Jaroslaw Pawlak
 */
public class DeckCreator extends JFrame {
    private JFrame parentFrame;

    SmallCardsViewer scv;
    LargeCardsViewer lcv;
    private JTree cardsTree;
    private JLabel cardsFound;
    private JButton back;
    JLabel deckName;
    private JButton load;
    private JButton save;

    Deck deck;

    private DeckFileChooser dfc = new DeckFileChooser();

    private DeckCreator() {}

    public DeckCreator(String title, JFrame parentFrame) {
        super(title);
        this.parentFrame = parentFrame;
        this.parentFrame.setVisible(false);
        
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                scv.close();
                DeckCreator.this.parentFrame.setVisible(true);
            }
        });

        deck = new Deck();

        createGUIComponents();
        createGUILayout();

        this.pack();
        this.setMinimumSize(this.getSize());
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setVisible(true);
    }

    private void createGUIComponents() {
        CardTreeNode treeRoot = new CardTreeNode(Main.CARDS);

        cardsTree = new JTree(treeRoot);
        cardsTree.setRootVisible(false);
        cardsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        cardsTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                lcv.setDirectory(
                        ((CardTreeNode) e.getPath().getLastPathComponent())
                        .getFile());
            }
        });

        cardsFound = new JLabel("Cards found: " + treeRoot.countFiles());
        cardsFound.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        cardsFound.setHorizontalAlignment(SwingConstants.CENTER);

        scv = new SmallCardsViewer(this);

        lcv = new LargeCardsViewer(this);
        lcv.setDirectory(Main.CARDS);

        back = new JButton("Back");
        back.setFocusable(false);
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scv.close();
                DeckCreator.this.dispose();
                parentFrame.setVisible(true);
            }
        });

        deckName = new JLabel("Deck: new");

        load = new JButton("Load");
        load.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (dfc.showOpenDialog(DeckCreator.this)
                        == JFileChooser.APPROVE_OPTION) {
                    File f = dfc.getSelectedFile();
                    Deck t = Deck.load(f);
                    if (t == null) {
                        JOptionPane.showMessageDialog(DeckCreator.this,
                                "Deck could not be loaded", Main.TITLE,
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        deck = t;
                        deckName.setText("Deck: "
                                + Utilities.getName(f));
                        scv.refresh();
                    }
                }
            }
        });

        save = new JButton("Save");
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (dfc.showSaveDialog(DeckCreator.this) == JFileChooser.APPROVE_OPTION) {
                    File f = dfc.getSelectedFile();
                    if (!"txt".equals(Utilities.getExtension(f))) {
                        f = new File(f.toString() + ".txt");
                    }
                    if (deck.save(f)) {
                        deckName.setText("Deck: "
                                + Utilities.getName(f));
                    } else {
                        JOptionPane.showMessageDialog(DeckCreator.this,
                                "Deck could not be saved", Main.TITLE,
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    private void createGUILayout() {
        JScrollPane treeScrollPane = new JScrollPane(cardsTree);
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(cardsFound, BorderLayout.NORTH);
        leftPanel.add(treeScrollPane, BorderLayout.CENTER);
        leftPanel.add(back, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(scv, BorderLayout.NORTH);
        centerPanel.add(lcv, BorderLayout.SOUTH);

        JPanel loadSavePanel = new JPanel(new GridLayout(3, 1));
        loadSavePanel.add(deckName);
        loadSavePanel.add(load);
        loadSavePanel.add(save);

        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.add(new JLabel("cards, lands"), BorderLayout.NORTH);
        rightPanel.add(new JLabel("card - amount"), BorderLayout.CENTER);
        rightPanel.add(loadSavePanel, BorderLayout.SOUTH);

        JSplitPane centerAndRight = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                centerPanel, rightPanel);
        centerAndRight.setResizeWeight(1);
        JSplitPane leftAndRest = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                leftPanel, centerAndRight);

        this.setContentPane(leftAndRest);
    }
}
