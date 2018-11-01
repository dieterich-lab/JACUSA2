package jacusa.method.rtarrest;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import lib.stat.AbstractStatFactory;

/**
 * This is a dummy statistic... TODO add comments
 *  
 * @param 
 */
public class DummyStatisticFactory 
extends AbstractStatFactory {

	private final static String NAME = "Dummy"; 
	private final static String DESC = "Does not do anything";
	
	public DummyStatisticFactory() {
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
	public DummyStatistic newInstance(final int conditions) {
		return new DummyStatistic(this);
	}
	
}
