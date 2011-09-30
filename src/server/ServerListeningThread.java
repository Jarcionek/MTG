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

                // REQUEST CARD
                } else if (object.getClass().equals(RequestCard.class)) {
                    RequestCard t = ((RequestCard) object);
                    Socket s = fileSocket.accept();
                    Utilities.sendFile(new File(Utilities.findPath(t.name)), s);
                    s.close();
                // READY
                } else if (object.getClass().equals(Ready.class)) {
                    Server.ready[id] = true;
                }
            } catch (Exception ex) {
                if (ex.getLocalizedMessage() != null
                        && ex.getLocalizedMessage().equals("Connection reset")) {
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
                            Server.sendToAll(
                                    new MoveCard(Zone.HAND,
                                    Zone.TABLE, id, mc.cardID));
                        }
                        break;
                    case GRAVEYARD:
//                        Server.game.handDestroy(id, mc.cardID);
                        break;
                    case EXILED:
//                        Server.game.handExile(id, mc.cardID);
                        break;
                    case LIBRARY:

                        break;
                    case TOP_LIBRARY:

                        break;
                }
                break;
            case TABLE:
                switch (mc.destination) {
                    case HAND:
//                        Server.game.tableTake(mc.cardID);
                        break;
                    case GRAVEYARD:
//                        Server.game.tableDestroy(mc.cardID);
                        break;
                    case EXILED:
//                        Server.game.tableExile(mc.cardID);
                        break;
                    case LIBRARY:

                        break;
                    case TOP_LIBRARY:

                        break;
                }
                break;
            case GRAVEYARD:
                switch (mc.destination) {
                    case HAND:

                        break;
                    case TABLE:

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

                        break;
                    case TABLE:

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

                        break;
                    case TABLE:

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
                        card = Server.game.libraryDraw(id);
                        Server.sendToAllInvisible(new MoveCard(Zone.TOP_LIBRARY,
                                Zone.HAND, id, card.ID));
                        break;
                    case TABLE:
                        card = Server.game.libraryPlayTop(id);
                        Server.sendToAll(new MoveCard(
                                Zone.TOP_LIBRARY, Zone.TABLE, id, card.ID));
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

}
