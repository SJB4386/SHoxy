package SHoxy.Cache;

import java.util.Date;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class CachedItem {
    public String URL;
    public Date lastModified;
    public Date lastTimeRequested;
    
    private final ReentrantLock lock = new ReentrantLock();

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
    	 	

    	
    	ArrayList <String> forwardSlashParts = new ArrayList();
    			
    	try {
    		for (String URLPart: URLParts[1].split(FORWARD_SLASH_DELIM)) {
    			forwardSlashParts.add(URLPart);
    		}
    	}
    	catch(ArrayIndexOutOfBoundsException e){
    		forwardSlashParts.add("");
    	}

    	
    	String PERIOD_DELIM = "\\.";
    	String[] domainParts = URLParts[0].split(PERIOD_DELIM);   	
    	
    	String URLToFileName = "";
    	
    	for(int i = domainParts.length - 1; i != -1; i--) {
    		URLToFileName = URLToFileName + "/" + domainParts[i];
    	}

    	for(int j = 0; j < forwardSlashParts.size(); j++) {
    		URLToFileName = URLToFileName + "/" + forwardSlashParts.get(j);
    	}
    	
        return URLToFileName;
    }
    
    public ReentrantLock getLock() {
        return lock;
    }

}
