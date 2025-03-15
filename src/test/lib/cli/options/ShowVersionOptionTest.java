package test.lib.cli.options;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;

import jacusa.VersionInfo;
import lib.cli.options.AbstractOption;
import lib.cli.options.ShowVersionOption;
import lib.util.AbstractTool;

/**
 * Tests @see lib.cli.options.ShowVersionOption#process(org.apache.commons.cli.CommandLine)
 */
class ShowVersionOptionTest 
implements OptionTest<String> {

	private ByteArrayOutputStream myOut;
	@SuppressWarnings("unused")
	private AbstractTool tool; // used in Option via static method
	private static final String EXPECTED_VERSION = "X.Y.Z"; 
	
	@BeforeEach
	void beforeEach() {
		myOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(myOut));
		
		tool = new AbstractTool(
				"test", new VersionInfo("master", EXPECTED_VERSION, new String[] {}), 
				new String[] {}, 
				new ArrayList<>() ) {
			
			@Override
			protected String getEpilog() {
				return null;
			}
		};
	}

	@Override
	public Stream<Arguments> testProcess() {
		return Stream.of(
				Arguments.of(createOptLine(), EXPECTED_VERSION),
				Arguments.of("", "") );
				
	}
	
	@Override
	public AbstractOption createTestInstance() {
		return new ShowVersionOption();
	}
	
	@Override
	public String getActualValue() {
		return myOut.toString().replaceAll("\n", "");
	}
	
}
