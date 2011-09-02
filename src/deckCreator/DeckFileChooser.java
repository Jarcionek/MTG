package deckCreator;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import mtg.Main;
import mtg.Utilities;

/**
 * @author Jaroslaw Pawlak
 */
public class DeckFileChooser extends JFileChooser {
    public DeckFileChooser() {
        super(Main.DECKS);
        this.setMultiSelectionEnabled(false);
        this.setFileSelectionMode(JFileChooser.FILES_ONLY);
        this.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || (f.isFile()
                        && f.getName().contains(".") &&
                        Utilities.getExtension(f)
                        .toLowerCase().equals("txt"));
            }
            @Override
            public String getDescription() {
                return "*.txt";
            }
        });
    }
}
