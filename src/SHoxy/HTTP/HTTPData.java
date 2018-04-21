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
