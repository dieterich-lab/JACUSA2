package test.lib.cli.options;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import lib.cli.options.AbstractACOption;
import lib.cli.options.BedCoordinatesOption;
import lib.cli.parameter.AbstractParameter;

class BedCoordinatesOptionTest extends AbstractACOptionTest<String> {
	
	/*
	 * Test
	 */

	@DisplayName("Check bedInputFilename are parsed correctly")
	@ParameterizedTest(name = "Parse line: {0} and expect maxThreads to be: {1}")
	@ValueSource(strings = { "BedCoordinatesOption.txt" } )
	void testProcess(String expected) throws Exception {
		expected = PATH + expected;
		super.testProcess(expected);
	}
	
	@Test
	void testProcessFails() throws Exception {
		final String expected = PATH + "missingBedCoordinatesOptionTest.txt";
		getParserWrapper().myAssertThrows(FileNotFoundException.class, getACOption(), expected);
	}

	/*
	 * Helper
	 */

	@Override
	protected AbstractACOption create(AbstractParameter parameter) {
		return new BedCoordinatesOption(parameter);
	}
	
	@Override
	protected String getActualValue(AbstractParameter parameter) {
		return parameter.getInputBedFilename();
	}
	
	@Override
	protected String createLine(String v) {
		return v;
	}
	
}
