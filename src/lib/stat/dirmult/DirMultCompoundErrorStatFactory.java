package lib.stat.dirmult;

import java.util.Arrays;


import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;

import lib.stat.AbstractStatFactory;
import lib.stat.dirmult.options.CalculatePvalueOption;
import lib.stat.dirmult.options.EpsilonOptions;
import lib.stat.dirmult.options.MaxIterationsOption;
import lib.stat.dirmult.options.ShowAlphaOption;
import lib.stat.dirmult.options.SubsampleRunsOptions;
import lib.stat.estimation.provider.ConditionEstimateProvider;
import lib.stat.estimation.provider.pileup.DefaultEstimationPileupProvider;
import lib.stat.estimation.provider.pileup.InSilicoEstimationPileupProvider;

public class DirMultCompoundErrorStatFactory
extends AbstractStatFactory {

	private static final String NAME 	= "DirMultCE";
	public static final String DESC 	= "Compound Error (estimated error {" + EstimationParameter.ESTIMATED_ERROR + "} + phred score)";
	
	private final CallEstimationParameter dirMultParameter;
	
	public DirMultCompoundErrorStatFactory() {
		this(new CallEstimationParameter());
	}
	
	public DirMultCompoundErrorStatFactory(final CallEstimationParameter dirMultParameter) {
		this(
				dirMultParameter,
				new ProcessCommandLine(
						new DefaultParser(),
						Arrays.asList(
								new EpsilonOptions(dirMultParameter.getMinkaParameter()),
								new ShowAlphaOption(dirMultParameter),
								new MaxIterationsOption(dirMultParameter.getMinkaParameter()),
								new SubsampleRunsOptions(dirMultParameter),
								new CalculatePvalueOption(dirMultParameter))));
	}
	
	public DirMultCompoundErrorStatFactory(
			final CallEstimationParameter dirMultParameter, final ProcessCommandLine processCommmandLine) {
		super(
				Option.builder(NAME)
					.desc(DESC)
					.build(),
					processCommmandLine);
		this.dirMultParameter = dirMultParameter;
	}

	@Override
	public CallStat newInstance(double threshold, final int conditions) {
		ConditionEstimateProvider pileupCountProvider;
		switch (conditions) {
		case 1:
			pileupCountProvider = new InSilicoEstimationPileupProvider(
					dirMultParameter.calcPValue(),
					dirMultParameter.getMinkaParameter().getMaxIterations(),
					dirMultParameter.getEstimatedError());
			break;
			
		case 2:
			pileupCountProvider = new DefaultEstimationPileupProvider(
					dirMultParameter.calcPValue(),
					dirMultParameter.getMinkaParameter().getMaxIterations(),
					dirMultParameter.getEstimatedError()); 
			break;

		default:
			throw new IllegalStateException("Number of conditions not supported: " + conditions);
		}
		return new CallStat(threshold, pileupCountProvider, dirMultParameter);
	}
	
}