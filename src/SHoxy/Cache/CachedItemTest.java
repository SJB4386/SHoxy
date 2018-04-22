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

	
	@Test
	public void noForwardSlash() {
		String testURL = "http://www.truman.edu";
		String expectedFilePath = "/edu/truman/";
		
		assertEquals(expectedFilePath, CachedItem.parseURLToFileName(testURL));
	}


}
