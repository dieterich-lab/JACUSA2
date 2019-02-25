
package jacusa.cli.options;

import jacusa.cli.parameters.StatParameter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import lib.cli.options.AbstractACOption;
import lib.cli.options.HelpOption;
import lib.io.InputOutput;
import lib.stat.AbstractStatFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * Enables the user to choose between different statistics.
 */
public class StatFactoryOption extends AbstractACOption {

	public static final String OPT 		= "u";
	public static final String LONG_OPT = "mode";
	
	private final StatParameter statParameter;
	// available statistics for a method
	private final Map<String, AbstractStatFactory> name2factory;

	public StatFactoryOption(final StatParameter statParameter, 
			final Map<String, AbstractStatFactory> factories) {

		super(OPT, LONG_OPT);
		this.statParameter	= statParameter;
		this.name2factory 		= factories;
	}

	@Override
	public Option getOption(final boolean printExtendedHelp) {
		// magic string
		final String REMOVE = "___REMOVE___";
		
		final StringBuilder sb = new StringBuilder();

		// name of default stat factory
		final String defaultValue = statParameter.getFactory().getName();
		
		// container for stat options
		final Options options = new Options(); 
		for (final String statName : name2factory.keySet()) {
			final AbstractStatFactory factory = name2factory.get(statName);

			// add magic string to identify main option
			final String opt = REMOVE + statName;
			// create main option
			Option option = Option.builder(opt)
					.desc(factory.getDescription())
					.build();

			options.addOption(option);
		}

		// print usage/description magic string is used to suppress normal options handling
		final HelpFormatter helpFormatter = new HelpFormatter();
		final StringWriter sw 	= new StringWriter();
		final PrintWriter pw 	= new PrintWriter(sw);
		helpFormatter.printOptions(pw, 100, options, 0, 1);
		final String s = sw.toString();
		// replace magic string with "-" to enable parsing of options
		sb.append(s.replaceAll("-" + REMOVE, ""));

		// print extended or normal help
		String desc = new String();
		if (printExtendedHelp) {
			desc = "Choose between different modes (Default: " + defaultValue + "):\n" 
					+ sb.toString();
		} else {
			desc = HelpOption.SHORT_MSG;
		}
		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc(desc)
				.build();
	}


	/**
	 * Process CLI for stat: e.g.: for DirMult showAlpha etc.
	 * Tested in @see test.jacusa.cli.options.StatFactoryOptionTest 
	 */
	@Override
	public void process(final CommandLine line) throws Exception {
		final String optionValue = line.getOptionValue(getOpt());
		// separator for optional arguments
		// e.g.: statNameWITHIN_FIELD_SEPopt1=arg1WITHIN_FIELD_SEPopt2=arg2
		final String[] t = optionValue.split(Character.toString(InputOutput.WITHIN_FIELD_SEP));

		// name of the statistic
		final String statName = t[0];
		// check if statName is a valid statistic name
		if (! name2factory.containsKey(statName)) {
			throw new IllegalArgumentException("Unknown statistic or wrong option: " + statName);
		}

		final int beginIndex = optionValue.indexOf(Character.toString(InputOutput.WITHIN_FIELD_SEP));
		String CLIoption = new String();
		// if there are options, make regular options for DefaultParser by adding "--"
		if (beginIndex > -1) {
			CLIoption = optionValue.substring(beginIndex).replaceAll(
					Character.toString(InputOutput.WITHIN_FIELD_SEP), "--"); 
		}

		// update statistic factory and set command line options
		statParameter.setFactory(CLIoption, name2factory.get(statName));
	}

}
