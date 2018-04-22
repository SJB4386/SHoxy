package SHoxy.Cache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import SHoxy.HTTP.HTTPData;
import SHoxy.Proxy.SHoxyProxy;
import SHoxy.Util.SHoxyUtils;

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
        Lock lock = item.getLock();
        URL destination;
        HttpURLConnection connection = null;
        BufferedReader responseBuffer;
        StringBuffer rawResponse;
        String responseLine;
        int responseCode;
        HTTPData response = null;
        
        if (lock.tryLock()) {
            try {
                destination = new URL(item.URL);
                connection = (HttpURLConnection) destination.openConnection();
                connection.setRequestMethod("GET");
                connection.setIfModifiedSince(item.lastModified.getTime());
                responseCode = connection.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    response = new HTTPData();
                    response.isReply = true;
                    response.protocol = SHoxyProxy.HTTP_VERSION;
                    response.statusCode = "200 OK\r\n";
                    response.responseCode = responseCode;
                    responseBuffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    rawResponse = new StringBuffer();
                    while ((responseLine = responseBuffer.readLine()) != null)
                        rawResponse.append(responseLine + "\n");
                    responseBuffer.close();
                    response.body = rawResponse.toString().getBytes();
                    response.headerLines.put("Content-Length:", String.format("%d\r\n", response.body.length));
                    
                    SHoxyUtils.writeFile(response.body, CachedItem.parseURLToFileName(item.URL));
                }
                // if (responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) or anything else, do nothing
            } catch (MalformedURLException e) {
                SHoxyUtils.logMessage(String.format("URL: %s not formatted properly", item.URL));
            } catch (IOException e) {
                SHoxyUtils.logMessage(String.format("Couldn't reach url %s", item.URL));
            }
            
            lock.unlock();
        }
    }
}
