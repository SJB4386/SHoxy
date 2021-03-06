package SHoxy.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SHoxyUtils {
    private final static String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    public static String FILENAME_END_IN_FILE_REGEX = ".*\\/.*\\..+$";
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

    /**
     * Stores a file based on the uri it was retrieved from
     * @param httpResponseBody the body of the http received
     * @param bodyFilename the file the body will be stored under
     */
    public static void writeFile(byte[] httpResponseBody, String bodyFilename) {
        FileOutputStream outStream;
        try {
            File bodyDirectory = Paths.get(bodyFilename).getParent().toFile();
            bodyDirectory.mkdirs();
            File bodyFile = new File(bodyFilename);
            bodyFile.createNewFile();
            outStream = new FileOutputStream(bodyFile, false);
            outStream.write(httpResponseBody);
            outStream.close();
        } catch (FileNotFoundException e) {
            logMessage(String.format("Couldn't cache file %s", bodyFilename));
        } catch (IOException e) {
            logMessage(String.format("Error caching file %s", bodyFilename));
        } catch (InvalidPathException e) {
            logMessage(String.format("Couldn't cache file %s, illegal filename", bodyFilename));
        }
    }

}
