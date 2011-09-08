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
    private boolean tapped;
    private int xpos;
    private int ypos;

    private Card() {}

    /**
     * @see Card(String)
     */
    public Card(File image) {
        super();

        this.image = image;
        this.tapped = true;
        this.untap();
    }

    /**
     * @see Card(File)
     */
    public Card(String path) {
        this(new File(path));
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
        if (tapped) {
            r.x = x - H / 2;
            r.y = y - W / 2;
            r.width = H;
            r.height = W;
        } else {
            r.x = x - W / 2;
            r.y = y - H / 2;
            r.width = W;
            r.height = H;
        }
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

    /**
     * Returns true if the card is tapped, false otherwise.
     * @return true if the card is tapped, false otherwise
     */
    public boolean isTapped() {
        return tapped;
    }

    /**
     * Taps the card. Does nothing if the card is already tapped.
     */
    public void tap() {
        if (!tapped) {
            this.tapped = true;
            this.setIcon(new ImageIcon(rotate(resize(this.load()))));
            this.setCardPosition(this.xpos, this.ypos);
        }
    }

    /**
     * Untaps the card. Does nothing if the card is already untapped.
     */
    public void untap() {
        if (tapped) {
            this.tapped = false;
            this.setIcon(new ImageIcon(resize(this.load())));
            this.setCardPosition(this.xpos, this.ypos);
        }
    }

    /**
     * Displays new, undecorated JFrame with original (not resized) image in
     * the centre of a screen. Frame is disposed when mouse button is released
     * while on the frame or when the focus is lost.
     */
    public void viewLarger() {
        final JFrame temp = new JFrame();
        temp.setUndecorated(true);
        temp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel contentPane = new JLabel(new ImageIcon(image.getPath()));
        contentPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                temp.dispose();
            }
//            @Override
//            public void mouseExited(MouseEvent e) {
//                temp.dispose();
//            }
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
        temp.setLocation((d.width - temp.getSize().width) / 2,
                (d.height - temp.getSize().height) / 2);
        temp.setVisible(true);
    }

    ///// OVERRIDES /////

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

    /**
     * Resizes BufferedImage to height equal <code>Card.H</code> and
     * width equal <code>Card.W</code>
     * @param org original BufferedImage
     * @return scaled BufferedImage
     */
    public static BufferedImage resize(BufferedImage org) {
        BufferedImage scaledImage = new BufferedImage(
                W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(org, 0, 0, W, H, null);
        graphics2D.dispose();
        return scaledImage;
    }

    /**
     * Rotates the BufferedImage with height equal <code>Card.H</code>
     * and width equal <code>Card.W</code> by 90 degrees
     * @param org original BufferedImage of sizes Card.H x Card.W
     * @return rotated BufferedImage with width equal <code>Card.H</code>
     * and height equal <code>Card.W</code>
     */
    private static BufferedImage rotate(BufferedImage org) {
        BufferedImage rotated = new BufferedImage(
            H, W, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = rotated.createGraphics();
        graphics2D.rotate(Math.toRadians(90), H / 2, H / 2);
        graphics2D.drawImage(resize(org), 0, 0, W, H, 0, 0, W, H, null);
        graphics2D.dispose();
        return rotated;
    }

}
