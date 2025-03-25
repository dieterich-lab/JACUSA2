package test.jacusa.cli.options;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import jacusa.cli.options.StatFilterOption;
import jacusa.cli.parameters.StatParameter;
import jacusa.method.rtarrest.DummyStatisticFactory;
import lib.cli.parameter.GeneralParameter;
import test.utlis.CLIUtils;

/**
 * Tests @see {@link jacusa.cli.options.StatFilterOption#process(org.apache.commons.cli.CommandLine)
 */

class StatFilterOptionTest {

	private static final double DEFAULT_VALUE = Double.NaN;
	
	private CommandLineParser parser;
	
	private StatParameter statParameter;
	private StatFilterOption testInstance;

	@BeforeEach
	void beforeEach() {
		parser = new DefaultParser();
		
		statParameter = new StatParameter(new DummyStatisticFactory(new GeneralParameter(0)), DEFAULT_VALUE);
		testInstance = new StatFilterOption(statParameter);
	}
	
	@ParameterizedTest(name = "Expected threshold {0}")
	@ValueSource(doubles = {0.0, 1.0})
	void testProcess(double expected) throws ParseException {
		final Options options = CLIUtils.getOptions(testInstance);
		final CommandLine line = parser.parse(
				options, 
				new String[] { "-" + StatFilterOption.OPT, Double.toString(expected) } );
		testInstance.process(line);
		final double actual = statParameter.getThreshold();
		assertEquals(expected, actual);
	}
	
	@Test
	void testProcessFailsOnMissingThreshold() {
		final Options options = CLIUtils.getOptions(testInstance);

		assertThrows(MissingArgumentException.class,
				() -> {
					final CommandLine line = parser.parse(
							options, 
							new String[] { "-" + StatFilterOption.OPT } );
					testInstance.process(line);
				});
	}

	@Test
	void testProcessFailsOnWrongFormat() {
		final Options options = CLIUtils.getOptions(testInstance);

		assertThrows(NumberFormatException.class,
				() -> {
					final CommandLine line = parser.parse(
							options, 
							new String[] { "-" + StatFilterOption.OPT, "WRONG" } );
					testInstance.process(line);
				});
	}
	
	@Test
	void testProcessFailsOnInvalidThreshold() {
		final Options options = CLIUtils.getOptions(testInstance);

		assertThrows(IllegalArgumentException.class,
				() -> {
					final CommandLine line = parser.parse(
							options, 
							new String[] { "-" + StatFilterOption.OPT, Double.toString(-0.1) } );
					testInstance.process(line);
				});
	}
	
}
