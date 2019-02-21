package lib.stat.betabin;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import lib.stat.AbstractStatFactory;
import lib.stat.dirmult.DirMultCLIprocessing;
import lib.stat.sample.provider.arrest.RTarrestCountSampleProvider;

public class RTarrestStatFactory extends AbstractStatFactory {

	private static final String NAME 	= "BetaBin";
	public static final String DESC 	= "Minka Newton iteration method";
	
	private final RTarrestBetaBinParameter dirMultParameter;
	private final DirMultCLIprocessing CLIprocessing;
	
	public RTarrestStatFactory() {

		super(Option.builder(NAME)
				.desc(DESC)
				.build());
		
		dirMultParameter 	= new RTarrestBetaBinParameter();
		CLIprocessing 		= new RTarrestBetaBinCLIProcessing(dirMultParameter);
	}

	@Override
	public RTarrestStat newInstance(double threshold, final int conditions) {
		RTarrestCountSampleProvider arrestCountProvider;
		switch (conditions) {
		case 2:
			arrestCountProvider = 
			new RTarrestCountSampleProvider(dirMultParameter.getMinkaEstimateParameter().getMaxIterations());
			break;

		default:
			throw new IllegalStateException("Number of conditions not supported: " + conditions);
		}
		return new RTarrestStat(threshold, arrestCountProvider, dirMultParameter);
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