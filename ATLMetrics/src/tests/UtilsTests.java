package tests;

import static org.junit.jupiter.api.Assertions.*;
import ATLUtils.*;

import org.junit.jupiter.api.Test;

class UtilsTests {

	@Test
	void test() {
		String prefix="log";
		String filetype="log";
		String fileName= Utils.generateFileNamePostfix(prefix, filetype);
		assertEquals(3+3+2+12, fileName.length());
	}
}
