package SHoxy.Cache;

import static org.junit.Assert.*;
import org.junit.Test;

public class CachedItemTest {

	@Test
	public void testParseURLToFileName() {
		String testURL = "http://www.borax.truman.edu/docs/index.html";
		String expectedFilePath = "/edu/truman/borax/docs/index.html";
		
		assertEquals(expectedFilePath, CachedItem.parseURLToFileName(testURL));
	}
}
