package test.lib.cli.options;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import lib.cli.options.AbstractOption;
import lib.cli.options.ReferenceFastaFilenameOption;

// TODO add test file in fasta format
/**
 * Tests @see lib.cli.options.ReferenceFastaFilenameOption#process(org.apache.commons.cli.CommandLine)
 */
class ReferenceFastaFilenameOptionTest 
extends AbstractGeneralParameterProvider 
implements OptionTest<String> {

	@Override
	public Stream<Arguments> testProcess() {
		return Arrays.asList("ReferenceFastaFilenameOptionTest_File1.fasta").stream()
				.map(s -> createArguments(s));
	}
	
	Arguments createArguments(final String fileName) {
		final String fullPath = PATH + fileName;
		return Arguments.of(
				createOptLine(fullPath),
				fullPath);
	}
	
	@Test
	@DisplayName("Check ReferenceFastaFilenameOption fails on wrong input")
	void testProcessFail() throws Exception {
		final String fullPath = PATH + "wrong.fasta";
		myAssertOptThrows(FileNotFoundException.class, fullPath);
	}
	
	@Override
	public AbstractOption createTestInstance() {
		return new ReferenceFastaFilenameOption(getGeneralParamter());
	}

	@Override
	public String getActualValue() {
		return getGeneralParamter().getReferenceFilename();
	}
	
}
