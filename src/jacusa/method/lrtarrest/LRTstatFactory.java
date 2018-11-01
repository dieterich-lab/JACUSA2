package jacusa.method.lrtarrest;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import lib.stat.AbstractStatFactory;

/**
 * TODO implement this test-statistic
 * @author Michael Piechotta
 * @param 
 */
public class LRTstatFactory 
extends AbstractStatFactory {

	private final static String NAME = "LRTstat"; 
	private final static String DESC = "Combined BetaBin and DirMult";
	
	public LRTstatFactory() {
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
	public LRTstat newInstance(final int conditions) {
		return new LRTstat(this);
	}

}
