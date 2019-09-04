package test.lib.cli.options;

import java.nio.file.FileAlreadyExistsException;
import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import lib.cli.options.AbstractACOption;
import lib.cli.options.ResultFileOption;

/**
 * Tests @see lib.cli.options.ResultFileOption#process(org.apache.commons.cli.CommandLine)
 */
class ResultFileOptionTest 
extends AbstractGeneralParameterProvider
implements ACOptionTest<String> {

	@Override
	public Stream<Arguments> testProcess() {
		return Arrays.asList("wrong.out").stream()
				.map(s -> createArguments(s));
	}
	
	Arguments createArguments(final String fileName) {
		final String fullPath = PATH + fileName;
		return Arguments.of(
				createOptLine(fullPath),
				fullPath);
	}
	
	@Disabled
	@Test
	void testProcessFails() throws Exception {
		final String fullPath = PATH + "ResultFileOptionTest_File1.out";
		myAssertOptThrows(FileAlreadyExistsException.class, fullPath);
	}
	
	@Override
	public AbstractACOption createTestInstance() {
		return new ResultFileOption(getGeneralParamter());
	}
	
	@Override
	public String getActualValue() {
		return getGeneralParamter().getResultFilename();
	}
	
}
