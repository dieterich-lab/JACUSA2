package test.lib.cli.options.filter;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import lib.cli.options.AbstractProcessingOption;
import lib.cli.options.filter.ConditionOption;
import lib.cli.options.filter.has.HasConditionIndex;
import test.lib.cli.options.OptionTest;

/**
 * Tests @see lib.cli.options.filter.ConditionOption#process(org.apache.commons.cli.CommandLine)
 */
class ConditionOptionTest implements OptionTest<Integer> {
	
	private final int conditionSize;
	
	private HasConditionIndex hasCondition;
	
	public ConditionOptionTest() {
		conditionSize = 4;
	}
	
	@BeforeEach
	void beforeEach() {
		hasCondition = new DefaultHasCondition(-1);
	}
	
	@DisplayName("Test processCLI fails on wrong input")
	@Test
	void testProcessCLIFails() throws Exception {
		// < 1
		myAssertLongOptThrows(IllegalArgumentException.class, "0");
		// > conditionSize
		myAssertLongOptThrows(IllegalArgumentException.class, Integer.toString(conditionSize + 1));
		// not a number
		myAssertLongOptThrows(IllegalArgumentException.class, "wrong");
	}
	
	@Override
	public AbstractProcessingOption createTestInstance() {
		return new ConditionOption(hasCondition, conditionSize);
	}
	
	@Override
	public Integer getActualValue() {
		return hasCondition.getConditionIndex();
	}
	
	@Override
	public Stream<Arguments> testProcess() {
		return IntStream.rangeClosed(1, conditionSize)
				.mapToObj(i -> createArguments(i));
	}
	
	Arguments createArguments(final int condition) {
		return Arguments.of(
				createLongOptLine(Integer.toString(condition)), 
				condition - 1);
	}

	private class DefaultHasCondition implements HasConditionIndex {
		
		private int conditionIndex;
		
		public DefaultHasCondition(final int conditionIndex) {
			this.conditionIndex = conditionIndex;
		}
		
		@Override
		public int getConditionIndex() {
			return conditionIndex;
		}
		
		@Override
		public void setConditionIndex(int condition) {
			this.conditionIndex = condition;
		}
		
	}
	
}
