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
        
        int listenerPort = 3081;
        int updateTimerSeconds = 10;
        int deleteTimerSeconds = 1000;
        String cacheDirectory = "cache";
        // TODO Load config and send values to threads

        
        Thread listenerThread = new Thread(new TCPListener(listenerPort, cache, cacheDirectory));
        Thread updaterThread = new Thread(new CacheUpdater(cache, updateTimerSeconds));
        Thread cleanerThread = new Thread(new CacheCleaner(cache, deleteTimerSeconds));

        listenerThread.start();
        updaterThread.start();
        cleanerThread.start();
    }
}
