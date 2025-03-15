package test.lib.cli.options;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import lib.cli.options.AbstractOption;
import lib.cli.options.MaxThreadOption;

/**
 * Tests @see lib.cli.options.MaxThreadOption#process(org.apache.commons.cli.CommandLine)
 */
class MaxThreadOptionTest 
extends AbstractGeneralParameterProvider
implements OptionTest<Integer> {

	@Override
	public Stream<Arguments> testProcess() {
		return Arrays.asList(1, 2, 5).stream()
				.map(i -> createArguments(i));
	}

	Arguments createArguments(final int maxThreads) {
		return Arguments.of(
				createOptLine(Integer.toString(maxThreads)),
				maxThreads);
	}

	@Test
	void testProcessFail() throws Exception {
		// < 1
		myAssertOptThrows(IllegalArgumentException.class, Integer.toString(0));
		// not a number
		myAssertOptThrows(IllegalArgumentException.class, "wrong");
	}
	
	@Override
	public AbstractOption createTestInstance() {
		return new MaxThreadOption(getGeneralParamter());
	}
	
	@Override
	public Integer getActualValue() {
		return getGeneralParamter().getMaxThreads();
	}
	
}
