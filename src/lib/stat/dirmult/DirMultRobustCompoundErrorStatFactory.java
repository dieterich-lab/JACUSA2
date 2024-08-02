package lib.stat.dirmult;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import lib.io.ResultFormat;
import lib.stat.AbstractStat;
import lib.stat.AbstractStatFactory;
import lib.stat.estimation.provider.EstimationContainerProvider;
import lib.stat.estimation.provider.pileup.InSilicoEstimationPileupProvider;
import lib.stat.estimation.provider.pileup.RobustEstimationPileupProvider;

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
	public AbstractStat newInstance(double threshold, final int conditions) {
		EstimationContainerProvider dirMultPileupCountProvider;
		switch (conditions) {
		case 1:
			dirMultPileupCountProvider = new InSilicoEstimationPileupProvider(
					dirMultParameter.isCalcPValue(),
					dirMultParameter.getMinkaEstimateParameter().getMaxIterations(),
					dirMultParameter.getEstimatedError());
			break;
			
		case 2:
			dirMultPileupCountProvider = new RobustEstimationPileupProvider(
					dirMultParameter.isCalcPValue(),
					dirMultParameter.getMinkaEstimateParameter().getMaxIterations(),
					dirMultParameter.getEstimatedError()); 
			break;

		default:
			throw new IllegalStateException("Number of conditions not supported: " + conditions);
		}
		
		final CallStat callStat = new CallStat(threshold, dirMultPileupCountProvider, dirMultParameter);
		if (dirMultParameter.getRuns() > 0) {
			return new AdvancedCallStat(callStat, dirMultParameter.getRuns(), dirMultParameter.getLimit());
		}
		
		return callStat;
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