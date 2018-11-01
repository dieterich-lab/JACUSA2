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
import lib.cli.options.condition.filter.FilterFlagConditionOption;
import lib.cli.parameter.AbstractConditionParameter;
import test.lib.cli.options.condition.AbstractConditionACOptionTest;
import test.utlis.CLIUtils;

public class FilterFlagConditionOptionTest extends AbstractConditionACOptionTest<Integer> {

	/*
	 * Tests
	 */

	@DisplayName("Test FilterFlagConditionOption is parsed correctly")
	@ParameterizedTest(name = "Set filterFlag to {1} for {0} conditions")
	@CsvSource( { "1, 1024", "2, 4", "3, 8" } )
	@Override
	public void testProcessGeneral(int conditions, Integer expected) throws Exception {
		super.testProcessGeneral(conditions, expected);
	}
	
	@Test
	@DisplayName("Check general FilterFlagConditionOption fails on wrong input")
	public void testProcessGeneralFail() throws Exception {
		final List<AbstractConditionParameter> conditionParameters = createConditionParameters(2); 
		final AbstractConditionACOption acOption = createACOption(conditionParameters);

		// < 0
		getParserWrapper().myAssertThrows(IllegalArgumentException.class, acOption, Integer.toString(-1));
		// not a number
		getParserWrapper().myAssertThrows(IllegalArgumentException.class, acOption, "wrong");
	}
	
	@DisplayName("Check individual FilterFlagConditionOption is parsed correctly")
	@ParameterizedTest(name = "filterFlag should be {2} after setting {1} conditions of total {0}")
	@MethodSource("testProcessIndividual")
	@Override
	public void testProcessIndividual(int conditions, List<Integer> conditionIndices, List<Integer> expected) throws Exception {
		super.testProcessIndividual(conditions, conditionIndices, expected);
	}
	
	/*
	 * Method Source
	 */

	static Stream<Arguments> testProcessIndividual() {
		final AbstractConditionParameter conditionParameter = createConditionParameter(-1);
		final Integer d = conditionParameter.getFilterFlags();

		return Stream.of(
				ArgumentsHelper(3, Arrays.asList(), Arrays.asList(d, d, d)),
				ArgumentsHelper(3, Arrays.asList(1, 2, 3), Arrays.asList(10, 20, 30)),
				ArgumentsHelper(3, Arrays.asList(1, 3), Arrays.asList(10, d, 30)) );
	}
	
	/*
	 * Helper
	 */
	@Override
	protected AbstractConditionACOption createACOption(int conditionIndex,
			AbstractConditionParameter conditionParameter) {
		
		return new FilterFlagConditionOption(conditionIndex, conditionParameter);
	}
	
	@Override
	protected AbstractConditionACOption createACOption(List<AbstractConditionParameter> conditionParameters) {
		return new FilterFlagConditionOption(conditionParameters);
	}
	
	@Override
	protected String createLine(AbstractConditionACOption acOption, Integer v) {
		return CLIUtils.assignValue(acOption.getOption(false), Integer.toString(v));
	}
	
	@Override
	protected Integer getActualValue(AbstractConditionParameter conditionParameter) {
		return conditionParameter.getFilterFlags();
	}
	
}
