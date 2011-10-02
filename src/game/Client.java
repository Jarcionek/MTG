package game;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Color;
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

    private game.Game g;

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
        this.playerName = playerName; //TODO remove, look below
        Debug.p("Connected to " + ip + ":" + port);

        oos = new ObjectOutputStream(s.getOutputStream());
        oos.flush();
        oos.writeObject(new CheckDeck(playerName, deck));
        oos.flush();

        ois = new ObjectInputStream(s.getInputStream());
        fileTransferPort = ois.readInt();
        int players = ois.readInt();
        //TODO in case players have the same name, here changed name must be
        //      obtained from the server

        g = new game.Game(players, Client.this);
        g.log("Connected to", ip + ":" + port, Color.black);
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
                    g.log(dc.ID, true, g.getPlayerName(dc.requestor)
                            + " drags " + Game.getCardName(dc.ID), game.Logger.C_DRAG);
                    g.cardDragOnTable(dc.ID, dc.newxpos, dc.newypos);
                    
                // TAP CARD
                } else if (object.getClass().equals(TapCard.class)) {
                    TapCard tc = (TapCard) object;
                    g.log(tc.ID, true, g.getPlayerName(tc.requestor) + " "
                            + (tc.tapped? "taps" : "untaps")
                            + " " + Game.getCardName(tc.ID), game.Logger.C_TAP);
                    g.cardTap(tc.ID, tc.tapped);

                // MOVE CARD
                } else if (object.getClass().equals(MoveCard.class)) {
                    handleMoveCard((MoveCard) object);

                // CHANGE HP OR POISON COUNTERS
                } else if (object.getClass().equals(Player.class)) {
                    Player p = (Player) object;
                    if (p.poisonOrHealth == Player.HEALTH) {
                        g.log("", g.getPlayerName(p.requestor)
                                + " changes " + g.getPlayerName(p.target)
                                + "'s health from "
                                + g.playerSetHealth(p.target, p.newValue)
                                + " to " + p.newValue, game.Logger.C_CHANGE_HP);
                    } else if (p.poisonOrHealth == Player.POISON) {
                        g.log("", g.getPlayerName(p.requestor)
                                + " changes " + g.getPlayerName(p.target)
                                + "'s poison counters from "
                                + g.playerSetPoison(p.target, p.newValue)
                                + " to " + p.newValue, game.Logger.C_CHANGE_HP);
                    }

                // SEARCH
                } else if (object.getClass().equals(Search.class)) {
                    Search s = (Search) object;
                    switch (s.zone) {
                        case LIBRARY:
                            if (s.amount == -1) {
                                g.log("", g.getPlayerName(s.requestor)
                                        + " searches his library",
                                        game.Logger.C_SEARCH_LIBRARY);
                            } else {
                                g.log("", g.getPlayerName(s.requestor)
                                        + " looks at the " + s.amount
                                        + " top cards of his library",
                                        game.Logger.C_SEARCH_LIBRARY);
                            }
                            if (s.cardsIDs != null) {
                                CardViewer.createViewerInFrame(s.cardsIDs,
                                        Zone.LIBRARY, g.getSize(),
                                        "Your library");
                            }
                            break;
                        case GRAVEYARD:
                            g.log("", g.getPlayerName(s.requestor)
                                    + " searches "
                                    + g.getPlayerName(s.zoneOwner)
                                    + "'s graveyard", game.Logger.C_SEARCH_GRAVEYARD);
                            if (s.cardsIDs != null) {
                                CardViewer.createViewerInFrame(s.cardsIDs,
                                        Zone.GRAVEYARD, g.getSize(),
                                        g.getPlayerName(s.zoneOwner)
                                        + "'s graveyard ("
                                        + s.cardsIDs.length + " cards)");
                            }
                            break;
                        case EXILED:
                            g.log("", g.getPlayerName(s.requestor)
                                    + " searches "
                                    + g.getPlayerName(s.zoneOwner)
                                    + "'s exiled zone", game.Logger.C_SEARCH_EXILED);
                            if (s.cardsIDs != null) {
                            CardViewer.createViewerInFrame(s.cardsIDs,
                                    Zone.EXILED, g.getSize(),
                                    g.getPlayerName(s.zoneOwner)
                                    + "'s exiled zone (" + s.cardsIDs.length
                                    + " cards)");
                            }
                            break;
                    }

                // SHUFFLE LIBRARY
                } else if (object.getClass().equals(Shuffle.class)) {
                    Shuffle s = (Shuffle) object;
                    g.log("", g.getPlayerName(s.owner) + " shuffles his library",
                            game.Logger.C_SHUFFLE);

                // REVEAL
                } else if (object.getClass().equals(Reveal.class)) {
                    Reveal r = (Reveal) object;
                    if (r.source == Zone.TOP_LIBRARY) {
                        g.log(r.cardID, false, g.getPlayerName(r.requstor)
                                + " reveals top card of his library: "
                                + Game.getCardName(r.cardID), game.Logger.C_REVEAL);
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

                    g.addPlayer(cd.owner);

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

                    g.setPlayerLibrarySize(cd.owner, d.getDeckSize());

                // CARDS LIST
                } else if (object.getClass().equals(CardsList.class)) {
                    g.setCardsList(((CardsList) object).list);
                }
            } catch (Exception ex) {
                if ("Connection reset".equals(ex.getLocalizedMessage())) {
                    Debug.p("Server closed", Debug.CE);
                    break;
                } else {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    Debug.p("Client error while dealing with " + object + ": " + ex,
                            Debug.E);
                }
            }
        }
    }

    private void handleMoveCard(MoveCard mc) {
        String owner;
        if (mc.cardID == null) {
            owner = "his";
        } else {
            owner = g.getPlayerName(mc.cardID.charAt(0) - 'A');
            if (owner.equals(g.getPlayerName(mc.requestor))) {
                owner = "his";
            } else if (owner.equals(playerName)) {
                owner = "your";
            } else {
                owner += "'s";
            }
        }
        switch (mc.source) {
            case HAND:
                switch (mc.destination) {
                    case TABLE:
                        g.log(mc.cardID, true, g.getPlayerName(mc.requestor)
                                + " plays " + Game.getCardName(mc.cardID),
                                game.Logger.C_MOVE_PLAY);
                        g.cardAddToTable(mc.cardID);
                        break;
                    case GRAVEYARD:
                        g.log(mc.cardID, false, g.getPlayerName(mc.requestor)
                                + " discards " + Game.getCardName(mc.cardID)
                                + " from his hand",
                                game.Logger.C_MOVE_DESTROY);
                        break;
                    case EXILED:
                        g.log(mc.cardID, false, g.getPlayerName(mc.requestor)
                                + " exiles " + Game.getCardName(mc.cardID)
                                + " from his hand",
                                game.Logger.C_MOVE_EXILE);
                        break;
                    case LIBRARY:
                        throw new UnsupportedOperationException();
                    case TOP_LIBRARY:
                        g.changeLibrarySize(mc.requestor, 1);
                        if (mc.reveal) {
                            g.log(mc.cardID, false, g.getPlayerName(mc.requestor)
                                    + " reveals " + Game.getCardName(mc.cardID)
                                    + " from his hand and puts it "
                                    + "on top of his library",
                                    game.Logger.C_MOVE_TO_LIBRARY);
                        } else if (mc.cardID == null) {
                            g.log(mc.cardID, false, g.getPlayerName(mc.requestor)
                                    + " puts a card from his hand on "
                                    + "top of his library",
                                    game.Logger.C_MOVE_TO_LIBRARY);
                        } else {
                            g.log(mc.cardID, false, "You put "
                                    + Game.getCardName(mc.cardID)
                                    + " from your hand on "
                                    + " top of your library",
                                    game.Logger.C_MOVE_TO_LIBRARY);
                        }
                        break;
                }
                g.changeHandSize(mc.requestor, -1);
                if (playerName.equals(g.getPlayerName(mc.requestor))) {
                    g.cardRemoveFromHand(mc.cardID);
                }
                break;
            case TABLE:
                switch (mc.destination) {
                    case HAND:
                        g.changeHandSize(mc.requestor, 1);
                        g.log(mc.cardID, false, g.getPlayerName(mc.requestor)
                                    + " returns " + Game.getCardName(mc.cardID)
                                    + " from table to " + owner + " hand",
                                game.Logger.C_MOVE_TO_HAND);
                        if (playerName.equals(g.getPlayerName(mc.requestor))) {
                            g.cardAddToHand(mc.cardID);
                        }
                        break;
                    case GRAVEYARD:
                        g.log(mc.cardID, false, g.getPlayerName(mc.requestor)
                                    + " puts " + Game.getCardName(mc.cardID)
                                    + " from table on " + owner + " graveyard",
                                game.Logger.C_MOVE_DESTROY);
                        break;
                    case EXILED:
                        g.log(mc.cardID, false,
                                g.getPlayerName(mc.requestor) + " exiles "
                                + Game.getCardName(mc.cardID)
                                + " from the table",
                                game.Logger.C_MOVE_EXILE);
                        break;
                    case LIBRARY:
                        throw new UnsupportedOperationException();
                    case TOP_LIBRARY:
                        g.changeLibrarySize(mc.requestor, 1);
                        g.log(mc.cardID, false, g.getPlayerName(mc.requestor)
                                    + " puts " + Game.getCardName(mc.cardID)
                                    + " on top of " + owner + " library",
                                game.Logger.C_MOVE_TO_LIBRARY);
                        break;
                }
                g.cardRemoveFromTable(mc.cardID);
                break;
            case GRAVEYARD:
                switch (mc.destination) {
                    case HAND:
                        g.changeHandSize(mc.requestor, 1);
                        g.log(mc.cardID, false, g.getPlayerName(mc.requestor)
                                + " takes " + Game.getCardName(mc.cardID)
                                + " from " + owner + " graveyard to his hand",
                                game.Logger.C_MOVE_PLAY);
                        if (playerName.equals(g.getPlayerName(mc.requestor))) {
                            g.cardAddToHand(mc.cardID);
                        }
                        break;
                    case TABLE:
                        g.log(mc.cardID, true, g.getPlayerName(mc.requestor)
                                + " plays " + Game.getCardName(mc.cardID)
                                + " from " + owner + " graveyard",
                                game.Logger.C_MOVE_PLAY);
                        g.cardAddToTable(mc.cardID);
                        break;
                    case EXILED:
                        g.log(mc.cardID, false, g.getPlayerName(mc.requestor)
                                + " exiles " + Game.getCardName(mc.cardID)
                                + " from " + owner + " graveyard",
                                game.Logger.C_MOVE_EXILE);
                        break;
                    case LIBRARY:
                        throw new UnsupportedOperationException();
                    case TOP_LIBRARY:
                        g.changeLibrarySize(mc.requestor, 1);
                        g.log(mc.cardID, false, g.getPlayerName(mc.requestor)
                                + " puts " + Game.getCardName(mc.cardID)
                                + " from " + owner + " graveyard on top of "
                                + owner + " library",
                                game.Logger.C_MOVE_TO_LIBRARY);
                        break;
                }
                if (playerName.equals(g.getPlayerName(mc.requestor))) {
                    CardViewer.removeCardFromCurrentlyOpenCardViewer(mc.cardID);
                }
                break;
            case EXILED:
                switch (mc.destination) {
                    case HAND:
                        g.changeHandSize(mc.requestor, 1);
                        g.log(mc.cardID, false, g.getPlayerName(mc.requestor)
                                + " takes " + Game.getCardName(mc.cardID)
                                + " from " + owner + " exiled zone to his hand",
                                game.Logger.C_MOVE_TO_HAND);
                        if (playerName.equals(g.getPlayerName(mc.requestor))) {
                            g.cardAddToHand(mc.cardID);
                        }
                        break;
                    case TABLE:
                        g.log(mc.cardID, true, g.getPlayerName(mc.requestor)
                                + " plays " + Game.getCardName(mc.cardID)
                                + " from " + owner + " exiled zone",
                                game.Logger.C_MOVE_PLAY);
                        g.cardAddToTable(mc.cardID);
                        break;
                    case GRAVEYARD:
                        g.log(mc.cardID, false, g.getPlayerName(mc.requestor)
                                + " moves " + Game.getCardName(mc.cardID)
                                + " from " + owner + " exiled zone to his"
                                + " graveyard",
                                game.Logger.C_MOVE_DESTROY);
                        break;
                    case LIBRARY:
                        throw new UnsupportedOperationException();
                    case TOP_LIBRARY:
                        g.changeLibrarySize(mc.requestor, 1);
                        g.log(mc.cardID, false, g.getPlayerName(mc.requestor)
                                + " puts " + Game.getCardName(mc.cardID)
                                + " from " + owner + " graveyard on top of "
                                + owner + " library",
                                game.Logger.C_MOVE_TO_LIBRARY);
                        break;
                }
                if (playerName.equals(g.getPlayerName(mc.requestor))) {
                    CardViewer.removeCardFromCurrentlyOpenCardViewer(mc.cardID);
                }
                break;
            case LIBRARY:
                switch (mc.destination) {
                    case HAND:
                        g.changeLibrarySize(mc.requestor, -1);
                        g.changeHandSize(mc.requestor, 1);
                        if (mc.reveal) {
                            g.log(mc.cardID, false, g.getPlayerName(mc.requestor)
                                    + " takes " + Game.getCardName(mc.cardID)
                                    + " from his library to hand",
                                    game.Logger.C_MOVE_TO_HAND);
                        } else if (mc.cardID == null) {
                            g.log(mc.cardID, false, g.getPlayerName(mc.requestor)
                                    + " takes a card from his library to hand",
                                    game.Logger.C_MOVE_TO_HAND);
                        } else {
                            g.log(mc.cardID, false, "You take "
                                    + Game.getCardName(mc.cardID)
                                    + " from your library to hand",
                                    game.Logger.C_MOVE_TO_HAND);
                        }
                        if (playerName.equals(g.getPlayerName(mc.requestor))) {
                            g.cardAddToHand(mc.cardID);
                        }
                        break;
                    case TABLE:
                        g.changeLibrarySize(mc.requestor, -1);
                        g.log(mc.cardID, true,
                                g.getPlayerName(mc.requestor) + " puts "
                                + Game.getCardName(mc.cardID) + " from his library "
                                + "onto table", game.Logger.C_MOVE_PLAY);
                        g.cardAddToTable(mc.cardID);
                        break;
                    case GRAVEYARD:
                        g.changeLibrarySize(mc.requestor, -1);
                        g.log(mc.cardID, false,
                                g.getPlayerName(mc.requestor) + " puts "
                                + Game.getCardName(mc.cardID) + " from his library "
                                + "onto his graveyard", game.Logger.C_MOVE_DESTROY);
                        break;
                    case EXILED:
                        g.changeLibrarySize(mc.requestor, -1);
                        g.log(mc.cardID, false,
                                g.getPlayerName(mc.requestor) + " exiles "
                                + Game.getCardName(mc.cardID) + " from his library",
                                game.Logger.C_MOVE_EXILE);
                        break;
                    case TOP_LIBRARY:
                        if (mc.reveal) {
                            g.log(mc.cardID, false, g.getPlayerName(mc.requestor)
                                    + " puts " + Game.getCardName(mc.cardID)
                                    + " from his library on top of it",
                                    game.Logger.C_MOVE_TO_LIBRARY);
                        } else if (mc.cardID == null) {
                            g.log(mc.cardID, false, g.getPlayerName(mc.requestor)
                                    + " puts a card from his library on top of it",
                                    game.Logger.C_MOVE_TO_LIBRARY);
                        } else {
                            g.log(mc.cardID, false, "You put "
                                    + Game.getCardName(mc.cardID)
                                    + " from your library on top of it",
                                    game.Logger.C_MOVE_TO_LIBRARY);
                        }
                        break;
                }
                if (playerName.equals(g.getPlayerName(mc.requestor))
                        && mc.destination != Zone.TOP_LIBRARY) {
                    CardViewer.removeCardFromCurrentlyOpenCardViewer(mc.cardID);
                }
                break;
            case TOP_LIBRARY:
                switch (mc.destination) {
                    case HAND:
                        g.changeLibrarySize(mc.requestor, -1);
                        g.changeHandSize(mc.requestor, 1);
                        if (mc.cardID != null) {
                            g.log(mc.cardID, false, "You draw "
                                    + Game.getCardName(mc.cardID),
                                    game.Logger.C_MOVE_TO_HAND);
                            g.cardAddToHand(mc.cardID);
                        } else {
                            g.log(mc.cardID, false,
                                    g.getPlayerName(mc.requestor)
                                    + " draws a card",
                                    game.Logger.C_MOVE_TO_HAND);
                        }
                        break;
                    case TABLE:
                        g.changeLibrarySize(mc.requestor, -1);
                        g.log(mc.cardID, true,
                                g.getPlayerName(mc.requestor)
                                + " plays top card of " + owner + " library: "
                                + Game.getCardName(mc.cardID),
                                game.Logger.C_MOVE_PLAY);
                        g.cardAddToTable(mc.cardID);
                        break;
                    case GRAVEYARD:
                        throw new UnsupportedOperationException(); //TODO this move is legal
                    case EXILED:
                        throw new UnsupportedOperationException(); //TODO this move is legal
                    case LIBRARY:
                        throw new UnsupportedOperationException();
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