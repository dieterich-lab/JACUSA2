package test.lib.cli.options;

import java.nio.file.FileAlreadyExistsException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import lib.cli.options.AbstractACOption;
import lib.cli.options.ResultFileOption;
import lib.cli.parameter.AbstractParameter;
import test.utlis.CLIUtils;

class ResultFileOptionTest extends AbstractACOptionTest<String> {

	/*
	 * Test
	 */
	
	@DisplayName("Check ResultFileOptionTest are parsed correctly")
	@ParameterizedTest(name = "Result fileName: {0}")
	@ValueSource( strings = { "missingResultFileOptionTest.out" } )
	@Override
	void testProcess(String expected) throws Exception {
		expected = PATH + expected;
		super.testProcess(expected);
	}

	@Test
	void testProcessFails() throws Exception {
		final String value = PATH + "ResultFileOptionTest.out";
		getParserWrapper().myAssertThrows(FileAlreadyExistsException.class, getACOption(), value);
	}

	/*
	 * Helper
	 */

	protected AbstractACOption create(final AbstractParameter parameter) {
		return new ResultFileOption(parameter);
	}
	
	@Override
	protected String getActualValue(AbstractParameter parameter) {
		return parameter.getResultFilename();
	}
	
	@Override
	protected String createLine(String v) {
		return CLIUtils.assignValue(getOption(), v);
	}
	
}
