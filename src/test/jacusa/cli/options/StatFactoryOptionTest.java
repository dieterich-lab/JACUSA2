package test.jacusa.cli.options;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Options;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jacusa.cli.options.StatFactoryOption;
import jacusa.cli.parameters.StatParameter;
import jacusa.method.rtarrest.CoverageStatisticFactory;
import jacusa.method.rtarrest.DummyStatisticFactory;
import lib.cli.parameter.GeneralParameter;
import lib.stat.AbstractStatFactory;
import lib.stat.betabin.LRTarrestStatFactory;
import lib.stat.dirmult.DirMultCompoundErrorStatFactory;
import lib.stat.dirmult.DirMultRobustCompoundErrorStatFactory;

/**
 * Tests @see {@link jacusa.cli.options.StatFactoryOption#process(org.apache.commons.cli.CommandLine)}
 */

@TestInstance(Lifecycle.PER_CLASS)
class StatFactoryOptionTest {

	private CommandLineParser parser;
	
	@BeforeEach
	void beforeEach() {
		parser = new DefaultParser();
	}
	
	@ParameterizedTest(name = "TODO")
	@MethodSource("testProcess")
	void testProcess(
			StatParameter statParameter,
			Map<String, AbstractStatFactory> factories,
			String[] args,
			AbstractStatFactory expected) throws Exception {

		final StatFactoryOption testInstance = 
				new StatFactoryOption(statParameter,factories);
		
		Options options = new Options();
		options.addOption(testInstance.getOption(false));

		final CommandLine line = parser.parse(options, args);
		testInstance.process(line);
		assertEquals(expected, statParameter.getFactory());
	}

	// test all statFactories with default values
	Stream<Arguments> testProcess() {
		final Map<String, AbstractStatFactory> factories = 
				Collections.unmodifiableMap(
						getFactories().stream()
							.collect(Collectors.toMap(AbstractStatFactory::getName, f -> f) ));
		
		return getFactories().stream()
			.map(f -> Arguments.of(
					new StatParameter(f, Double.NaN), 
					factories,
					new String[] { "-" + StatFactoryOption.OPT, f.getName() },
					f) );
	}
	
	@Test
	void testProcessFailsOnMissingStatFactory() {
		// Double.Nan -> ignore threshold!
		final StatFactoryOption testInstance = 
				new StatFactoryOption(
						new StatParameter(new DummyStatisticFactory(new GeneralParameter(0)), Double.NaN),  
						new HashMap<String, AbstractStatFactory>());
		
		Options options = new Options();
		options.addOption(testInstance.getOption(false));

		assertThrows(MissingArgumentException.class,
				() -> {
					final CommandLine line = parser.parse(
							options, 
							new String[] { "-" + StatFactoryOption.OPT } );
					testInstance.process(line);
				});
	}

	// should correspond to all implemented StatFactories
	public static List<AbstractStatFactory> getFactories() {
		final GeneralParameter parameters = new GeneralParameter(0);
		return Arrays.asList(
				new CoverageStatisticFactory(parameters),
				new DirMultCompoundErrorStatFactory(parameters),
				new DirMultRobustCompoundErrorStatFactory(parameters),
				new DummyStatisticFactory(parameters),
				new LRTarrestStatFactory(parameters) );
	}
	
}
