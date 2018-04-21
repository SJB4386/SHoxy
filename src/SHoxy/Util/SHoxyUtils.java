package SHoxy.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SHoxyUtils {
    private final static String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    private static SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);

    /**
     * Retrieves a document, typically HTML.
     * @param filename the location of the document
     * @return the document as a byte[]
     */
    public static byte[] retrieveDocument(String filename) {
        byte[] docBytes = {};
        try {
            docBytes = Files.readAllBytes(Paths.get(filename));
        } catch (IOException e) {
            System.out.printf("File %s doesn't exist\n", filename);
        }
        return docBytes;
    }

    /**
     * Prints a given message out to the terminal with a timestamp
     * @param message the message to print
     */
    public static void logMessage(String message) {
        long currentTime = System.currentTimeMillis();
        String timestamp = dateFormat.format(new Date(currentTime));
        System.out.printf("%s: %s\n", timestamp, message);
    }

}
