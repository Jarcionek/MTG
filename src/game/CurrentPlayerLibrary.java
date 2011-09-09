package game;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;

/**
 * @author Jaroslaw Pawlak
 */
public class CurrentPlayerLibrary extends JPanel {

    private JLabel title;
    private JButton drawButton;
    private JButton searchButton;
    private JButton revealTopButton;
    private JButton playTopButton;
    private JButton shuffleButton;

    public CurrentPlayerLibrary() {
        super();
        createComponents();
        createGUI();
    }

    private void createComponents() {
        title = new JLabel("Your library");
        title.setHorizontalAlignment(JLabel.CENTER);

        drawButton = new JButton("Draw");
        drawButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        drawButton.setFocusable(false);

        searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        searchButton.setFocusable(false);

        revealTopButton = new JButton("Reveal top");
        revealTopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        revealTopButton.setFocusable(false);

        playTopButton = new JButton("Play top");
        playTopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        playTopButton.setFocusable(false);

        shuffleButton = new JButton("Shuffle");
        shuffleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        shuffleButton.setFocusable(false);
    }

    private void createGUI() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        int outside = 2;
        int between = 3;
        int b = 5;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;

        c.insets = new Insets(outside, outside, between, outside);
        title.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.RAISED),
                BorderFactory.createEmptyBorder(b, b, b, b)));
        this.add(title, c);

        c.insets = new Insets(0, outside, outside, between);
        this.add(drawButton, c);
        this.add(searchButton, c);
        this.add(revealTopButton, c);
        this.add(playTopButton, c);
        c.insets = new Insets(0, outside, outside, outside);
        this.add(shuffleButton, c);
    }

    public static void main(String[] asfasf) throws Exception {
        JFrame x = new JFrame();

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        x.setContentPane(new CurrentPlayerLibrary());
        x.pack();
        x.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        x.setVisible(true);
    }
}
