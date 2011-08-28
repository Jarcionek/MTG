package mtg;

import java.io.File;
import javax.swing.JFrame;

/**
 * @author Jaroslaw Pawlak
 */
public class TempTest {
    public static void main(String[] args) {
        Deck x = new Deck("c:/Documents and Settings/Jarek/Desktop/MTG/nice");
        x.addCard("Nin, the Pain Artist", 4);
        x.addCard("Curiosity", 4);
        x.addCard("Ivory Tower", 4);
        x.addCard("Insist", 4);
        x.addCard("Explore", 4);
        x.addCard("Quicken", 4);
        x.addCard("Reroute", 4);
        x.addCard("Plains", 14);

        Library y = new Library(x);
        y.shuffle();
        y.tempprint();
    }
}