package lib.stat.dirmult;

import java.util.Arrays;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;

import lib.cli.parameter.GeneralParameter;
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
	public static final String DESC 	= "Compound Error (estimated error {" + DirMultParameter.ESTIMATED_ERROR + "} + phred score)";
	
	private final CallDirMultParameter dirMultParameter;
	
	public DirMultCompoundErrorStatFactory(final GeneralParameter parameters) {
		this(parameters, new CallDirMultParameter(parameters));
	}
	
	public DirMultCompoundErrorStatFactory(
			final GeneralParameter parameter,
			final CallDirMultParameter dirMultParameter) {
		this(
				parameter,
				dirMultParameter,
				new ProcessCommandLine(
						new DefaultParser(),
						Arrays.asList(
								new EpsilonOptions(dirMultParameter.getMinkaEstimateParameter()),
								new ShowAlphaOption(parameter, dirMultParameter),
								new MaxIterationsOption(dirMultParameter.getMinkaEstimateParameter()),
								new SubsampleRunsOptions(parameter,dirMultParameter),
								new CalculatePvalueOption(parameter,dirMultParameter))));
	}
	
	public DirMultCompoundErrorStatFactory(
			final GeneralParameter parameters,
			final CallDirMultParameter dirMultParameter, final ProcessCommandLine processCommmandLine) {
		super(
				parameters,
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
					dirMultParameter.getMinkaEstimateParameter().getMaxIterations(),
					dirMultParameter.getEstimatedError());
			break;
			
		case 2:
			pileupCountProvider = new DefaultEstimationPileupProvider(
					dirMultParameter.calcPValue(),
					dirMultParameter.getMinkaEstimateParameter().getMaxIterations(),
					dirMultParameter.getEstimatedError()); 
			break;

		default:
			throw new IllegalStateException("Number of conditions not supported: " + conditions);
		}
		return new CallStat(threshold, pileupCountProvider, dirMultParameter);
	}
	
}