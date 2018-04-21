package SHoxy.Proxy;

import java.io.*;
import java.net.*;
import java.util.Map;

import SHoxy.Cache.CachedItem;
import SHoxy.HTTP.HTTPData;
import SHoxy.HTTP.HTTPEncoderDecoder;
import SHoxy.Util.DocRetriever;

public class HTTPRequestHandler implements Runnable {
    private final static String ERROR_501_FILENAME = "HTTPErrorDocs/501.html";
    private final static int REQUEST_BUFFER_SIZE = 800;

    private Socket clientSocket;
    private OutputStream replyStream;
    private InputStream requestStream;

    public HTTPRequestHandler(Socket clientSocket, Map<String, CachedItem> cache,
            String cacheDirectory) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        byte[] requestBuffer = new byte[REQUEST_BUFFER_SIZE];
        byte[] rawRequest;
        int requestSize;
        HTTPData clientRequest;

        try {
            System.out.printf("Client connected on port: %d\n", clientSocket.getPort());

            requestStream = clientSocket.getInputStream();
            replyStream = clientSocket.getOutputStream();
            requestSize = requestStream.read(requestBuffer);
            if (requestSize > 0) {
                rawRequest = new byte[requestSize];
                System.arraycopy(requestBuffer, 0, rawRequest, 0, requestSize);
                clientRequest = HTTPEncoderDecoder.decodeMessage(rawRequest);
                System.out.println(clientRequest.toString());
            }

            replyStream.write(get501Packet());
            requestStream.close();
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
