package SHoxy.Util;

import static org.junit.Assert.*;

import org.junit.Test;

public class SHoxyUtilsTest {

    @Test
    public void testWriteFile() {
        String url = "truman.edu";
        byte[] data = "Hello, world!".getBytes();

        SHoxyUtils.writeFile(data, url, "test");
    }

}
