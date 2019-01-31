package test.lib.cli.options;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import lib.cli.options.AbstractACOption;
import lib.cli.options.WindowSizeOption;

// TODO add test windowSize > threadWindowSize
/**
 * Tests @see lib.cli.options.WindowSizeOption#process(org.apache.commons.cli.CommandLine)
 */
class WindowSizeOptionTest 
extends AbstractGeneralParameterProvider
implements ACOptionTest<Integer> {

	@Test
	@DisplayName("Check WindowSizeOption fails on wrong input")
	void testProcessFail() throws Exception {
		// < 1
		myAssertOptThrows(IllegalArgumentException.class, Integer.toString(0));
		// not a number
		myAssertOptThrows(IllegalArgumentException.class, "wrong");
	}

	@Override
	public Stream<Arguments> testProcess() {
		return Arrays.asList(100, 200, 1000).stream()
				.map(i -> createArguments(i));
	}

	Arguments createArguments(final int windowSize) {
		return Arguments.of(
				createOptLine(Integer.toString(windowSize)),
				windowSize);
	}
	
	@Override
	public Integer getActualValue() {
		return getGeneralParamter().getActiveWindowSize();
	}
	
	@Override
	public AbstractACOption createTestInstance() {
		return new WindowSizeOption(getGeneralParamter());
	}
	
}
