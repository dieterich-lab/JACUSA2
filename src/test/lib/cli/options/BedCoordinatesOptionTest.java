package test.lib.cli.options;

import java.io.FileNotFoundException;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import lib.cli.options.AbstractACOption;
import lib.cli.options.BedCoordinatesOption;

/**
 * Tests @see lib.cli.options.BedCoordinatesOption#process(org.apache.commons.cli.CommandLine)
 */
class BedCoordinatesOptionTest 
extends AbstractGeneralParameterProvider
implements ACOptionTest<String> {
	
	// TODO add tests that this is a BED file
	@Test
	void testProcessFails() throws Exception {
		final String fullPath = PATH + "wrong.txt";
		myAssertOptThrows(FileNotFoundException.class, fullPath);
	}
	
	@Override
	public Stream<Arguments> testProcess() {
		return Stream.of(
				createArguments("BedCoordinatesOptionTest_File1.txt"),
				createArguments("BedCoordinatesOptionTest_File2.txt") );
	}
	
	Arguments createArguments(final String fileName) {
		final String fullPath = PATH + "/" + fileName;
		return Arguments.of(
				createOptLine(fullPath),
				fullPath);
	}
	
	@Override
	public AbstractACOption createTestInstance() {
		return new BedCoordinatesOption(getGeneralParamter()); 
	}
	
	@Override
	public String getActualValue() {
		return getGeneralParamter().getInputBedFilename();
	}
	
}
