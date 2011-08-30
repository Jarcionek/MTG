package mtg;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.imageio.ImageIO;

/**
 * @author Jaroslaw Pawlak
 */
public class Server {
    private static final int PORT = 12345;
    private static final int FILE_TRANSFER_PORT = 12346;

    private static final File IMAGES = new File("c:/Documents and Settings/Jarek/Desktop/MTG2");
    private static final int PLAYERS = 2;

    private static Socket[] socket = new Socket[PLAYERS];
    private static ObjectOutputStream[] oos = new ObjectOutputStream[PLAYERS];
    private static ObjectInputStream[] ois = new ObjectInputStream[PLAYERS];

    private static Deck[] deck = new Deck[PLAYERS];
    private static String[] name = new String[PLAYERS];

    public static void main(String[] args) throws Exception {
        if (!IMAGES.exists()) {
            IMAGES.mkdirs();
        }
        ServerSocket ss = new ServerSocket(PORT);
        for (int i = 0; i < PLAYERS; i++) {
            socket[i] = ss.accept();
            ois[i] = new ObjectInputStream(socket[i].getInputStream());
            oos[i] = new ObjectOutputStream(socket[i].getOutputStream());
            oos[i].flush();
            // get basic info
            name[i] = (String) ois[i].readObject();
            deck[i] = (Deck) ois[i].readObject();
            // check and download cards
            ServerSocket ssf = new ServerSocket(FILE_TRANSFER_PORT);
            for (int j = 0; j < deck[i].getArraySize(); j++) {
                if (Utilities.findPath(IMAGES, deck[i].getArrayNames(j)) == null) {
                    oos[i].writeObject(deck[i].getArrayNames(j));
                    oos[i].flush();
                    Utilities.receiveFile(
                            new File(IMAGES, deck[i].getArrayNames(j).concat(".jpg")),
                            ssf.accept());
                }
            }
            ssf.close();
        }
    }

    
}
