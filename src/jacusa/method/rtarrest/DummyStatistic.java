package jacusa.method.rtarrest;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

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
		super(Option.builder(NAME)
				.desc(DESC)
				.build());
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
	public AbstractStatisticCalculator<T> newInstance() {
		return new DummyStatistic<T>();
	}
	
}
