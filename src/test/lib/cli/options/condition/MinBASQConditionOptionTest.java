package test.lib.cli.options.condition;

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
import lib.cli.options.condition.MinBASQConditionOption;
import lib.cli.parameter.ConditionParameter;
import lib.phred2prob.Phred2Prob;
import test.utlis.CLIUtils;

@DisplayName("Test CLI processing of MinBASQConditionOption")
class MinBASQConditionOptionTest extends AbstractConditionACOptionTest<Byte> {

	/*
	 * Tests
	 */
	
	@DisplayName("Check general MinBASQConditionOption is parsed correctly")
	@ParameterizedTest(name = "Set maxDepth to {1} for {0} conditions")
	@CsvSource( { "1, 1", "2, 10", "3, 5" } )
	@Override
	public void testProcessGeneral(int conditions, Byte expected) throws Exception {
		super.testProcessGeneral(conditions, expected);
	}
	
	@Test
	@DisplayName("Check general MinBASQConditionOption fails on wrong input")
	void testProcessGeneralFail() throws Exception {
		final List<ConditionParameter> conditionParameters = createConditionParameters(2); 
		final AbstractConditionACOption acOption = createACOption(conditionParameters);

		// < -1
		getParserWrapper().myAssertThrows(IllegalArgumentException.class, acOption, Byte.toString((byte)(-1)));
		// > max Phred2Prob.MAX_Q
		getParserWrapper().myAssertThrows(IllegalArgumentException.class, acOption, Byte.toString((byte)(Phred2Prob.MAX_Q + 1)));
		// not a number
		getParserWrapper().myAssertThrows(IllegalArgumentException.class, acOption, "wrong");
	}
	
	@DisplayName("Check individual MinBASQConditionOption is parsed correctly")
	@ParameterizedTest(name = "minBASQ should be {2} after setting {1} conditions of total {0}")
	@MethodSource("testProcessIndividual")
	@Override
	public void testProcessIndividual(int conditions, List<Integer> conditionIndices, List<Byte> expected) throws Exception {
		super.testProcessIndividual(conditions, conditionIndices, expected);
	}
	
	/*
	 * Method Source
	 */
	
	static Stream<Arguments> testProcessIndividual() {
		final ConditionParameter conditionParameter = createConditionParameter(-1);
		// FIXME ugly
		final Byte d = conditionParameter.getMinBASQ();

		return Stream.of(
				ArgumentsHelper(3, Arrays.asList(), Arrays.asList(d, d, d)),
				ArgumentsHelper(3, Arrays.asList(1, 2, 3), Arrays.asList((byte)10, (byte)20, (byte)30)),
				ArgumentsHelper(3, Arrays.asList(1, 3), Arrays.asList((byte)10, d, (byte)30)) );
	}
	
	/*
	 * Helper
	 */
	
	@Override
	protected String createLine(AbstractConditionACOption actOption, Byte v) {
		return CLIUtils.assignValue(actOption.getOption(false), Byte.toString(v));
	}
	
	@Override
	protected Byte getActualValue(ConditionParameter conditionParameter) {
		return conditionParameter.getMinBASQ();
	}
	
	@Override
	protected AbstractConditionACOption createACOption(int conditionIndex, ConditionParameter conditionParameter) {
		return new MinBASQConditionOption(conditionIndex, conditionParameter);
	}
	
	@Override
	protected AbstractConditionACOption createACOption(List<ConditionParameter> conditionParameter) {
		return new MinBASQConditionOption(conditionParameter);
	}
	
}
