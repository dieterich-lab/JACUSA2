package test.lib.cli.options;

import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import lib.cli.options.AbstractProcessingOption;
import lib.cli.options.ShowDeletionCountOption;

public class ShowDeletionCountOptionTest 
extends AbstractGeneralParameterProvider
implements OptionTest<Boolean> {

	@Override
	public Stream<Arguments> testProcess() {
		return Stream.of(
				Arguments.of(createOptLine(), true),
				Arguments.of("", false) );
	}
	
	@Override
	public AbstractProcessingOption createTestInstance() {
		return new ShowDeletionCountOption(getGeneralParamter());
	}
	
	@Override
	public Boolean getActualValue() {
		return getGeneralParamter().showDeletionCount();
	}
		
}
