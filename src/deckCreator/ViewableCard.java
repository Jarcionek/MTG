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
import mtg.Utilities;

/**
 * Card which does not have any play mechanisms. It can be only enlarged.
 *
 * @author Jaroslaw Pawlak
 */
public class ViewableCard extends JLabel implements Comparable<ViewableCard> {
    private File image;

    private ViewableCard() {}
    public ViewableCard(final File image) {
        super();
        this.image = image;
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

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new ViewableCard(image);
    }

    public int compareTo(ViewableCard o) {
        if (this.image.equals(o.image)) {
            return 0;
        } else {
            return this.image.getName().toLowerCase()
                    .compareTo(o.image.getName().toLowerCase());
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null
                && obj.getClass() == ViewableCard.class
                && this.image.equals(((ViewableCard) obj).image);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.image != null ? this.image.hashCode() : 0);
        return hash;
    }

    public File getImage() {
        return image;
    }

    public String getCardName() {
        return Utilities.getName(image);
    }
}
