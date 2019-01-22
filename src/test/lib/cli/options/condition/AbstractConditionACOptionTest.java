package test.lib.cli.options.condition;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;

import lib.cli.options.condition.AbstractConditionACOption;
import lib.cli.parameter.ConditionParameter;
import test.lib.cli.options.ParserWrapper;

public abstract class AbstractConditionACOptionTest<T> {

	public static final String PATH = "src/test/lib/cli/options/";
	
	private ParserWrapper parserWrapper;
	
	@BeforeEach
	public void beforeEach() {
		parserWrapper = new ParserWrapper();
	}

	/*
	 * Tests
	 */
	
	public void testProcessGeneral(int conditions, T expected) throws Exception {
		// get 
		final List<ConditionParameter> conditionParameters = createConditionParameters(conditions); 
		final AbstractConditionACOption acOption = createACOption(conditionParameters);

		// parse and process
		getParserWrapper().process(acOption, createLine(acOption, expected));

		// check
		for (final ConditionParameter conditionParameter : conditionParameters) {
			final T actual = getActualValue(conditionParameter);
			assertEquals(expected, actual);
		}
	}

	public void testProcessIndividual(int conditions, List<Integer> conditionIndices, List<T> expected) throws Exception {
		// create list of conditions
		final List<ConditionParameter> conditionParameters = createConditionParameters(conditions);
		
		// create list of acOptions - size need not == conditionParameters
		final List<AbstractConditionACOption> acOptions = conditionIndices.stream()
				.map(i -> createACOption(conditionParameters.get(i - 1)))
				.collect(Collectors.toList());
		
		// container for command line options
		final StringBuilder sb = new StringBuilder();
		// container for options
		final Options options = new Options();
		
		// for reach provided conditionIndex in conditionIndices add option with corresponding element from expected
		for (int i = 0; i < acOptions.size(); ++i) {
			final AbstractConditionACOption acOption = acOptions.get(i);
			options.addOption(acOption.getOption(false));
			final int conditionIndex = conditionIndices.get(i);
			sb.append(' ');
			sb.append(createLine(acOption, expected.get(conditionIndex - 1)));
		}
		// construct cmd from all provided options
		final CommandLine cmd = getParserWrapper().parse(options, sb.toString());
		// try to parse
		process(acOptions, cmd);
		
		// check over all conditions - only options provided via conditionIndex should be changed
		for (int i = 0; i < conditions; ++i) {
			final ConditionParameter conditionParameter = conditionParameters.get(i);
			final T actual = getActualValue(conditionParameter);
			final T e = expected.get(i);  
			assertEquals(e, actual);
		}
	}
	
	/*
	 * Others
	 */
	
	public static ConditionParameter createConditionParameter(final int conditionIndex) {
		return new ConditionParameter(conditionIndex);
	}

	public List<ConditionParameter> createConditionParameters(final int conditions) {
		return IntStream.rangeClosed(1, conditions)
			.mapToObj(i -> createConditionParameter(i))
			.collect(Collectors.toList());
	}

	protected void process(final List<AbstractConditionACOption> conditionACoptions, final CommandLine cmd) throws Exception {
		for (AbstractConditionACOption conditionACoption : conditionACoptions) {
			process(conditionACoption, cmd);
		}
	}
	
	protected void process(final AbstractConditionACOption conditionACoption, final CommandLine cmd) throws Exception {
		if (conditionACoption.getOpt() != null && cmd.hasOption(conditionACoption.getOpt()) ||
				conditionACoption.getLongOpt() != null && cmd.hasOption(conditionACoption.getLongOpt())) {
			conditionACoption.process(cmd);
		}
	}

	protected List<AbstractConditionACOption> createACOptions(List<ConditionParameter> conditionParameter) {
		return IntStream.rangeClosed(1, conditionParameter.size() + 1)
				.mapToObj(i -> createACOption(conditionParameter.get(i - 1)))
				.collect(Collectors.toList());
	}
	
	public static <T> Arguments ArgumentsHelper(final int conditions, final List<Integer> conditionIndicies, final List<T> expected) {
		if (conditionIndicies.size() > conditions) {
			throw new IllegalStateException("Size of conditionIndicies > conditions");
		}
		if (conditions != expected.size()) {
			throw new IllegalStateException("Size of conditions != expected");
		}
		for (final int conditionIndex : conditionIndicies) {
			if (conditionIndex - 1 >= conditions) {
				throw new IllegalStateException("Size of conditionIndex > conditions + 1");	
			}
		}
		return Arguments.of(conditions, conditionIndicies, expected);
	}

	/*
	 * Helper
	 */
	
	protected ParserWrapper getParserWrapper() {
		return parserWrapper;
	}
	
	/*
	 * Abstract
	 */

	protected abstract AbstractConditionACOption createACOption(List<ConditionParameter> conditionParameters);
	protected abstract AbstractConditionACOption createACOption(ConditionParameter conditionParameter);
	protected abstract T getActualValue(ConditionParameter conditionParameter);
	protected abstract String createLine(AbstractConditionACOption acOption, T v);
}
