package test.lib.cli.options.condition;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import lib.cli.options.condition.AbstractConditionACOption;
import lib.cli.options.condition.MinCoverageConditionOption;
import lib.cli.parameter.ConditionParameter;

/**
 * Tests @see lib.cli.options.condition.MinCoverageConditionOption#process(org.apache.commons.cli.CommandLine)
 */
class MinCoverageConditionOptionTest extends AbstractConditionACOptionTest<Integer> {
	
	@Override
	protected Stream<Arguments> testProcessGeneral() {
		final int d = getDefaultValue();
		return Stream.of(
				createGeneralArguments(d, 1, 1),
				createGeneralArguments(d, 2, 1),
				createGeneralArguments(d, 3, 5) );
	}
	
	@Test
	public void testProcessGeneralFail() throws Exception {
		final List<ConditionParameter> conditionParameters 	= 
				createConditionParameters(2); 
		final AbstractConditionACOption testInstance 		= 
				createGeneralTestInstance(conditionParameters);

		// < 1
		myAssertOptThrows(IllegalArgumentException.class, testInstance, Integer.toString(0));
		// not a number
		myAssertOptThrows(IllegalArgumentException.class, testInstance, "wrong");
	}
	
	protected Stream<Arguments> testProcessIndividual() {
		final int d = getDefaultValue();
		return Stream.of(
				createIndividualArguments(d, Arrays.asList(), Arrays.asList()),
				createIndividualArguments(
						d, Arrays.asList(1, 2, 3), Arrays.asList(10, 20, 30)),
				createIndividualArguments(
						d, Arrays.asList(1, 2, 3), Arrays.asList(10, d, 30)) );
		
	}

	@Override
	protected
	Integer getActualValue(ConditionParameter conditionParameter) {
		return conditionParameter.getMinCoverage();
	}
	
	@Override
	protected
	AbstractConditionACOption createGeneralTestInstance(List<ConditionParameter> conditionParameters) {
		return new MinCoverageConditionOption(conditionParameters);
	}
	
	@Override
	protected
	AbstractConditionACOption createIndividualTestInstance(ConditionParameter conditionParameter) {
		return new MinCoverageConditionOption(conditionParameter);
	}
	
	@Override
	protected
	String convertString(Integer value) {
		return Integer.toString(value);
	}
	
}
