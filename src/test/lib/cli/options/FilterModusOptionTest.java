package test.lib.cli.options;

import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import lib.cli.options.AbstractACOption;
import lib.cli.options.FilterModusOption;

/**
 * Tests @see lib.cli.options.FilterModusOption#process(org.apache.commons.cli.CommandLine)
 */
class FilterModusOptionTest
extends AbstractGeneralParameterProvider
implements ACOptionTest<String> {

	@Override
	public Stream<Arguments> testProcess() {
		return Stream.of(
				Arguments.of(createOptLine(), "filtered_output"),
				Arguments.of("", null) );
	}
	
	@Override
	public AbstractACOption createTestInstance() {
		return new FilterModusOption(getGeneralParamter());
	}
	
	@Override
	public String getActualValue() {
		return getGeneralParamter().getFilteredFilename();
	}
	
}
