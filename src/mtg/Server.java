package mtg;

import flags.CheckDeck;
import flags.RequestCard;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Jaroslaw Pawlak
 */
public class Server extends Thread {
    private static final int PORT = 12345;

    private static final File IMAGES = new File("c:/Documents and Settings/Jarek/Desktop/MTG2/server");
    private static final int PLAYERS = 3;

    private static Socket[] socket = new Socket[PLAYERS];
    private static ServerSocket[] fileSocket = new ServerSocket[PLAYERS];
    private static ObjectOutputStream[] oos = new ObjectOutputStream[PLAYERS];
    private static ObjectInputStream[] ois = new ObjectInputStream[PLAYERS];

    private static Deck[] deck = new Deck[PLAYERS];
    private static String[] name = new String[PLAYERS];

    private int id;

    public static void main(String[] args) throws Exception {
        if (!IMAGES.exists()) {
            IMAGES.mkdirs();
        }
        ServerSocket ss = new ServerSocket(PORT);
        for (int i = 0; i < PLAYERS; i++) {
            fileSocket[i] = new ServerSocket(PORT + i + 1);
            Debug.p("Waiting for player " + i + "/" + PLAYERS, Debug.I);
            socket[i] = ss.accept();
            Debug.p("Player connected", Debug.I);
            ois[i] = new ObjectInputStream(socket[i].getInputStream());
            oos[i] = new ObjectOutputStream(socket[i].getOutputStream());
            oos[i].flush();

            // exchange basic info
            name[i] = (String) ois[i].readObject();
            Debug.p("Player's name received: " + name[i], Debug.I);
            deck[i] = (Deck) ois[i].readObject();
            Debug.p("Player's deck received", Debug.I);
            oos[i].writeInt(PORT + i + 1);
            oos[i].flush();
            Debug.p("Player's file transfer port sent: " + (PORT + i + 1), Debug.I);

            // check new deck and download missing cards
            for (int j = 0; j < deck[i].getArraySize(); j++) {
                if (Utilities.findPath(IMAGES, deck[i].getArrayNames(j)) == null) {
                    // send card request
                    Debug.p("Card \"" + deck[i].getArrayNames(j) + "\" not found", Debug.I);
                    oos[i].writeObject(new RequestCard(deck[i].getArrayNames(j)));
                    oos[i].flush();
                    Debug.p("Card request sent", Debug.I);

                    // receive file
                    Socket t = fileSocket[i].accept();
                    Debug.p("Receiving file", Debug.I);
                    Utilities.receiveFile(
                            new File(IMAGES, deck[i].getArrayNames(j).concat(".jpg")),
                            t);
                    t.close();
                    Debug.p("File received", Debug.I);
                }
            }
            Debug.p("All cards downloaded", Debug.I);

            // start listening to the new client
            (new Server(i)).start();
            Debug.p("Listening thread started", Debug.I);

            // all clients check all decks
            for (int j = 0; j < i; j++) {
                // send new deck to already connected clients
                oos[j].writeObject(new CheckDeck(deck[i]));
                oos[j].flush();
                Debug.p(i + "'s deck sent to " + j, Debug.I);
                // send already connected clients' decks to the new client
                oos[i].writeObject(new CheckDeck(deck[j]));
                oos[i].flush();
                Debug.p(j + "'s deck sent to " + i, Debug.I);
            }
        }
        Debug.p("Game initialisation finished", Debug.I);
    }

    private Server(int id) {
        super();
        this.id = id;
    }

    @Override
    public void run() {
        Object object = null;
        while(true) {
            try {
                object = null;
                object = ois[id].readObject();
                Debug.p("Listening thread " + id + " received object", Debug.I);

                if (object.getClass().equals(RequestCard.class)) {
                    RequestCard t = ((RequestCard) object);
                    Debug.p("Listening thread " + id + " received request card \"" + t.name + "\"", Debug.I);
                    Socket s = fileSocket[id].accept();
                    Debug.p("Listening thread " + id + " sending card \"" + t.name + "\"", Debug.I);
                    Utilities.sendFile(new File(Utilities.findPath(IMAGES, t.name)), s);
                    s.close();
                    Debug.p("Listening thread " + id + " sent card \"" + t.name + "\"", Debug.I);
                }
            } catch (Exception ex) {
                Debug.p("Listening thread " + id + " crashed while dealing with " + object + ": " + ex, Debug.E);
                if (ex.getLocalizedMessage().equals("Connection reset")) {
                    break;
                }
            }
        }
    }
}
