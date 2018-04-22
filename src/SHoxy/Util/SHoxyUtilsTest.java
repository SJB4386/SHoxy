package SHoxy.Util;

import org.junit.Test;

public class SHoxyUtilsTest {

    @Test
    public void testWriteFile() {
        byte[] data = "Hello, world!".getBytes();

        SHoxyUtils.writeFile(data, "test/hello.txt");
    }

}
