package test.jacusa.cli.options.librarytype;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

import jacusa.cli.options.librarytype.nConditionLibraryTypeOption;
import lib.cli.options.AbstractACOption;
import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.util.LibraryType;
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
	
	@ParameterizedTest(name = "args: {2}")
	@MethodSource("testProcess")
	void testProcess(
			List<nConditionLibraryTypeOption> testInstances,
			List<ConditionParameter> conditionParameters,
			String line,
			List<LibraryType> expected) throws Exception {

		final Options options = getOptions(testInstances);
		final CommandLine cmd = parser.parse(options, line.split(" "));
		for (nConditionLibraryTypeOption testInstance : testInstances) {
			if (cmd.hasOption(testInstance.getOpt())) {
				testInstance.process(cmd);
			}
		}
		final List<LibraryType> actual = conditionParameters.stream()
				.map(c -> c.getLibraryType())
				.collect(Collectors.toList());
		assertEquals(expected, actual);
	}
	
	static Stream<Arguments> testProcess() {
		return Stream.of(
				createArgs(1, new ArrayList<LibraryType>(Collections.nCopies(1, null))),
				createArgs(1, Arrays.asList(LibraryType.UNSTRANDED)),
				createArgs(1, Arrays.asList(LibraryType.RF_FIRSTSTRAND)),
				createArgs(1, Arrays.asList(LibraryType.FR_SECONDSTRAND)),
				
				createArgs(2, new ArrayList<LibraryType>(Collections.nCopies(2, null))),
				createArgs(2, Arrays.asList(LibraryType.UNSTRANDED, LibraryType.UNSTRANDED)),
				createArgs(2, Arrays.asList(LibraryType.RF_FIRSTSTRAND, LibraryType.RF_FIRSTSTRAND)),
				createArgs(2, Arrays.asList(LibraryType.FR_SECONDSTRAND, LibraryType.FR_SECONDSTRAND)),
				
				createArgs(2, Arrays.asList(LibraryType.UNSTRANDED, null)),
				createArgs(2, Arrays.asList(LibraryType.RF_FIRSTSTRAND, null)),
				createArgs(2, Arrays.asList(LibraryType.FR_SECONDSTRAND, null)),
				
				createArgs(2, Arrays.asList(null, LibraryType.UNSTRANDED)),
				createArgs(2, Arrays.asList(null, LibraryType.RF_FIRSTSTRAND)),
				createArgs(2, Arrays.asList(null, LibraryType.FR_SECONDSTRAND)) );
	}

	static Arguments createArgs(final int conditions, final List<LibraryType> libraryTypes) {
		assert(conditions == libraryTypes.size());
		
		final GeneralParameter generalParameter = new GeneralParameter(conditions);
		
		final List<ConditionParameter> conditionParameters = 
				new ArrayList<ConditionParameter>(conditions);
		final List<nConditionLibraryTypeOption> testInstances = 
				new ArrayList<nConditionLibraryTypeOption>(conditions);
		final StringBuilder sb = new StringBuilder(); 
		final List<LibraryType> expected = new ArrayList<>(conditions);
		
		final Set<LibraryType> availableLibType = new HashSet<LibraryType>(
				Arrays.asList(
						LibraryType.UNSTRANDED, 
						LibraryType.RF_FIRSTSTRAND,
						LibraryType.FR_SECONDSTRAND));
		
		for (int conditionIndex = 1; conditionIndex <= conditions; ++conditionIndex) {
			final ConditionParameter conditionParameter = new ConditionParameter(conditionIndex);
			conditionParameters.add(conditionParameter);
			testInstances.add(new nConditionLibraryTypeOption(
					getAvailableLibType(), conditionParameter, generalParameter));
			testInstances.add(new nConditionLibraryTypeOption(availableLibType, conditionParameters, generalParameter));
			if (libraryTypes.get(conditionIndex - 1) == null) {
				expected.add(conditionParameter.getLibraryType());
			} else {
				expected.add(libraryTypes.get(conditionIndex - 1));
				if (sb.length() > 0) {
					sb.append(' ');
				}
				sb.append('-');
				sb.append(nConditionLibraryTypeOption.OPT);
				sb.append((conditionIndex));
				sb.append(' ');
				sb.append(libraryTypes.get(conditionIndex - 1));
			}
		}
		
		return Arguments.of(
				testInstances,
				conditionParameters,
				sb.toString(),
				expected);
	}
	
	static Set<LibraryType> getAvailableLibType() {
		return new HashSet<LibraryType>(
			Arrays.asList(
					LibraryType.UNSTRANDED, 
					LibraryType.RF_FIRSTSTRAND,
					LibraryType.FR_SECONDSTRAND));
	}
	
	@Test
	void testProcessFailsOnMissingInput1() {
		final GeneralParameter generalParameter = new GeneralParameter(1);
		
		final nConditionLibraryTypeOption testInstance = 
				new nConditionLibraryTypeOption(
						getAvailableLibType(),
						Arrays.asList(new ConditionParameter(1)), 
						generalParameter);
		Options options = CLIUtils.getOptions(testInstance);
		
		assertThrows(MissingArgumentException.class,
				() -> {
					final CommandLine line = parser.parse(
							options, 
							new String[] { "-" + nConditionLibraryTypeOption.OPT } );
					testInstance.process(line);
				});
	}
	
	@Test
	void testProcessFailsOnMissingInput2() {
		final GeneralParameter generalParameter = new GeneralParameter(1);
		
		final nConditionLibraryTypeOption testInstance = 
				new nConditionLibraryTypeOption(
						getAvailableLibType(), 
						new ConditionParameter(1), 
						generalParameter);
		Options options = CLIUtils.getOptions(testInstance);
		
		assertThrows(MissingArgumentException.class,
				() -> {
					final CommandLine line = parser.parse(
							options, 
							new String[] { "-" + nConditionLibraryTypeOption.OPT + 1} );
					testInstance.process(line);
				});
	}
	
	@Test
	void testProcessFailsOnWrongInput1() {
		final GeneralParameter generalParameter = new GeneralParameter(1);
		
		final nConditionLibraryTypeOption testInstance = 
				new nConditionLibraryTypeOption(
						getAvailableLibType(),
						Arrays.asList(new ConditionParameter(1)), 
						generalParameter);
		Options options = CLIUtils.getOptions(testInstance);
		
		assertThrows(IllegalArgumentException.class,
				() -> {
					final CommandLine line = parser.parse(
							options, 
							new String[] { "-" + nConditionLibraryTypeOption.OPT, "WRONG"} );
					testInstance.process(line);
				});
	}
	
	@Test
	void testProcessFailsOnWrongInput2() {
		final GeneralParameter generalParameter = new GeneralParameter(1);
		
		final nConditionLibraryTypeOption testInstance = 
				new nConditionLibraryTypeOption(
						getAvailableLibType(), 
						new ConditionParameter(1), 
						generalParameter);
		Options options = CLIUtils.getOptions(testInstance);
		
		assertThrows(IllegalArgumentException.class,
				() -> {
					final CommandLine line = parser.parse(
							options, 
							new String[] { "-" + nConditionLibraryTypeOption.OPT + 1, "WRONG"} );
					testInstance.process(line);
				});
	}

	@Test
	void testProcessFailsOnWrongInvalid1() {
		final GeneralParameter generalParameter = new GeneralParameter(1);
		
		final nConditionLibraryTypeOption testInstance = 
				new nConditionLibraryTypeOption(
						getAvailableLibType(),
						Arrays.asList(new ConditionParameter(1)), 
						generalParameter);
		Options options = CLIUtils.getOptions(testInstance);
		
		assertThrows(IllegalArgumentException.class,
				() -> {
					final CommandLine line = parser.parse(
							options, 
							new String[] { 
									"-" + nConditionLibraryTypeOption.OPT, 
									LibraryType.MIXED.toString() } );
					testInstance.process(line);
				});
	}
	
	@Test
	void testProcessFailsOnWrongInvalid2() {
		final GeneralParameter generalParameter = new GeneralParameter(1);
		
		final nConditionLibraryTypeOption testInstance = 
				new nConditionLibraryTypeOption(
						getAvailableLibType(),
						new ConditionParameter(1), 
						generalParameter);
		Options options = CLIUtils.getOptions(testInstance);
		
		assertThrows(IllegalArgumentException.class,
				() -> {
					final CommandLine line = parser.parse(
							options, 
							new String[] { 
									"-" + nConditionLibraryTypeOption.OPT + 1, 
									LibraryType.MIXED.toString() } );
					testInstance.process(line);
				});
	}
	
	private Options getOptions(final List<nConditionLibraryTypeOption> acOptions) {
		final Options options = new Options();
		for (final AbstractACOption acOption : acOptions) {
			options.addOption(acOption.getOption(false));
		}
		return options;
	}
	
}
