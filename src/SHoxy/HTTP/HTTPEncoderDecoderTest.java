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
        String expectedToString = "GET /index.html HTTP/1.1\r\n" +
                                     "Host: www.truman.edu\r\n" +
                                     "Connection: keep-alive\r\n";
        HTTPData actualOutput = HTTPEncoderDecoder.decodeRequest(rawInput);

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
        String expectedToString = "GET / HTTP/1.1\r\n" +
                                     "Host: www.truman.edu\r\n" +
                                     "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0\r\n" +
                                     "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n" +
                                     "Accept-Language: en-US,en;q=0.5\r\n" +
                                     "Accept-Encoding: gzip, deflate\r\n" +
                                     "Cookie: gmp%5Fcid=D6PUX0TQNVKP8TS39JJG; _ga=GA1.2.348482433.1502902021; tc_ptidexpiry=1574293530791; tc_ptid=5zsV4KNZ2crh1skN4tqFzL\r\n" +
                                     "Connection: keep-alive\r\n" +
                                     "Upgrade-Insecure-Requests: 1\r\n";
        HTTPData actualOutput = HTTPEncoderDecoder.decodeRequest(rawInput);

        assertEquals(expectedToString, actualOutput.toString());
    }
}
