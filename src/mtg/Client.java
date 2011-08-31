package mtg;

import flags.CheckDeck;
import flags.RequestCard;
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
    private static String temp = "c:/Documents and Settings/Jarek/Desktop/MTG2/client3";

    private static final File IMAGES
            = new File(temp);

    private static final String IP = "localhost";
    private static final int PORT = 12345;
    private static int fileTransferPort;

    public static void main(String[] args) throws Exception {
        Deck deck = new Deck(temp);
//        deck.addCard("Abyssal Specter", 4);
//        deck.addCard("Befoul", 4);
        deck.addCard("Coercion", 4);

        String playerName = "Jarek";

        Socket s = new Socket(IP, PORT);
        Debug.p("Connected to " + IP + ":" + PORT, Debug.I);
        ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
        oos.flush();
        ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
        oos.writeObject(playerName);
        Debug.p("Name sent", Debug.I);
        oos.writeObject(deck);
        Debug.p("Deck sent", Debug.I);
        fileTransferPort = ois.readInt();
        Debug.p("File transfer port received " + fileTransferPort, Debug.I);

        Object object;
        while(true) {
            object = ois.readObject();
            Debug.p("Object received", Debug.I);

            // REQUEST CARD - server requests client to send card's image
            if (object.getClass().equals(RequestCard.class)) {
                RequestCard t = (RequestCard) object;
                Debug.p("Card \"" + t.name + "\" request received", Debug.I);
                Socket socket = new Socket(IP, fileTransferPort);
                Debug.p("Sending card \"" + t.name + "\"", Debug.I);
                Utilities.sendFile(
                        new File(Utilities.findPath(deck.getDirectory(), t.name)),
                        socket);
                socket.close();
                Debug.p("Card \"" + t.name + "\" sent", Debug.I);

            // CHECK DECK - server requests client to check if
            //              it has all cards in deck sent
            } else if (object.getClass().equals(CheckDeck.class)) {
                Deck d = ((CheckDeck) object).deck;
                Debug.p("Check deck request received", Debug.I);
                for (int j = 0; j < d.getArraySize(); j++) {
                    if (Utilities.findPath(IMAGES, d.getArrayNames(j)) == null) {
                        // send card request
                        Debug.p("Card \"" + d.getArrayNames(j) + "\" not found", Debug.I);
                        oos.writeObject(new RequestCard(d.getArrayNames(j)));
                        oos.flush();
                        Debug.p("Card request sent", Debug.I);

                        // receive file
                        Socket t = new Socket(IP, fileTransferPort);
                        Debug.p("Receiving file", Debug.I);
                        Utilities.receiveFile(
                                new File(IMAGES, d.getArrayNames(j).concat(".jpg")),
                                t);
                        t.close();
                        Debug.p("File received", Debug.I);
                    }
                }
            }
        }
    }
}