package server;

import server.flags.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import mtg.Debug;
import mtg.Deck;
import mtg.Main;
import mtg.Utilities;

/**
 * @author Jaroslaw Pawlak
 */
public class Client {

    private int fileTransferPort;

    private Client() {}

    /**
     * Creates and starts client thread.
     * @param playerName player's name to send to the server
     * @param ip ip of the server
     * @param port port the server is listening to
     * @param deck player's deck
     * @throws IOException if client could not connect to the server, could
     * not send player's name and deck or could not receive file transfer port
     */
    public Client(String playerName, String ip, int port, Deck deck)
            throws IOException {
        Socket s = new Socket(ip, port);
        Debug.p("Connected to " + ip + ":" + port);
        ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
        oos.flush();
        ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
        oos.writeObject(new CheckDeck(playerName, deck));
        fileTransferPort = ois.readInt();

        Object object = null;
        while (true) {
            object = null;
            try {
                object = ois.readObject();

                // REQUEST CARD - server requests client to send card's image
                if (object.getClass().equals(RequestCard.class)) {
                    RequestCard t = (RequestCard) object;
                    Socket socket = new Socket(ip, fileTransferPort);
                    Utilities.sendFile(new File(Utilities.findPath(t.name)), socket);
                    socket.close();

                // CHECK DECK - server requests client to check if
                //              it has all cards in deck sent
                } else if (object.getClass().equals(CheckDeck.class)) {
                    Deck d = ((CheckDeck) object).deck;
                    for (int j = 0; j < d.getArraySize(); j++) {
                        if (Utilities.findPath(d.getArrayNames(j)) == null) {
                            // send card request
                            oos.writeObject(new RequestCard(d.getArrayNames(j)));
                            oos.flush();

                            // receive file
                            Socket t = new Socket(ip, fileTransferPort);
                            Utilities.receiveFile(new File(Main.CARDS_DL,
                                    d.getArrayNames(j) + ".jpg"), t);
                            t.close();
                        }
                    }
                    oos.writeObject(new Ready());
                    oos.flush();
                    // save deck
                    d.save(new File(Main.DECKS_DL, Utilities
                            .getCurrentTimeForFile()
                            + " " + ((CheckDeck) object).owner + " "
                            + d.getName() + ".txt"));
                }
            } catch (Exception ex) {
                Debug.p("Error while dealing with " + object + ": " + ex,
                        Debug.E);
                if (ex.getLocalizedMessage() != null
                        && ex.getLocalizedMessage().equals("Connection reset")) {
                    break;
                }
            }
        }
    }
}