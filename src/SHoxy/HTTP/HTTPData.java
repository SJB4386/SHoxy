package SHoxy.HTTP;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;

public class HTTPData {
    public boolean isRequest;
    public boolean isReply;
    public Map<String, String> headerLines;
    public byte[] body;

    // Request fields
    public String method;
    public String URI;
    public String version;

    // Reply fields
    public String protocol;
    public String statusCode;

    public int responseCode;

    public HTTPData() {
        this.isRequest = false;
        this.isReply = false;
        this.headerLines = new LinkedHashMap<String, String>();
    }

    /**
     * Constructs an HTTP packet based on the data assigned
     * @return A properly formatted HTTP packet ready for transport
     */
    public byte[] constructPacket() {
        byte[] constructedPacket;
        byte[] currentHeaderAttr;

        String reqReplyLine = "";
        if (isRequest)
            reqReplyLine = String.format("%s %s %s", method, URI, version);
        else if (isReply)
            reqReplyLine = String.format("%s %s", protocol, statusCode);
        constructedPacket = reqReplyLine.getBytes();
        for (Entry<String, String> headerLine : headerLines.entrySet()) {
            currentHeaderAttr = String
                    .format("%s %s", headerLine.getKey(), headerLine.getValue())
                    .getBytes();
            constructedPacket = combineByteArrays(constructedPacket, currentHeaderAttr);
        }
        constructedPacket = combineByteArrays(constructedPacket, "\r\n".getBytes());
        if (body != null)
            constructedPacket = combineByteArrays(constructedPacket, body);

        return constructedPacket;
    }

    /**
     * Method found here:
     * https://stackoverflow.com/questions/5683486/how-to-combine-two-byte-arrays
     */
    public static byte[] combineByteArrays(byte[] a, byte[] b) {
        byte[] combined = new byte[a.length + b.length];

        System.arraycopy(a, 0, combined, 0, a.length);
        System.arraycopy(b, 0, combined, a.length, b.length);
        return combined;
    }

    @Override
    public String toString() {
        String http = "";
        if (isRequest)
            http += String.format("%s %s %s", method, URI, version);
        else if (isReply)
            http += String.format("%s %s", protocol, statusCode);
        for (Entry<String, String> headerLine : headerLines.entrySet())
            http += String.format("%s %s", headerLine.getKey(), headerLine.getValue());
        http += "\r\n";
        if (body != null)
            http += new String(body, StandardCharsets.UTF_8);
        return http;
    }
}
