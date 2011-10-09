package mtg;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
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

/**
 * A class representing a playable card. It displays no Swing components,
 * the only visible element is resized image loaded from JPG file given to
 * the constructor. Cards should not be used in components with layout
 * managers. To access card's bounds use {@link setCardPosition(int, int)} and
 * {@link getCardPosition()}. Card's name is a name of its image file with no
 * extension, e.g. if image file is <code>C:/MTG/Forest.jpg</code> then card's
 * name will be <code>Forest</code>.
 *
 * @author Jaroslaw Pawlak
 */
public final class Card extends JLabel {
    private static boolean isAnyCardEnlarged;

    /**
     * Card's width.
     */
    public final static int W = 160;
    /**
     * Card's height.
     */
    public final static int H = W * 3 / 2;

    /**
     * File with card's image.
     */
    private File image;
    private int xpos;
    private int ypos;
    private String ID;

    private Card() {}

    public Card(String path, String ID) {
        super();

        if (path != null) {
            this.image = new File(path);
        }
        this.ID = ID;

        this.addMouseWheelListener(new MouseWheelListener() {
              JFrame x;
              public void mouseWheelMoved(MouseWheelEvent e) {
                  if (e.getUnitsToScroll() < 0) {
                      x = Card.this.viewLarger();
                  } else if (x != null) {
                      x.dispose();
                  }
            }
        });

        if (path != null) {
            this.setIcon(new ImageIcon(Utilities.resize(this.load(), W, H)));
        }
    }

    public Card(String path) {
        this(path, null);
    }

    /**
     * Returns true if the card is a basic land, false otherwise. Ignores case.
     * @return true if the card is a basic land, false otherwise.
     */
    public boolean isBasicLand() {
        return isBasicLand(Utilities.getName(image));
    }

    /**
     * Returns card's name.
     * @return card's name
     */
    public String getCardName() {
        return Utilities.getName(image);
    }

    /**
     * Returns BufferedImage loaded from card's <code>image</code>
     * @return BufferedImage loaded from card's <code>image</code>
     */
    private BufferedImage load() {
        BufferedImage img = null;
        try {
            img = ImageIO.read(image);
        } catch (IOException e) {
            Debug.p("Could not load card's image from " + image + ": " + e,
                    Debug.CE);
        }
        return img;
    }

    /**
     * Sets bounds of a card where x and y are the centre of a card.
     * @param x x coordinate
     * @param y y coordinate
     * @see getXpos()
     * @see getYpos()
     */
    public void setCardPosition(int x, int y) {
        Rectangle r = this.getBounds();
        xpos = x;
        ypos = y;
        r.x = x - W / 2;
        r.y = y - H / 2;
        r.width = W;
        r.height = H;
        this.setBounds(r);
    }

    /**
     * Returns the centre point of a card (from its bounds).
     * @return the centre point of a card
     * @see getXpos()
     * @see getYpos()
     */
    public Point getCardPosition() {
        return new Point(xpos, ypos);
    }

    /**
     * Returns x coordinate of card's centre
     * @return x coordinate of card's centre
     */
    public int getXpos() {
        return xpos;
    }

    /**
     * Returns y coordinate of card's centre
     * @return y coordinate of card's centre
     */
    public int getYpos() {
        return ypos;
    }

    public String getID() {
        return ID;
    }
    
    /**
     * Displays new, undecorated JFrame with original (not resized) image in
     * the centre of a screen. Frame is disposed when mouse button is released
     * while on the frame or when the focus is lost.
     */
    public JFrame viewLarger() {
        isAnyCardEnlarged = true;
        final JFrame frame = new JFrame();
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel contentPane = new JLabel(new ImageIcon(image.getPath()));
        contentPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                frame.dispose();
                isAnyCardEnlarged = false;
            }
        });
        contentPane.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getUnitsToScroll() > 0) {
                    frame.dispose();
                    isAnyCardEnlarged = false;
                }
            }
        });
        frame.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                frame.dispose();
                isAnyCardEnlarged = false;
            }
        });

        frame.setContentPane(contentPane);
        frame.pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((d.width - frame.getSize().width) / 2,
                (d.height - frame.getSize().height) / 2);
        frame.setVisible(true);
        
        return frame;
    }

    ///// OVERRIDES /////

    /**
     * Cards are considered equal if their ID are the same. If any of them is
     * null then cards are not equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass().equals(Card.class)) {
            if (this.ID == null || ((Card) obj).ID == null) {
                Debug.p("Comparing cards with null ID!", Debug.W);
                return false;
            } else {
                return this.ID.equals(((Card) obj).ID);
            }
        } else {
            return false;
        }
//        return obj != null
//                && obj.getClass().equals(Card.class)
//                && this.ID != null
//                && ((Card) obj).ID != null
//                && this.ID.equals(((Card) obj).ID);
    }

    @Override
    public String toString() {
        return Utilities.getName(image);
    }

    public int compareTo(Card o) {
        if (this.image.equals(o.image)) {
            return 0;
        } else {
            return this.image.getName().toLowerCase()
                    .compareTo(o.image.getName().toLowerCase());
        }
    }

    ///// STATIC /////

    public static boolean isAnyCardEnlarged() {
        return isAnyCardEnlarged;
    }

    /**
     * Returns true if <code>name</code> is equal "plains", "island", "swamp",
     * "mountain" or "forest". Ignores case.
     * @param name String to be checked
     * @return true if a given name is a basic land, false otherwise
     */
    public static boolean isBasicLand(String name) {
        return name.equalsIgnoreCase("plains")
                || name.equalsIgnoreCase("island")
                || name.equalsIgnoreCase("swamp")
                || name.equalsIgnoreCase("mountain")
                || name.equalsIgnoreCase("forest");
    }

}
