package mtg;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * @author Jaroslaw Pawlak
 */
public class ServerFrame extends JFrame {

    private static int PORT_MIN = 49152;
    private static int PORT_MAX = 65535;

    private JFrame parentFrame;
    
    private JLabel ipLabel;
    private JLabel ipValue;
    private JLabel portLabel;
    private JTextField portField;
    private JLabel playersLabel;
    private JTextField playersField;
    private JTextArea messagesField;
    private JButton createButton;
    private JButton cancelButton;

    boolean allOK = false;

    private ServerFrame() {}

    public ServerFrame(JFrame parentFrame) {
        super(Main.TITLE);
        this.parentFrame = parentFrame;
        this.parentFrame.setVisible(false);

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ServerFrame.this.parentFrame.setVisible(true);
            }
        });

        createComponents();
        createGUI();
//        check();
        center();

        this.setResizable(false);
        this.setVisible(true);
    }

    private void createComponents() {
        ipLabel = new JLabel("IP:");
        ipLabel.setHorizontalAlignment(JLabel.CENTER);
        ipValue = new JLabel(getExternalIP());
        ipValue.setHorizontalAlignment(JLabel.CENTER);

        portLabel = new JLabel("Port:");
        portLabel.setHorizontalAlignment(JLabel.CENTER);
        portField = new JTextField("56789");
        portField.setHorizontalAlignment(JTextField.CENTER);

        playersLabel = new JLabel("Players:");
        playersLabel.setHorizontalAlignment(JLabel.CENTER);
        playersField = new JTextField("2");
        playersField.setHorizontalAlignment(JTextField.CENTER);
        
        messagesField = new JTextArea();
        messagesField.setBackground(ipLabel.getBackground());
        messagesField.setEditable(false);
        messagesField.setFont(ipLabel.getFont());

        createButton = new JButton("Create");
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!ServerFrame.this.allOK) {
                    ServerFrame.this.check();
                    return;
                }
                ServerFrame.this.check();
                if (ServerFrame.this.allOK) {
                    try {
                        ServerFrame.this.setVisible(false);
                        Server.start(
                                Integer.parseInt(portField.getText()),
                                Integer.parseInt(playersField.getText()));
                        ServerFrame.this.dispose();
                        //TODO start client
                    } catch (IOException ex) {
                        ServerFrame.this.setVisible(true);
                        ServerFrame.this.messagesField.setText("Could not create a server:\n" + ex);
                        ServerFrame.this.pack();
                        ServerFrame.this.allOK = false;
                    }
                }
            }
        });

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ServerFrame.this.dispose();
                parentFrame.setVisible(true);
            }
        });
    }

    private void createGUI() {
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

        c.gridy += 1;
        c.gridwidth = 2;
        c.insets = new Insets(0, outside, outside, between);
        contentPane.add(messagesField, c);

        c.gridwidth = 1;
        c.weightx = 0.5;
        c.gridy += 1;
        c.insets = new Insets(0, outside, outside, between);
        contentPane.add(createButton, c);
        c.insets = new Insets(0, 0, outside, outside);
        contentPane.add(cancelButton, c);

        this.setContentPane(contentPane);
    }

    private void center() {
        this.pack();
        Dimension d = this.getSize();
        Dimension pd = parentFrame.getSize();
        Point p = parentFrame.getLocation();
        this.setLocation(
                (pd.width - d.width) / 2 + p.x,
                (pd.height - d.height) / 2 + p.y);
    }

    private void check() {
        allOK = true;
        messagesField.setText("");
        
        String ip = getExternalIP();
        if (ip.matches("\\d+.\\d+.\\d+.\\d+")) {
            ipValue.setText(ip);
        } else {
            messagesField.append(ip);
            messagesField.append("Check your firewall and Internet connection\n");
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
                messagesField.append("Port must be greater than " + PORT_MIN + "\n");
                allOK = false;
            } else if (port > PORT_MAX) {
                messagesField.append("Port must be smaller than " + PORT_MAX + "\n");
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
            } else if (players > 8) {
                messagesField.append("There cannot be more than 8 players\n");
                allOK = false;
            }
        }

        if (allOK && !ipLabel.getText().equals(getInternalIP())) {
            messagesField.append("Your internal IP (" + getInternalIP() + ") "
                    + "and external IP differ.\n");
            messagesField.append("You may need port forwarding for ports <"
                    + port + ";" + (port + players) + ">.\n");
        }

        if (allOK) {
            messagesField.append("Your are about to create a server at "
                    + ipValue.getText() + ":" + port + ".\n");
            messagesField.append("Press \"create\" to confirm.\n");
        }

        messagesField.setText(messagesField.getText()
                .substring(0, messagesField.getText().lastIndexOf("\n")));
        pack();
    }

    @Override
    public void pack() {
        Dimension before = this.getSize();
        super.pack();
        Dimension after = this.getSize();
        Point p = this.getLocation();
        this.setLocation(p.x - (after.width - before.width) / 2,
                p.y - (after.height - before.height) / 2);
    }



    private static String getInternalIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            return null;
        }
    }

    private static String getExternalIP() {
        return Utilities.getExternalIP();
    }
}
