package jacusa.method.rtarrest;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import lib.stat.AbstractStatFactory;

/**
 * TODO implement this test-statistic
 * 
 * @param 
 */
public class BetaBinFactory 
extends AbstractStatFactory {

	private final static String NAME = "BetaBin"; 
	private final static String DESC = "BetaBinomial DUMMY";
	
	public BetaBinFactory() {
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
	public BetaBin newInstance(final int conditions) {
		return new BetaBin(this);
	}
	
	
}
