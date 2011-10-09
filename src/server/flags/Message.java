package server.flags;

/**
 * @author Jaroslaw Pawlak
 * 
 * Message sent by player to other players.
 */
public class Message extends Action {
    public String msg;

    public Message(String msg) {
        super(-1);
        this.msg = msg;
    }

    @Override
    public String toString() {
        return super.toString() + ", msg = \"" + msg + "\")";
    }
    
}
