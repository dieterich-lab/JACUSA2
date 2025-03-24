package test.lib.cli.options.filter;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import jacusa.filter.factory.MaxAlleleCountFilterFactory;
import lib.cli.options.AbstractProcessingOption;
import lib.cli.options.filter.MaxAlleleCountOption;
import lib.cli.options.filter.has.HasMaxAlleleCount;
import lib.util.Base;
import test.lib.cli.options.OptionTest;

/**
 * Tests @see lib.cli.options.filter.MaxAlleleCountOption#process(org.apache.commons.cli.CommandLine)
 */
class MaxAlleleCountOptionTest implements OptionTest<Integer> {

	private HasMaxAlleleCount hasMaxAlleleCount;
	
	@BeforeEach
	void beforeEach() {
		hasMaxAlleleCount = new DefaultHasMaxAlleleCount(MaxAlleleCountFilterFactory.MAX_ALLELES); 
	}
	
	@Test
	void testProcessFails() throws Exception {
		// < 1
		myAssertLongOptThrows(IllegalArgumentException.class, "0");
		// > Base.length
		myAssertLongOptThrows(
				IllegalArgumentException.class, 
				Integer.toBinaryString(Base.validValues().length + 1) );
		// not a number
		myAssertLongOptThrows(IllegalArgumentException.class, "wrong");
	}

	@Override
	public Stream<Arguments> testProcess() {
		return IntStream.rangeClosed(1, Base.validValues().length)
			.mapToObj(i -> createArguments(i));
	}
	
	Arguments createArguments(final int maxAlleleCount) {
		return Arguments.of(
				createLongOptLine(Integer.toString(maxAlleleCount)),
				maxAlleleCount);
	}

	@Override
	public AbstractProcessingOption createTestInstance() {
		return new MaxAlleleCountOption(hasMaxAlleleCount);
	}

	@Override
	public Integer getActualValue() {
		return hasMaxAlleleCount.getMaxAlleleCount();
	}
	
	private class DefaultHasMaxAlleleCount implements HasMaxAlleleCount {
		
		private int count;
		
		public DefaultHasMaxAlleleCount(final int count) {
			this.count = count;
		}
		
		@Override
		public int getMaxAlleleCount() {
			return count;
		}
		
		@Override
		public void setMaxAlleleCount(int count) {
			this.count = count;
		}
		
	}	
	
}
