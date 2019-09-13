package lib.stat.betabin;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import lib.stat.AbstractStatFactory;
import lib.stat.dirmult.DirMultCLIprocessing;
import lib.stat.estimation.provider.arrest.LRTarrestEstimationCountProvider;

public class LRTarrestStatFactory extends AbstractStatFactory {

	private static final String NAME = "BetaBin"; 
	private static final String DESC = "Minka Newton iteration method";
	
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
		LRTarrestEstimationCountProvider arrestCountProvider;
		
		if (conditions == 2) {
			arrestCountProvider = 
				new LRTarrestEstimationCountProvider(dirMultParameter.getMinkaEstimateParameter().getMaxIterations());
		} else {
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
