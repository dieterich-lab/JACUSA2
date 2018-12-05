package test.lib.cli.options;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import lib.cli.options.AbstractACOption;
import lib.cli.options.MaxThreadOption;
import lib.cli.parameter.GeneralParameter;
import test.utlis.CLIUtils;

@DisplayName("Test CLI processing of MaxThreadOption")
class MaxThreadOptionTest extends AbstractACOptionTest<Integer> {

	/*
	 * Tests
	 */
	
	@DisplayName("Check MaxThreadOption are parsed correctly")
	@ParameterizedTest(name = "Parse line: {0} and expect maxThreads to be: {1}")
	@ValueSource(strings = { "1", "2", "5" } )
	void testProcess(Integer expected) throws Exception {
		super.testProcess(expected);
	}

	@Test
	@DisplayName("Check MaxThreadOption fails on wrong input")
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
	protected AbstractACOption create(GeneralParameter parameter) {
		return new MaxThreadOption(parameter);
	}
	
	@Override
	protected Integer getActualValue(GeneralParameter parameter) {
		return parameter.getMaxThreads();
	}
	
	@Override
	protected String createLine(Integer v) {
		return CLIUtils.assignValue(getOption(), Integer.toString(v));
	}
	
}
