package test.lib.cli.options;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import lib.cli.options.AbstractProcessingOption;
import lib.cli.options.ThreadWindowSizeOption;

/**
 * Tests @see lib.cli.options.ThreadWindowSizeOption#process(org.apache.commons.cli.CommandLine)
 */
class ThreadWindowSizeOptionTest
extends AbstractGeneralParameterProvider
implements OptionTest<Integer> {
	
	@Test
	void testProcessFail() throws Exception {
		// < ThreadWindowSizeOption.MIN_WINDOWS
		myAssertOptThrows(IllegalArgumentException.class, Integer.toString(ThreadWindowSizeOption.MIN_WINDOWS - 1));
		myAssertOptThrows(IllegalArgumentException.class, "wrong");
	}

	@Override
	public Stream<Arguments> testProcess() {
		return Arrays.asList(100, 200, 1000).stream()
				.map(i -> createArguments(i));
	}

	Arguments createArguments(final int threadWindowSize) {
		return Arguments.of(
				createOptLine(Integer.toString(threadWindowSize)),
				threadWindowSize);
	}
	
	@Override
	public AbstractProcessingOption createTestInstance() {
		return new ThreadWindowSizeOption(getGeneralParamter());
	}
	
	@Override
	public Integer getActualValue() {
		return getGeneralParamter().getReservedWindowSize();
	}
		
}
