package mtg;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

/**
 * @author Jaroslaw Pawlak
 */
public class CardsViewer {
    private static ArrayList<Card> cards = new ArrayList<Card>(60);
    private static int mid = 3;
    private static JPanel contentPane = new JPanel(null);
    private static JFrame frame;
    public static JPanel table;

    private CardsViewer() {}

    public static void addCard(final Card c) {
        for (MouseListener e : c.getMouseListeners()) {
            c.removeMouseListener(e);
        }
        cards.add(c);
        c.untap();
        c.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
//                int newmid = -1;
//                for (int i = 0; i < cards.size(); i++) {
//                    if (cards.get(i) == c) {
//                        newmid = i;
//                    }
//                }
//                showCards(newmid);

//                if (newmid == mid || e.getButton() == MouseEvent.BUTTON3) {
//                    c.viewLarger();
//                } else if (e.getButton() == MouseEvent.BUTTON1) {
//                    showCards(newmid);
//                }
                JPopupMenu popupMenu = new JPopupMenu();
                JMenuItem viewerLarger = new JMenuItem("view");
                viewerLarger.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        c.viewLarger();
                    }
                });
                JMenuItem exile = new JMenuItem("exile");
                exile.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        cards.remove(c);
//                        contentPane.remove(c);
//                        contentPane.repaint();
                        showCards();
                    }
                });
                JMenuItem play = new JMenuItem("play");
                play.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        OldMain.putCard(c, OldMain.library, table);
                        cards.remove(c);
//                        contentPane.remove(c);
//                        contentPane.revalidate();
//                        contentPane.repaint();
                        showCards();
                    }
                });
                popupMenu.add(play);
                popupMenu.add(viewerLarger);
                popupMenu.add(exile);
                popupMenu.show(c, e.getX(), e.getY());
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                final Card source = (Card) e.getSource();
                showCards();
                contentPane.remove(source);
                contentPane.add(source, 0);
//                source.repaint();
                contentPane.revalidate();
                contentPane.repaint();
            }
        });
//        showCards(3);
        showCards();
    }

    private static void showCards() {
        contentPane.removeAll();
        int d = cards.size() == 1? 0 : (contentPane.getSize().width - Card.W) / (cards.size() - 1);
        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).setCardPosition(Card.W / 2 + i * d, Card.H / 2);
            contentPane.add(cards.get(i));
        }
        contentPane.repaint();
    }

    private static void showCards(int middle) {
        int j = 1;
        if (cards.size() < 7) {
            for (int i = 0; i < cards.size(); i++) {
                cards.get(i).setCardPosition(Card.H / 2 * j, Card.H / 2);
                contentPane.add(cards.get(i));
                j++;
            }
            mid = cards.size() / 2;
        } else {
            if (middle < 3) {
                middle = 3;
            } else if (middle > cards.size() - 4) {
                middle = cards.size() - 4;
            }
            contentPane.removeAll();
            for (int i = middle - 3; i < middle + 4; i++) {
                cards.get(i).setCardPosition(Card.H / 2 * j, Card.H / 2);
                contentPane.add(cards.get(i));
                j++;
            }
            mid = middle;
        }
        contentPane.repaint();
    }

    public static void show() {
        if (frame == null) {
             frame = new JFrame("cards viewer");
             frame.setContentPane(contentPane);
             Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
             frame.setSize(d.width / 2, Card.H + 40);
             frame.setLocation(0, d.height - Card.H - 80);
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
        showCards();
    }

    public static void hide() {
        frame.setVisible(false);
    }

}