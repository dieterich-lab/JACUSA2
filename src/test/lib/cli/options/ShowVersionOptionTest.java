package test.lib.cli.options;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import lib.cli.options.AbstractACOption;
import lib.cli.options.ShowVersionOption;
import lib.cli.parameter.AbstractParameter;
import lib.util.AbstractTool;

// FIXME
@DisplayName("Test CLI processing of ShowVersionOption")
class ShowVersionOptionTest extends AbstractACOptionTest<String> {

	private ByteArrayOutputStream myOut;
	private AbstractTool tool;
	private static final String EXPECTED_VERSION = "X.Y.Z"; 
	
	@BeforeEach
	public void beforeEach() {
		super.beforeEach();
		
		myOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(myOut));
		
		tool = new AbstractTool("test", EXPECTED_VERSION, new String[] {}, new ArrayList<>() ) {
			
			@Override
			protected String getEpilog() {
				return null;
			}
		};
	}
	
	/*
	 * Tests
	 */

	@DisplayName("Check ShowVersionOption are parsed correctly")
	@ParameterizedTest(name = "Option should be used: {0}")
	@ValueSource(strings = { "true", "false" })
	@Override
	void testProcess(String expected) throws Exception {
		super.testProcess(expected);
	}

	/*
	 * Helper
	 */
	
	protected AbstractTool getTool() {
		return tool;
	}
	
	@Override
	protected AbstractACOption create(AbstractParameter parameter) {
		return new ShowVersionOption();
	}
	
	@Override
	protected String getActualValue(AbstractParameter parameter) {
		return myOut.toString().replaceAll("\n", "");
	}
	
	@Override
	protected String createLine(String v) {
		return v; // FIXME
	}
	
}
