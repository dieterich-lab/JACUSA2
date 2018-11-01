package test.lib.cli.options;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import lib.cli.options.AbstractACOption;
import lib.cli.options.WindowSizeOption;
import lib.cli.parameter.AbstractParameter;
import test.utlis.CLIUtils;

@DisplayName("Test CLI processing of WindowSizeOption")
class WindowSizeOptionTest extends AbstractACOptionTest<Integer> {
	
	/*
	 * Tests
	 */
	
	@DisplayName("Check WindowSizeOption is parsed correctly")
	@ParameterizedTest(name = "Parse line and expect activeWindowSize to be: {0}")
	@ValueSource(strings = { "100", "200", "1000" } )
	@Override
	void testProcess(Integer expected) throws Exception {
		super.testProcess(expected);
	}

	@Test
	@DisplayName("Check WindowSizeOption fails on wrong input")
	void testProcessFail() throws Exception {
		// < 1
		getParserWrapper().myAssertThrows(IllegalArgumentException.class, getACOption(), Integer.toString(0));
		// not a number
		getParserWrapper().myAssertThrows(IllegalArgumentException.class, getACOption(), "wrong");
	}
	
	/*
	 * Helper
	 */

	@Override
	protected String createLine(Integer v) {
		return CLIUtils.assignValue(getOption(), Integer.toString(v));
	}
	
	@Override
	protected Integer getActualValue(AbstractParameter parameter) {
		return parameter.getActiveWindowSize();
	}
	
	@Override
	protected AbstractACOption create(AbstractParameter parameter) {
		return new WindowSizeOption(parameter);
	}
	
}
