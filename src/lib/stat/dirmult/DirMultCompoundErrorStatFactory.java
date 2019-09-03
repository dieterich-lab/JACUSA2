package lib.stat.dirmult;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import lib.io.ResultFormat;
import lib.stat.AbstractStatFactory;
import lib.stat.estimation.provider.EstimationContainerProvider;
import lib.stat.estimation.provider.pileup.DefaultEstimationPileupProvider;
import lib.stat.estimation.provider.pileup.InSilicoEstimationPileupProvider;

public class DirMultCompoundErrorStatFactory
extends AbstractStatFactory {

	private static final String NAME 	= "DirMultCE";
	public static final String DESC 	= "Compound Error (estimated error {" + DirMultParameter.ESTIMATED_ERROR + "} + phred score)";
	
	private final CallDirMultParameter dirMultPrm;
	private final DirMultCLIprocessing CLIproc;
	
	public DirMultCompoundErrorStatFactory(final ResultFormat resFormat) {

		super(Option.builder(NAME)
				.desc(DESC)
				.build());
		dirMultPrm 	= new CallDirMultParameter();
		CLIproc 	= new CallDirMultCLIProcessing(resFormat, dirMultPrm);
	}

	@Override
	public CallStat newInstance(double threshold, final int conditions) {
		EstimationContainerProvider dirMultPileupCountProvider;
		switch (conditions) {
		case 1:
			dirMultPileupCountProvider = new InSilicoEstimationPileupProvider(
					dirMultPrm.isCalcPValue(),
					dirMultPrm.getMinkaEstimateParameter().getMaxIterations(),
					dirMultPrm.getEstimatedError());
			break;
			
		case 2:
			dirMultPileupCountProvider = new DefaultEstimationPileupProvider(
					dirMultPrm.isCalcPValue(),
					dirMultPrm.getMinkaEstimateParameter().getMaxIterations(),
					dirMultPrm.getEstimatedError()); 
			break;

		default:
			throw new IllegalStateException("Number of conditions not supported: " + conditions);
		}
		return new CallStat(threshold, dirMultPileupCountProvider, dirMultPrm);
	}

	@Override
	protected Options getOptions() {
		return CLIproc.getOptions();
	}
	
	@Override
	public void processCLI(final CommandLine cmd) {
		CLIproc.processCLI(cmd);
	}
	
}