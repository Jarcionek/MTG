package deckCreator;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;

/**
 * @author Jaroslaw Pawlak
 */
public class DeckCreator extends JFrame {
    private File cardsDirectory;
    private JFrame parent;

    private LargeCardsViewer lcv;
    private JTree cardsTree;
    private JLabel cardsFound;
    private JButton back;


    private DeckCreator() {}

    public DeckCreator(String title, final JFrame parent, File cardsDirectory) {
        super(title);
        this.parent = parent;
        this.cardsDirectory = cardsDirectory;

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                parent.setVisible(true);
            }
        });

        createComponents();
        createLayout();

        this.pack();
        this.setMinimumSize(this.getSize());
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        parent.setVisible(false);
        this.setVisible(true);
    }

    private void createComponents() {
        CardTreeNode treeRoot = new CardTreeNode(cardsDirectory.listFiles());

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

        lcv = new LargeCardsViewer();
        lcv.setDirectory(cardsDirectory);

        back = new JButton("Back");
        back.setFocusable(false);
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DeckCreator.this.dispose();
                parent.setVisible(true);
            }
        });
    }

    private void createLayout() {
        JScrollPane treeScrollPane = new JScrollPane(cardsTree);
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(cardsFound, BorderLayout.NORTH);
        leftPanel.add(treeScrollPane, BorderLayout.CENTER);
        leftPanel.add(back, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JLabel("there will be chosen deck here"), BorderLayout.NORTH);
        centerPanel.add(lcv, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.add(new JLabel("cards, lands"), BorderLayout.NORTH);
        rightPanel.add(new JLabel("card - amount"), BorderLayout.CENTER);
        rightPanel.add(new JLabel("save/load deck"), BorderLayout.SOUTH);

        JSplitPane centerAndRight = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                centerPanel, rightPanel);
        centerAndRight.setResizeWeight(1);
        JSplitPane leftAndRest = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                leftPanel, centerAndRight);

        this.setContentPane(leftAndRest);
    }
}
