package jacusa.method.rtarrest;

import jacusa.method.call.statistic.AbstractStatisticCalculator;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.util.Info;

/**
 * This is a dummy statistic... TODO add comments
 *  
 * @param <T>
 */
public class DummyStatistic<T extends AbstractData> 
extends AbstractStatisticCalculator<T> {

	private final static String NAME = "Dummy"; 
	private final static String DESC = "Does not do anything";
	
	public DummyStatistic() {
		super(NAME, DESC);
	}

	private DummyStatistic(final double threshold) {
		super(NAME, DESC, threshold);
	}
	
	@Override
	public boolean processCLI(final String line) {
		return true;
	}

	@Override
	protected void addInfo(final Info info) {
	}

	@Override
	public double getStatistic(ParallelData<T> parallelData) {
		return -1;
	}

	@Override
	public boolean filter(final double statistic, final double threshold) {
		return false;
	}

	@Override
	public AbstractStatisticCalculator<T> newInstance(final double threshold) {
		return new DummyStatistic<T>(threshold);
	}
	
}