package test.lib.cli.options;

import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import lib.cli.options.AbstractOption;
import lib.cli.options.FilterModusOption;

/**
 * Tests @see lib.cli.options.FilterModusOption#process(org.apache.commons.cli.CommandLine)
 */
class FilterModusOptionTest
extends AbstractGeneralParameterProvider
implements OptionTest<String> {

	@Override
	public Stream<Arguments> testProcess() {
		return Stream.of(
				Arguments.of(createOptLine(), "filtered_output"),
				Arguments.of("", null) );
	}
	
	@Override
	public AbstractOption createTestInstance() {
		return new FilterModusOption(getGeneralParamter());
	}
	
	@Override
	public String getActualValue() {
		return getGeneralParamter().getFilteredFilename();
	}
	
}
