package game;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;

/**
 * @author Jaroslaw Pawlak
 */
public class TableDragListener extends MouseAdapter {

    private int startX;
    private int startY;
    private int powerSteering = 1;

    @Override
    public void mousePressed(MouseEvent e) {
        startX = e.getX();
        startY = e.getY();
        if (e.getButton() == MouseEvent.BUTTON1) {
            powerSteering = 5;
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            powerSteering = 15;
        } else {
            powerSteering = 1;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        JComponent source = (JComponent) e.getSource();
        
        int a = e.getX() - startX;
        int b = e.getY() - startY;

        a *= powerSteering;
        b *= powerSteering;

        Rectangle r = source.getVisibleRect();
        r.x += a;
        r.y += b;
        startX = e.getX() + a;
        startY = e.getY() + b;
        
        source.scrollRectToVisible(r);
    }
}
