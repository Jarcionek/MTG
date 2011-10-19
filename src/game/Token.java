package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import mtg.Card;
import mtg.Utilities;
import server.flags.CreateToken;

/**
 * @author Jaroslaw Pawlak
 */
public class Token extends TCard {
    private static final Color WHITE = new Color(213, 196, 180);
    private static final Color BLUE = new Color(103, 132, 192);
    private static final Color RED = new Color(147, 61, 46);
    private static final Color BLACK = new Color(25, 24, 19);
    private static final Color GREEN = new Color(110, 137, 94);
    private static final Color COLORLESS = new Color(165, 169, 172);
    private static final Color FILL = new Color(200, 200, 200);
    
    
    public Token(CreateToken ct) {
        super(null, ct.cardID);
        
        BufferedImage bi = new BufferedImage(Card.W, Card.H,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D) bi.getGraphics();
        
        // DRAW BACKGROUND
        Color[] colc = new Color[] {BLUE, BLACK, RED, WHITE, GREEN};
        boolean[] colb = new boolean[] {ct.blue, ct.black, ct.red, ct.white, ct.green};
        
        int colors = 0;
        if (ct.black) colors++;
        if (ct.blue) colors++;
        if (ct.green) colors++;
        if (ct.red) colors++;
        if (ct.white) colors++;
        Color one = null, two = null, three = null, four = null;
        Polygon p;
        switch (colors) {
            case 0:
                g2.setColor(COLORLESS);
                g2.fillRect(0, 0, Card.W, Card.H);
                break;
            case 1:
                for (int i = 0; i < colb.length; i++) {
                    if (colb[i]) {
                        g2.setColor(colc[i]);
                        g2.fillRect(0, 0, Card.W, Card.H);
                        break;
                    }
                }
                break;
            case 2:
                for (int i = 0; i < colb.length; i++) {
                    if (colb[i]) {
                        if (one == null) {
                            one = colc[i];
                        } else if (two == null) {
                            two = colc[i];
                        } else {
                            break;
                        }
                    }
                }
                
                p = new Polygon(
                        new int[] {0, Card.W * 2 / 3, Card.W /3, 0},
                        new int[] {0, 0, Card.H, Card.H},
                        4);
                g2.setColor(one);
                g2.fillPolygon(p);
                
                p = new Polygon(
                        new int[] {Card.W * 2 / 3, Card.W, Card.W, Card.W / 3},
                        new int[] {0, 0, Card.H, Card.H},
                        4);
                g2.setColor(two);
                g2.fillPolygon(p);
                break;
            case 3:
                for (int i = 0; i < colb.length; i++) {
                    if (colb[i]) {
                        if (one == null) {
                            one = colc[i];
                        } else if (two == null) {
                            two = colc[i];
                        } else if (three == null) {
                            three = colc[i];
                        } else {
                            break;
                        }
                    }
                }
                
                p = new Polygon(
                        new int[] {0, Card.W, Card.W, Card.W / 2, 0},
                        new int[] {0, 0, Card.H / 3 - Card.W / 6, Card.H / 2, Card.H / 3 - Card.W / 6},
                        5);
                g2.setColor(one);
                g2.fillPolygon(p);
                
                p = new Polygon(
                        new int[] {0, Card.W / 2, Card.W / 2, 0},
                        new int[] {Card.H / 3 - Card.W / 6, Card.H / 2, Card.H, Card.H},
                        4);
                g2.setColor(two);
                g2.fillPolygon(p);
                
                p = new Polygon(
                        new int[] {Card.W, Card.W, Card.W / 2, Card.W / 2},
                        new int[] {Card.H / 3 - Card.W / 6, Card.H, Card.H, Card.H / 2},
                        4);
                g2.setColor(three);
                g2.fillPolygon(p);
                break;
            case 4:
                for (int i = 0; i < colb.length; i++) {
                    if (colb[i]) {
                        if (one == null) {
                            one = colc[i];
                        } else if (two == null) {
                            two = colc[i];
                        } else if (three == null) {
                            three = colc[i];
                        } else if (four == null) {
                            four = colc[i];
                        } else {
                            break;
                        }
                    }
                }
                
                p = new Polygon(
                        new int[] {0, Card.W / 2, Card.W / 2, 0},
                        new int[] {0, 0, Card.H / 2, Card.H / 2},
                        4);
                g2.setColor(one);
                g2.fillPolygon(p);
                
                p = new Polygon(
                        new int[] {Card.W / 2, Card.W, Card.W, Card.W / 2},
                        new int[] {0, 0, Card.H / 2, Card.H / 2},
                        4);
                g2.setColor(two);
                g2.fillPolygon(p);
                
                p = new Polygon(
                        new int[] {0, Card.W / 2, Card.W / 2, 0},
                        new int[] {Card.H / 2, Card.H / 2, Card.H, Card.H},
                        4);
                g2.setColor(three);
                g2.fillPolygon(p);
                
                p = new Polygon(
                        new int[] {Card.W / 2, Card.W, Card.W, Card.W / 2},
                        new int[] {Card.H / 2, Card.H / 2, Card.H, Card.H},
                        4);
                g2.setColor(four);
                g2.fillPolygon(p);
                break;
            case 5:
                p = new Polygon(new int[] {0, Card.W, Card.W / 2},
                        new int[] {0, 0, Card.H / 2}, 3);
                g2.setColor(colc[0]);
                g2.fillPolygon(p);
                
                p = new Polygon(new int[] {Card.W, Card.W, Card.W / 2},
                        new int[] {0, Card.H - Card.W / 2, Card.H / 2}, 3);
                g2.setColor(colc[1]);
                g2.fillPolygon(p);
                
                p = new Polygon(new int[] {Card.W, Card.W, Card.W / 2, Card.W / 2},
                        new int[] {Card.H - Card.W / 2, Card.H, Card.H, Card.H / 2}, 4);
                g2.setColor(colc[2]);
                g2.fillPolygon(p);
                
                p = new Polygon(new int[] {0, Card.W / 2, Card.W / 2, 0},
                        new int[] {Card.H - Card.W / 2, Card.H / 2, Card.H, Card.H}, 4);
                g2.setColor(colc[3]);
                g2.fillPolygon(p);
                
                p = new Polygon(new int[] {0, Card.W / 2, 0},
                        new int[] {0, Card.H / 2, Card.H - Card.W / 2}, 3);
                g2.setColor(colc[4]);
                g2.fillPolygon(p);
        }
        // BACKGROUND DRAWN
        
        g2.setColor(Color.black);
        g2.drawRect(0, 0, Card.W - 1, Card.H - 1);
        g2.drawRect(1, 1, Card.W - 3, Card.H - 3);
        g2.drawRect(2, 2, Card.W - 5, Card.H - 5);
        
        // code below has to be changed in case of changing Card.W or Card.H 
        Rectangle r1 = d(g2, 4, 6, Card.W - 9, 16, FILL, FILL.darker().darker());
        Rectangle r2 = d(g2, 6, 22, Card.W - 13, 112, FILL.brighter(), FILL.darker().darker());
        Rectangle r3 = d(g2, 4, 134, Card.W - 9, 16, FILL, FILL.darker().darker());
        Rectangle r4 = d(g2, 6, 150, Card.W - 13, 72, FILL.brighter(), FILL.darker().darker());
        Rectangle r5 = null;
        if (ct.creature) {
            r5 = d(g2, 124, 219, 31, 14, FILL, FILL.darker().darker());
        }
        
        g2.setColor(Color.black);
        
        g2.setFont(new Font("Arial", Font.PLAIN, r1.height - 6));
        g2.drawString(ct.name, r1.x + 5, r1.y + r1.height - 3);
        
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("TOKEN", 40, r2.y + r2.height / 2 + 10);
        
        g2.setFont(new Font("Arial", Font.PLAIN, r1.height - 6));
        g2.drawString(ct.type, r3.x + 5, r3.y + r3.height - 3);
        
        g2.setFont(new Font("Arial", Font.PLAIN, 8));
        String[] desc = ct.desc.split("\n");
        for (int i = 0; i < desc.length && i < 5; i++) {
            g2.drawString(desc[i], r4.x + 5, r4.y + 13 * (i+1));
        }
        
        if (ct.creature) {
            g2.setFont(new Font("Arial", Font.PLAIN, r5.height - 3));
            String t = ct.atk + "/" + ct.def;
            int shift = t.length() == 3? 9 : t.length() == 4? 7 : 3;
            g2.drawString(t, r5.x + shift, r5.y + r5.height - 2);
        }
        
        g2.dispose();
        
        super.setBufferedImage(Utilities.resize(bi, W(), H()));
    }
    
    private static Rectangle d(Graphics2D g2, int x, int y, int width, int height, Color fill, Color outline) {
        g2.setColor(fill);
        g2.fillRect(x, y, width, height);
        g2.setColor(outline);
        g2.drawRect(x, y, width, height);
        return new Rectangle(x, y, width, height);
    }
}
