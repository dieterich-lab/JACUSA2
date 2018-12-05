package test.lib.cli.options;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.cli.Option;
import org.junit.jupiter.api.BeforeEach;

import jacusa.cli.parameters.CallParameter;
import lib.cli.options.AbstractACOption;
import lib.cli.parameter.GeneralParameter;

public abstract class AbstractACOptionTest<T> {

	public static final String PATH = "src/test/lib/cli/options/";
	public static final int CONDITION_SIZE = 2;
	
	private ParserWrapper parserWrapper;
	
	private GeneralParameter parameter;
	private AbstractACOption testInstance;
	
	@BeforeEach
	public void beforeEach() {
		parserWrapper = new ParserWrapper();

		parameter = new CallParameter(CONDITION_SIZE);
		testInstance = create(parameter);
	}

	/*
	 * Tests
	 */

	void testProcess(T expected) throws Exception {
		final String line = createLine(expected);
		parserWrapper.process(testInstance, line);

		// check
		final T actual = getActualValue(getParameter());
		assertEquals(expected, actual);
	}
	
	public GeneralParameter getParameter() {
		return parameter;
	}

	/*
	 * Abstract
	 */
	
	protected abstract AbstractACOption create(GeneralParameter parameter);
	protected abstract T getActualValue(GeneralParameter parameter);
	protected abstract String createLine(T v);
	
	protected AbstractACOption getTestInstance() {
		return testInstance;
	}
	
	protected AbstractACOption getACOption() {
		return testInstance;
	}

	protected Option getOption() {
		return testInstance.getOption(false);
	}
	
	protected ParserWrapper getParserWrapper() {
		return parserWrapper;
	}

}
