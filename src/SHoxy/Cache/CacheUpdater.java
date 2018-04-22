package SHoxy.Cache;

import java.util.Map;

public class CacheUpdater implements Runnable {

    private Map<String, CachedItem> cache;
    private int updateTimerSeconds;

    public CacheUpdater(Map<String, CachedItem> cache, int updateTimerSeconds) {
        this.cache = cache;
        this.updateTimerSeconds = updateTimerSeconds;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }

}
