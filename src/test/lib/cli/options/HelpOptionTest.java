package test.lib.cli.options;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import lib.cli.CLI;
import lib.cli.options.AbstractACOption;
import lib.cli.options.HelpOption;
import lib.cli.parameter.AbstractParameter;
import test.utlis.CLIUtils;

@DisplayName("Test CLI processing of HelpOptionOption")
class HelpOptionTest extends AbstractACOptionTest<Boolean> {

	private CLI cli;
	
	@BeforeEach
	public void beforeEach() {
		cli = new CLI(new ArrayList<>());
		super.beforeEach();
	}

	/*
	 * Tests
	 */
	
	@DisplayName("Check HelpOption are parsed correctly")
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
		return new HelpOption(cli);
	}

	@Override
	protected Boolean getActualValue(AbstractParameter parameter) {
		return cli.printExtendedHelp();
	}
	
	@Override
	protected String createLine(Boolean v) {
		if (v) {
			return CLIUtils.assignValue(getOption(), "");
		}
		return new String();
	}
	
}
