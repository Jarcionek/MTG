package mtg;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

/**
 * @author Jaroslaw Pawlak
 */
public class Main {

    static Card tempCard;
    static int tempX;
    static int tempY;

    private static int szer = Card.H / 2;
    private static int wys = Card.H / 2;

    public static Library library;

    public static void main(String[] args) {
        JFrame frame = new JFrame("MTG");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Deck deck = new Deck("c:/Documents and Settings/Jarek/Desktop/MTG");
        deck.addCard("Ezuri's Archers", 4);
        deck.addCard("Joraga Treespeaker", 1);
        deck.addCard("Joraga Warcaller", 1);
        deck.addCard("Scattershot Archer", 2);
        deck.addCard("Twinblade Slasher", 2);
        deck.addCard("Bramblewood Paragon", 1);
        deck.addCard("Elvish Vanguard", 1);
        deck.addCard("Gaea's Herald", 1);
        deck.addCard("Joiner Adept", 1);
        deck.addCard("Pendelhaven Elder", 1);
        deck.addCard("Tajaru Preserver", 1);
        deck.addCard("Thornweald Archer", 2);
        deck.addCard("Wellwisher", 2);
        deck.addCard("Wirewood Herald", 1);
        deck.addCard("Elvish Archdruid", 1);
        deck.addCard("Elvish Champion", 1);
        deck.addCard("Elvish Harbinger", 4);
        deck.addCard("Ezuri, Renegade Leader", 1);
        deck.addCard("Glissa, the Traitor", 1);
        deck.addCard("Imperious Perfect", 1);
        deck.addCard("Jagged-Scar Archers", 4);
        deck.addCard("Lys Alana Bowmaster", 4);
        deck.addCard("Rhys the Exiled", 1);
        deck.addCard("Lys Alana Huntmaster", 1);
        deck.addCard("Nullmage Shepherd", 1);
        deck.addCard("Wirewood Channeler", 1);
        deck.addCard("Ambush Commander", 1);
        deck.addCard("Greatbow Doyen", 1);
        deck.addCard("Kaysa", 1);
        deck.addCard("Nath of the Gilt-Leaf", 1);
        deck.addCard("Regal Force", 1);
        deck.addCard("Gilt-Leaf Palace", 4);
        deck.addCard("Golgari Rot Farm", 4);
        deck.addCard("Oran-Rief, the Vastwood", 4);
        deck.addCard("Reliquary Tower", 4);
        deck.addCard("Asceticism", 2);
        deck.addCard("Avoid Fate", 4);
        deck.addCard("Collective Unconscious", 2);
        deck.addCard("Darksteel Plate", 4);
        deck.addCard("Elvish Promenade", 3);
        deck.addCard("Konda's Banner", 1);
        deck.addCard("Leyline of Lifeforce", 1);
        deck.addCard("Leyline of Vitality", 1);
        deck.addCard("Nissa Revane", 1);
        deck.addCard("Praetor's Counsel", 1);
        deck.addCard("Prowess of the Fair", 1);
        deck.addCard("Tooth and Nail", 1);
        deck.addCard("Windstorm", 1);
        deck.addCard("Æther Web", 4);
        deck.addCard("Silhana Starfletcher", 2);
        deck.addCard("Spider Umbra", 4);
        deck.addCard("Forest", 24);
        library = new Library(deck);

        final JPanel contentPane = new JPanel(null);
        frame.setContentPane(new JScrollPane(contentPane));

        Dimension blah = new Dimension(2000, 2000);
        contentPane.setMinimumSize(blah);
        contentPane.setSize(blah);
        contentPane.setMaximumSize(blah);
        contentPane.setPreferredSize(blah);

        CardsViewer.table = contentPane;

        JMenuBar menuBar = new JMenuBar();
        JMenu card = new JMenu("card");
        JMenuItem drawAndPlace = new JMenuItem("draw and play: " + library.getSize());
        drawAndPlace.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Card c = library.draw();

                JMenuItem source = (JMenuItem) e.getSource();
                source.setText("draw and play: " + (library.getSize()));
                if (library.getSize() == 0) {
                    source.setEnabled(false);
                }

                CardsViewer.addCard(c);
//                putCard(c, library, contentPane);
            }
        });
        JMenuItem createCard = new JMenuItem("create card");
        createCard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser(new File("C:/Documents and Settings/Jarek/Desktop/MTG/"));
                jfc.showOpenDialog(null);
                Card c = new Card(jfc.getSelectedFile());
                putCard(c, library, contentPane);
            }
        });

        card.add(drawAndPlace);
        card.add(createCard);
        menuBar.add(card);
        frame.setJMenuBar(menuBar);

        for (int i = 1; i <= 7; i++) {
            Card x = library.draw();
//            putCard(x, library, contentPane);
//            x.setCardPosition(Card.H / 2 * i, Card.H / 2);
            CardsViewer.addCard(x);
        }

//        frame.pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(d.width / 2, d.height - Card.H - 80);
        frame.setLocation(0, 0);
//        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        CardsViewer.show();
    }

    public static void putCard(final Card c, Library library, final JPanel contentPane) {
//        c.setCardPosition(Card.H / 2, Card.H / 2);
        c.setCardPosition(szer, wys);
         szer += Card.H / 2;
        if (szer == Card.H * 4) {
            szer = Card.H / 2;
            wys += Card.H / 2;
        }

        for (MouseListener e : c.getMouseListeners()) {
            c.removeMouseListener(e);
        }

        c.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                final Card source = (Card) e.getSource();
                contentPane.remove(source);
                contentPane.add(source, 0);
                source.repaint();
            }
            @Override
            public void mousePressed(MouseEvent e) {
                final Card source = (Card) e.getSource();
                if (e.getButton() == MouseEvent.BUTTON1) {
                    contentPane.remove(source);
                    contentPane.add(source, 0);
                    source.repaint();
                    tempCard = (Card) e.getSource();
                    tempX = e.getX();
                    tempY = e.getY();
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem tapper = new JMenuItem(source.isTapped()? "untap" : "tap");
                    tapper.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            if (source.isTapped()) {
                                source.untap();
                            } else {
                                source.tap();
                            }
                        }
                    });
                    JMenuItem viewerLarger = new JMenuItem("view");
                    viewerLarger.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            c.viewLarger();
                        }
                    });
                    JMenuItem exile = new JMenuItem("exile");
                    exile.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            contentPane.remove(c);
                            contentPane.repaint();
                        }
                    });
                    JMenuItem play = new JMenuItem("return to hand");
                    play.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            CardsViewer.addCard(c);
                            contentPane.remove(c);
                            contentPane.repaint();
                        }
                    });
                    popupMenu.add(tapper);
                    popupMenu.add(play);
                    popupMenu.add(viewerLarger);
                    popupMenu.add(exile);
                    popupMenu.show(source, e.getX(), e.getY());
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1
                        && tempX != e.getX() && tempY != e.getY()) {
                    int newx = tempCard.getXpos() - tempX + e.getX();
                    int newy = tempCard.getYpos() - tempY + e.getY();
                    int margin = Card.H / 2;
                    if (newx < margin) {
                        newx = margin;
                    } else if (newx > contentPane.getWidth() - margin) {
                        newx = contentPane.getWidth() - margin;
                    }
                    if (newy < margin) {
                        newy = margin;
                    } else if (newy > contentPane.getHeight() - margin) {
                        newy = contentPane.getHeight() - margin;
                    }
                    tempCard.setCardPosition(newx, newy);
                }
            }
        });
        contentPane.add(c, 0);
        c.repaint();
    }
}