package test.lib.cli.options;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;

import jacusa.JACUSA;
import jacusa.method.call.CallMethod;
import lib.cli.options.AbstractProcessingOption;
import lib.cli.options.DebugModusOption;
import lib.util.AbstractMethod;
import lib.util.AbstractTool;

/**
 * Tests @see lib.cli.options.DebugModusOption#process(org.apache.commons.cli.CommandLine)
 */
class DebugModusOptionTest
extends AbstractGeneralParameterProvider
implements OptionTest<Boolean> {

	@SuppressWarnings("unused")
	private AbstractTool tool;
	private AbstractMethod method;
	
	@BeforeEach
	public void beforeEach() {
		super.beforeEach();

		tool 	= new JACUSA(new String[] {});
		method 	= new CallMethod.Factory(getGeneralParamter().getConditionsSize()).createMethod();
	}

	@Override
	public Stream<Arguments> testProcess() {
		return Stream.of(
				Arguments.of(createOptLine(), true),
				Arguments.of("", false) );
	}
	
	@Override
	public AbstractProcessingOption createTestInstance() {
		return new DebugModusOption(getGeneralParamter(), method);
	}
	
	@Override
	public Boolean getActualValue() {
		return getGeneralParamter().isDebug();
	}
	
}
