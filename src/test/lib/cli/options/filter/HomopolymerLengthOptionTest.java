package test.lib.cli.options.filter;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import jacusa.filter.factory.HomopolymerFilterFactory;
import lib.cli.options.AbstractProcessingOption;
import lib.cli.options.filter.HomopolymerLengthOption;
import lib.cli.options.filter.has.HasHomopolymerLength;
import test.lib.cli.options.OptionTest;

/**
 * Tests @see lib.cli.options.filter.HomopolymerLengthOption#process(org.apache.commons.cli.CommandLine)
 */
class HomopolymerLengthOptionTest 
implements OptionTest<Integer> {

	private TestHasHomopolymerLength hasHomopolymerLength;
	
	@BeforeEach
	void beforeEach() {
		hasHomopolymerLength = 
				new TestHasHomopolymerLength(HomopolymerFilterFactory.MIN_HOMOPOLYMER_LENGTH);
	}
	
	@DisplayName("Check processCLI fails on wrong input")
	@Test
	void testProcessFails() throws Exception {
		// < 1
		myAssertLongOptThrows(IllegalArgumentException.class, "0");
		// not a number
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
	public AbstractProcessingOption createTestInstance() {
		return new HomopolymerLengthOption(hasHomopolymerLength);
	}
	
	@Override
	public Integer getActualValue() {
		return hasHomopolymerLength.getHomopolymerLength();
	}
	
	private class TestHasHomopolymerLength implements HasHomopolymerLength {

		private int length;
		
		public TestHasHomopolymerLength(final int length) {
			this.length = length;
		}
		
		@Override
		public int getHomopolymerLength() {
			return length;
		}

		@Override
		public void setHomopolymerLength(int length) {
			this.length = length;
		}
		
	}
	
}
