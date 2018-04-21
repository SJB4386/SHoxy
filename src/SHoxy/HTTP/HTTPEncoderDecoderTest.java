package SHoxy.HTTP;

import static org.junit.Assert.*;

import org.junit.Test;

public class HTTPEncoderDecoderTest {

    @Test
    public void testDecodeRequest() {
        String rawHttpData = "GET /index.html HTTP/1.1\r\n" +
                                "Host: www.truman.edu\r\n" +
                                "Connection: keep-alive\r\n" +
                                "\r\n";
        byte[] rawInput = rawHttpData.getBytes();
        String expectedMethod = "GET";
        String expectedURI = "/index.html";
        String expectedVersion = "HTTP/1.1\r\n";
        String expectedToString = "GET /index.html HTTP/1.1\r\n" +
                                     "Host: www.truman.edu\r\n" +
                                     "Connection: keep-alive\r\n" +
                                     "\r\n";
        HTTPData actualOutput = HTTPEncoderDecoder.decodeMessage(rawInput);

        assertTrue(actualOutput.isRequest);
        assertFalse(actualOutput.isReply);
        assertEquals(expectedMethod, actualOutput.method);
        assertEquals(expectedURI, actualOutput.URI);
        assertEquals(expectedVersion, actualOutput.version);
        assertEquals(expectedToString, actualOutput.toString());
    }
    
    @Test
    public void testDecodeRequestTwo() {
        String rawHttpData = "GET / HTTP/1.1\r\n" +
                                "Host: www.truman.edu\r\n" +
                                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0\r\n" +
                                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n" +
                                "Accept-Language: en-US,en;q=0.5\r\n" +
                                "Accept-Encoding: gzip, deflate\r\n" +
                                "Cookie: gmp%5Fcid=D6PUX0TQNVKP8TS39JJG; _ga=GA1.2.348482433.1502902021; tc_ptidexpiry=1574293530791; tc_ptid=5zsV4KNZ2crh1skN4tqFzL\r\n" +
                                "Connection: keep-alive\r\n" +
                                "Upgrade-Insecure-Requests: 1\r\n" +
                                "\r\n";
        byte[] rawInput = rawHttpData.getBytes();
        String expectedMethod = "GET";
        String expectedURI = "/";
        String expectedVersion = "HTTP/1.1\r\n";
        String expectedToString = "GET / HTTP/1.1\r\n" +
                                     "Host: www.truman.edu\r\n" +
                                     "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0\r\n" +
                                     "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n" +
                                     "Accept-Language: en-US,en;q=0.5\r\n" +
                                     "Accept-Encoding: gzip, deflate\r\n" +
                                     "Cookie: gmp%5Fcid=D6PUX0TQNVKP8TS39JJG; _ga=GA1.2.348482433.1502902021; tc_ptidexpiry=1574293530791; tc_ptid=5zsV4KNZ2crh1skN4tqFzL\r\n" +
                                     "Connection: keep-alive\r\n" +
                                     "Upgrade-Insecure-Requests: 1\r\n" +
                                     "\r\n";
        HTTPData actualOutput = HTTPEncoderDecoder.decodeMessage(rawInput);

        assertTrue(actualOutput.isRequest);
        assertFalse(actualOutput.isReply);
        assertEquals(expectedMethod, actualOutput.method);
        assertEquals(expectedURI, actualOutput.URI);
        assertEquals(expectedVersion, actualOutput.version);
        assertEquals(expectedToString, actualOutput.toString());
    }

    @Test
    public void testDecodeReply() {
        String rawHttpData = "HTTP/1.1 403 Forbidden\r\n"
                + "Date: Fri, 20 Apr 2018 23:23:20 GMT\r\n"
                + "Server: Apache/2.4.10 (Raspbian)\r\n"
                + "Content-Length: 308\r\n"
                + "Keep-Alive: timeout=5, max=100\r\n"
                + "Connection: Keep-Alive\r\n"
                + "Content-Type: text/html; charset=iso-8859-1\r\n"
                + "\r\n"
                + "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\n"
                + "<html><head>\n"
                + "<title>403 Forbidden</title>\n"
                + "</head><body>\n"
                + "<h1>Forbidden</h1>\n"
                + "<p>You don't have permission to access /~will/helloworld.html\n"
                + "on this server.<br />\n"
                + "</p>\n"
                + "<hr>\n"
                + "<address>Apache/2.4.10 (Raspbian) Server at ryuunosuke Port 80</address>\n"
                + "</body></html>\n";
        byte[] rawInput = rawHttpData.getBytes();
        String expectedProtocol = "HTTP/1.1";
        String expectedStatusCode = "403 Forbidden\r\n";
        String expectedToString = "HTTP/1.1 403 Forbidden\r\n"
                + "Date: Fri, 20 Apr 2018 23:23:20 GMT\r\n"
                + "Server: Apache/2.4.10 (Raspbian)\r\n"
                + "Content-Length: 308\r\n"
                + "Keep-Alive: timeout=5, max=100\r\n"
                + "Connection: Keep-Alive\r\n"
                + "Content-Type: text/html; charset=iso-8859-1\r\n"
                + "\r\n"
                + "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\n"
                + "<html><head>\n"
                + "<title>403 Forbidden</title>\n"
                + "</head><body>\n"
                + "<h1>Forbidden</h1>\n"
                + "<p>You don't have permission to access /~will/helloworld.html\n"
                + "on this server.<br />\n"
                + "</p>\n"
                + "<hr>\n"
                + "<address>Apache/2.4.10 (Raspbian) Server at ryuunosuke Port 80</address>\n"
                + "</body></html>\n";
        HTTPData actualOutput = HTTPEncoderDecoder.decodeMessage(rawInput);

        assertFalse(actualOutput.isRequest);
        assertTrue(actualOutput.isReply);
        assertEquals(expectedProtocol, actualOutput.protocol);
        assertEquals(expectedStatusCode, actualOutput.statusCode);
        assertEquals(expectedToString, actualOutput.toString());
    }
}
