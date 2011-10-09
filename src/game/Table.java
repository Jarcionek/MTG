package game;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import mtg.Card;
import mtg.Debug;
import mtg.Utilities;

/**
 * @author Jaroslaw Pawlak
 */
public class Table extends JScrollPane {
    private static final boolean LOAD_GRAPHICS = true;
    
    public static final Dimension SIZE = new Dimension(Card.W * 100, Card.W * 100);
    
    private static final int ZOOM_MIN = 25;
    private static final int ZOOM_MAX = 200;
    static int zoom = 100;
    
    private JPanel table;
    
    private static BufferedImage tableCentre;
    private static BufferedImage tableBackground;

    public Table() {
        super();
        
        if (LOAD_GRAPHICS) {
            try {
                tableBackground = ImageIO.read(this.getClass().getResource("/resources/Table.jpg"));
            } catch (IOException ex) {
                Debug.p("Could not load table background: " + ex, Debug.E);
            }
        }
        if (tableBackground != null) {
            this.table = new JPanel(null) {
                @Override
                public void paint(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    Rectangle r = this.getVisibleRect();
                    Dimension ts = table.getPreferredSize();
                    g2.drawImage(tableBackground,
                            2 * r.x * (ts.width / 2 - 2000) / (ts.width - r.width),
                            2 * r.y * (ts.height / 2 - 2000) / (ts.height - r.height),
                            null);
                    g2.dispose();
                    super.paint(g);
                }
            };
            this.table.setOpaque(false);
        } else {
            this.table = new JPanel(null);           
        }

        
        this.table.setPreferredSize(SIZE);
        this.setViewportView(this.table);
        
        if (LOAD_GRAPHICS) {
            try {
                tableCentre = ImageIO.read(this.getClass()
                        .getResource("/resources/TableShadow.png"));
                BufferedImage shadow = Utilities.resize(tableCentre, 480 * zoom / 100,
                        560 * zoom / 100);
                
                final JLabel centre = new JLabel(new ImageIcon(shadow));
                centre.setHorizontalAlignment(SwingConstants.CENTER);
                centre.setVerticalAlignment(SwingConstants.CENTER);
                centre.setSize(480 * zoom / 100, 560 * zoom / 100);
                centre.setBounds(
                        (table.getPreferredSize().width - 480 * zoom / 100) / 2,
                        (table.getPreferredSize().height - 560 * zoom / 100) / 2,
                        480 * zoom / 100, 560 * zoom / 100);
                table.add(centre);
            } catch (IOException ex) {
                Debug.p("Could not load table graphics: " + ex, Debug.E);
            }
        } else {
            JLabel centre = new JLabel();
            centre.setSize(Card.W, Card.H);
            centre.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createBevelBorder(BevelBorder.RAISED),
                        BorderFactory.createBevelBorder(BevelBorder.LOWERED)
                    ));
            centre.setBounds((table.getPreferredSize().width - Card.W) / 2 - 4,
                    (table.getPreferredSize().height - Card.H) / 2 - 4,
                    Card.W + 8,
                    Card.H + 8);
            table.add(centre);
        }

        TableDragListener tdl = new TableDragListener();
        this.table.addMouseListener(tdl);
        this.table.addMouseMotionListener(tdl);
        this.table.setAutoscrolls(true);
        
        this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        
        this.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public synchronized void mouseWheelMoved(MouseWheelEvent e) {
                int oldV = zoom;
                int newV = oldV - 3 * e.getUnitsToScroll();
                if (newV < oldV) { //zoom out
                    if (oldV == ZOOM_MIN) {
                        return;
                    } else if (newV < ZOOM_MIN) {
                        newV = ZOOM_MIN;
                    }
                } else { //zoom in
                    if (oldV == ZOOM_MAX) {
                        return;
                    } else if (newV > ZOOM_MAX) {
                        newV = ZOOM_MAX;
                    }
                }
                
                zoom = newV;
                table.setPreferredSize(new Dimension(
                        SIZE.width * zoom / 100,
                        SIZE.height * zoom / 100));
                
                final int fnewV = newV;
                final int foldV = oldV;
                recalculatePosition(fnewV, foldV);
                
                for (Object o : table.getComponents()) {
                    if (o.getClass().equals(TCard.class)
                            || o.getClass().equals(Token.class)) {
                        TCard t = (TCard) o;
                        if (t.isTapped()) {
                            t.tap();
                        } else {
                            t.untap();
                        }
                    } else if (o.getClass().equals(JLabel.class)) {
                        JLabel l = (JLabel) o;
                        if (LOAD_GRAPHICS) {
                            BufferedImage b = Utilities.resize(tableCentre, 480 * zoom / 100,
                                    560 * zoom / 100);
                            l.setIcon(new ImageIcon(b));
                            l.setSize(480 * zoom / 100, 560 * zoom / 100);
                            l.setBounds(
                                    (table.getPreferredSize().width - 480 * zoom / 100) / 2,
                                    (table.getPreferredSize().height - 560 * zoom / 100) / 2,
                                    480 * zoom / 100, 560 * zoom / 100);
                        } else {
                            l.setSize(Card.W, Card.H);
                            l.setBorder(BorderFactory.createCompoundBorder(
                                        BorderFactory.createBevelBorder(BevelBorder.RAISED),
                                        BorderFactory.createBevelBorder(BevelBorder.LOWERED)
                                    ));
                            l.setBounds((table.getPreferredSize().width - TCard.W()) / 2 - 4,
                                    (table.getPreferredSize().height - TCard.H()) / 2 - 4,
                                    TCard.W() + 8,
                                    TCard.H() + 8); 
                        }
                    }
                }
            }
        });

        this.setWheelScrollingEnabled(false);
    }
    
    private void recalculatePosition(int newV, int oldV) {
        /* Consider two Cartesian coordinate systems T and S. There is the
         * table in the T system. Somewhere over the table there is a frame
         * with scrollpane in it. Imagine that when a player uses scroll
         * bars, table stays static and the frame moves in a T system.
         * S system is a system which stays static relative to the scroll pane.
         * MouseEvent.getMousePosition should return position of a pointer
         * in S system, but unfortunately it returns that the pointer is not
         * over a scrollpane, so this position has to be calculated in different
         * way.
         * 
         * When a player zooms a table, we take a position of a pointer within
         * S system - let's say point P. Its position in S system will be
         * denoted by S(P). We calculate position of P in T system - T(P).
         * We rescale the table by a known value with point P stuck
         * to the T system. Coordinates of P in T does not change, but in
         * S it does, so T(P) remains unchanged, but position of P in S is now
         * denoted by S(P1).
         * 
         * The task is to move entire T system (scrollbar.setValue) relative
         * to the S system, so that the point S(P1) = S(P).
         */
        class Point extends java.awt.Point {
            public Point(java.awt.Point p) {
                super(p);
            }
            public Point(Rectangle r) {
                super(r.x, r.y);
            }
            public Point(int x, int y) {
                super(x, y);
            }
            /**
             * this.x -= o.x, this.y -= o.y
             */
            Point substract(java.awt.Point o) {
                this.x -= o.x;
                this.y -= o.y;
                return this;
            }
            /**
             * this.x += o.x, this.y += o.y
             */
            Point add(java.awt.Point o) {
                this.x += o.x;
                this.y += o.y;
                return this;
            }
        }
        Point sp = new Point(MouseInfo.getPointerInfo().getLocation())
                .substract(Table.this.getLocationOnScreen());
        Point tp = new Point(table.getVisibleRect())
                .add(sp);
        int changex = tp.x - tp.x * newV / oldV;
        int changey = tp.y - tp.y * newV / oldV;
        JScrollBar h = Table.this.getHorizontalScrollBar();
        JScrollBar v = Table.this.getVerticalScrollBar();
        h.setValue(table.getVisibleRect().x - changex);
        v.setValue(table.getVisibleRect().y - changey);
    }

    /**
     * Scrolls to the centre of a table. Invoke it when a frame is displayed,
     * otherwise it may not work.
     */
    public void centerView() {
        centerView(this.getHorizontalScrollBar().getMaximum() / 2,
                this.getVerticalScrollBar().getMaximum() / 2);
    }
    
    /**
     * Centers view at given point on table
     * @param x
     * @param y 
     */
    private void centerView(int x, int y) {
        int h = x - this.getHorizontalScrollBar().getVisibleAmount() / 2;
        int v = y - this.getVerticalScrollBar().getVisibleAmount() / 2;

        this.getHorizontalScrollBar().setValue(h);
        this.getVerticalScrollBar().setValue(v);
    }

    /**
     * Adds a cards at the top layer in the middle of a table. Repaints
     * the container.
     * @param card card to be played
     */
    public void addCard(TCard card) {
        for (MouseListener e : card.getMouseListeners()) {
            card.removeMouseListener(e);
        }
        for (MouseMotionListener e : card.getMouseMotionListeners()) {
            card.removeMouseMotionListener(e);
        }

        OnTableMouseAdapter t = new OnTableMouseAdapter();
        card.addMouseListener(t);
        card.addMouseMotionListener(t);
        card.setCardPosition(SIZE.width / 2, SIZE.height / 2);
        table.add(card, 0);
        card.repaint();
    }

    public void dragCard(String ID, int newx, int newy) {
        for (Object e : table.getComponents()) {
            if ((e.getClass().equals(TCard.class)
                    || e.getClass().equals(Token.class))
                    &&((TCard) e).getID().equals(ID)) {
                ((TCard) e).setCardPosition(newx, newy);
                break;
            }
        }
    }

    public void tapCard(String ID, boolean tapped) {
        for (Object e : table.getComponents()) {
            if ((e.getClass().equals(TCard.class)
                    || e.getClass().equals(Token.class))
                    &&((TCard) e).getID().equals(ID)) {
                if (tapped) {
                    ((TCard) e).tap();
                } else {
                    ((TCard) e).untap();
                }
                break;
            }
        }
    }
    
    public void untapAll(int player) {
        for (Object e : table.getComponents()) {
            if ((e.getClass().equals(TCard.class)
                    || e.getClass().equals(Token.class))
                    &&((TCard) e).getID().charAt(0) == 'A' + player) {
                ((TCard) e).untap();
            }
        }
    }

    /**
     * Scrolls table to the given card or returns false and does nothing
     * if card is not on the table.
     * @param ID Card ID
     * @return true if scrolled to the card or false if card not on the table
     */
    public boolean scrollToCard(String ID) {
        for (Component c : table.getComponents()) {
            if ((c.getClass().equals(TCard.class) || c.getClass().equals(Token.class))) {
                final TCard card = (TCard) c;
                if (card.getID().equals(ID)) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            Table.this.getHorizontalScrollBar().setValue(
                                    card.getBounds().x + TCard.W() / 2
                                    - Table.this.getWidth() / 2);
                            Table.this.getVerticalScrollBar().setValue(
                                    card.getBounds().y + TCard.H() / 2
                                    - Table.this.getHeight() / 2);
                        }
                    });
                    return true;
                }
            }
        }
        return false;
    }

    public void removeCard(String ID) {
        for (Component c : table.getComponents()) {
            if ((c.getClass().equals(TCard.class)
                    || c.getClass().equals(Token.class))
                    && ((TCard) c).getID().equals(ID)) {
                table.remove(c);
                table.repaint();
                return;
            }
        }
    }
    
    /**
     * Removes all cards own by <code>player</code> from the table
     * @param player 
     */
    public void removeCards(int player) {
        for (Component c : table.getComponents()) {
            if ((c.getClass().equals(TCard.class) || c.getClass().equals(Token.class))
                    && ((TCard) c).getID().charAt(0) == player + 'A') {
                table.remove(c);
            }
        }
        table.repaint();
    }
}