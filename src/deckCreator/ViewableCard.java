package deckCreator;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import mtg.Card;
import mtg.Debug;

/**
 * Card which does not have any play mechanisms. It can be only enlarged.
 *
 * @author Jaroslaw Pawlak
 */
public class ViewableCard extends JLabel {
    private ViewableCard() {}
    public ViewableCard(final File image) {
        super();
        BufferedImage img = null;
        try {
            img = ImageIO.read(image);
        } catch (IOException e) {
            Debug.p("ViewableCard error: " + e, Debug.E);
        }
        setIcon(new ImageIcon(Card.resize(img)));
        setHorizontalAlignment(SwingConstants.CENTER);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    viewLarger(image);
                }
            }
        });
    }

    private void viewLarger(File file) {
        final JFrame temp = new JFrame();
        temp.setUndecorated(true);
        temp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel contentPane = new JLabel(new ImageIcon(file.getPath()));
        contentPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                temp.dispose();
            }
        });
        temp.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                temp.dispose();
            }
        });

        temp.setContentPane(contentPane);
        temp.pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        temp.setLocation(
                (d.width - temp.getSize().width) / 2,
                (d.height - temp.getSize().height) / 2
                );
        temp.setVisible(true);
    }
}
