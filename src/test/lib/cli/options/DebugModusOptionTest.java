package test.lib.cli.options;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import jacusa.JACUSA;
import jacusa.method.call.CallMethod;
import lib.cli.options.AbstractACOption;
import lib.cli.options.DebugModusOption;
import lib.cli.parameter.GeneralParameter;
import lib.method.AbstractMethod;
import lib.util.AbstractTool;
import test.utlis.CLIUtils;

@DisplayName("Test CLI processing of DebugModusOption")
class DebugModusOptionTest extends AbstractACOptionTest<Boolean> {

	@SuppressWarnings("unused")
	private AbstractTool tool;
	private AbstractMethod method;
	
	@BeforeEach
	public void beforeEach() {
		super.beforeEach();

		// FIXME
		tool = new JACUSA(new String[] {});
		method = new CallMethod.Factory().createMethod();
	}
	
	/*
	 * Tests
	 */
	@DisplayName("Test process DebugModusOption is parsed correctly")
	@ParameterizedTest(name = "Option should be used: {0}")
	@ValueSource(strings = { "true", "false" })
	void testProcess(Boolean expected) throws Exception {
		super.testProcess(expected);
	}
	
	/*
	 * Helper
	 */
	
	@Override
	protected AbstractACOption create(GeneralParameter parameter) {
		return new DebugModusOption(parameter, method);
	}
	
	@Override
	protected Boolean getActualValue(GeneralParameter parameter) {
		return parameter.isDebug();
	}
	
	@Override
	protected String createLine(Boolean v) {
		if (v) {
			return CLIUtils.assignValue(getOption(), "");
		}
		return new String();
	}

}
