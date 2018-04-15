package SHoxy.Proxy;

import java.util.ArrayList;

import SHoxy.Cache.*;

public class SHoxyProxy {

    public static void main(String[] args) {
        ArrayList<CachedItem> cache = new ArrayList<CachedItem>();
        // cache doesn't need to be an ArrayList, but it will need to be passed in some way
        // to each thread
        Thread listenerThread = new Thread(new TCPListener());
        Thread updaterThread = new Thread(new CacheUpdater());
        Thread cleanerThread = new Thread(new CacheCleaner());

        listenerThread.start();
        updaterThread.start();
        cleanerThread.start();
    }

}
