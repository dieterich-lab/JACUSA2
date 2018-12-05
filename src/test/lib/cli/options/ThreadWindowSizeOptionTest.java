package test.lib.cli.options;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import lib.cli.options.AbstractACOption;
import lib.cli.options.ThreadWindowSizeOption;
import lib.cli.parameter.GeneralParameter;
import test.utlis.CLIUtils;

@DisplayName("Test CLI processing of ThreadWindowSizeOption")
class ThreadWindowSizeOptionTest extends AbstractACOptionTest<Integer> {

	/*
	 * Tests
	 */
	
	@DisplayName("Check ThreadWindowSizeOption are parsed correctly")
	@ParameterizedTest(name = "Parse line and expect threadWindowSize to be: {0}")
	@ValueSource(strings = { "100", "200", "1000" } )
	@Override
	void testProcess(Integer expected) throws Exception {
		super.testProcess(expected);
	}

	@Test
	@DisplayName("Check ThreadWindowSizeOption fails on wrong input")
	void testProcessFail() throws Exception {
		// < ThreadWindowSizeOption.MIN_WINDOWS
		getParserWrapper().myAssertThrows(IllegalArgumentException.class, getACOption(), Integer.toString(ThreadWindowSizeOption.MIN_WINDOWS - 1));
		getParserWrapper().myAssertThrows(IllegalArgumentException.class, getACOption(), "wrong");
	}
	
	/*
	 * Helper
	 */

	@Override
	protected AbstractACOption create(GeneralParameter parameter) {
		return new ThreadWindowSizeOption(parameter);
	}
	
	@Override
	protected Integer getActualValue(GeneralParameter parameter) {
		return parameter.getReservedWindowSize();
	}
	
	@Override
	protected String createLine(Integer v) {
		return CLIUtils.assignValue(getOption(), Integer.toString(v));
	}
	
}
