package test.lib.cli.options.condition.filter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import lib.cli.options.condition.AbstractConditionACOption;
import lib.cli.options.condition.filter.FilterNHsamTagOption;
import lib.cli.options.condition.filter.samtag.MaxValueSamTagFilter;
import lib.cli.parameter.ConditionParameter;
import test.lib.cli.options.condition.AbstractConditionACOptionTest;
import test.utlis.CLIUtils;

public class FilterNHsamTagOptionTest extends AbstractConditionACOptionTest<Integer> {

	/*
	 * Tests
	 */

	@DisplayName("Check general FilterNHsamTagOption is parsed correctly")
	@ParameterizedTest(name = "Add FilterNHsamTag = {1} to samTagFilters for {0} conditions")
	@CsvSource( { "1, 1", "2, 10", "3, 5" } )
	@Override
	public void testProcessGeneral(int conditions, Integer expected) throws Exception {
		super.testProcessGeneral(conditions, expected);
	}
	
	@Test
	@DisplayName("Check general FilterNHsamTagOption fails on wrong input")
	public void testProcessGeneralFail() throws Exception {
		final List<ConditionParameter> conditionParameters = createConditionParameters(2); 
		final AbstractConditionACOption acOption = createACOption(conditionParameters);

		// < 1
		getParserWrapper().myAssertThrows(IllegalArgumentException.class, acOption, Integer.toString(0));
		// not a number
		getParserWrapper().myAssertThrows(IllegalArgumentException.class, acOption, "wrong");
	}
	
	@DisplayName("Check individual FilterNHsamTagOption is parsed correctly")
	@ParameterizedTest(name = "FilterNHsamTag should be {2} after setting {1} conditions of total {0}")
	@MethodSource("testProcessIndividual")
	@Override
	public void testProcessIndividual(int conditions, List<Integer> conditionIndices, List<Integer> expected) throws Exception {
		super.testProcessIndividual(conditions, conditionIndices, expected);
	}
	
	/*
	 * Method Source
	 */
	
	static Stream<Arguments> testProcessIndividual() {
		final Integer d = 0;

		return Stream.of(
				ArgumentsHelper(3, Arrays.asList(), Arrays.asList(d, d, d)),
				ArgumentsHelper(3, Arrays.asList(1, 2, 3), Arrays.asList(10, 20, 30)),
				ArgumentsHelper(3, Arrays.asList(1, 3), Arrays.asList(10, d, 30)) );
	}
	
	/*
	 * Helper
	 */
	
	@Override
	protected Integer getActualValue(ConditionParameter conditionParameter) {
		for (final MaxValueSamTagFilter filter : conditionParameter.getSamTagFilters()) {
			if (filter.getTag().equals(FilterNHsamTagOption.TAG)) {
				return filter.getValue();
			}
		}
		return 0;
	}
	
	@Override
	protected String createLine(AbstractConditionACOption acOption, Integer v) {
		return CLIUtils.assignValue(acOption.getOption(false), Integer.toString(v));
	}
	
	@Override
	protected AbstractConditionACOption createACOption(ConditionParameter conditionParameter) {
		return new FilterNHsamTagOption(conditionParameter);
	}
	
	@Override
	protected AbstractConditionACOption createACOption(List<ConditionParameter> conditionParameters) {
		return new FilterNHsamTagOption(conditionParameters);
	}
	
}
