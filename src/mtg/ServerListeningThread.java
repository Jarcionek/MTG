package mtg;

import flags.*;
import java.io.File;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
        Object object = null;
        while (true) {
            object = null;
            try {
                object = ois.readObject();

                if (object.getClass().equals(RequestCard.class)) {
                    RequestCard t = ((RequestCard) object);
                    Socket s = fileSocket.accept();
                    Utilities.sendFile(new File(Utilities.findPath(Main.CARDS, t.name)), s);
                    s.close();
                }
            } catch (Exception ex) {
                if (ex.getLocalizedMessage() != null
                        && ex.getLocalizedMessage().equals("Connection reset")) {
                    break;
                }
            }
        }
    }

}
