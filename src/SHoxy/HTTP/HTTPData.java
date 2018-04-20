package SHoxy.HTTP;

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
        return null;
    }

    @Override
    public String toString() {
        String http = "";
        for (Entry<String, String> headerLine : headerLines.entrySet())
            http += String.format("%s %s", headerLine.getKey(), headerLine.getValue());
        return http;
    }
}
