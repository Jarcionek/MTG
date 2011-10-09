package game;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import mtg.Card;
import mtg.Debug;
import mtg.Utilities;

/**
 * @author Jaroslaw Pawlak
 * 
 * Table card.
 */
public class TCard extends JLabel {
    private File image;
    protected boolean tapped;
    protected int xpos;
    protected int ypos;
    private String ID;
    private BufferedImage bi;

    private TCard() {}

    public TCard(String path, String ID) {
        super();

        if (path != null) {
            this.image = new File(path);
        }
        this.tapped = false;
        this.ID = ID;

        if (path != null) {
            this.setIcon(new ImageIcon(Utilities.resize(bi = this.load(), W(), H())));
        }
    }
    
    protected void setBufferedImage(BufferedImage bi) {
        this.bi = bi;
        this.setIcon(new ImageIcon(bi));
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
            r.x = x * Table.zoom / 100 - H() / 2;
            r.y = y * Table.zoom / 100 - W() / 2;
            r.width = H();
            r.height = W();
        } else {
            r.x = x * Table.zoom / 100 - W() / 2;
            r.y = y * Table.zoom / 100 - H() / 2;
            r.width = W();
            r.height = H();
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
     * Taps the card.
     */
    public void tap() {
        this.tapped = true;
        this.setIcon(new ImageIcon(Utilities.rotate(bi, W(), H())));
        this.setCardPosition(this.xpos, this.ypos);
    }
    
    /**
     * Untaps the card.
     */
    public void untap() {
        this.tapped = false;
        this.setIcon(new ImageIcon(Utilities.resize(bi, W(), H())));
        this.setCardPosition(this.xpos, this.ypos);
    }
    
    public String getID() {
        return ID;
    }
    
    /**
     * Cards are considered equal if their ID are the same. If any of them is
     * null then cards are not equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj != null
                && (obj.getClass().equals(TCard.class)
                || obj.getClass().equals(Token.class))) {
            if (this.ID == null || ((TCard) obj).ID == null) {
                Debug.p("Comparing cards with null ID!", Debug.W);
                return false;
            } else {
                return this.ID.equals(((TCard) obj).ID);
            }
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "TCard(ID = " + ID + ", xpos = " + xpos + ", ypos = " + ypos + ")";
    }
    
    public static int H() {
        return Card.H * Table.zoom / 100;
    }
    
    public static int W() {
        return Card.W * Table.zoom / 100;
    }
}
