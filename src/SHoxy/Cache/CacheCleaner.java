package SHoxy.Cache;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Lock;

import SHoxy.Util.SHoxyUtils;

public class CacheCleaner implements Runnable {
    
    private Map<String, CachedItem> cache;
    private int deleteTimerSeconds;
    
    private static final int milliseconds = 1000;
    private Random rand;
    
    public CacheCleaner (Map<String, CachedItem> cache, int deleteTimerSeconds) {
        this.cache = cache;
        this.deleteTimerSeconds = deleteTimerSeconds;
        rand = new Random();
    }

    @Override
    public void run() {
        scheduleClean();
    }
    
    /**
     * At random intervals, check the cache and remove entries older than the delete timer interval.
     */
    private void scheduleClean() {
        while (true) {
            try {
                Thread.sleep(rand.nextInt(30) * milliseconds);
            } catch (InterruptedException e) {
                SHoxyUtils.logMessage("CacheCleaner interrupted");
            }
            cleanOldEntries();
            scheduleClean();
        }
    }
    
    
    /**
     * Remove items from the cache that are past their specified expiration time.
     */
    private void cleanOldEntries() {
        Collection<CachedItem> entries = cache.values();
        for (Iterator<CachedItem> iterator = entries.iterator(); iterator.hasNext();) {
            CachedItem item = iterator.next();
            if (isTooOld(item)) {
                removeFromCache(item);
            }
        }
    }
    
    
    /**
     * @param item the item to check
     * @return whether the item is older than the value in deleteTimerSeconds
     */
    private boolean isTooOld(CachedItem item) {
        boolean result = false;

        Lock itemLock = item.getLock();
        if (itemLock.tryLock()) {
            Date now = new Date();
            Date lastRequested = item.lastTimeRequested;
            
            result = (now.getTime() - lastRequested.getTime() <= deleteTimerSeconds * milliseconds);
            itemLock.unlock();
        }
        
        return result;
    }

    /**
     * remove the item from the cache and from the disk.
     * @param item
     */
    private void removeFromCache(CachedItem item) {
        // Try to get a lock and remove the item from the cache.
        // If that fails, give up, because it's in use still
        Lock itemLock = item.getLock();
        if (itemLock.tryLock()) {
            String fileURL = item.fileLocation;
            deleteFile(fileURL);
            cache.remove(item.URL);
            
            itemLock.unlock();
        }

    }

    private void deleteFile(String filename) {
        File file = new File(filename);

        if (file.delete())
            SHoxyUtils.logMessage(String.format("Deleted file at %s", filename));
        else
            SHoxyUtils.logMessage("Failed to delete the file");
    }
}
