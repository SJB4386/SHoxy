package SHoxy.Proxy;

import java.io.*;
import java.net.*;
import java.util.Map;

import SHoxy.Cache.CachedItem;
import SHoxy.HTTP.HTTPData;
import SHoxy.HTTP.HTTPEncoderDecoder;
import SHoxy.Util.SHoxyUtils;

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
        HTTPData forwardReply;
        String clientIP = clientSocket.getInetAddress().toString();

        try {
            SHoxyUtils.logMessage(String.format("%s connected on port: %d", clientIP,
                    clientSocket.getPort()));
            requestStream = clientSocket.getInputStream();
            replyStream = clientSocket.getOutputStream();
            requestSize = requestStream.read(requestBuffer);
            if (requestSize > 0) {
                rawRequest = new byte[requestSize];
                System.arraycopy(requestBuffer, 0, rawRequest, 0, requestSize);
                clientRequest = HTTPEncoderDecoder.decodeMessage(rawRequest);
                if (clientRequest.method.equals("GET")) {
                    SHoxyUtils.logMessage(
                            String.format("%s requests %s", clientIP, clientRequest.URI));
                    forwardReply = forwardGETRequest(clientRequest.URI);
                    if (forwardReply.responseCode == 200) {
                        replyStream.write(forwardReply.constructPacket());
                        SHoxyUtils.logMessage(String.format("200 OK to %s", clientIP));
                    } else {
                        replyStream.write(get501Packet());
                        SHoxyUtils.logMessage(
                                String.format("501 Not Implemented to %s, %d not handled",
                                        clientIP, forwardReply.responseCode));
                    }
                } else {
                    replyStream.write(get501Packet());
                    SHoxyUtils.logMessage(String.format(
                            "%s request recieved, 501 Not Implemented to %s",
                            clientRequest.method, clientIP));
                }
            }

            requestStream.close();
            replyStream.close();
        } catch (IOException e) {
            SHoxyUtils.logMessage("Error connecting to client");
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
        error501.body = SHoxyUtils.retrieveDocument(ERROR_501_FILENAME);
        error501.headerLines.put("Content-Length:",
                String.format("%d\r\n", error501.body.length));

        return error501.constructPacket();
    }

    /**
     * Creates a HTTP packet from a specified document
     * @param filename the name of the file stored on disk
     * @return a packet ready for transport
     */
    public byte[] create200PacketFromDoc(String filename) {
        HTTPData docHTTP = new HTTPData();

        docHTTP.isReply = true;
        docHTTP.protocol = SHoxyProxy.HTTP_VERSION;
        docHTTP.statusCode = "200 OK\r\n";
        docHTTP.body = SHoxyUtils.retrieveDocument(filename);
        docHTTP.headerLines.put("Content-Length:", String.format("%d\r\n", docHTTP.body.length));

        return docHTTP.constructPacket();
    }

    /**
     * Forwards a GET request to a given url
     * @param url the url the request is to be sent to
     * @return the response gotten from the GET request as HTTPData
     */
    public HTTPData forwardGETRequest(String url) {
        URL destination;
        HttpURLConnection connection = null;
        BufferedReader responseBuffer;
        StringBuffer rawResponse;
        String responseLine;
        int responseCode;
        HTTPData response = null;

        if (!url.matches("^http://.*"))
            url = String.format("http://%s", url);
        try {
            destination = new URL(url);
            connection = (HttpURLConnection) destination.openConnection();
            connection.setRequestMethod("GET");
            responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                response = new HTTPData();
                response.isReply = true;
                response.protocol = SHoxyProxy.HTTP_VERSION;
                response.statusCode = "200 OK\r\n";
                response.responseCode = responseCode;
                responseBuffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                rawResponse = new StringBuffer();
                while ((responseLine = responseBuffer.readLine()) != null)
                    rawResponse.append(responseLine + "\n");
                responseBuffer.close();
                response.body = rawResponse.toString().getBytes();
                response.headerLines.put("Content-Length:", String.format("%d\r\n", response.body.length));
            }
            else {
                response = new HTTPData();
                response.responseCode = responseCode;
            }
        } catch (MalformedURLException e) {
            SHoxyUtils.logMessage(String.format("URL: %s not formatted properly", url));
        } catch (IOException e) {
            SHoxyUtils.logMessage(String.format("Couldn't reach url %s", url));
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return response;
    }
}
