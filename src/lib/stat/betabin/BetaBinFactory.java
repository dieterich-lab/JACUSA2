package lib.stat.betabin;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import lib.stat.AbstractStat;
import lib.stat.AbstractStatFactory;
import lib.stat.dirmult.DirMult;
import lib.stat.dirmult.DirMultCLIprocessing;
import lib.stat.sample.provider.EstimationSampleProvider;
import lib.stat.sample.provider.arrest.RTarrestCountSampleProvider;

public class BetaBinFactory
extends AbstractStatFactory {

	private final ArrestDirMultBinParameter dirMultParameter;
	private final DirMultCLIprocessing CLIprocessing;
	
	public BetaBinFactory() {
		super(Option.builder("BetaBin")
				.desc("Minka Newton iteration method")
				.build());

		dirMultParameter 	= new ArrestDirMultBinParameter();
		CLIprocessing 		= new ArrestBetaBinCLIProcessing(dirMultParameter);
	}

	@Override
	public AbstractStat newInstance(final int conditions) {
		EstimationSampleProvider arrestCountProvider;
		// TODO lrt-arrest
		switch (conditions) {
		case 2:
			arrestCountProvider = new RTarrestCountSampleProvider(dirMultParameter.getMinkaEstimateParameter().getMaxIterations()); 
			break;

		default:
			throw new IllegalStateException("Number of conditions not supported: " + conditions);
		}
		return new DirMult(arrestCountProvider, dirMultParameter);
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