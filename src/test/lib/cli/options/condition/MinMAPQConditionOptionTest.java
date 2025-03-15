package test.lib.cli.options.condition;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import lib.cli.options.condition.AbstractConditionOption;
import lib.cli.options.condition.MinMAPQconditionOption;
import lib.cli.parameter.ConditionParameter;

/**
 * Tests @see lib.cli.options.condition.MinMAPQConditionOption#process(org.apache.commons.cli.CommandLine)
 */
class MinMAPQConditionOptionTest extends AbstractConditionACOptionTest<Integer> {
	
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
		final AbstractConditionOption testInstance 		= 
				createGeneralTestInstance(conditionParameters);

		// < 1
		myAssertOptThrows(IllegalArgumentException.class, testInstance, Integer.toString(-1));
		// > 255
		myAssertOptThrows(IllegalArgumentException.class, testInstance, Integer.toString(256));
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
		return conditionParameter.getMinMAPQ();
	}
	
	@Override
	protected
	AbstractConditionOption createGeneralTestInstance(List<ConditionParameter> conditionParameters) {
		return new MinMAPQconditionOption(conditionParameters);
	}
	
	@Override
	protected
	AbstractConditionOption createIndividualTestInstance(ConditionParameter conditionParameter) {
		return new MinMAPQconditionOption(conditionParameter);
	}
	
	@Override
	protected
	String convertString(Integer value) {
		return Integer.toString(value);
	}
	
}
