package mtg;

import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * @author Jaroslaw Pawlak
 */
public class JoinGameFrame {
    private JoinGameFrame() {}
    
    public static void show(JFrame parent) {
        Deck deck = Settings.getDeck();
        if (deck == null) {
            JOptionPane.showMessageDialog(parent,
                    "Could not load chosen deck",
                    Main.TITLE_SHORT, JOptionPane.ERROR_MESSAGE);
        } else {
            String ip = "";
            String msg = "Host:";
            String portStr;
            int port = -1;

            while (true) {
                ip = (String) JOptionPane.showInputDialog(parent,
                        msg, Main.TITLE_SHORT, JOptionPane.PLAIN_MESSAGE, null,
                        null, Settings.getLastIP());
                if (ip == null) {
                    return; //user cancel
                } else if (ip.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")
                        || ip.matches("\\d+\\.\\d+\\.\\d+\\.\\d+:\\d{2,}")
                        || ip.matches("[\\w+\\.]+\\w+")
                        || ip.matches("[\\w+\\.]+\\w+:\\d{2,}")
                        || ip.equals("localhost")
                        || ip.matches("localhost:\\d{2,}")) {
                    break;
                } else {
                    msg = "Invalid IP! Enter a valid IP address";
                }
            }

            if (ip.contains(":")) {
                portStr = ip.split(":")[1];
                ip = ip.split(":")[0];
                if (portStr.matches("\\d{2,5}")) {
                    port = Integer.parseInt(portStr);
                    if (port > ServerFrame.PORT_MAX) {
                        port = -1;
                    }
                }
            }

            if (port == -1) {
                msg = "Port:";
                while (true) {
                    portStr = (String) JOptionPane.showInputDialog(
                            parent, msg, Main.TITLE_SHORT,
                            JOptionPane.PLAIN_MESSAGE, null, null,
                            "56789");
                    if (portStr == null) {
                        return; //user cancel
                    } else if (!portStr.matches("\\d{2,5}")
                            || (port = Integer.parseInt(portStr))
                            > ServerFrame.PORT_MAX) {
                        msg = "Invalid port! Enter a valid port:";
                    } else {
                        port = Integer.parseInt(portStr);
                        break;
                    }
                }
            }

            try {
                Settings.setLastIP(ip + ":" + port);
                Settings.save();
                new game.Client(parent, Settings.getName(), ip, port, deck);
                parent.setVisible(false);
            } catch (Exception ex) {
                Debug.p("Exception while joining the server: " + ex);
                if (ex.getClass().equals(InvalidDeckException.class)) {
                    JOptionPane.showMessageDialog(parent,
                            "Your deck has been rejected by the server: "
                            + ex.getLocalizedMessage(), Main.TITLE_SHORT,
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    switch (ex.getLocalizedMessage()) {
                        case "Connection refused: connect":
                            JOptionPane.showMessageDialog(parent,
                                    "Connection refused", Main.TITLE_SHORT,
                                    JOptionPane.WARNING_MESSAGE);
                            break;
                        case "Connection timed out: connect":
                            JOptionPane.showMessageDialog(parent,
                                    "Connection timed out", Main.TITLE_SHORT,
                                    JOptionPane.WARNING_MESSAGE);
                            break;
                        default:
                            Debug.p(ex, Debug.CE);
                            break;
                    }
                }
            }
        }
    }
}
