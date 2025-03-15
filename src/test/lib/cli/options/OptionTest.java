package test.lib.cli.options;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.stream.Stream;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import lib.cli.options.AbstractOption;

/**
 * Tests @see lib.cli.options.AbstractACOption
 */
@TestInstance(Lifecycle.PER_CLASS)
public interface OptionTest<T> {

	public static final String PATH = "src/test/lib/cli/options/";

	@ParameterizedTest(name = "Parse line: {0}")
	@MethodSource("testProcess")
	default void testProcess(String line, T expected) throws Exception {
		final CommandLineParser parser = new DefaultParser();
		final Options options = new Options();
		
		final AbstractOption testInstance = createTestInstance();
		options.addOption(testInstance.getOption(false));
		
		final CommandLine cmd = parser.parse(options, line.split(" "));
		for (final String tmp : Arrays.asList(testInstance.getOpt(), testInstance.getLongOpt())) {
			if (tmp == null) {
				continue;
			}
			
			if (cmd.hasOption(tmp)) {
				testInstance.process(cmd);
			}

			final T actual = getActualValue();
			assertEquals(expected, actual);
		}
	}
	
	default String getOpt() {
		return createTestInstance().getOpt();
	}
	
	default String createOptLine() {
		return createOptLine("");
	}
	
	default String createOptLine(final String value) {
		return createOptLine(getOpt(), value);
	}
	
	default String createOptLine(final String opt, final String value) {
		if (value.isEmpty()) {
			return " -" + opt;
		}
		return " -" + opt + "=" + value;
	}

	default String getLongOpt() {
		return createTestInstance().getLongOpt();
	}
	
	default String createLongOptLine() {
		return createOptLine("");
	}
	
	default String createLongOptLine(final String value) {
		return createLongOptLine(getLongOpt(), value);
	}
	
	default String createLongOptLine(final String longOpt, final String value) {
		if (value.isEmpty()) {
			return " --" + longOpt;
		}
		return " --" + longOpt + "=" + value;
	}
	
	default <E extends Throwable> void myAssertThrows(
			final Class<E> expectedType, 
			final String line) throws Exception{
		
		final AbstractOption testInstance	= createTestInstance();
		final Options options				= new Options();
		options.addOption(testInstance.getOption(false));
		final CommandLineParser parser		= new DefaultParser();
		Executable executable 				= () -> {
			final CommandLine cmd = parser.parse(options, line.split(" "));
			testInstance.process(cmd); 
			};
		assertThrows(expectedType, executable);
	}
	
	default <E extends Throwable> void myAssertOptThrows(
			final Class<E> expectedType, 
			final String value) throws Exception{

		final String line = createOptLine(value);
		myAssertThrows(expectedType, line);
	}
	
	default <E extends Throwable> void myAssertLongOptThrows(
			final Class<E> expectedType, 
			final String value) throws Exception{
		
		final String line = createLongOptLine(value);
		myAssertThrows(expectedType, line);
	}
	
	abstract Stream<Arguments> testProcess();
	
	abstract AbstractOption createTestInstance();
	abstract T getActualValue();
	
}
