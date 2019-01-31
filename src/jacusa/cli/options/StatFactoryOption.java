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
 * @param 
 */
public class StatFactoryOption 
extends AbstractACOption {

	public static final String OPT 		= "u";
	public static final String LONG_OPT = "mode";
	
	private final StatParameter statParameter;
	// available statistics for a method
	private final Map<String, AbstractStatFactory> factories;

	public StatFactoryOption(final StatParameter statParameter, 
			final Map<String, AbstractStatFactory> factories) {

		super(OPT, LONG_OPT);
		this.statParameter	= statParameter;
		this.factories 		= factories;
	}

	@Override
	public Option getOption(final boolean printExtendedHelp) {
		final String REMOVE = "___REMOVE___";
		
		final StringBuilder sb = new StringBuilder();

		final String defaultValue = statParameter.getFactory().getName();
		
		final Options options = new Options(); 
		for (final String statName : factories.keySet()) {
			final AbstractStatFactory factory = factories.get(statName);

			final String opt = REMOVE + statName;
			Option option = Option.builder(opt)
					.desc(factory.getDescription())
					.build();

			options.addOption(option);
		}

		final HelpFormatter helpFormatter = new HelpFormatter();
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		helpFormatter.printOptions(pw, 100, options, 0, 1);
		final String s = sw.toString();
		sb.append(s.replaceAll("-" + REMOVE, ""));

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
	 * Tested in @see test.jacusa.cli.options.StatFactoryOptionTest 
	 */
	@Override
	public void process(final CommandLine line) throws Exception {
		final String s = line.getOptionValue(getOpt());
		// separator for optional arguments
		final String[] t = s.split(Character.toString(InputOutput.WITHIN_FIELD_SEP));

		// name of the statistic
		final String statName = t[0];
		// check if statName is a valid statistic name
		if (! factories.containsKey(statName)) {
			throw new IllegalArgumentException("Unknown statistic or wrong option: " + statName);
		}

		final int beginIndex = s.indexOf(Character.toString(InputOutput.WITHIN_FIELD_SEP));
		String a = new String();
		// parse name and options
		if (beginIndex > -1) {
			a = s.substring(beginIndex).replaceAll(":", "--"); 
		}

		// update statistic factory and set command line options
		statParameter.setFactory(a, factories.get(statName));
	}

}
