package test.lib.cli.options.filter;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import jacusa.io.FileType;
import lib.cli.options.AbstractProcessingOption;
import lib.cli.options.filter.FileTypeOption;
import lib.cli.options.filter.has.HasFileType;
import test.lib.cli.options.OptionTest;

/**
 * Tests @see lib.cli.options.filter.FileTypeOption#process(org.apache.commons.cli.CommandLine)
 */
public class FileTypeOptionTest implements OptionTest<FileType> {

	private DefaultHasFileType hasFileType;
	
	@BeforeEach
	void beforeEach() {
		hasFileType = new DefaultHasFileType(null);
	}
	
	@Test
	void testProcessFails() throws Exception {
		// unknown file type
		myAssertLongOptThrows(IllegalArgumentException.class, "wrong");
	}
	
	@Override
	public Stream<Arguments> testProcess() {
		return Arrays.asList(FileType.values()).stream()
				.map(f -> createArguments(f));
	}
	
	Arguments createArguments(final FileType fileType) {
		return Arguments.of(
				createLongOptLine(fileType.toString()),
				fileType);
	}

	@Override
	public AbstractProcessingOption createTestInstance() {
		return new FileTypeOption(hasFileType);
	}
	
	@Override
	public FileType getActualValue() {
		return hasFileType.getFileType();
	}
	
	private class DefaultHasFileType implements HasFileType {
		
		private FileType fileType;
		
		public DefaultHasFileType(final FileType fileType) {
			this.fileType = fileType;
		}
		
		@Override
		public FileType getFileType() {
			return fileType;
		}
		
		@Override
		public void setFileType(FileType fileType) {
			this.fileType = fileType;
		}
	}
	
}
