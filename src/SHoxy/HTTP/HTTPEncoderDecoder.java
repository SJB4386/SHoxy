package SHoxy.HTTP;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class HTTPEncoderDecoder {
    public static final String HTTP_METHODS_REGEX = "GET|POST|HEAD|PUT|DELETE|OPTIONS|CONNECT";
    public static final String HTTP_REGEX = "HTTP.*";

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
        String[] reqReplyLine;
        String[] requestVals;
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

            if (currentHttpAttr.equals("\r\n"))
                headerRead = true;
            else if (!headerRead && currStartIndex == 0) {
                reqReplyLine = currentHttpAttr.split(" ", 2);
                if (reqReplyLine[0].matches(HTTP_METHODS_REGEX)) {
                    decodedData.isRequest = true;
                    decodedData.method = reqReplyLine[0];
                    requestVals = reqReplyLine[1].split(" ");
                    decodedData.URI = requestVals[0];
                    decodedData.version = requestVals[1];
                } else if (reqReplyLine[0].matches(HTTP_REGEX)) {
                    decodedData.isReply = true;
                    decodedData.protocol = reqReplyLine[0];
                    decodedData.statusCode = reqReplyLine[1];
                }
            } else if (!headerRead && currStartIndex > 0) {
                attrValPair = currentHttpAttr.split(" ", 2);
                decodedData.headerLines.put(attrValPair[0], attrValPair[1]);
            }
            currStartIndex = currEndIndex;
        }
        return decodedData;
    }
}
