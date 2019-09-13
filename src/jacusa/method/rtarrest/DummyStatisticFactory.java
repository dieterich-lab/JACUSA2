package jacusa.method.rtarrest;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import lib.stat.AbstractStatFactory;

/**
 * This is a dummy statistic... to display a dummy value
 *  
 * @param 
 */
public class DummyStatisticFactory 
extends AbstractStatFactory {

	private static final String NAME = "Dummy"; 
	private static final String DESC = "Does not do anything";
	
	public DummyStatisticFactory() {
		super(Option.builder(NAME)
				.desc(DESC)
				.build());
	}

	@Override
	public void processCLI(CommandLine cmd) {}
	
	@Override
	protected Options getOptions() {
		return new Options();
	}
	
	@Override
	public DummyStatistic newInstance(double threshold, final int conditions) {
		return new DummyStatistic();
	}
	
}
