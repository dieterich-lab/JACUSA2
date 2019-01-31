package test.lib.cli.options;

import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import lib.cli.options.AbstractACOption;
import lib.cli.options.ShowReferenceOption;

/**
 * Tests @see lib.cli.options.ShowReferenceOption#process(org.apache.commons.cli.CommandLine)
 */
class ShowReferenceOptionTest 
extends AbstractGeneralParameterProvider
implements ACOptionTest<Boolean> {

	@Override
	public Stream<Arguments> testProcess() {
		return Stream.of(
				Arguments.of(createOptLine(), true),
				Arguments.of("", false) );
	}
	
	@Override
	public AbstractACOption createTestInstance() {
		return new ShowReferenceOption(getGeneralParamter());
	}
	
	@Override
	public Boolean getActualValue() {
		return getGeneralParamter().showReferenceBase();
	}
	
}
