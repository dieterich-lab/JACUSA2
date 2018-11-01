package test.lib.cli.options;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.function.Executable;

import lib.cli.options.AbstractACOption;
import test.utlis.CLIUtils;

public class ParserWrapper {

	private CommandLineParser parser;
	
	public ParserWrapper() {
		parser = new DefaultParser();
	}
	
	/*
	 * use parser to populate option
	 */

	public CommandLine parse(final Options options, final String line) throws ParseException {
		return parser.parse(options, line.split(" "));
	}
	public CommandLine parse(final Option option, final String line) throws ParseException {
		final Options options = new Options();
		options.addOption(option);
		return parse(options, line);
	}
	
	/*
	 * process parsed option
	 */

	public void process(final AbstractACOption testInstance, final String line) throws Exception {
		final CommandLine cmd = parse(testInstance.getOption(false), line);
		process(testInstance, cmd);
	}
	public void process(final AbstractACOption testInstance, final CommandLine cmd) throws Exception {
		if (testInstance.getOpt() != null && cmd.hasOption(testInstance.getOpt()) ||
				testInstance.getLongOpt() != null && cmd.hasOption(testInstance.getLongOpt())) {
			testInstance.process(cmd);
		}
	}
	
	public <E extends Throwable> void myAssertThrows(final Class<E> expectedType, 
			final AbstractACOption testInstance, final String value) throws Exception{

		final Option option = testInstance.getOption(false);
		final String line = CLIUtils.assignValue(option, value);
		Executable executable = () -> {
			final CommandLine cmd = parse(testInstance.getOption(false), line);
			testInstance.process(cmd); 
			};
		assertThrows(expectedType, executable);
	}

}
