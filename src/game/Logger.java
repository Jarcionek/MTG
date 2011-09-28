package game;

import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author Jaroslaw Pawlak
 */
public class Logger extends JPanel {
    private JTextArea textArea;

    public Logger() {
        super(new GridLayout(1, 1));
        textArea = new JTextArea();
        textArea.setEditable(false);
        this.add(new JScrollPane(textArea));
    }

    public void log(String text) {
        textArea.append("\n" + text);
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

}
