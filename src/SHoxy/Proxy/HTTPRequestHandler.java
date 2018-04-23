package SHoxy.Proxy;

import java.io.*;
import java.net.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;

import SHoxy.Cache.CachedItem;
import SHoxy.HTTP.HTTPData;
import SHoxy.HTTP.HTTPEncoderDecoder;
import SHoxy.Util.SHoxyUtils;

public class HTTPRequestHandler implements Runnable {
    private final static String ERROR_501_FILENAME = "HTTPErrorDocs/501.html";
    private final static String ERROR_400_FILENAME = "HTTPErrorDocs/400.html";
    private final static int REQUEST_BUFFER_SIZE = 800;
    private final static int HTTP_READ_TIMEOUT = 5000;
    private final static int HTTP_CONNECT_TIMEOUT = 5000;

    private Socket clientSocket;
    private Map<String, CachedItem> cache;
    private String cacheDirectory;
    private OutputStream replyStream;
    private InputStream requestStream;

    public HTTPRequestHandler(Socket clientSocket, Map<String, CachedItem> cache,
            String cacheDirectory) {
        this.clientSocket = clientSocket;
        this.cache = cache;
        this.cacheDirectory = cacheDirectory;
    }

    @Override
    public void run() {
        byte[] requestBuffer = new byte[REQUEST_BUFFER_SIZE];
        byte[] rawRequest;
        int requestSize;
        HTTPData clientRequest;
        HTTPData forwardReply;
        String clientIP = clientSocket.getInetAddress().toString();
        String requestCacheKey;

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
                    if (forwardReply.responseCode == HttpURLConnection.HTTP_OK) {
                        replyStream.write(forwardReply.constructPacket());
                        SHoxyUtils.logMessage(String.format("200 OK to %s", clientIP));
                    } else if (forwardReply.responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) {
                        requestCacheKey = clientRequest.URI.replace("http://", "")
                                .replace("www.", "");
                        synchronized (cache.get(requestCacheKey)) {
                            replyStream.write(create200Packet(cache.get(requestCacheKey).fileLocation));
                            SHoxyUtils.logMessage(String
                                    .format("Cached document sent to %s", clientIP));
                        }
                    } else if (forwardReply.responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                        replyStream.write(get400Packet());
                        SHoxyUtils.logMessage(
                                String.format("400 Bad Request to %s, reply sent to %s",
                                        clientRequest.URI, clientIP));
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
     * Creates a HTTP packet with a 400 status code
     * @return the packet ready for transport
     */
    public byte[] get400Packet() {
        HTTPData erro400 = new HTTPData();

        erro400.isReply = true;
        erro400.protocol = SHoxyProxy.HTTP_VERSION;
        erro400.statusCode = "400 Bad Request\r\n";
        erro400.body = SHoxyUtils.retrieveDocument(ERROR_400_FILENAME);
        erro400.headerLines.put("Content-Length:",
                String.format("%d\r\n", erro400.body.length));

        return erro400.constructPacket();
    }
    /**
     * Creates a HTTP packet from a locally stored document
     * @param filename the key the document is stored under
     * @return a packet ready for transport
     */
    public byte[] create200Packet(String filename) {
        HTTPData docHTTP = new HTTPData();

        docHTTP.isReply = true;
        docHTTP.protocol = SHoxyProxy.HTTP_VERSION;
        docHTTP.statusCode = "200 OK\r\n";
        docHTTP.body = SHoxyUtils.retrieveDocument(filename);
        docHTTP.headerLines.put("Content-Length:",
                String.format("%d\r\n", docHTTP.body.length));

        return docHTTP.constructPacket();
    }

    /**
     * Forwards a GET request to a given url Performs a conditional GET if the file is
     * determined to be cached. Caches files as need.
     * @param url the url the request is to be sent to
     * @return the response gotten from the GET request as HTTPData
     */
    public HTTPData forwardGETRequest(String url) {
        URL destination;
        String urlCacheKey;
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
            connection.setConnectTimeout(HTTP_CONNECT_TIMEOUT);
            connection.setReadTimeout(HTTP_READ_TIMEOUT);
            connection.setRequestMethod("GET");
            urlCacheKey = url.replace("http://", "").replace("www.", "");
            if (cache.containsKey(urlCacheKey)) {
                synchronized (cache.get(urlCacheKey)) {
                    connection.setIfModifiedSince(
                            cache.get(urlCacheKey).lastModified.getTime());
                    cache.get(urlCacheKey).lastTimeRequested = new Date();
                }
            }
            responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                response = new HTTPData();
                response.isReply = true;
                response.protocol = SHoxyProxy.HTTP_VERSION;
                response.statusCode = "200 OK\r\n";
                response.responseCode = responseCode;
                responseBuffer = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                rawResponse = new StringBuffer();
                while ((responseLine = responseBuffer.readLine()) != null)
                    rawResponse.append(responseLine + "\n");
                responseBuffer.close();
                response.body = rawResponse.toString().getBytes();
                response.headerLines.put("Content-Length:",
                        String.format("%d\r\n", response.body.length));
                cacheFile(urlCacheKey, response.body, connection.getLastModified());
            } else {
                response = new HTTPData();
                response.responseCode = responseCode;
            }
        } catch (MalformedURLException e) {
            SHoxyUtils.logMessage(String.format("URL: %s not formatted properly", url));
        } catch (SocketTimeoutException e) {
            SHoxyUtils.logMessage(String.format("Connection timeout to %s", url));
            response = new HTTPData();
            response.responseCode = 408;
        } catch (IOException e) {
            SHoxyUtils.logMessage(String.format("Couldn't reach url %s", url));
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return response;
    }

    /**
     * Caches a file to disk and updates the cache HashMap accordingly
     * @param cacheKey the filename of the file which is also its key for the HashMap
     * @param data the file data
     * @param lastModifed the last time the file was modified according to the HTTP
     *        response
     */
    public void cacheFile(String cacheKey, byte[] data, long lastModifed) {
        String cachedDocFilename = String.format("%s%s", cacheDirectory,
                CachedItem.parseURLToFileName(cacheKey));
        if (!cachedDocFilename.matches(SHoxyUtils.FILENAME_END_IN_FILE_REGEX))
            cachedDocFilename = String.format("%sdefault.html", cachedDocFilename);
        try {
            Paths.get(cachedDocFilename);
            if (!cache.containsKey(cacheKey)) {
                cache.put(cacheKey, new CachedItem());
                synchronized (cache.get(cacheKey)) {
                    cache.get(cacheKey).URL = cacheKey;
                    cache.get(cacheKey).fileLocation = cachedDocFilename;
                    cache.get(cacheKey).lastTimeRequested = new Date();
                    if (lastModifed > 0)
                        cache.get(cacheKey).lastModified = new Date(lastModifed);
                    else
                        cache.get(cacheKey).lastModified = new Date();
                    SHoxyUtils.writeFile(data, cachedDocFilename);
                }
            } else {
                synchronized (cache.get(cacheKey)) {
                    cache.get(cacheKey).lastTimeRequested = new Date();
                    if (lastModifed > 0)
                        cache.get(cacheKey).lastModified = new Date(lastModifed);
                    else
                        cache.get(cacheKey).lastModified = new Date();
                    SHoxyUtils.writeFile(data, cachedDocFilename);
                }
            }
        } catch (InvalidPathException e) {
            SHoxyUtils.logMessage(String.format(
                    "Couldn't cache file %s, illegal filename", cachedDocFilename));
        }
    }
}
