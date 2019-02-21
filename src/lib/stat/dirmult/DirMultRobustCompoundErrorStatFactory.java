package lib.stat.dirmult;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import lib.io.ResultFormat;
import lib.stat.AbstractStatFactory;
import lib.stat.sample.provider.EstimationSampleProvider;
import lib.stat.sample.provider.pileup.InSilicoEstimationSamplePileupProvider;
import lib.stat.sample.provider.pileup.RobustEstimationSamplePileupProvider;

public class DirMultRobustCompoundErrorStatFactory
extends AbstractStatFactory {

	private final CallDirMultParameter dirMultParameter;
	private final DirMultCLIprocessing CLIprocessing;
	
	public DirMultRobustCompoundErrorStatFactory(final ResultFormat resultFormat) {
		super(Option.builder("DirMult")
				.desc(DirMultCompoundErrorStatFactory.DESC + "\n"+
						"Adjusts variant condition")
				.build());
		
		dirMultParameter 	= new CallDirMultParameter();
		CLIprocessing 		= new CallDirMultCLIProcessing(resultFormat, dirMultParameter);
	}

	@Override
	public CallStat newInstance(final int conditions) {
		EstimationSampleProvider dirMultPileupCountProvider;
		switch (conditions) {
		case 1:
			dirMultPileupCountProvider = new InSilicoEstimationSamplePileupProvider(
					dirMultParameter.getMinkaEstimateParameter().getMaxIterations(),
					dirMultParameter.getEstimatedError());
			break;
			
		case 2:
			dirMultPileupCountProvider = new RobustEstimationSamplePileupProvider(
					dirMultParameter.getMinkaEstimateParameter().getMaxIterations(),
					dirMultParameter.getEstimatedError()); 
			break;

		default:
			throw new IllegalStateException("Number of conditions not supported: " + conditions);
		}
		return new CallStat(dirMultPileupCountProvider, dirMultParameter);
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