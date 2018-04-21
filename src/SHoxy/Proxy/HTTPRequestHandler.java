package SHoxy.Proxy;

import java.io.*;
import java.net.*;
import java.util.Map;

import SHoxy.Cache.CachedItem;
import SHoxy.HTTP.HTTPData;
import SHoxy.Util.DocRetriever;

public class HTTPRequestHandler implements Runnable {
    private final static String ERROR_501_FILENAME = "HTTPErrorDocs/501.html";

    private Socket clientSocket;
    private OutputStream replyStream;

    public HTTPRequestHandler(Socket clientSocket, Map<String, CachedItem> cache,
            String cacheDirectory) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            System.out.printf("Client connected on port: %d\n", clientSocket.getPort());
            replyStream = clientSocket.getOutputStream();
            replyStream.write(get501Packet());
            replyStream.close();
        } catch (IOException e) {
            System.out.println("Error connecting to client");
        }
    }

    /**
     * Creates a HTTP packet with a 501 status code
     * @return the packet ready for transport
     */
    public byte[] get501Packet() {
        HTTPData error501 = new HTTPData();

        error501.isReply = true;
        error501.protocol = SHoxyProxy.HTTP_VERSION;
        error501.statusCode = "501 Not Implemented\r\n";
        error501.body = DocRetriever.retrieveDocument(ERROR_501_FILENAME);
        error501.headerLines.put("Content-Length:",
                String.format("%d\r\n", error501.body.length));

        return error501.constructPacket();
    }
}
