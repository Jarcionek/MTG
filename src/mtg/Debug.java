package mtg;

import java.text.DecimalFormat;
import java.util.Calendar;
import javax.swing.JOptionPane;

/**
 * @author Jaroslaw Pawlak
 */
public class Debug {
    /**
     * warning
     */
    public static int W = 0;
    /**
     * information
     */
    public static int I = 1;
    /**
     * error
     */
    public static int E = 2;
    /**
     * critical error
     */
    public static int CE = 3;

    private static boolean warning = true;
    private static boolean info = true;
    private static boolean error = true;
    private static boolean other = true;

    private static DecimalFormat df2 = new DecimalFormat("00");
    private static DecimalFormat df3 = new DecimalFormat("000");

    private Debug() {};

    /**
     * print
     */
    public static void p(Object message, int type) {
        if (type == W) {
            if (warning) {
               print(message);
            }
        } else if (type == I) {
            if (info) {
               print(message);
            }
        } else if (type == E) {
            if (error) {
               print(message);
            }
        } else if (type == CE) {
            JOptionPane.showMessageDialog(null, print(message),
                    Main.TITLE, JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } else {
            if (other) {
                print("Message of unknown type (" + type + "): " + message.toString());
            }
        }
    }

    private static String print(Object message) {
        Calendar c = Calendar.getInstance();
        String msg = df2.format(c.get(Calendar.HOUR_OF_DAY)) + ":"
                   + df2.format(c.get(Calendar.MINUTE)) + ":"
                   + df2.format(c.get(Calendar.SECOND)) + ","
                   + df3.format(c.get(Calendar.MILLISECOND)) + " - "
                   + message.toString();
        System.out.println(msg);
        return msg;
    }
}
