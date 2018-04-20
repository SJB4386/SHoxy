package SHoxy.Proxy;

import java.net.*;
import java.util.Map;

import SHoxy.Cache.CachedItem;

public class HTTPRequestHandler implements Runnable {
    private Socket clientSocket;

    public HTTPRequestHandler(Socket clientSocket, Map<String, CachedItem> cache, String cacheDirectory) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }

}
