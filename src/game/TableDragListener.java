package game;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

/**
 * @author Jaroslaw Pawlak
 */
public class TableDragListener extends MouseAdapter {
    private int startX;
    private int startY;

    @Override
    public void mousePressed(MouseEvent e) {
        startX = e.getX();
        startY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int a = e.getX() - startX;
        int b = e.getY() - startY;

        //power steering ;)
        a *= 5;
        b *= 5;

        Rectangle r = ((JPanel) e.getSource()).getVisibleRect();
        r.x += a;
        r.y += b;
        startX = e.getX() + a;
        startY = e.getY() + b;

        ((JPanel) e.getSource()).scrollRectToVisible(r);
    }
}
