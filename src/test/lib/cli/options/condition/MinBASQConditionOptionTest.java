package test.lib.cli.options.condition;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import lib.cli.options.condition.AbstractConditionACOption;
import lib.cli.options.condition.MinBASQConditionOption;
import lib.cli.parameter.ConditionParameter;
import lib.phred2prob.Phred2Prob;

/**
 * Tests @see lib.cli.options.condition.MinBASQConditionOption#process(org.apache.commons.cli.CommandLine)
 */
class MinBASQConditionOptionTest extends AbstractConditionACOptionTest<Byte> {

	@Test
	void testProcessGeneralFail() throws Exception {
		final List<ConditionParameter> conditionParameters 	=
				createConditionParameters(2); 
		final AbstractConditionACOption testInstance 		= 
				createGeneralTestInstance(conditionParameters);
		
		// < 0
		myAssertOptThrows(IllegalArgumentException.class, testInstance, Integer.toString(-1));
		// > Phred2Prob.MAX_Q
		myAssertOptThrows(IllegalArgumentException.class, testInstance, Byte.toString((byte)(Phred2Prob.MAX_Q + 1)));
		// not a number
		myAssertOptThrows(IllegalArgumentException.class, testInstance, "wrong");
	}

	@Override
	protected Stream<Arguments> testProcessIndividual() {
		final Byte b = getDefaultValue();
		final int i = Byte.toUnsignedInt(getDefaultValue());
		return Stream.of(
				createIndividualArguments(b, Arrays.asList(), Arrays.asList()),
				createIndividualArguments(
						b, Arrays.asList(1, 2, 3), c(Arrays.asList(10, 20, 30))),
				createIndividualArguments(
						b, Arrays.asList(1, 2, 3), c(Arrays.asList(10, i, 30))) );
	}
	
	@Override
	protected Stream<Arguments> testProcessGeneral() {
		final Byte d = getDefaultValue();
		return Stream.of(
				createGeneralArguments(d, 1, c(1)),
				createGeneralArguments(d, 2, c(10)),
				createGeneralArguments(d, 3, c(5)) );
	}

	Byte c(int i) {
		return new Byte((byte)i);
	}
	
	List<Byte> c(List<Integer> l) {
		return l.stream()
				.map(i -> c(i))
				.collect(Collectors.toList());
	}
	
	@Override
	protected
	String convertString(Byte value) {
		return Byte.toString(value);
	}
	
	@Override
	protected
	Byte getActualValue(ConditionParameter conditionParameter) {
		return conditionParameter.getMinBASQ();
	}
	
	@Override
	protected
	AbstractConditionACOption createGeneralTestInstance(List<ConditionParameter> conditionParameters) {
		return new MinBASQConditionOption(conditionParameters);
	}
	
	@Override
	protected
	AbstractConditionACOption createIndividualTestInstance(ConditionParameter conditionParameter) {
		return new MinBASQConditionOption(conditionParameter);
	}
	
}
