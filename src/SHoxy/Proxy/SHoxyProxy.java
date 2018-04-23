package SHoxy.Proxy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import SHoxy.Cache.*;
import SHoxy.Util.SHoxyUtils;

public class SHoxyProxy {
    public static final String HTTP_VERSION = "HTTP/1.1";

    private static final String CONFIG_FILE = "SHoxyProxy.config";
    

    public static void main(String[] args) {
        Map<String, CachedItem> cache = new ConcurrentHashMap<String, CachedItem>();
        int listenerPort = 0;
        int updateTimerSeconds = 0;
        int deleteTimerSeconds = 0;
        String cacheDirectory = null;
        String[] configValues = {"Listening-Port:", "Update-Timer:",
                                 "Deletion-Timer:", "Cache-Directory:"};
        
        // Load config and send values to threads
        
        try {
            Scanner configReader;
            configReader = new Scanner(new File(CONFIG_FILE));
            while (configReader.hasNextLine()) {
                String line = configReader.nextLine();
                for (int i = 0; i < configValues.length; i++) {
                    if (line.startsWith(configValues[i])) {
                        String lineValue = line.substring(configValues[i].length()).trim();
                        if (i == 0) {
                            try {
                                listenerPort = Integer.parseInt(lineValue);
                            } catch (NumberFormatException e) {
                                SHoxyUtils.logMessage(String.format("%s cannot be a port number.", lineValue));
                            }
                        } else if (i == 1) {
                            try {
                                updateTimerSeconds = Integer.parseInt(lineValue);
                            } catch (NumberFormatException e) {
                                SHoxyUtils.logMessage("Update timer must be a value in seconds.");
                            }
                        } else if (i == 2) {
                            try {
                                deleteTimerSeconds = Integer.parseInt(lineValue);
                            } catch (NumberFormatException e) {
                                SHoxyUtils.logMessage("Delete timer must be a value in seconds.");
                            }
                        } else if (i == 3) {
                            cacheDirectory = lineValue;
                        }
                        break;
                    }
                }
                
            }
            configReader.close();
        } catch (FileNotFoundException e) {
            SHoxyUtils.logMessage("Config file not found.");
            System.exit(0);
        }

        Thread listenerThread = new Thread(new RequestListener(listenerPort, cache, cacheDirectory));
        Thread updaterThread = new Thread(new CacheUpdater(cache, updateTimerSeconds));
        Thread cleanerThread = new Thread(new CacheCleaner(cache, deleteTimerSeconds));

        listenerThread.start();
        updaterThread.start();
        cleanerThread.start();
    }
    
    
}
