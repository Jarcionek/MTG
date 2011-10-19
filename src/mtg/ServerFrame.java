package mtg;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import server.Server;

/**
 * @author Jaroslaw Pawlak
 */
public class ServerFrame extends JFrame {
    private static final int MAX_PLAYERS = 8;

    private static final int PORT_MIN = 49152;
    public static final int PORT_MAX = 65535 - MAX_PLAYERS;
    
    private static JLabel ipLabel;
    private static JLabel ipValue;
    private static JLabel portLabel;
    private static JTextField portField;
    private static JLabel playersLabel;
    private static JTextField playersField;
    private static JTextArea messagesField;
    private static JCheckBox joinGameBox;

    private static boolean allOK = false;

    private ServerFrame() {}
    
    public static void show(JFrame parent) {
        createComponents();
        JPanel conent = createGUI();

        int choice = 0;
        
        while (true) {
            choice = JOptionPane.showOptionDialog(parent, conent, Main.TITLE_MED,
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                    new String[] {"Create", "Cancel"}, 0);
            if (choice != 0) {
                allOK = false;
                break;
            }
            if (!allOK) {
                check();
            } else {
                check();
                if (allOK) {
                    int port = Integer.parseInt(portField.getText());
                    int players = Integer.parseInt(playersField.getText());
                    Settings.setLastCreateInfo(port, players, joinGameBox.isSelected());
                    Settings.save();
                    try {
                        Server.start(port, players);
                        StringSelection ipStr = new StringSelection(
                                ipValue.getText() + ":" + portField.getText());
                        Toolkit.getDefaultToolkit().getSystemClipboard()
                                .setContents(ipStr, null);
                        
                        if (joinGameBox.isSelected()) {
                            Deck deck = Settings.getDeck();
                            if (deck == null) {
                                JOptionPane.showMessageDialog(parent,
                                        "Could not load chosen deck",
                                        Main.TITLE_SHORT, JOptionPane.ERROR_MESSAGE);
                            } else {
                                try {
                                    new game.Client(parent, Settings.getName(), "localhost",
                                            Integer.parseInt(portField.getText()), deck);
                                    parent.setVisible(false);
                                } catch (InvalidDeckException ex) {
                                    Debug.p("Deck rejected by the server: " + ex);
                                    JOptionPane.showMessageDialog(parent,
                                            "Your deck has been rejected by the server: "
                                            + ex.getLocalizedMessage(), Main.TITLE_SHORT,
                                            JOptionPane.WARNING_MESSAGE);
                                    break;
                                } catch (Exception ex) {
                                    Debug.p("Could not join a host: " + ex, Debug.CE);
                                }
                            }
                        }
                        break;
                    } catch (IOException ex1) {
//                        Logger.getLogger(ServerFrame.class.getName()).log(Level.SEVERE, null, ex1);
                        messagesField.setText("Could not create a server:\n" + ex1);
                        allOK = false;
                    }
                }
            }
        }
        allOK = false;
    }

    private static void createComponents() {
        ipLabel = new JLabel("IP:");
        ipLabel.setHorizontalAlignment(JLabel.CENTER);
        ipValue = new JLabel(Utilities.getExternalIP());
        ipValue.setHorizontalAlignment(JLabel.CENTER);

        portLabel = new JLabel("Port:");
        portLabel.setHorizontalAlignment(JLabel.CENTER);
        portField = new JTextField("" + Settings.getLastPort());
        portField.setHorizontalAlignment(JTextField.CENTER);

        playersLabel = new JLabel("Players:");
        playersLabel.setHorizontalAlignment(JLabel.CENTER);
        playersField = new JTextField("" + Settings.getLastPlayers());
        playersField.setHorizontalAlignment(JTextField.CENTER);

        joinGameBox = new JCheckBox("Join this game", Settings.getLastJoin());
        
        messagesField = new JTextArea();
        messagesField.setBackground(ipLabel.getBackground());
        messagesField.setEditable(false);
        messagesField.setFont(ipLabel.getFont());
    }

    private static JPanel createGUI() {
        JPanel contentPane = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        int outside = 3;
        int between = 2;

        c.gridy = 0;
        c.insets = new Insets(outside, outside, between, between);
        contentPane.add(ipLabel, c);
        c.insets = new Insets(outside, 0, between, outside);
        contentPane.add(ipValue, c);

        c.gridy += 1;
        c.insets = new Insets(0, outside, between, between);
        contentPane.add(portLabel, c);
        c.insets = new Insets(0, 0, between, outside);
        contentPane.add(portField, c);

        c.gridy += 1;
        c.insets = new Insets(0, outside, between, between);
        contentPane.add(playersLabel, c);
        c.insets = new Insets(0, 0, between, outside);
        contentPane.add(playersField, c);

        c.gridwidth = 2;
        c.gridy += 1;
        c.insets = new Insets(0, outside, between, outside);
        contentPane.add(joinGameBox, c);
        
        c.gridy += 1;
        c.gridwidth = 2;
        c.insets = new Insets(0, outside, outside, outside);
        contentPane.add(messagesField, c);

        return contentPane;
    }

    private static void check() {
        allOK = true;
        messagesField.setText("");
        
        String ip = Utilities.getExternalIP();
        if (ip.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
            ipValue.setText(ip);
        } else {
            messagesField.append(ip);
            messagesField.append(".\nCheck your firewall and Internet connection.\n");
        }

        int port = -1;
        try {
            port = Integer.parseInt(portField.getText());
        } catch (NumberFormatException ex) {
            messagesField.append("Port must be an integer\n");
            allOK = false;
        }
        if (port != -1) {
            if (port < PORT_MIN) {
                messagesField.append("Port must be greater than " + (PORT_MIN - 1) + "\n");
                allOK = false;
            } else if (port > PORT_MAX) {
                messagesField.append("Port must be smaller than " + (PORT_MAX + 1) + "\n");
                allOK = false;
            }
        }

        int players = -1;
        try {
            players = Integer.parseInt(playersField.getText());
        } catch (NumberFormatException ex) {
            messagesField.append("Number of players must be an integer\n");
            allOK = false;
        }
        if (players != -1) {
            if (players < 1) {
                messagesField.append("There must be at least one player\n");
                allOK = false;
            } else if (players > MAX_PLAYERS) {
                messagesField.append("There cannot be more than " + MAX_PLAYERS
                        + " players\n");
                allOK = false;
            }
        }

        if (allOK && !ipValue.getText().equals(Utilities.getInternalIP())) {
            messagesField.append("Your internal IP ("
                    + Utilities.getInternalIP() + ") "
                    + "and external IP differ.\n");
            messagesField.append("You may need port forwarding for ports <"
                    + port + ";" + (port + players) + ">.\n");
        }

        if (allOK) {
            String IP = ipValue.getText();
            if (!IP.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
                IP = Utilities.getInternalIP();
            }
            messagesField.append("Your are about to create a server at\n"
                    + IP + ":" + port + ".\n");
            messagesField.append("Press \"create\" to confirm.\n");
        }

        messagesField.setText(messagesField.getText()
                .substring(0, messagesField.getText().lastIndexOf("\n")));
    }
}
