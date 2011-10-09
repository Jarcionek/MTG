package game;

import java.awt.Color;
import server.flags.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import mtg.Debug;
import mtg.Deck;
import mtg.InvalidDeckException;
import mtg.Main;
import mtg.Utilities;
import mtg.Zone;

/**
 * @author Jaroslaw Pawlak
 */
public class Client extends Thread {

    private String serverIP;
    private int fileTransferPort;

    private Game g;

    private Socket s;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private String playerName;
    
    private JFrame parentFrame;

    private Client() {}

    /**
     * Creates and starts client thread.
     * @param parent sets invisible when client successfully connects and
     * sets visible if game GUI is closed
     * @param playerName player's name to send to the server
     * @param ip ip of the server
     * @param port port the server is listening to
     * @param deck player's deck
     * @throws IOException if client could not connect to the server, could
     * not send player's name and deck or could not receive file transfer port
     */
    public Client(JFrame parent, String playerName, String ip, int port, Deck deck)
            throws IOException, ClassNotFoundException, InvalidDeckException {
        super("Client");
    
        this.s = new Socket(ip, port);
        this.serverIP = ip;
        this.parentFrame = parent;
        Debug.p("Client: Connected to " + ip + ":" + port);

        oos = new ObjectOutputStream(s.getOutputStream());
        oos.flush();
        oos.writeObject(new CheckDeck(playerName, deck));
        oos.flush();

        ois = new ObjectInputStream(s.getInputStream());
        
        try {
            Object obj = ois.readObject();
            if (obj.getClass().equals(InvalidDeckException.class)) {
                throw (InvalidDeckException) obj;
            }
            this.playerName = (String) obj;
        } catch (ClassNotFoundException | InvalidDeckException ex) {
            try {
                this.s.close();
            } catch (Exception ex1) {}
            throw ex;
        }
        fileTransferPort = ois.readInt();
        int players = ois.readInt();

        g = new game.Game(players, Client.this);
        g.log("Connected to", ip + ":" + port, Color.black);
        parent.setVisible(false);
    }

    @Override
    public void run() {
        Action object = null;
        while (!isInterrupted()) {
            object = null;
            try {
                object = (Action) ois.readObject();
                Debug.p("Client: Client received: " + object);

                // MESSAGE
                if (object.getClass().equals(Message.class)) {
                    Message m = (Message) object;
                    g.log(g.getPlayerName(m.requestor) + ":", m.msg,
                            Logger.C_MESSAGE);
                    
                // DRAG
                } else if (object.getClass().equals(DragCard.class)) {
                    DragCard dc = (DragCard) object;
                    g.log(dc.ID, true, g.getPlayerName(dc.requestor)
                            + " drags " + Game.getCardName(dc.ID), game.Logger.C_DRAG);
                    g.cardDragOnTable(dc.ID, dc.newxpos, dc.newypos);
                    
                // TAP CARD
                } else if (object.getClass().equals(TapCard.class)) {
                    TapCard tc = (TapCard) object;
                    String owner;
                    if (tc.requestor == tc.ID.charAt(0) - 'A') {
                        owner = "his";
                    } else {
                        owner = g.getPlayerName(tc.ID.charAt(0) - 'A') + "'s";
                    }
                    g.log(tc.ID, true, g.getPlayerName(tc.requestor) + " "
                            + (tc.tapped? "taps" : "untaps")
                            + " " + owner + " "
                            + Game.getCardName(tc.ID), game.Logger.C_TAP);
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

                // UNTAP ALL
                } else if (object.getClass().equals(UntapAll.class)) {
                    g.log("", g.getPlayerName(object.requestor)
                            + " untaps all cards he owns", Logger.C_TAP);
                    g.cardUntapAll(object.requestor);

                // SEARCH
                } else if (object.getClass().equals(Search.class)) {
                    Search se = (Search) object;
                    switch (se.zone) {
                        case LIBRARY:
                            if (se.amount == -1) {
                                g.log("", g.getPlayerName(se.requestor)
                                        + " searches his library",
                                        game.Logger.C_SEARCH_LIBRARY);
                            } else {
                                g.log("", g.getPlayerName(se.requestor)
                                        + " looks at the " + se.amount
                                        + " top cards of his library",
                                        game.Logger.C_SEARCH_LIBRARY);
                            }
                            if (se.cardsIDs != null) {
                                CardViewer.createViewerInFrame(se.cardsIDs,
                                        Zone.LIBRARY, g.getSize(),
                                        "Your library");
                            }
                            break;
                        case GRAVEYARD:
                            g.log("", g.getPlayerName(se.requestor)
                                    + " searches "
                                    + g.getPlayerName(se.zoneOwner)
                                    + "'s graveyard", game.Logger.C_SEARCH_GRAVEYARD);
                            if (se.cardsIDs != null) {
                                CardViewer.createViewerInFrame(se.cardsIDs,
                                        Zone.GRAVEYARD, g.getSize(),
                                        g.getPlayerName(se.zoneOwner)
                                        + "'s graveyard ("
                                        + se.cardsIDs.length + " cards)");
                            }
                            break;
                        case EXILED:
                            g.log("", g.getPlayerName(se.requestor)
                                    + " searches "
                                    + g.getPlayerName(se.zoneOwner)
                                    + "'s exiled zone", game.Logger.C_SEARCH_EXILED);
                            if (se.cardsIDs != null) {
                            CardViewer.createViewerInFrame(se.cardsIDs,
                                    Zone.EXILED, g.getSize(),
                                    g.getPlayerName(se.zoneOwner)
                                    + "'s exiled zone (" + se.cardsIDs.length
                                    + " cards)");
                            }
                            break;
                    }

                // SHUFFLE LIBRARY
                } else if (object.getClass().equals(Shuffle.class)) {
                    Shuffle sh = (Shuffle) object;
                    g.log("", g.getPlayerName(sh.requestor) + " shuffles his library",
                            game.Logger.C_SHUFFLE);

                // REVEAL
                } else if (object.getClass().equals(Reveal.class)) {
                    Reveal r = (Reveal) object;
                    if (r.source == Zone.TOP_LIBRARY) {
                        g.log(r.cardID, false, g.getPlayerName(r.requestor)
                                + " reveals top card of his library: "
                                + Game.getCardName(r.cardID), game.Logger.C_REVEAL);
                    }
                    
                // CREATE TOKEN
                } else if (object.getClass().equals(CreateToken.class)) {
                    CreateToken ct = (CreateToken) object;
                    g.log(null, g.getPlayerName(ct.requestor) + " creates "
                            + "a token " + ct.name, Logger.C_MOVE_PLAY);
                    g.createToken(ct);
                    
                // RAND
                } else if (object.getClass().equals(RandomValue.class)) {
                    RandomValue rv = (RandomValue) object;
                    String text = g.getPlayerName(rv.requestor);
                    if (rv.max == RandomValue.COIN) {
                        text += " tosses a coin and gets: ";
                        if (rv.value == 0) {
                            text += "head";
                        } else if (rv.value == 1) {
                            text += "tail";
                        }
                    } else if (rv.max == RandomValue.DIE) {
                        text += " rolls a die and gets: ";
                        text += (rv.value + 1);
                    } else {
                        text += " rolls a " + rv.max + "-sided die and gets: ";
                        text += (rv.value + 1);
                    }
                    g.log(null, true, text, Logger.C_RANDOM);
                    
                // RESTART
                } else if (object.getClass().equals(Restart.class)) {
                    Restart r = (Restart) object;
                    g.log(null, g.getPlayerName(r.requestor) + " starts a game "
                            + "again", Logger.C_RESTART);
                    g.restart(r.requestor, r.deckSize);
                    if (r.IDs != null) {
                        g.cardDiscardEntireHand();
                        for (int i = 0; i < r.IDs.length; i++) {
                            g.cardAddToHand(r.IDs[i]);
                        }
                    }
                // RANDOM CARD
                } else if (object.getClass().equals(RandomCard.class)) {
                    RandomCard rc = (RandomCard) object;
                    g.log(rc.cardID, false, g.getPlayerName(rc.requestor)
                            + " choses a card at random from his hand: "
                            + Game.getCardName(rc.cardID), Logger.C_REVEAL);
                    
                // REQUEST CARD - server requests client to send card's image
                } else if (object.getClass().equals(RequestCard.class)) {
                    RequestCard t = (RequestCard) object;
                    try (Socket socket = new Socket(serverIP, fileTransferPort)) {
                        Utilities.sendFile(new File(Utilities.findPath(t.name)), socket);
                    }

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
                            try (Socket t = new Socket(serverIP, fileTransferPort)) {
                                Utilities.receiveFile(new File(Main.CARDS_DL,
                                        d.getArrayNames(j) + ".jpg"), t);
                            }
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
                    
                // DISCONNECT
                } else if (object.getClass().equals(Disconnect.class)) {
                    Disconnect d = (Disconnect) object;
                    if (d.requestor == -1) {
                        JOptionPane.showMessageDialog(parentFrame,
                                "Server has been closed", Main.TITLE,
                                JOptionPane.ERROR_MESSAGE);
                        s.close();
                        g.dispose();
                        parentFrame.setVisible(true);
                        return;
                    }
                    String text = d.intentional? " has left the game" :
                            " has lost connection";
                    g.log(null, false, g.getPlayerName(d.requestor) + text,
                            game.Logger.C_DISCONNECT);
                    g.kill(d.requestor);

                // CARDS LIST
                } else if (object.getClass().equals(CardsList.class)) {
                    g.setCardsList(((CardsList) object).list);
                }
            } catch (Exception ex) {
                switch (ex.getLocalizedMessage() != null?
                        ex.getLocalizedMessage() : "") {
                    case "Connection reset":
                        Debug.p("Client: Server closed");
                        JOptionPane.showMessageDialog(parentFrame,
                                "Server has been closed unexpectedly", Main.TITLE,
                                JOptionPane.ERROR_MESSAGE);
                        g.dispose();
                        parentFrame.setVisible(true);
                    case "socket closed":
                        //it happens when client has been closed
                        return;
                    default:
                        java.util.logging.Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                        Debug.p("Client: Error while dealing with " + object + ": " + ex,
                                Debug.E);
                        break;
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
                        throw new UnsupportedOperationException("Illegal move");
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
                if (mc.cardID.charAt(1) == 'X') { //token
                    g.log(null, g.getPlayerName(mc.requestor) + " destroyes "
                            + owner + " token", Logger.C_MOVE_DESTROY);
                    g.cardRemoveFromTable(mc.cardID);
                    break;
                }
                switch (mc.destination) {
                    case HAND:
                        g.changeHandSize(mc.requestor, 1);
                        g.log(mc.cardID, false, g.getPlayerName(mc.requestor)
                                    + " returns " + Game.getCardName(mc.cardID)
                                    + " from table to " + owner + " hand",
                                game.Logger.C_MOVE_TO_HAND);
                        if (playerName.equals(g.getPlayerName(mc.cardID.charAt(0) - 'A'))) {
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
                        throw new UnsupportedOperationException("Illegal move");
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
                        if (playerName.equals(g.getPlayerName(mc.cardID.charAt(0) - 'A'))) {
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
                        throw new UnsupportedOperationException("Illegal move");
                    case TOP_LIBRARY:
                        g.changeLibrarySize(mc.requestor, 1);
                        g.log(mc.cardID, false, g.getPlayerName(mc.requestor)
                                + " puts " + Game.getCardName(mc.cardID)
                                + " from " + owner + " graveyard on top of "
                                + owner + " library",
                                game.Logger.C_MOVE_TO_LIBRARY);
                        break;
                }
                CardViewer.removeCardFromCurrentlyOpenCardViewer(mc.cardID);
                break;
            case EXILED:
                switch (mc.destination) {
                    case HAND:
                        g.changeHandSize(mc.requestor, 1);
                        g.log(mc.cardID, false, g.getPlayerName(mc.requestor)
                                + " takes " + Game.getCardName(mc.cardID)
                                + " from " + owner + " exiled zone to his hand",
                                game.Logger.C_MOVE_TO_HAND);
                        if (playerName.equals(g.getPlayerName(mc.cardID.charAt(0) - 'A'))) {
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
                        throw new UnsupportedOperationException("Illegal move");
                    case TOP_LIBRARY:
                        g.changeLibrarySize(mc.requestor, 1);
                        g.log(mc.cardID, false, g.getPlayerName(mc.requestor)
                                + " puts " + Game.getCardName(mc.cardID)
                                + " from " + owner + " graveyard on top of "
                                + owner + " library",
                                game.Logger.C_MOVE_TO_LIBRARY);
                        break;
                }
                CardViewer.removeCardFromCurrentlyOpenCardViewer(mc.cardID);
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
                        if (playerName.equals(g.getPlayerName(mc.cardID.charAt(0) - 'A'))) {
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
                                    + " reveals " + Game.getCardName(mc.cardID)
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
                if (playerName.equals(g.getPlayerName(mc.cardID.charAt(0) - 'A'))
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
            Debug.p("Client: Error while sending " + object + " to server: "
                    + ex, Debug.E);
        }
    }
    
    /**
     * Sends <code>Disconnect</code>, closes sockets 
     * and restores main menu frame
     */
    void closeClient() {
        this.interrupt();
        try {
            send(new Disconnect(true));
            s.close();
        } catch (IOException ex1) {}
        parentFrame.setVisible(true);
    }

}