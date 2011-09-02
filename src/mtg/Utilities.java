package mtg;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author Jaroslaw Pawlak
 */
public class Utilities {
    public static String findPath(File directory, String name) {
        for (File e : directory.listFiles()) {
            if (e.isFile() && Utilities.getName(e).equals(name)) {
                return e.getPath();
            }
            if (e.isDirectory()) {
                String x = findPath(e, name);
                if (x != null) {
                    return x;
                }
            }
        }
        return null;
    }
    public static void receiveFile(File path, Socket socket) throws FileNotFoundException, IOException  {
        BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));

        byte[] b = new byte[256];
        int read = -1;

        while ((read = bis.read(b)) >= 0) {
            bos.write(b, 0, read);
        }
        bos.close();
        bis.close();
    }

    public static void sendFile(File file, Socket socket) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));

        byte[] b = new byte[256];
        int read = -1;
        while ((read = bis.read(b)) >= 0) {
            bos.write(b, 0, read);
        }
        bis.close();
        bos.close();
    }

    /**
     * Returns file name without extension
     * @param file
     * @return
     */
    public static String getName(File file) {
        if (!file.getName().contains(".")) {
            return file.getName();
        } else {
            return file.getName().substring(0, file.getName().lastIndexOf("."));
        }
    }

    /**
     * Returns file extension or null if file is a directory or does not
     * contain "."
     * @param file file
     * @return file extension or null if none
     */
    public static String getExtension(File file) {
        if (!file.isFile() || !file.getName().contains(".")) {
            return null;
        } else {
            return file.getName().substring(file.getName().lastIndexOf(".") + 1);
        }
    }
}
