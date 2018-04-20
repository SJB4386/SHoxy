package SHoxy.Proxy;

import java.io.*;
import java.net.*;
import java.util.Map;

import SHoxy.Cache.CachedItem;

public class HTTPRequestHandler implements Runnable {
    private Socket clientSocket;
    private OutputStream replyStream;

    public HTTPRequestHandler(Socket clientSocket, Map<String, CachedItem> cache, String cacheDirectory) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            System.out.printf("Client connected on port: %d", clientSocket.getPort());
            replyStream = clientSocket.getOutputStream();
            replyStream.write("HTTP/1.1 501 Not Implemented\r\n\r\n".getBytes());
            replyStream.close();
        } catch (IOException e) {
            System.out.println("Error connecting to client");
        }
    }
}
