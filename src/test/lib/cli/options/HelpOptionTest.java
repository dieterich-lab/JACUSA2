package test.lib.cli.options;

import java.util.ArrayList;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;

import lib.cli.CLI;
import lib.cli.options.AbstractACOption;
import lib.cli.options.HelpOption;

/**
 * Tests @see lib.cli.options.HelpOption#process(org.apache.commons.cli.CommandLine)
 */
class HelpOptionTest
implements ACOptionTest<Boolean> {

	private CLI cli;
	
	@BeforeEach
	public void beforeEach() {
		cli = new CLI(new ArrayList<>());
	}

	@Override
	public Stream<Arguments> testProcess() {
		return Stream.of(
				Arguments.of(createOptLine(), true),
				Arguments.of("", false) );
	}
	
	@Override
	public AbstractACOption createTestInstance() {
		return new HelpOption(cli);
	}

	@Override
	public Boolean getActualValue() {
		return cli.printExtendedHelp();
	}
	
}
