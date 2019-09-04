package test.lib.cli.options.filter;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import jacusa.filter.factory.basecall.AbstractBCCfilterFactory;
import lib.cli.options.AbstractACOption;
import lib.cli.options.filter.FilterDistanceOption;
import lib.cli.options.filter.has.HasFilterDistance;
import test.lib.cli.options.ACOptionTest;

/**
 * Tests @see lib.cli.options.filter.FilterDistanceOption#process(org.apache.commons.cli.CommandLine)o  
 */
class FilterDistanceOptionTest
implements ACOptionTest<Integer> {

	private TestHasFilterDistance hasFilterDistance;
	
	@BeforeEach
	void beforeEach() {
		hasFilterDistance = 
				new TestHasFilterDistance(AbstractBCCfilterFactory.DEFAULT_FILTER_DISTANCE);
	}
	
	@Test
	void testProcessFails() throws Exception{
		// < 0
		myAssertLongOptThrows(IllegalArgumentException.class, "-1");
		// wrong 
		myAssertLongOptThrows(IllegalArgumentException.class, "wrong");
	}

	@Override
	public Stream<Arguments> testProcess() {
		return IntStream.of(1, 3)
				.mapToObj(i -> createArguments(i));
	}
	
	Arguments createArguments(final int distance) {
		return Arguments.of(
				createLongOptLine(Integer.toString(distance)),
				distance);
	}
	
	@Override
	public AbstractACOption createTestInstance() {
		return new FilterDistanceOption(hasFilterDistance);
	}
	
	@Override
	public Integer getActualValue() {
		return hasFilterDistance.getFilterDistance();
	}
	
	private class TestHasFilterDistance implements HasFilterDistance {

		private int distance;
		
		public TestHasFilterDistance(final int distance) {
			this.distance = distance;
		}
		
		@Override
		public int getFilterDistance() {
			return distance;
		}

		@Override
		public void setFilterDistance(int distance) {
			this.distance = distance;
		}
		
	}
	
}
