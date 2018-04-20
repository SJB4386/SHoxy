package SHoxy.HTTP;

import java.util.*;
import java.util.Map.Entry;

public class HTTPData {
    public Map<String, String> headerLines;
    public byte[] body;

    public HTTPData() {
        this.headerLines = new LinkedHashMap<String, String>();
    }

    @Override
    public String toString() {
        String http = "";
        for (Entry<String, String> headerLine : headerLines.entrySet())
            http += String.format("%s %s", headerLine.getKey(), headerLine.getValue());
        return http;
    }
}
