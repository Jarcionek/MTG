package server;

import server.flags.*;
import java.io.File;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import mtg.Debug;
import mtg.Utilities;

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
        switch (mc.source) {
            case MoveCard.HAND:
                switch (mc.destination) {
                    case MoveCard.TABLE:
                        if (Server.game.handPlay(id, mc.cardID)) {
                            Server.sendToAll(
                                    new MoveCard(MoveCard.HAND,
                                    MoveCard.TABLE, id, mc.cardID));
                        }
                        break;
                    case MoveCard.GRAVEYARD:
//                        Server.game.handDestroy(id, mc.cardID);
                        break;
                    case MoveCard.EXILED:
//                        Server.game.handExile(id, mc.cardID);
                        break;
                    case MoveCard.LIBRARY:

                        break;
                    case MoveCard.TOP_LIBRARY:

                        break;
                }
                break;
            case MoveCard.TABLE:
                switch (mc.destination) {
                    case MoveCard.HAND:
//                        Server.game.tableTake(mc.cardID);
                        break;
                    case MoveCard.GRAVEYARD:
//                        Server.game.tableDestroy(mc.cardID);
                        break;
                    case MoveCard.EXILED:
//                        Server.game.tableExile(mc.cardID);
                        break;
                    case MoveCard.LIBRARY:

                        break;
                    case MoveCard.TOP_LIBRARY:

                        break;
                }
                break;
            case MoveCard.GRAVEYARD:
                switch (mc.destination) {
                    case MoveCard.HAND:

                        break;
                    case MoveCard.TABLE:

                        break;
                    case MoveCard.EXILED:

                        break;
                    case MoveCard.LIBRARY:

                        break;
                    case MoveCard.TOP_LIBRARY:

                        break;
                }
                break;
            case MoveCard.EXILED:
                switch (mc.destination) {
                    case MoveCard.HAND:

                        break;
                    case MoveCard.TABLE:

                        break;
                    case MoveCard.GRAVEYARD:

                        break;
                    case MoveCard.LIBRARY:

                        break;
                    case MoveCard.TOP_LIBRARY:

                        break;
                }
                break;
            case MoveCard.LIBRARY:
                switch (mc.destination) {
                    case MoveCard.HAND:

                        break;
                    case MoveCard.TABLE:

                        break;
                    case MoveCard.GRAVEYARD:

                        break;
                    case MoveCard.EXILED:

                        break;
                    case MoveCard.TOP_LIBRARY:

                        break;
                }
                break;
            case MoveCard.TOP_LIBRARY:
                switch (mc.destination) {
                    case MoveCard.HAND:
                        Card card = Server.game.libraryDraw(id);
                        Server.sendToAllInvisible(new MoveCard(MoveCard.TOP_LIBRARY,
                                MoveCard.HAND, id, card.ID));
                        break;
                    case MoveCard.TABLE:

                        break;
                    case MoveCard.GRAVEYARD:

                        break;
                    case MoveCard.EXILED:

                        break;
                    case MoveCard.LIBRARY:

                        break;
                }
                break;
        }
    }

}
