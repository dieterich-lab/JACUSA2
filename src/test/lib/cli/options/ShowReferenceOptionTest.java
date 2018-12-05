package test.lib.cli.options;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import lib.cli.options.AbstractACOption;
import lib.cli.options.ShowReferenceOption;
import lib.cli.parameter.GeneralParameter;
import test.utlis.CLIUtils;

class ShowReferenceOptionTest extends AbstractACOptionTest<Boolean> {

	/*
	 * Tests
	 */
	
	@DisplayName("Check ShowReferenceOption are parsed correctly")
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
	protected AbstractACOption create(GeneralParameter parameter) {
		return new ShowReferenceOption(parameter);
	}
	
	@Override
	protected Boolean getActualValue(GeneralParameter parameter) {
		return parameter.showReferenceBase();
	}
	
	@Override
	protected String createLine(Boolean v) {
		if (v) {
			return CLIUtils.assignValue(getOption(), "");
		}
		return new String();
	}
	
}
