package SHoxy.HTTP;

import static org.junit.Assert.*;

import org.junit.Test;

public class HTTPDataTest {

    @Test
    public void testConstructPacket() {
        HTTPData testData = new HTTPData();
        testData.isRequest = true;
        testData.method = "GET";
        testData.URI = "/index.html";
        testData.version = "HTTP/1.1\r\n";
        testData.headerLines.put("Host:", "www.truman.edu\r\n");
        testData.headerLines.put("Connection:", "keep-alive\r\n");

        String rawHttpData = "GET /index.html HTTP/1.1\r\n" +
                "Host: www.truman.edu\r\n" +
                "Connection: keep-alive\r\n" +
                "\r\n";
        byte[] expectedPacket = rawHttpData.getBytes();
        byte[] actualPacket = testData.constructPacket();
        assertEquals(new String(expectedPacket), new String(actualPacket));
    }
}
