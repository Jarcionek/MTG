package mtg;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;
import javax.swing.filechooser.FileFilter;

/**
 * @author Jaroslaw Pawlak
 */
public class Settings {
    private static final File FILE = new File(Main.DIRECTORY, "settings.txt");

    private static final String NAME = "name";
    private static final String DECK = "deck";
    private static final String LAST_JOINED_IP = "last IP";
    private static final String LAST_CREATED_PORT = "last port";
    private static final String LAST_CREATED_PLAYERS = "last players";
    private static final String LAST_CREATED_JOINED = "last joined";

    private static HashMap<String, Object> settings;
    
    private static File tempDeck;

    public static void load() {
        settings = new HashMap<>();
        settings.put(NAME, System.getProperty("user.name"));
        settings.put(DECK, chooseDeckAtRandom());
        settings.put(LAST_JOINED_IP, "localhost:56789");
        settings.put(LAST_CREATED_PORT, 56789);
        settings.put(LAST_CREATED_PLAYERS, 2);
        settings.put(LAST_CREATED_JOINED, true);
        try (Scanner s = new Scanner(FILE, "Unicode")) {
            while (s.hasNextLine()) {
                String[] l = s.nextLine().split(":", 2);
                if (l.length == 2) {
                    switch(l[0]) {
                        case NAME:
                            settings.put(l[0], Utilities.checkName(l[1]));
                            break;
                        case DECK:
                            settings.put(l[0], new File(l[1]));
                            break;
                        case LAST_JOINED_IP:
                            settings.put(l[0], l[1]);
                            break;
                        case LAST_CREATED_PORT:
                            if (l[1].matches("\\d{2,5}")) {
                                settings.put(l[0], Integer.parseInt(l[1]));
                            }
                            break;
                        case LAST_CREATED_PLAYERS:
                            if (l[1].matches("\\d")) {
                                settings.put(l[0], Integer.parseInt(l[1]));
                            }
                            break;
                        case LAST_CREATED_JOINED:
                            if (l[1].equalsIgnoreCase("true")
                                    || l[1].equalsIgnoreCase("false")) {
                                settings.put(l[0], Boolean.parseBoolean(l[1]));
                            }
                            break;
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Debug.p("Settings file not found. Default settings loaded.");
        }
    }
    
    public static void save() {
        try (Writer bf = new OutputStreamWriter(
                new FileOutputStream(FILE), "Unicode")) {
            if (!FILE.exists()) {
                FILE.createNewFile();
            }
            for (String s : settings.keySet()) {
                bf.write(s + ":" + settings.get(s)
                        + System.getProperty("line.separator"));
            }
        } catch (IOException ex) {
            Debug.p("Could not save settings: " + ex, Debug.E);
        }
    }

    public static void openSettingsPanel(final JFrame parentFrame) {
        JPanel message = new JPanel(new GridBagLayout());
        GridBagConstraints f = new GridBagConstraints();
        GridBagConstraints s = new GridBagConstraints();
        f.gridx = 0;
        s.gridx = 1;
        f.insets = s.insets = new Insets(2, 2, 2, 2);
        f.ipady = s.ipady = 10;
        f.fill = s.fill = GridBagConstraints.BOTH;
        
        message.add(new JLabel("Player name:"), f);
        final JTextField nametf = new JTextField((String) settings.get(NAME)) {
            @Override
            public void paste() {} // do not let paste anything
        };
        nametf.setHorizontalAlignment(JTextField.CENTER);
        nametf.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (("" + e.getKeyChar()).matches("\\W")) {
                    e.consume();
                    Toolkit.getDefaultToolkit().beep();
                    nametf.setToolTipText("only characters, digits and _");
                    ToolTipManager.sharedInstance().mouseMoved(
                            new MouseEvent(nametf, 0, 10, 0,
                            10, 10, // X-Y of the mouse for the tool tip
                            0, false));
                } else if (nametf.getText().length() == 15) {
                    e.consume();
                    Toolkit.getDefaultToolkit().beep();
                    nametf.setToolTipText("max 15 characters");
                    ToolTipManager.sharedInstance().mouseMoved(
                            new MouseEvent(nametf, 0, 10, 0,
                            10, 10, // X-Y of the mouse for the tool tip
                            0, false));
                }
            }
        });
        message.add(nametf, s);
        
        message.add(new JLabel("Active deck:"), f);
        tempDeck = (File) settings.get(DECK);
        final JButton deckButton = new JButton(
                Utilities.getName((File) settings.get(DECK)));
        deckButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser(Main.DECKS);
                jfc.setMultiSelectionEnabled(false);
                jfc.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.isDirectory()
                                || Utilities.getExtension(f).equals("txt");
                    }
                    @Override
                    public String getDescription() {
                        return "*.txt";
                    }
                });
                if (jfc.showOpenDialog(parentFrame)
                        == JFileChooser.APPROVE_OPTION) {
                    tempDeck = jfc.getSelectedFile();
                    deckButton.setText(Utilities.getName(tempDeck));
                }
            }
        });
        message.add(deckButton, s);
        
        if(JOptionPane.showOptionDialog(parentFrame, message, Main.TITLE,
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                new String[] {"Apply", "Cancel"}, 0) == 0) {
            settings.put(NAME, nametf.getText());
            settings.put(DECK, tempDeck);
            save();
        }
    }

    private static File chooseDeckAtRandom() {
        boolean containsDeck = false;
        File[] files = Main.DECKS.listFiles();
        for (File e : files) {
            if (e.isFile() && Utilities.getExtension(e).equals("txt")) {
                containsDeck = true;
                break;
            }
        }
        if (containsDeck) {
            int k = new Random().nextInt(files.length);
            for (int i = k; i < files.length; i++) {
                if (files[i].isFile()
                        && Utilities.getExtension(files[i]).equals("txt")) {
                    return files[i];
                }
            }
            for (int i = k - 1; i >= 0; i--) {
                if (files[i].isFile()
                        && Utilities.getExtension(files[i]).equals("txt")) {
                    return files[i];
                }
            }
        }
        return new File("");
    }
    
    /**
     * Returns player name.
     * @return player name
     */
    public static String getName() {
        return (String) settings.get(NAME);
    }
    
    /**
     * Returns active deck or null for error.
     * @return active deck or null
     */
    public static Deck getDeck() {
        return Deck.load((File) settings.get(DECK));
    }
    
    public static void setLastIP(String ip) {
        settings.put(LAST_JOINED_IP, ip);
    }
    
    /**
     * @return last IP with port to which client was joined
     */
    public static String getLastIP() {
        return (String) settings.get(LAST_JOINED_IP);
    }
    
    public static void setLastCreateInfo(int port, int players, boolean join) {
        settings.put(LAST_CREATED_PORT, port);
        settings.put(LAST_CREATED_PLAYERS, players);
        settings.put(LAST_CREATED_JOINED, join);
    }
    
    /**
     * @return last port on which server has been created
     */
    public static int getLastPort() {
        return (int) settings.get(LAST_CREATED_PORT);
    }
    
    /**
     * @return number of players of last created server
     */
    public static int getLastPlayers() {
        return (int) settings.get(LAST_CREATED_PLAYERS);
    }
    
    /**
     * @return if server creator has joined his last server at creation time
     */
    public static boolean getLastJoin() {
        return (boolean) settings.get(LAST_CREATED_JOINED);
    }
}
