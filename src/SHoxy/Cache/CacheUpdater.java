package SHoxy.Cache;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class CacheUpdater implements Runnable {

    private Map<String, CachedItem> cache;
    private int updateTimerSeconds;
    
    private static final int milliseconds = 1000;

    public CacheUpdater(Map<String, CachedItem> cache, int updateTimerSeconds) {
        this.cache = cache;
        this.updateTimerSeconds = updateTimerSeconds;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        updateCache();
        scheduleCacheUpdate();
    }

    private void scheduleCacheUpdate() {
        try {
            while (true) {
                Thread.sleep(updateTimerSeconds * milliseconds);
                updateCache();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void updateCache() {
        
        Collection<CachedItem> entries = cache.values();
        for (Iterator<CachedItem> iterator = entries.iterator(); iterator.hasNext();) {
            CachedItem item = iterator.next();
            updateCachedItem(item);
        }
        
    }

    private void updateCachedItem(CachedItem item) {
        // TODO Get lock on item. If file at URL has been modified, get item from URL, write file to disk
        
    }
    

}
