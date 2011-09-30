package server;

import java.util.logging.Level;
import java.util.logging.Logger;
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
import mtg.Zone;

/**
 * @author Jaroslaw Pawlak
 */
public class Client extends Thread {

    private String serverIP;
    private int fileTransferPort;

    private game.Game game;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private String playerName;

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
        serverIP = ip;
        this.playerName = playerName;
        Debug.p("Connected to " + ip + ":" + port);

        oos = new ObjectOutputStream(s.getOutputStream());
        oos.flush();
        oos.writeObject(new CheckDeck(playerName, deck));
        oos.flush();

        ois = new ObjectInputStream(s.getInputStream());
        fileTransferPort = ois.readInt();
        int players = ois.readInt();

        game = new game.Game(players, Client.this);
        game.log("Connected to", ip + ":" + port);
    }

    @Override
    public void run() {
        Object object = null;
        while (true) {
            object = null;
            try {
                object = ois.readObject();

                // DRAG
                if (object.getClass().equals(DragCard.class)) {
                    DragCard dc = (DragCard) object;
                    game.log(dc.ID, true, game.getPlayerName(dc.requestor)
                            + " drags " + game.getCardName(dc.ID));
                    game.cardDragOnTable(dc.ID, dc.newxpos, dc.newypos);
                    
                // TAP CARD
                } else if (object.getClass().equals(TapCard.class)) {
                    TapCard tc = (TapCard) object;
                    game.log(tc.ID, true, game.getPlayerName(tc.requestor) + " "
                            + (tc.tapped? "taps" : "untaps")
                            + " " + game.getCardName(tc.ID));
                    game.cardTap(tc.ID, tc.tapped);

                // MOVE CARD
                } else if (object.getClass().equals(MoveCard.class)) {
                    handleMoveCard((MoveCard) object);

                // SHUFFLE LIBRARY
                } else if (object.getClass().equals(Shuffle.class)) {
                    Shuffle s = (Shuffle) object;
                    game.log("", game.getPlayerName(s.owner) + " shuffles library");

                // REVEAL
                } else if (object.getClass().equals(Reveal.class)) {
                    Reveal r = (Reveal) object;
                    if (r.source == Zone.TOP_LIBRARY) {
                        game.log(r.cardID, false, game.getPlayerName(r.requstor)
                                + " reveals top card of his library: "
                                + game.getCardName(r.cardID));
                    }

                // REQUEST CARD - server requests client to send card's image
                } else if (object.getClass().equals(RequestCard.class)) {
                    RequestCard t = (RequestCard) object;
                    Socket socket = new Socket(serverIP, fileTransferPort);
                    Utilities.sendFile(new File(Utilities.findPath(t.name)), socket);
                    socket.close();

                // CHECK DECK - server requests client to check if
                //              it has all cards in deck sent
                } else if (object.getClass().equals(CheckDeck.class)) {
                    CheckDeck cd = ((CheckDeck) object);
                    Deck d = cd.deck;

                    game.addPlayer(cd.owner);

                    for (int j = 0; j < d.getArraySize(); j++) {
                        if (Utilities.findPath(d.getArrayNames(j)) == null) {
                            // send card request
                            oos.writeObject(new RequestCard(d.getArrayNames(j)));
                            oos.flush();

                            // receive file
                            Socket t = new Socket(serverIP, fileTransferPort);
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
                            + " " + ((CheckDeck) object).owner + "'s "
                            + d.getName() + ".txt"));

                    game.setPlayerLibrarySize(cd.owner, d.getDeckSize());

                // CARDS LIST
                } else if (object.getClass().equals(CardsList.class)) {
                    game.setCardsList(((CardsList) object).list);
                }
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                Debug.p("Error while dealing with " + object + ": " + ex,
                        Debug.E);
                if (ex.getLocalizedMessage() != null
                        && ex.getLocalizedMessage().equals("Connection reset")) {
                    break;
                }
            }
        }
    }

    private void handleMoveCard(MoveCard mc) {
        switch (mc.source) {
            case HAND:
                switch (mc.destination) {
                    case TABLE:
                        game.log(mc.cardID, true, game.getPlayerName(mc.requestor)
                                + " plays " + game.getCardName(mc.cardID));
                        game.cardAddToTable(mc.cardID);
                        break;
                    case GRAVEYARD:

                        break;
                    case EXILED:

                        break;
                    case LIBRARY:

                        break;
                    case TOP_LIBRARY:

                        break;
                }
                if (playerName.equals(game.getPlayerName(mc.requestor))) {
                    game.cardRemoveFromHand(mc.cardID);
                }
                break;
            case TABLE:
                switch (mc.destination) {
                    case HAND:
//                        //TODO log
//                        if (playerName.equals(game.getPlayerName(mc.requestor))) {
//                            game.cardAddToHand(mc.cardID);
//                        }
                        break;
                    case GRAVEYARD:

                        break;
                    case EXILED:

                        break;
                    case LIBRARY:

                        break;
                    case TOP_LIBRARY:

                        break;
                }
                game.cardRemoveFromTable(mc.cardID);
                break;
            case GRAVEYARD:
                switch (mc.destination) {
                    case HAND:
//                        //TODO log
//                        if (playerName.equals(game.getPlayerName(mc.requestor))) {
//                            game.cardAddToHand(mc.cardID);
//                        }
                        break;
                    case TABLE:
//                        //TODO log
//                        game.cardAddToTable(mc.cardID);
                        break;
                    case EXILED:

                        break;
                    case LIBRARY:

                        break;
                    case TOP_LIBRARY:

                        break;
                }
                break;
            case EXILED:
                switch (mc.destination) {
                    case HAND:
//                        //TODO log
//                        if (playerName.equals(game.getPlayerName(mc.requestor))) {
//                            game.cardAddToHand(mc.cardID);
//                        }
                        break;
                    case TABLE:
//                        //TODO log
//                        game.cardAddToTable(mc.cardID);
                        break;
                    case GRAVEYARD:

                        break;
                    case LIBRARY:

                        break;
                    case TOP_LIBRARY:

                        break;
                }
                break;
            case LIBRARY:
                switch (mc.destination) {
                    case HAND:
//                        //TODO log
//                        if (playerName.equals(game.getPlayerName(mc.requestor))) {
//                            game.cardAddToHand(mc.cardID);
//                        }
                        break;
                    case TABLE:
//                        //TODO log
//                        game.cardAddToTable(mc.cardID);
                        break;
                    case GRAVEYARD:

                        break;
                    case EXILED:

                        break;
                    case TOP_LIBRARY:

                        break;
                }
                break;
            case TOP_LIBRARY:
                switch (mc.destination) {
                    case HAND:
                        if (mc.cardID != null) {
                            game.log(mc.cardID, false, "You draw "
                                    + game.getCardName(mc.cardID));
                            game.cardAddToHand(mc.cardID);
                        } else {
                            game.log(mc.cardID, false,
                                    game.getPlayerName(mc.requestor)
                                    + " draws a card");
                        }
                        break;
                    case TABLE:
//                        //TODO log
//                        game.cardAddToTable(mc.cardID);
                        break;
                    case GRAVEYARD:

                        break;
                    case EXILED:

                        break;
                    case LIBRARY:

                        break;
                }
                break;
        }
    }

    public void send(Action object) {
        try {
            oos.writeObject(object);
            oos.flush();
        } catch (IOException ex) {
            Debug.p("Error while sending " + object + " to server: "
                    + ex, Debug.E);
        }
    }

}