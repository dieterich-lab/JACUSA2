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


	private final StatisticParameter<T> statisticParameter;
	// available statistics for a method
	private final Map<String, AbstractStatisticCalculator<T>> statisticCalculator;

	public StatisticCalculatorOption(final StatisticParameter<T> statisticFactory, 
			final Map<String, AbstractStatisticCalculator<T>> statisticCalculator) {

		super("u", "mode");
		this.statisticParameter 		= statisticFactory;
		this.statisticCalculator 	= statisticCalculator;
	}

	@Override
	public Option getOption() {
		final StringBuilder sb = new StringBuilder();

		for (final String statName : statisticCalculator.keySet()) {
			final AbstractStatisticCalculator<T> statistic = 
					statisticCalculator.get(statName);

			if (statisticParameter.newInstance() != null && 
					statistic.getName().equals(statisticParameter.newInstance().getName())) {
				sb.append("<*>");
			} else {
				sb.append("< >");
			}
			sb.append(" " + statName);
			sb.append(" : ");
			sb.append(statistic.getDescription());
			sb.append("\n");
		}

		return Option.builder(getOpt())
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc("Choose between different modes:\n" + sb.toString())
				.build();
	}

	@Override
	public void process(final CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
			// name of the statistic
			String statName = line.getOptionValue(getOpt());
			// separator for optional arguments
			String str = Character.toString(AbstractFilterFactory.SEP);
			// parse name and options
			if (statName.indexOf(str) > -1) {
				String[] cols = statName.split(str, 2);
				statName = cols[0];
			}

			// check if statName is a valid statistic name
			if (! statisticCalculator.containsKey(statName)) {
				throw new IllegalArgumentException("Unknown statistic: " + statName);
			}

			// update statistic factory
			statisticParameter.setStatisticCalculator(statisticCalculator.get(statName));
			// create and process command line options
			// FIXME what is done with the newInstance
			statisticParameter.newInstance().processCLI(line.getOptionValue(getOpt()));
		}
	}

}
