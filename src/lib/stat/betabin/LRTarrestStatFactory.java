package lib.stat.betabin;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import lib.stat.AbstractStatFactory;
import lib.stat.dirmult.DirMultCLIprocessing;
import lib.stat.sample.provider.arrest.LRTarrestCountSampleProvider;

public class LRTarrestStatFactory extends AbstractStatFactory {

	private final static String NAME = "BetaBin"; 
	private final static String DESC = "Minka Newton iteration method";
	
	private final LRTarrestBetaBinParameter dirMultParameter;
	private final DirMultCLIprocessing CLIprocessing;
	
	public LRTarrestStatFactory() {
		super(Option.builder(NAME)
				.desc(DESC)
				.build());
		
		dirMultParameter 	= new LRTarrestBetaBinParameter();
		CLIprocessing 		= new LRTarrestBetaBinCLIProcessing(dirMultParameter);
	}

	@Override
	public LRTarrestStat newInstance(double threshold, final int conditions) {
		LRTarrestCountSampleProvider arrestCountProvider;
		switch (conditions) {
		case 2:
			arrestCountProvider = 
			new LRTarrestCountSampleProvider(dirMultParameter.getMinkaEstimateParameter().getMaxIterations());
			break;

		default:
			throw new IllegalStateException("Number of conditions not supported: " + conditions);
		}
		return new LRTarrestStat(threshold, arrestCountProvider, dirMultParameter);
	}

	@Override
	protected Options getOptions() {
		return CLIprocessing.getOptions();
	}
	
	@Override
	public void processCLI(final CommandLine cmd) {
		CLIprocessing.processCLI(cmd);
	}

}
