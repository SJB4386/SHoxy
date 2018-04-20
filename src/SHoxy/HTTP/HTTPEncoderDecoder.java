package SHoxy.HTTP;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class HTTPEncoderDecoder {

    public static byte[] encode(HTTPData decodedData) {
        return null;
    }

    /**
     * Decodes an HTTP Request message into HTTPData
     * @param encodedData The raw HTTP data
     * @return an HTTPData object containing the data that was encodes in the input
     */
    public static HTTPData decodeRequest(byte[] encodedData) {
        HTTPData decodedData = new HTTPData();
        boolean carriageReturnFound = false;
        boolean newLineFound = false;
        boolean headerRead = false;
        int currStartIndex = 0;
        int currEndIndex = 0;
        byte currentByte;
        String currentHttpAttr;
        String[] attrValPair;

        while (headerRead != true) {
            carriageReturnFound = false;
            newLineFound = false;
            while (!carriageReturnFound || !newLineFound) {
                currentByte = encodedData[currEndIndex];
                if (currentByte == 13)
                    carriageReturnFound = true;
                else if (currentByte == 10)
                    newLineFound = true;
                currEndIndex++;
            }
            currentHttpAttr = new String(
                    Arrays.copyOfRange(encodedData, currStartIndex, currEndIndex),
                    StandardCharsets.UTF_8);
            currStartIndex = currEndIndex;
            if (currentHttpAttr.equals("\r\n"))
                headerRead = true;
            if (!headerRead) {
                attrValPair = currentHttpAttr.split(" ", 2);
                decodedData.headerLines.put(attrValPair[0], attrValPair[1]);
            }
        }
        return decodedData;
    }
}
