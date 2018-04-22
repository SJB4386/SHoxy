package SHoxy.Proxy;

import static org.junit.Assert.*;

import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;

import SHoxy.Cache.CachedItem;
import SHoxy.HTTP.HTTPData;

public class HTTPRequestHandlerTest {

    @Test
    public void testGet501Packet() {
        HTTPRequestHandler handler = new HTTPRequestHandler(null, null, null);
        String raw501Packet = "HTTP/1.1 501 Not Implemented\r\n"
                + "Content-Length: 125\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "<title>501 Not Implemented</title>\n"
                + "</head>\n"
                + "<body>\n"
                + "<h1>501 Not Implemented</h1>\n"
                + "</body>\n"
                + "</html>\n";
        byte[] expected501Packet = raw501Packet.getBytes();
        byte[] actual501Packet = handler.get501Packet();
        assertEquals(new String(expected501Packet), new String(actual501Packet));
    }

    @Test
    public void testForwardGETRequest() {
        HTTPRequestHandler handler = new HTTPRequestHandler(null, new ConcurrentHashMap<String, CachedItem>(), "cache");
        HTTPData testHTTP = handler.forwardGETRequest("http://truman.edu");

        assertTrue(testHTTP != null);
    }

    @Test
    public void testForwardGETRequestNoHttp() {
        HTTPRequestHandler handler = new HTTPRequestHandler(null, new ConcurrentHashMap<String, CachedItem>(), "cache");
        HTTPData testHTTP = handler.forwardGETRequest("truman.edu");

        assertTrue(testHTTP != null);
    }
}
