package SHoxy.Cache;

import java.io.File;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;

public class CacheCleaner implements Runnable {
    
    private Map<String, CachedItem> cache;
    private int deleteTimerSeconds;
    
    private static final int milliseconds = 1000;
    private Random rand;
    
    public CacheCleaner (Map<String, CachedItem> cache, int deleteTimerSeconds) {
        this.cache = cache;
        this.deleteTimerSeconds = deleteTimerSeconds;
    }

    @Override
    public void run() {
        scheduleClean();
    }
    
    private void scheduleClean() {
        Thread thread = new Thread(){
            public void run(){
                try {
                    sleep(rand.nextInt(30) * 1000);
                    cleanOldEntries();
                    scheduleClean();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
          };
          thread.start();
    }
    
    
    private void cleanOldEntries() {
        Collection<CachedItem> entries = cache.values();
        for (CachedItem item : entries) {
            if (isTooOld(item)) {
                removeFromCache(item);
            }
        }
    }
    
    private boolean isTooOld(CachedItem item) {
        Lock readLock = item.getLock().readLock();
        readLock.lock();
        boolean result = false;
        try {
            Date now = new Date();
            Date lastRequested = item.lastTimeRequested;
            
            result = (now.getTime() - lastRequested.getTime() <= deleteTimerSeconds * milliseconds);
        } catch (Exception e) {
            result = false;
        } finally {
            readLock.unlock();
        }
        return result;
    }

    private void removeFromCache(CachedItem item) {
        // Try to get a lock and remove the item from the cache.
        // If that fails, give up, because it's in use still
        Lock writeLock = item.getLock().writeLock();
        writeLock.lock();
        try {
            String fileURL = CachedItem.parseURLToFileName(item.URL);
            deleteFile(fileURL);
            cache.remove(item.URL);
        } finally {
            writeLock.unlock();
        }

    }

    private void deleteFile(String filename) {
        File file = new File(filename);
        
        if(file.delete()) {
            System.out.println("Deleted file at " + filename);
        } else {
            System.out.println("Failed to delete the file");
        }
    }
}
