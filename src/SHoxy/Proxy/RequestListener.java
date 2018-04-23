package SHoxy.Proxy;

import java.io.IOException;
import java.net.*;
import java.util.Map;

import SHoxy.Cache.CachedItem;
import SHoxy.Util.SHoxyUtils;

public class RequestListener implements Runnable {
    private int listeningPort;
    private Map<String, CachedItem> cache;
    private String cacheDirectory;
    private ServerSocket serverSocket;
    private Socket clientSocket;

    /**
     * Constructs a TCPListener
     * @param listeningPort The port to listen on for incoming traffic
     * @param cache The files cached
     * @param cacheDirectory The location of the cache
     */
    public RequestListener(int listeningPort, Map<String, CachedItem> cache, String cacheDirectory) {
        this.listeningPort = listeningPort;
        this.cache = cache;
        this.cacheDirectory = cacheDirectory;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(listeningPort);
            SHoxyUtils.logMessage(String.format("Server now listening on port: %d", listeningPort));
            while (true) {
                clientSocket = serverSocket.accept();
                new Thread(new HTTPRequestHandler(clientSocket, cache, cacheDirectory))
                        .start();
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format("Error creating socket on port %d", listeningPort));
        }
    }

}
