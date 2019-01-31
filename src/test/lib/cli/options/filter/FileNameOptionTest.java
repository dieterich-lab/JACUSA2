package test.lib.cli.options.filter;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import htsjdk.samtools.SAMException;
import lib.cli.options.AbstractACOption;
import lib.cli.options.filter.FileNameOption;
import lib.cli.options.filter.has.HasFileName;
import test.lib.cli.options.ACOptionTest;

/**
 * Tests @see lib.cli.options.filter.FileNameOption#process(org.apache.commons.cli.CommandLine)
 */
// TODO add test for codec
public class FileNameOptionTest 
implements ACOptionTest<String> {

	// TODO remove
	public static final String PATH = "src/test/jacusa/filter/factory/excludesitefilterfactory/";

	private HasFileName hasFileName;
	
	@BeforeEach
	void beforeEach() {
		hasFileName = new DefaultHasFileName(null);
	}
	
	@Test
	void testProcessFails() throws Exception {
		// file does not exist
		myAssertLongOptThrows(SAMException.class, PATH + "wrong.bed");
	}
	
	@Override
	public Stream<Arguments> testProcess() {
		return Arrays.asList("test.bed", "test.vcf", "test.vcf4").stream()
				.map(f -> createArguments(f));
	}
	
	Arguments createArguments(final String fileName) {
		final String fullPath = PATH + fileName;
		return Arguments.of(
				createLongOptLine(fullPath),
				fullPath);
	}

	@Override
	public AbstractACOption createTestInstance() {
		return new FileNameOption(hasFileName);
	}
	
	@Override
	public String getActualValue() {
		return hasFileName.getFileName();
	}
	
	private class DefaultHasFileName implements HasFileName {
		
		private String fileName;
		
		public DefaultHasFileName(final String fileName) {
			this.fileName = fileName;
		}
		@Override
		public String getFileName() {
			return fileName;
		}
		
		@Override
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		
	}
	
}
