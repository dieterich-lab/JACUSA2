package test.lib.cli.options.filter;

import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import jacusa.filter.factory.basecall.AbstractBCCfilterFactory;
import lib.cli.options.AbstractProcessingOption;
import lib.cli.options.filter.FilterMinRatioOption;
import lib.cli.options.filter.has.HasFilterMinRatio;
import test.lib.cli.options.OptionTest;

/**
 * Tests @see lib.cli.options.filter.FilterMinRatioOption#process(org.apache.commons.cli.CommandLine)o  
 */
class FilterMinRatioOptionTest
implements OptionTest<Double> {

	private TestHasFilterMinRatio hasFilterMinRatio;
	
	@BeforeEach
	void beforeEach() {
		hasFilterMinRatio = 
				new TestHasFilterMinRatio(AbstractBCCfilterFactory.DEFAULT_FILTER_MINRATIO);
	}
	
	@Test
	void testProcessCLICommandLineFails() throws Exception {
		// < 0.0
		myAssertLongOptThrows(IllegalArgumentException.class, "-0.5");
		// > 1.0
		myAssertLongOptThrows(IllegalArgumentException.class, "1.1");
		// wrong 
		myAssertLongOptThrows(IllegalArgumentException.class, "wrong");
	}
	
	@Override
	public Stream<Arguments> testProcess() {
		return DoubleStream.of(0.1, 0.4)
				.mapToObj(i -> createArguments(i));
	}
	
	Arguments createArguments(final double minRatio) {
		return Arguments.of(
				createLongOptLine(Double.toString(minRatio)),
				minRatio);
	}

	@Override
	public AbstractProcessingOption createTestInstance() {
		return new FilterMinRatioOption(hasFilterMinRatio);
	}
	
	@Override
	public Double getActualValue() {
		return hasFilterMinRatio.getFilterMinRatio();
	}
	
	private class TestHasFilterMinRatio implements HasFilterMinRatio {

		private double minRatio;
		
		public TestHasFilterMinRatio(final double minRatio) {
			this.minRatio = minRatio;
		}
		
		@Override
		public double getFilterMinRatio() {
			return minRatio;
		}

		@Override
		public void setFilterMinRatio(double minRatio) {
			this.minRatio = minRatio;
		}
		
	}
	
}
