package jacusa.cli.options;

import jacusa.cli.parameters.StatisticFactory;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.method.call.statistic.AbstractStatisticCalculator;

import java.util.Map;

import lib.cli.options.AbstractACOption;
import lib.data.AbstractData;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class StatisticCalculatorOption<T extends AbstractData> 
extends AbstractACOption {

	private StatisticFactory<T> parameters;
	private Map<String,AbstractStatisticCalculator<T>> statistics;

	public StatisticCalculatorOption(final StatisticFactory<T> parameters, 
			final Map<String, AbstractStatisticCalculator<T>> statisticCalculator) {
		super("u", "modues");
		this.parameters = parameters;
		this.statistics = statisticCalculator;
	}

	@Override
	public Option getOption() {
		StringBuilder sb = new StringBuilder();

		for (String name : statistics.keySet()) {
			AbstractStatisticCalculator<T> statistic = statistics.get(name);

			if(parameters.newInstance() != null && statistic.getName().equals(parameters.newInstance().getName())) {
				sb.append("<*>");
			} else {
				sb.append("< >");
			}
			sb.append(" " + name);
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
	public void process(CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
			String name = line.getOptionValue(getOpt());
			String str = Character.toString(AbstractFilterFactory.SEP);
			if (name.indexOf(str) > -1) {
				String[] cols = name.split(str, 2);
				name = cols[0];
			}

			if (! statistics.containsKey(name)) {
				throw new IllegalArgumentException("Unknown statistic: " + name);
			}
			parameters.setStatisticCalculator(statistics.get(name));
			parameters.newInstance().processCLI(line.getOptionValue(getOpt()));
		}
	}

}
