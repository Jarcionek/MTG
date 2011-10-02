package deckCreator;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
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
        this.setIcon(new ImageIcon(Card.resize(img)));
        this.setHorizontalAlignment(SwingConstants.CENTER);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    viewLarger(image);
                }
            }
        });
        this.addMouseWheelListener(new MouseWheelListener() {
              JFrame x;
              public void mouseWheelMoved(MouseWheelEvent e) {
                  if (e.getUnitsToScroll() < 0) {
                      x = ViewableCard.this.viewLarger(image);
                  } else if (x != null) {
                      x.dispose();
                  }
            }
        });
    }

    private JFrame viewLarger(File file) {
        final JFrame frame = new JFrame();
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel contentPane = new JLabel(new ImageIcon(file.getPath()));
        contentPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.dispose();
            }
        });
        contentPane.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getUnitsToScroll() > 0) {
                    frame.dispose();
                }
            }
        });
        frame.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                frame.dispose();
            }
        });

        frame.setContentPane(contentPane);
        frame.pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(
                (d.width - frame.getSize().width) / 2,
                (d.height - frame.getSize().height) / 2
                );
        frame.setVisible(true);
        return frame;
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
