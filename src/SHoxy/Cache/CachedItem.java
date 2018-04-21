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
    public String parseURLtoFileName(String URL) {
    	
    	// https://www.tutorialspoint.com/java/util/scanner_delimiter.htm
    	
    	URL.rep
    	
    	String final FORWARD_SLASH_DELIM = "\\/";
    	ArrayList <String> URLParts = new ArrayList<String>();
    	URLParts = URL.split(FORWARD_SLASH_DELIM, 2);
    	
    	
    	
    	String final PERIOD_DELIM = "\\.";
    	
    	
    	URLParts = URL.split(PERIOD_DELIM);
    	

    	
    	String URLToFileName = "";.
        return URLToFileName;
    }

}
