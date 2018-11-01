package test.lib.cli.options;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import lib.cli.options.AbstractACOption;
import lib.cli.options.FilterModusOption;
import lib.cli.parameter.AbstractParameter;
import test.utlis.CLIUtils;

@DisplayName("Test CLI processing of FilterModusOption")
class FilterModusOptionTest extends AbstractACOptionTest<Boolean> {

	/*
	 * Tests
	 */
	
	@DisplayName("Check FilterModusOption are parsed correctly")
	@ParameterizedTest(name = "Option should be used: {0}")
	@ValueSource(strings = { "true", "false" })
	@Override
	void testProcess(Boolean expected) throws Exception {
		super.testProcess(expected);
	}
	
	/*
	 * Helper
	 */

	@Override
	protected AbstractACOption create(AbstractParameter parameter) {
		return new FilterModusOption(parameter);
	}
	
	@Override
	protected Boolean getActualValue(AbstractParameter parameter) {
		return parameter.splitFiltered();
	}
	
	@Override
	protected String createLine(Boolean v) {
		if (v) {
			return CLIUtils.assignValue(getOption(), "");
		}
		return new String();
	}
	
}
