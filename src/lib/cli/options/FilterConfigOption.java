package lib.cli.options;

import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.FilterFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import lib.cli.parameter.GeneralParameter;
import lib.util.Util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class FilterConfigOption extends AbstractACOption {

	final private GeneralParameter parameters;

	public static final char OR = ',';
	//public static char AND = '&';

	final private Map<Character, AbstractFilterFactory> filterFactories;

	public FilterConfigOption(final GeneralParameter parameter, 
			final Map<Character, AbstractFilterFactory> filterFactories) {

		super("a", "feature-filter");
		this.parameters 		= parameter;
		this.filterFactories 	= filterFactories;
	}

	@Override
	public Option getOption(final boolean printExtendedHelp) {
		final StringBuffer sb = new StringBuffer();
		
		final Options options = new Options(); 
		for (final char c : filterFactories.keySet()) {
			final FilterFactory filterFactory = filterFactories.get(c);

			final String opt = "___REMOVE___" + Character.toString(c);
			Option option = Option.builder(opt)
					.desc(filterFactory.getDesc())
					.build();

			options.addOption(option);
		}

		final HelpFormatter helpFormatter = new HelpFormatter();
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		helpFormatter.printOptions(pw, 100, options, 0, 1);
		final String s = sw.toString();
		sb.append(s.replaceAll("-___REMOVE___", ""));

		final String argName = "FEATURE-FILTER";
		String desc = new String();
		if (printExtendedHelp) {
			desc = "Chain of " + argName + ". Join " + argName + " D and I with '" + OR + "' and add options with ':'\n" +
					"e.g.: D" + OR + "I:I_OPTION1:I_OPTION2=I_VALUE2\n" + 
					sb.toString();
		} else {
			desc = HelpOption.SHORT_MSG;
		}

		return Option.builder(getOpt())
				.argName(argName)
				.hasArg(true)
				.desc(desc)
				.build(); 
	}

	@Override
	public void process(final CommandLine line) throws Exception {
		final String s = line.getOptionValue(getOpt());
		final String[] t = s.split(Character.toString(Util.VALUE_SEP));

		for (String a : t) {
			final char c = a.charAt(0);
			if (! filterFactories.containsKey(c)) {
				throw new IllegalArgumentException("Unknown filter or wrong option: " + s);
			}
			final AbstractFilterFactory filterFactory = filterFactories.get(c);
			if (a.length() > 1) {
				a = a.substring(1);
				filterFactory.processCLI(a.replaceAll(":", " --").trim());
			}
			parameters.getFilterConfig().addFactory(filterFactory);
		}
	}

}