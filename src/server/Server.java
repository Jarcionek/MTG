package server;

import server.flags.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import mtg.Debug;
import mtg.Deck;
import mtg.Main;
import mtg.Utilities;

/**
 * @author Jaroslaw Pawlak
 */
public class Server extends Thread {

    private static Thread serverMainThread;

    private static int port;

    private static ServerSocket ss;

    private static ServerListeningThread[] serverListeningThreads;
    private static Socket[] socket;
    private static ServerSocket[] fileSocket;
    private static ObjectOutputStream[] oos;
    private static ObjectInputStream[] ois;

    private static Deck[] decks;
    private static String[] names;

    static boolean[] ready;

    static Game game;

    private Server() {}

    /**
     * Waits for connection of <code>players</code> number of players. Downloads
     * their decks and exchange with other players missing cards.
     * @param port port to be used for communication. For exchanging cards
     * there will be used ports between <code>port + 1</code> and
     * <code>port + players</code>, both inclusive.
     * @param players number of players
     * @throws IOException if an I/O error occurs when opening the socket.
     * See {@link java.net.ServerSocket#ServerSocket(int)}
     */
    public static void start(int port, int players) throws IOException {
        Server.port = port;

        serverListeningThreads = new ServerListeningThread[players];
        socket = new Socket[players];
        fileSocket = new ServerSocket[players];
        oos = new ObjectOutputStream[players];
        ois = new ObjectInputStream[players];
        
        decks = new Deck[players];
        names = new String[players];

        ready = new boolean[players]; //TODO shouldn't it be synchronized?

        ss = new ServerSocket(port);

        serverMainThread = new Server();
        serverMainThread.start();
    }

    @Override
    public void run() {
        for (int i = 0; i < ready.length; i++) {
            Debug.p("waiting for player " + i + "/" + ready.length);
            ready[i] = false;
            CheckDeck newdeck = null;
            
            try {
                fileSocket[i] = new ServerSocket(port + i + 1);
                socket[i] = ss.accept();
                ois[i] = new ObjectInputStream(socket[i].getInputStream());
                oos[i] = new ObjectOutputStream(socket[i].getOutputStream());
                oos[i].flush();

                // exchange basic info
                newdeck = (CheckDeck) ois[i].readObject();
                names[i] = checkName(newdeck.owner);
                decks[i] = newdeck.deck;
//                decks[i].save(new File(Main.DECKS_DL, Utilities
//                        .getCurrentTimeForFile()+ " " + names[i] + "'s "
//                        + decks[i].getName() + ".txt"));
                oos[i].writeInt(port + i + 1);
                oos[i].flush();
                oos[i].writeInt(ready.length);
                oos[i].flush();

                // check new deck and download missing cards
                for (int j = 0; j < decks[i].getArraySize(); j++) {
                    if (Utilities.findPath(decks[i].getArrayNames(j)) == null) {
                        // send card request
                        send(i, new RequestCard(decks[i].getArrayNames(j)));

                        // receive file
                        Socket t = fileSocket[i].accept();
                        Utilities.receiveFile(new File(Main.CARDS_DL, 
                                decks[i].getArrayNames(j) + ".jpg"), t);
                        t.close();
                    }
                }
                Debug.p("Missing cards downloaded");

                // start listening to the new client
                serverListeningThreads[i]
                        = new ServerListeningThread(i, ois[i], fileSocket[i]);
                serverListeningThreads[i].start();
            } catch (Exception ex) {
                String ip = socket[i].getLocalAddress() == null?
                    "not received" : "" + socket[i].getLocalAddress();
                String msg = "Error while dealing with player " + i + ": "
                        + "IP = " + ip + ", name = " + names[i]
                        + ", exception = " + ex;
                Debug.p(msg, Debug.W);
                i--;
                try {
                    fileSocket[i].close();
                } catch (Exception ex2) {}
                continue;
            }


            // all clients check all decks
            for (int prev = 0; prev < i; prev++) {
                ready[prev] = false;
                // send new deck to already connected clients
                send(prev, newdeck);
                // send already connected clients' decks to the new client
                send(i, new CheckDeck(names[prev], decks[prev]));
            }

            send(i, newdeck);
        }
        Debug.p("Game initialisation finished", Debug.I);

        boolean allReady = false;
        while (!allReady) {
            for (int i = 0; i < ready.length; i++) {
                if (!ready[i]) {
                    break;
                }
            }
            allReady = true;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {}
        }

        game = new Game(decks);
        CardsList x = new CardsList(game.getAllCardsList());
        for (int i = 0; i < ready.length; i++) {
            send(i, x);
        }

        for (int p = 0; p < ready.length; p++) {
            game.libraryShuffle(p);
            sendToAll(new Shuffle(p));
            for (int c = 0; c < 7; c++) {
                Card card = game.libraryDraw(p);
                sendToAllInvisible(new MoveCard(MoveCard.TOP_LIBRARY,
                        MoveCard.HAND, p, card.ID));
            }
        }

        Debug.p("Server main thread terminates");
    }

    /**
     * Sends an action to specified player.
     * @param player player to be sent to
     * @param object action to be sent
     */
    static void send(int player, Action object) {
        if (oos[player] != null) {
            try {
                oos[player].writeObject(object);
                oos[player].flush();
            } catch (IOException ex) {
                Debug.p("Error while sending " + object + " to player "
                        + player + ": " + ex, Debug.E);
            }
        }
    }

    /**
     * Sends an action to all players.
     * @param object action to be sent
     */
    static void sendToAll(Action object) {
        for (int i = 0; i < ready.length; i++) {
            send(i, object);
        }
    }

    /**
     * Sends an MoveCard action to all players but only to a requestor player
     * is sent a card's ID. For example, if there are four players and player 2
     * draws a card, players 0, 1 and 3 receives MoveCard object but with
     * no card ID, while player 2 receives a full object with a proper ID.
     * @param mc object to be sent
     */
    static void sendToAllInvisible(MoveCard mc) {
        String id = mc.cardID;
        mc.cardID = null;
        for (int i = 0; i < ready.length; i++) {
            if (i == mc.requestor) {
                mc.cardID = id;
                Server.send(i, mc);
                mc.cardID = null;
            } else {
                Server.send(i, mc);
            }
        }
    }

    /**
     * Closes all streams and sockets of given player.
     * @param player player
     */
    static void disconnect(int player) {
        Debug.p("Player " + player + " - " + names[player] + " disconneced");
        try {
            socket[player].close();
        } catch (IOException ex) {}
        try {
            fileSocket[player].close();
        } catch (IOException ex) {}
        fileSocket[player] = null;
        socket[player] = null;
        ois[player] = null;
        oos[player] = null;
    }

    static String checkName(String name) {
        name = name.replaceAll("\\W", "");
        if (name.length() > 15) {
            name = name.substring(0, 15);
        }
        for (int i = 0; i < names.length; i++) {
            if (name.equals(names[i])) {
                return checkName(name + "-");
            }
        }
        return name;
    }
}
