package server;

import server.flags.*;
import java.io.File;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import mtg.Debug;
import mtg.Utilities;
import mtg.Zone;

/**
 * @author Jaroslaw Pawlak
 */
public class ServerListeningThread extends Thread {

    private int id;
    private ObjectInputStream ois;
    private ServerSocket fileSocket;

    private ServerListeningThread() {}

    public ServerListeningThread(int id,
            ObjectInputStream ois, ServerSocket fileSocket) {
        this.id = id;
        this.ois = ois;
        this.fileSocket = fileSocket;
    }

    @Override
    public void run() {
        Debug.p("Server Listening Thread ID = " + id + " started");
        Object object = null;
        while (true) {
            object = null;
            try {
                object = ois.readObject();

                if (!Server.areAllPlayersConnected()) {
                     //REQUEST CARD
                    if (object.getClass().equals(RequestCard.class)) {
                        RequestCard t = ((RequestCard) object);
                        Socket s = fileSocket.accept();
                        Utilities.sendFile(new File(Utilities.findPath(t.name)), s);
                        s.close();
                    // READY
                    } else if (object.getClass().equals(Ready.class)) {
                        Server.ready[id] = true;
                    }
                    continue;
                }

                // DRAG
                if (object.getClass().equals(DragCard.class)) {
                    DragCard dc = (DragCard) object;
                    dc.requestor = id;
                    Server.sendToAll(dc);

                // TAP CARD
                } else if (object.getClass().equals(TapCard.class)) {
                    TapCard tc = (TapCard) object;
                    tc.requestor = id;
                    Server.sendToAll(tc);

                // MOVE CARD
                } else if (object.getClass().equals(MoveCard.class)) {
                    handleMoveCard((MoveCard) object);

                // CHANGE HP OR POISON COUNTERS
                } else if (object.getClass().equals(Player.class)) {
                    Player p = (Player) object;
                    p.requestor = id;
                    if (p.poisonOrHealth == Player.HEALTH) {
                        Server.game.playerSetHealth(p.target, p.newValue);
                        Server.sendToAll(p);
                    } else if (p.poisonOrHealth == Player.POISON) {
                        Server.game.playerSetPoison(p.target, p.newValue);
                        Server.sendToAll(p);
                    }

                // SEARCH
                } else if (object.getClass().equals(Search.class)) {
                    Search s = (Search) object;
                    s.requestor = id;
                    switch (s.zone) {
                        case LIBRARY:
                            if (s.amount < -1 || s.amount == 0) {
                                continue; // ignore client's request
                            }
                            s.cardsIDs = Server.game.librarySearch(id, s.amount);
                            if (s.amount >= Server.game.libraryGetSize(id)) {
                                s.amount = -1;
                            }
                            break;
                        case GRAVEYARD:
                            s.cardsIDs = Server.game.graveyardView(s.zoneOwner);
                            break;
                        case EXILED:
                            s.cardsIDs = Server.game.exiledView(s.zoneOwner);
                            break;
                    }
                    Server.sendToAllInvisible(s);

                // SHUFFLE
                } else if (object.getClass().equals(Shuffle.class)) {
                    Shuffle s = (Shuffle) object;
                    s.owner = id;
                    Server.game.libraryShuffle(id);
                    Server.sendToAll(s);

                // REVEAL
                } else if (object.getClass().equals(Reveal.class)) {
                    Card c = Server.game.libraryGetTop(id);
                    if (c != null) {
                        Reveal r = (Reveal) object;
                        r.cardID = c.ID;
                        r.requstor = id;
                        Server.sendToAll(r);
                    }
                }
            } catch (Exception ex) {
//                Logger.getLogger(ServerListeningThread.class.getName())
//                        .log(Level.SEVERE, null, ex);
                Debug.p("ServerListeningThread (id=" + id + ") error while " +
                            "dealing with " + object + ": " + ex);
                if ("Connection reset".equals(ex.getLocalizedMessage())) {
                    Server.disconnect(id);
                    break;
                }
            }
        }
    }

    private void handleMoveCard(MoveCard mc) {
        Card card = null;
        switch (mc.source) {
            case HAND:
                switch (mc.destination) {
                    case TABLE:
                        if (Server.game.handPlay(id, mc.cardID)) {
                            mc.requestor = id;
                            Server.sendToAll(mc);
                        }
                        break;
                    case GRAVEYARD:
                        if (Server.game.handDestroy(id, mc.cardID)) {
                            mc.requestor = id;
                            Server.sendToAll(mc);
                        }
                        break;
                    case EXILED:
                        if (Server.game.handExile(id, mc.cardID)) {
                            mc.requestor = id;
                            Server.sendToAll(mc);
                        }
                        break;
                    case LIBRARY:
                        throw new UnsupportedOperationException();
                    case TOP_LIBRARY:
                        if (Server.game.handToLibrary(mc.requestor = id, mc.cardID)) {
                            if (mc.reveal) {
                                Server.sendToAll(mc);
                            } else {
                                Server.sendToAllInvisible(mc);
                            }
                        }
                        break;
                }
                break;
            case TABLE:
                switch (mc.destination) {
                    case HAND:
                        if (Server.game.tableTake(mc.cardID)) {
                            mc.requestor = id;
                            Server.sendToAll(mc);
                        }
                        break;
                    case GRAVEYARD:
                        if (Server.game.tableDestroy(mc.cardID)) {
                            mc.requestor = id;
                            Server.sendToAll(mc);
                        }
                        break;
                    case EXILED:
                        if (Server.game.tableExile(mc.cardID)) {
                            mc.requestor = id;
                            Server.sendToAll(mc);
                        }
                        break;
                    case LIBRARY:
                        throw new UnsupportedOperationException();
                    case TOP_LIBRARY:
                        if (Server.game.tablePutOnTopOfLibrary(mc.cardID)) {
                            mc.requestor = id;
                            Server.sendToAll(mc);
                        }
                        break;
                }
                break;
            case GRAVEYARD:
                switch (mc.destination) {
                    case HAND:
                        if (Server.game.graveyardToHand(mc.cardID)) {
                            mc.requestor = id;
                            Server.sendToAll(mc);
                        }
                        break;
                    case TABLE:
                        if (Server.game.graveyardPlay(mc.cardID)) {
                            mc.requestor = id;
                            Server.sendToAll(mc);
                        }
                        break;
                    case EXILED:
                        if (Server.game.graveyardExile(mc.cardID)) {
                            mc.requestor = id;
                            Server.sendToAll(mc);
                        }
                        break;
                    case LIBRARY:
                        throw new UnsupportedOperationException();
                    case TOP_LIBRARY:
                        if (Server.game.graveyardToLibrary(mc.cardID)) {
                            mc.requestor = id;
                            Server.sendToAll(mc);
                        }
                        break;
                }
                break;
            case EXILED:
                switch (mc.destination) {
                    case HAND:
                        if (Server.game.exiledToHand(mc.cardID)) {
                            mc.requestor = id;
                            Server.sendToAll(mc);
                        }
                        break;
                    case TABLE:
                        if (Server.game.exiledPlay(mc.cardID)) {
                            mc.requestor = id;
                            Server.sendToAll(mc);
                        }
                        break;
                    case GRAVEYARD:
                        if (Server.game.exiledToGraveyard(mc.cardID)) {
                            mc.requestor = id;
                            Server.sendToAll(mc);
                        }
                        break;
                    case LIBRARY:
                        throw new UnsupportedOperationException();
                    case TOP_LIBRARY:
                        if (Server.game.exiledToLibrary(mc.cardID)) {
                            mc.requestor = id;
                            Server.sendToAll(mc);
                        }
                        break;
                }
                break;
            case LIBRARY:
                switch (mc.destination) {
                    case HAND:
                        if (Server.game.libraryToHand(mc.requestor = id, mc.cardID)) {
                            if (mc.reveal) {
                                Server.sendToAll(mc);
                            } else {
                                Server.sendToAllInvisible(mc);
                            }
                        }
                        break;
                    case TABLE:
                        if (Server.game.libraryPlay(mc.requestor = id, mc.cardID)) {
                            Server.sendToAll(mc);
                        }
                        break;
                    case GRAVEYARD:
                        if (Server.game.libraryDestroy(mc.requestor = id, mc.cardID)) {
                            Server.sendToAll(mc);
                        }
                        break;
                    case EXILED:
                        if (Server.game.libraryExile(mc.requestor = id, mc.cardID)) {
                            Server.sendToAll(mc);
                        }
                        break;
                    case TOP_LIBRARY:
                        if (Server.game.libraryToTop(mc.requestor = id, mc.cardID)) {
                            if (mc.reveal) {
                                Server.sendToAll(mc);
                            } else {
                                Server.sendToAllInvisible(mc);
                            }
                        }
                        break;
                }
                break;
            case TOP_LIBRARY:
                switch (mc.destination) {
                    case HAND:
                        if ((card = Server.game.libraryDraw(id)) != null) {
                            Server.sendToAllInvisible(new MoveCard(
                                    Zone.TOP_LIBRARY, Zone.HAND, id, card.ID));
                        }
                        break;
                    case TABLE:
                        if ((card = Server.game.libraryPlayTop(id)) != null){
                            Server.sendToAll(new MoveCard(
                                    Zone.TOP_LIBRARY, Zone.TABLE, id, card.ID));
                        }
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

}
