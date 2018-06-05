package jacusa.method.call.statistic;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.result.StatisticResult;
import lib.util.Info;

/**
 * 
 * @author Michael Piechotta
 */
public abstract class AbstractStatisticCalculator<T extends AbstractData> {

	private final Option option;

	// add CLI options after OPTION_SEP, 
	// e.g.: C:opt1=val1
	// FIXME
	public static final char SEP = ':';
	
	public AbstractStatisticCalculator(final Option option) {
		this.option = option;
		// TODO add help from HelpFormatter to desc
	}
	
	/**
	 * Add test-statistic to result.
	 * May populate info fields of result.
	 * 
	 * @param result
	 */
	protected abstract void addInfo(final Info info);

	/**
	 * Calculate test-statistic for parallelPileup.
	 * 
	 * @param parallelData
	 * @return
	 */
	public abstract double getStatistic(final ParallelData<T> parallelData);

	public abstract boolean filter(double statistic, double threshold);
	
	public StatisticResult<T> filter(final double threshold, final ParallelData<T> parallelData) {
		final double statistic = getStatistic(parallelData);
		if (filter(statistic, threshold)) {
			return null;
		}
		StatisticResult<T> result = new StatisticResult<T>(statistic, parallelData);
		addInfo(result.getResultInfo());
		return result;
	}

	/**
	 * Returns a new instance of this StatisticCalculator.
	 * @param threshold
	 * @return
	 */
	public abstract AbstractStatisticCalculator<T> newInstance();

	/**
	 * Return the short name of this StatisticCalculator.
	 * @return
	 */
	public String getName() {
		return option.getOpt();
	}
	
	/**
	 * Return a short description of this StatisticCalculator.
	 * @return
	 */
	public String getDescription() {
		return option.getDescription();
	}

	/**
	 * Process command lines options.
	 * 
	 * @param line
	 * @return
	 */
	public abstract void processCLI(final CommandLine cmd);
	
	public void processCLI(final String line) {
		final Options options = getOptions();
		if (options.getOptions().size() == 0 || line == null || line.isEmpty()) {
			return;
		}

		final String[] args = line.split(Character.toString(SEP));
		final CommandLineParser parser = new DefaultParser();
		
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		processCLI(cmd);
	}
	
	protected abstract Options getOptions();
}
