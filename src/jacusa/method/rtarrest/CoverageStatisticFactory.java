package jacusa.method.rtarrest;

import org.apache.commons.cli.Option;

import lib.stat.AbstractStatFactory;

/**
 * This fake test-statistic calculates the total coverage...
 */
public class CoverageStatisticFactory 
extends AbstractStatFactory {

	private static final String NAME = "Coverage"; 
	private static final String DESC = "Calculates the total coverage";
	
	public CoverageStatisticFactory() {
		super(Option.builder(NAME)
				.desc(DESC)
				.build());
	}
	
	@Override
	public CoverageStatistic newInstance(double threshold, final int conditions) {
		return new CoverageStatistic();
	}
	
}
