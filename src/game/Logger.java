package game;

import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import mtg.Utilities;

/**
 * @author Jaroslaw Pawlak
 */
public class Logger extends JPanel {
    private JTextArea textArea;

    public Logger() {
        super(new GridLayout(1, 1));

        String text = "Internal IP: ";
        text += Utilities.getInternalIP() + "\n";
        text += "External IP: ";
        text += Utilities.getExternalIP();

        textArea = new JTextArea(text);

        textArea.setEditable(false);
        JScrollPane jsp = new JScrollPane(textArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(jsp);
    }

    public void log(String text) {
        textArea.append("\n" + text);
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

}
