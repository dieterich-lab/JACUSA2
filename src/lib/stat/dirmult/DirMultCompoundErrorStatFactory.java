package lib.stat.dirmult;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import lib.io.ResultFormat;
import lib.stat.AbstractStatFactory;
import lib.stat.sample.provider.EstimationSampleProvider;
import lib.stat.sample.provider.pileup.DefaultEstimationSamplePileupProvider;
import lib.stat.sample.provider.pileup.InSilicoEstimationSamplePileupProvider;

public class DirMultCompoundErrorStatFactory
extends AbstractStatFactory {

	private static final String NAME 	= "DirMultCE";
	public static final String DESC 	= "Compound Error (estimated error {" + DirMultParameter.ESTIMATED_ERROR + "} + phred score)";
	
	private final CallDirMultParameter dirMultParameter;
	private final DirMultCLIprocessing CLIprocessing;
	
	public DirMultCompoundErrorStatFactory(final ResultFormat resultFormat) {

		super(Option.builder(NAME)
				.desc(DESC)
				.build());
		dirMultParameter 	= new CallDirMultParameter();
		CLIprocessing 		= new CallDirMultCLIProcessing(resultFormat, dirMultParameter);
	}

	@Override
	public CallStat newInstance(double threshold, final int conditions) {
		EstimationSampleProvider dirMultPileupCountProvider;
		switch (conditions) {
		case 1:
			dirMultPileupCountProvider = new InSilicoEstimationSamplePileupProvider(
					dirMultParameter.getMinkaEstimateParameter().getMaxIterations(),
					dirMultParameter.getEstimatedError());
			break;
			
		case 2:
			dirMultPileupCountProvider = new DefaultEstimationSamplePileupProvider(
					dirMultParameter.getMinkaEstimateParameter().getMaxIterations(),
					dirMultParameter.getEstimatedError()); 
			break;

		default:
			throw new IllegalStateException("Number of conditions not supported: " + conditions);
		}
		return new CallStat(threshold, dirMultPileupCountProvider, dirMultParameter);
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