package SHoxy.Cache;

import java.util.Date;

public class CachedItem {
    private String URL;
    private Date lastModified;
    private Date lastTimeRequested;

    /**
     * Parses URL into the filename that is used for storage of the CachedItem
     * @return The filename the cached item is stored under
     */
    public String parseURLtoFileName() {
        return null;
    }

}
