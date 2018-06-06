package jacusa.cli.options;

import jacusa.cli.parameters.StatisticParameter;
import jacusa.method.call.statistic.AbstractStatisticCalculator;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import lib.cli.options.AbstractACOption;
import lib.data.AbstractData;
import lib.util.Util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * Enables the user to choose between different statistics. 
 * @author Michael Piechotta
 * @param <T>
 */
// TODO remove T
public class StatisticCalculatorOption<T extends AbstractData> 
extends AbstractACOption {

	private final StatisticParameter<T> currentStatisticParameter;
	// available statistics for a method
	private final Map<String, AbstractStatisticCalculator<T>> availableStatisticCalculator;

	public StatisticCalculatorOption(final StatisticParameter<T> currentStatisticParameter, 
			final Map<String, AbstractStatisticCalculator<T>> availableStatisticCalculator) {

		super("u", "mode");
		this.currentStatisticParameter  	= currentStatisticParameter;
		this.availableStatisticCalculator 	= availableStatisticCalculator;
	}

	@Override
	public Option getOption() {
		final StringBuilder sb = new StringBuilder();

		final String defaultValue = currentStatisticParameter.getStatisticCalculatorName();
		
		final Options options = new Options(); 
		for (final String statName : availableStatisticCalculator.keySet()) {
			final AbstractStatisticCalculator<T> tmpStatisticCalculator = 
					availableStatisticCalculator.get(statName);

			final String opt = "___REMOVE___" + statName;
			Option option = Option.builder(opt)
					.desc(": " + tmpStatisticCalculator.getDescription())
					.build();

			options.addOption(option);
		}

		final HelpFormatter helpFormatter = new HelpFormatter();
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		helpFormatter.printOptions(pw, 100, options, 0, 1);
		final String s = sw.toString();
		sb.append(s.replaceAll("-___REMOVE___", ""));

		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc("Choose between different modes (Default: " + defaultValue + "):\n" + sb.toString())
				.build();
	}

	@Override
	public void process(final CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
			// name of the statistic
			String statName = line.getOptionValue(getOpt());
			// separator for optional arguments
			String str = Character.toString(Util.WITHIN_FIELD_SEP);
			// parse name and options
			if (statName.indexOf(str) > -1) {
				String[] cols = statName.split(str, 2);
				statName = cols[0];
			}

			// check if statName is a valid statistic name
			if (! availableStatisticCalculator.containsKey(statName)) {
				throw new IllegalArgumentException("Unknown statistic: " + statName);
			}

			// update statistic factory and set command line options
			currentStatisticParameter.setStatisticCalculator(line.getOptionValue(getOpt()), 
					availableStatisticCalculator.get(statName));
		}
	}

}
