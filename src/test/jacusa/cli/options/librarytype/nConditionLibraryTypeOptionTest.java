package test.jacusa.cli.options.librarytype;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Options;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jacusa.cli.options.librarytype.AbstractLibraryTypeOption;
import jacusa.cli.options.librarytype.nConditionLibraryTypeOption;
import lib.cli.options.AbstractACOption;
import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.data.has.LibraryType;
import test.utlis.CLIUtils;

/**
 * Tests @see jacusa.cli.options.librarytype.nConditionLibraryTypeOption#process(org.apache.commons.cli.CommandLine)
 */
class nConditionLibraryTypeOptionTest {

	private CommandLineParser parser;

	@BeforeEach
	void beforeEach() {
		parser = new DefaultParser();
	}
	
	@ParameterizedTest(name = "args: {3}")
	@MethodSource("testProcess")
	void testProcess(
			List<AbstractLibraryTypeOption> testInstances,
			List<ConditionParameter> conditionParameters,
			String[] args,
			List<LibraryType> expected) throws Exception {

		final Options options = getOptions(testInstances);
		final CommandLine line = parser.parse(options, args);
		for (AbstractLibraryTypeOption testInstance : testInstances) {
			if (line.hasOption(testInstance.getOpt())) {
				testInstance.process(line);
			}
		}
		final List<LibraryType> actual = conditionParameters.stream()
				.map(c -> c.getLibraryType())
				.collect(Collectors.toList());
		assertEquals(expected, actual);
	}
	
	static Stream<Arguments> testProcess() {
		return Stream.of(
				testProcess(1, new ArrayList<LibraryType>(Collections.nCopies(1, null))),
				testProcess(1, Arrays.asList(LibraryType.UNSTRANDED)),
				testProcess(1, Arrays.asList(LibraryType.RF_FIRSTSTRAND)),
				testProcess(1, Arrays.asList(LibraryType.FR_SECONDSTRAND)),
				
				testProcess(2, new ArrayList<LibraryType>(Collections.nCopies(2, null))),
				testProcess(2, Arrays.asList(LibraryType.UNSTRANDED, LibraryType.UNSTRANDED)),
				testProcess(2, Arrays.asList(LibraryType.RF_FIRSTSTRAND, LibraryType.RF_FIRSTSTRAND)),
				testProcess(2, Arrays.asList(LibraryType.FR_SECONDSTRAND, LibraryType.FR_SECONDSTRAND)),
				
				testProcess(2, Arrays.asList(LibraryType.UNSTRANDED, null)),
				testProcess(2, Arrays.asList(LibraryType.RF_FIRSTSTRAND, null)),
				testProcess(2, Arrays.asList(LibraryType.FR_SECONDSTRAND, null)),
				
				testProcess(2, Arrays.asList(null, LibraryType.UNSTRANDED)),
				testProcess(2, Arrays.asList(null, LibraryType.RF_FIRSTSTRAND)),
				testProcess(2, Arrays.asList(null, LibraryType.FR_SECONDSTRAND)) );
	}

	static Arguments testProcess(final int conditions, final List<LibraryType> libraryTypes) {
		assert(conditions == libraryTypes.size());
		
		final GeneralParameter generalParameter = new GeneralParameter(conditions);
		
		final List<ConditionParameter> conditionParameters = 
				new ArrayList<ConditionParameter>(conditions);
		final List<AbstractLibraryTypeOption> testInstances = 
				new ArrayList<AbstractLibraryTypeOption>(conditions);
		final StringBuilder sb = new StringBuilder(); 
		final List<LibraryType> expected = new ArrayList<>(conditions);
		
		for (int conditionIndex = 0; conditionIndex < conditions; ++conditionIndex) {
			final ConditionParameter conditionParameter = new ConditionParameter(conditionIndex);
			conditionParameters.add(conditionParameter);
			testInstances.add(
					new nConditionLibraryTypeOption(conditionParameter, generalParameter));
			testInstances.add(new nConditionLibraryTypeOption(conditionParameters, generalParameter));
			if (libraryTypes.get(conditionIndex) == null) {
				expected.add(conditionParameter.getLibraryType());
			} else {
				expected.add(libraryTypes.get(conditionIndex));
				if (sb.length() > 0) {
					sb.append(' ');
				}
				sb.append('-');
				sb.append(AbstractLibraryTypeOption.OPT);
				sb.append((conditionIndex + 1));
				sb.append(' ');
				sb.append(libraryTypes.get(conditionIndex));
			}
		}
		
		return Arguments.of(
				testInstances,
				conditionParameters,
				sb.toString().split(" "),
				expected);
	}
	
	@Test
	void testProcessFailsOnMissingInput1() {
		final GeneralParameter generalParameter = new GeneralParameter(1);
		
		final AbstractLibraryTypeOption testInstance = 
				new nConditionLibraryTypeOption(
						Arrays.asList(new ConditionParameter(0)), 
						generalParameter);
		Options options = CLIUtils.getOptions(testInstance);
		
		assertThrows(MissingArgumentException.class,
				() -> {
					final CommandLine line = parser.parse(
							options, 
							new String[] { "-" + AbstractLibraryTypeOption.OPT } );
					testInstance.process(line);
				});
	}
	
	@Test
	void testProcessFailsOnMissingInput2() {
		final GeneralParameter generalParameter = new GeneralParameter(1);
		
		final AbstractLibraryTypeOption testInstance = 
				new nConditionLibraryTypeOption(new ConditionParameter(0), generalParameter);
		Options options = CLIUtils.getOptions(testInstance);
		
		assertThrows(MissingArgumentException.class,
				() -> {
					final CommandLine line = parser.parse(
							options, 
							new String[] { "-" + AbstractLibraryTypeOption.OPT + 1} );
					testInstance.process(line);
				});
	}
	
	@Test
	void testProcessFailsOnWrongInput1() {
		final GeneralParameter generalParameter = new GeneralParameter(1);
		
		final AbstractLibraryTypeOption testInstance = 
				new nConditionLibraryTypeOption(
						Arrays.asList(new ConditionParameter(0)), 
						generalParameter);
		Options options = CLIUtils.getOptions(testInstance);
		
		assertThrows(IllegalArgumentException.class,
				() -> {
					final CommandLine line = parser.parse(
							options, 
							new String[] { "-" + AbstractLibraryTypeOption.OPT, "WRONG"} );
					testInstance.process(line);
				});
	}
	
	@Test
	void testProcessFailsOnWrongInput2() {
		final GeneralParameter generalParameter = new GeneralParameter(1);
		
		final AbstractLibraryTypeOption testInstance = 
				new nConditionLibraryTypeOption(new ConditionParameter(0), generalParameter);
		Options options = CLIUtils.getOptions(testInstance);
		
		assertThrows(IllegalArgumentException.class,
				() -> {
					final CommandLine line = parser.parse(
							options, 
							new String[] { "-" + AbstractLibraryTypeOption.OPT + 1, "WRONG"} );
					testInstance.process(line);
				});
	}

	@Test
	void testProcessFailsOnWrongInvalid1() {
		final GeneralParameter generalParameter = new GeneralParameter(1);
		
		final AbstractLibraryTypeOption testInstance = 
				new nConditionLibraryTypeOption(
						Arrays.asList(new ConditionParameter(0)), 
						generalParameter);
		Options options = CLIUtils.getOptions(testInstance);
		
		assertThrows(IllegalArgumentException.class,
				() -> {
					final CommandLine line = parser.parse(
							options, 
							new String[] { 
									"-" + AbstractLibraryTypeOption.OPT, 
									LibraryType.MIXED.toString() } );
					testInstance.process(line);
				});
	}
	
	@Test
	void testProcessFailsOnWrongInvalid2() {
		final GeneralParameter generalParameter = new GeneralParameter(1);
		
		final AbstractLibraryTypeOption testInstance = 
				new nConditionLibraryTypeOption(new ConditionParameter(0), generalParameter);
		Options options = CLIUtils.getOptions(testInstance);
		
		assertThrows(IllegalArgumentException.class,
				() -> {
					final CommandLine line = parser.parse(
							options, 
							new String[] { 
									"-" + AbstractLibraryTypeOption.OPT + 1, 
									LibraryType.MIXED.toString() } );
					testInstance.process(line);
				});
	}
	
	private Options getOptions(final List<AbstractLibraryTypeOption> acOptions) {
		final Options options = new Options();
		for (final AbstractACOption acOption : acOptions) {
			options.addOption(acOption.getOption(false));
		}
		return options;
	}
	
}
