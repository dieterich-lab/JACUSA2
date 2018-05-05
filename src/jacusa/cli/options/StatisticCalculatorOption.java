package jacusa.cli.options;

import jacusa.cli.parameters.StatisticParameter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.method.call.statistic.AbstractStatisticCalculator;

import java.util.Map;

import lib.cli.options.AbstractACOption;
import lib.data.AbstractData;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

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

		for (final String statName : availableStatisticCalculator.keySet()) {
			final AbstractStatisticCalculator<T> tmpStatisticCalculator = 
					availableStatisticCalculator.get(statName);
			final String tmpName = tmpStatisticCalculator.getName();
			
			if (currentStatisticParameter.newInstance() != null && 
					tmpName.equals(currentStatisticParameter.getStatisticCalculatorName())) {
				sb.append("<*>"); // pre-chosen option
			} else {
				sb.append("< >"); // possible option
			}
			sb.append(' ');
			sb.append(statName);
			sb.append(" : ");
			sb.append(tmpStatisticCalculator.getDescription());
			sb.append('\n');
		}

		return Option.builder(getOpt())
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc("Choose between different modes:\n" + sb.toString())
				.build();
	}

	// FIXME statistic and calculator options
	@Override
	public void process(final CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
			// name of the statistic
			String statName = line.getOptionValue(getOpt());
			// separator for optional arguments
			String str = Character.toString(AbstractFilterFactory.OPTION_SEP);
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
