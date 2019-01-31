package test.jacusa.io;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import jacusa.io.FileType;

class FileTypeTest {

	private String PATH = "src/test/jacusa/io/";
	
	/**
	 * Tests jacusa.io.FileType#valueOfFileName(String)
	 */
	@ParameterizedTest(name = "Filename: {0}")
	@CsvSource(
			delimiter = '\t',
			value = {
					"test.bed	BED",
					"test.vcf	VCF",
					"test.vcf4	VCF",
			})
	void testValueOfFileName(String fileName, FileType expected) {
		final FileType actual = FileType.valueOfFileName(fileName);
		assertEquals(expected, actual);
	}
	
	/**
	 * Tests jacusa.io.FileType#valueOfFileName(String)
	 */
	@Test
	void testValueOfFileNameFails() {
		final FileType actual = FileType.valueOfFileName("test.out");
		assertNull(actual);
	}
	
	/**
	 * Tests jacusa.io.FileType#valueOfFileContent(String)
	 */
	@ParameterizedTest(name = "Filename: {0}")
	@CsvSource(
			delimiter = '\t',
			value = {
					"FileTypeTest_File1.bed	BED",
					"FileTypeTest_File2.vcf	VCF",
					"FileTypeTest_File3.vcf4	VCF",
			})
	void testValueOfFileContent(String fileName, FileType expected) {
		final FileType actual = FileType.valueOfFileContent(PATH + fileName);
		assertEquals(expected, actual);
	}	
	
}
