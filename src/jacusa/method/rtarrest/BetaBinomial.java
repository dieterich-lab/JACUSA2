package jacusa.method.rtarrest;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import jacusa.method.call.statistic.AbstractStatisticCalculator;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.util.Info;

/**
 * TODO implement this test-statistic
 * @author Michael Piechotta
 * @param <T>
 */
public class BetaBinomial<T extends AbstractData> 
extends AbstractStatisticCalculator<T> {

	private final static String NAME = "BetaBin"; 
	private final static String DESC = "BetaBinomial DUMMY";
	
	public BetaBinomial() {
		super(NAME, DESC);
	}

	@Override
	public void processCLI(final String line) {}

	@Override
	public void processCLI(CommandLine cmd) {}
	
	@Override
	protected Options getOptions() {
		return new Options();
	}

	@Override
	protected void addInfo(final Info info) {}

	@Override
	public double getStatistic(ParallelData<T> parallelData) {
		return -1;
	}

	@Override
	public boolean filter(final double statistic, final double threshold) {
		return statistic <= threshold;
	}
	
	@Override
	public final AbstractStatisticCalculator<T> newInstance() {
		return new BetaBinomial<T>();
	}
	
}
