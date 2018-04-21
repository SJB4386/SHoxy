package SHoxy.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DocRetriever {
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

}
