package test.jacusa.io.format;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public abstract class ResultWriterTestUtils {

	public static void assertEqualFiles(final File expectedFile, final File actualFile) throws IOException {
		assertEqualFiles(expectedFile, actualFile, 0);
	}
	public static void assertEqualFiles(final File expectedFile, final File actualFile, final int ignoreLines) throws IOException {
		assertTrue(expectedFile.exists() && actualFile.exists(), expectedFile + " or " + actualFile + " does not exist!");
		// due to call line in actual, files have different size 
		// assertTrue(expected.length() == actual.length(), "Size of files is different: " + expected.length() + " vs. " + actual.length());
		
		BufferedReader expectedBr = new BufferedReader(new FileReader(expectedFile));
		BufferedReader actualBr = new BufferedReader(new FileReader(actualFile));
		int tmpLines = 0;
		while (tmpLines < ignoreLines) {
			actualBr.readLine();
			++tmpLines;
		}

		int lineNumber = 1;
		while (true) {
			final String expectedLine = expectedBr.readLine();
			final String actualLine = actualBr.readLine();
			// end of file
			if (expectedLine == null && expectedLine == actualLine) {
				expectedBr.close();
				expectedBr = null;
				actualBr.close();
				actualBr = null;
				return;
			}
			// files different -> close
			final boolean result = expectedLine.equals(actualLine);
			if (! result) {
				expectedBr.close();
				expectedBr = null;
				actualBr.close();
				actualBr = null;
			}
			assertTrue(result, "On line " + lineNumber + " files are different: " + expectedLine + " vs. " + actualLine);
		}
	}

}
