package jacusa.method.rtarrest;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import lib.stat.AbstractStatFactory;

/**
 * This fake test-statistic calculates the total coverage... TODO add comments
 *  
 * @param 
 */
public class CoverageStatisticFactory 
extends AbstractStatFactory {

	private final static String NAME = "Coverage"; 
	private final static String DESC = "Calculates the total coverage";
	
	public CoverageStatisticFactory() {
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
	public CoverageStatistic newInstance(final int conditions) {
		return new CoverageStatistic(this);
	}
	
}
