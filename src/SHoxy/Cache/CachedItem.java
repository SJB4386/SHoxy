package SHoxy.Cache;

import java.util.ArrayList;
import java.util.Date;



public class CachedItem {
    private String URL;
    private Date lastModified;
    private Date lastTimeRequested;

    /**
     * Parses URL into the filename that is used for storage of the CachedItem
     * @return The filename the cached item is stored under
     */
    public static String parseURLToFileName(String URL) {
    	
    	//TODO consider no file type.
    	
    	URL = URL.replace("http://", "");
    	URL = URL.replace("www.", "");   	
    	
    	String FORWARD_SLASH_DELIM = "\\/";
    	String[] URLParts = URL.split(FORWARD_SLASH_DELIM,2);
    	 	

    	
    	String[] forwardSlashParts = URLParts[1].split(FORWARD_SLASH_DELIM);
    	
    	String PERIOD_DELIM = "\\.";
    	String[] domainParts = URLParts[0].split(PERIOD_DELIM);   	
    	
    	String URLToFileName = "";
    	
    	for(int i = domainParts.length - 1; i != -1; i--) {
    		URLToFileName = URLToFileName + "/" + domainParts[i];
    	}
    	
    	for(int j = 0; j < forwardSlashParts.length; j++) {
    		URLToFileName = URLToFileName + "/" + forwardSlashParts[j];
    	}
    	
        return URLToFileName;
    }

}
