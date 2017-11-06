package jacusa.cli.parameters;

import jacusa.data.AbstractData;
import jacusa.data.ParallelPileupData;
import jacusa.data.Result;
import jacusa.method.call.statistic.StatisticCalculator;

public class BetaBinomial<T extends AbstractData> implements StatisticCalculator<T> {

	public BetaBinomial() {}
	
	@Override
	public void addStatistic(Result<T> result) {
		result.setStatistic(getStatistic(result.getParellelData()));
	}

	@Override
	public double getStatistic(ParallelPileupData<T> parallelData) {
		// TODO
		return Double.NaN;
	}

	@Override
	public boolean filter(double value) {
		return false;
	}

	@Override
	public StatisticCalculator<T> newInstance() {
		return new BetaBinomial<T>();
	}

	@Override
	public String getName() {
		return "BetaBinomial DUMMY";
	}

	@Override
	public String getDescription() {
		return "BetaBinomial DUMMY";
	}

	@Override
	public boolean processCLI(String line) {
		return true;
	}

	
	
}
