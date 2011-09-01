package mtg;

import java.awt.Dimension;
import java.awt.Graphics2D;
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
 *
 * @author Jaroslaw Pawlak
 */
public final class Card extends JLabel {
    public final static int W = 160;
    public final static int H = (int) (W * 1.5);

    private File image;
    private boolean tapped;
    private int xpos;
    private int ypos;

    private Card() {}
    public Card(File image) {
        super();

        this.image = image;
        this.tapped = true;
        this.untap();
    }
    public Card(String path) {
        this(new File(path));
    }

    public boolean isBasicLand() {
        String t = image.getName()
                .substring(0, image.getName().lastIndexOf("."))
                .toLowerCase();
        return t.equals("plains")
                || t.equals("island")
                || t.equals("swamp")
                || t.equals("mountain")
                || t.equals("forest");
    }

    public String getCardName() {
        return image.getName().substring(0, image.getName().lastIndexOf("."));
    }

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

    private static BufferedImage rotate(BufferedImage org) {
        BufferedImage rotated = new BufferedImage(
            H, W, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = rotated.createGraphics();
        graphics2D.rotate(Math.toRadians(90), H / 2, H / 2);
        graphics2D.drawImage(resize(org), 0, 0, W, H, 0, 0, W, H, null);
        graphics2D.dispose();
        return rotated;
    }

    private BufferedImage load() {
        BufferedImage img = null;
        try {
            img = ImageIO.read(image);
        } catch (IOException e) {
            System.err.println(e);
            System.exit(1);
        }
        return img;
    }

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

    public boolean isTapped() {
        return tapped;
    }

    public void tap() {
        if (!tapped) {
            this.tapped = true;
            this.setIcon(new ImageIcon(rotate(resize(this.load()))));
            this.setCardPosition(this.xpos, this.ypos);
        }
    }

    public void untap() {
        if (tapped) {
            this.tapped = false;
            this.setIcon(new ImageIcon(resize(this.load())));
            this.setCardPosition(this.xpos, this.ypos);
        }
    }

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
            @Override
            public void mouseExited(MouseEvent e) {
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
//        try {
//            new Robot().mouseMove(d.width / 2, d.height / 2);
//        } catch (AWTException ex) {}
        temp.setVisible(true);
    }

    public int getXpos() {
        return xpos;
    }

    public int getYpos() {
        return ypos;
    }

    @Override
    public String toString() {
        return image.getName().substring(0, image.getName().lastIndexOf("."));
    }
}
