package jacusa.method.rtarrest;

import org.apache.commons.cli.Option;

import lib.cli.parameter.GeneralParameter;
import lib.stat.AbstractStatFactory;

/**
 * This fake test-statistic calculates the total coverage...
 */
public class CoverageStatisticFactory 
extends AbstractStatFactory {

	private static final String NAME = "Coverage"; 
	private static final String DESC = "Calculates the total coverage";
	
	public CoverageStatisticFactory(final GeneralParameter parameters) {
		super(
				parameters,
				Option.builder(NAME)
				.desc(DESC)
				.build());
	}
	
	@Override
	public CoverageStatistic newInstance(double threshold, int conditions) {
		return new CoverageStatistic();
	}
	
}
