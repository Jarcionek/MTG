package mtg;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import javax.imageio.ImageIO;

/**
 * @author Jaroslaw Pawlak
 */
public class Client {
    public static void main(String[] args) throws Exception {
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

        String playerName = "Jarek";

        Socket s = new Socket("localhost", 12345);
        ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
        oos.flush();
        ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
        oos.writeObject(playerName);
        oos.writeObject(deck);

        Object object;
        while((object = ois.readObject()).getClass().equals(String.class)) {
            String name = (String) object;

            Utilities.sendFile(
                    new File(Utilities.findPath(deck.getDirectory(), name)),
                    new Socket("localhost", 12346));
        }
    }
}