package SHoxy.Proxy;

import java.util.*;

import SHoxy.Cache.*;

public class SHoxyProxy {
    public static final String HTTP_VERSION = "HTTP/1.1";

    private static final String CONFIG_FILE = "SHoxyProxy.config";

    public static void main(String[] args) {
        Map<String, CachedItem> cache = new LinkedHashMap<String, CachedItem>();
        // cache doesn't need to be an ArrayList, but it will need to be passed in some
        // way
        // to each thread
        Thread listenerThread = new Thread(new TCPListener(3081, cache, "cache"));
        Thread updaterThread = new Thread(new CacheUpdater());
        Thread cleanerThread = new Thread(new CacheCleaner());

        listenerThread.start();
        updaterThread.start();
        cleanerThread.start();
    }
}
