package jacusa.cli.parameters;

import jacusa.method.call.statistic.AbstractStatisticCalculator;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.util.Info;

public class BetaBinomial<T extends AbstractData> 
extends AbstractStatisticCalculator<T> {

	private final static String NAME = "BetaBin"; 
	private final static String DESC = "BetaBinomial DUMMY";
	
	public BetaBinomial() {
		super(NAME, DESC);
	}

	private BetaBinomial(final double threshold) {
		super(NAME, DESC, threshold);
	}
	
	@Override
	public boolean processCLI(final String line) {
		return true;
	}

	@Override
	protected void addInfo(final Info info) {
		// TODO
		
	}

	@Override
	public double getStatistic(ParallelData<T> parallelData) {
		// TODO
		return 0;
	}

	@Override
	public boolean filter(final double statistic, final double threshold) {
		return statistic <= threshold;
	}

	@Override
	public AbstractStatisticCalculator<T> newInstance(final double threshold) {
		return new BetaBinomial<T>(threshold);
	}
	
	
	
}
