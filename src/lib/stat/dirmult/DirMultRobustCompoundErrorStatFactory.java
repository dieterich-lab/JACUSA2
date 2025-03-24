package lib.stat.dirmult;

import java.util.Arrays;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;

import lib.stat.AbstractStat;
import lib.stat.AbstractStatFactory;
import lib.stat.dirmult.options.CalculatePvalueOption;
import lib.stat.dirmult.options.EpsilonOptions;
import lib.stat.dirmult.options.MaxIterationsOption;
import lib.stat.dirmult.options.ShowAlphaOption;
import lib.stat.dirmult.options.SubsampleRunsOptions;
import lib.stat.estimation.provider.ConditionEstimateProvider;
import lib.stat.estimation.provider.pileup.InSilicoEstimationPileupProvider;
import lib.stat.estimation.provider.pileup.RobustEstimationPileupProvider;

public class DirMultRobustCompoundErrorStatFactory
extends AbstractStatFactory {

	private final CallDirMultParameter dirMultParameter;
	
	public DirMultRobustCompoundErrorStatFactory() {
		this(new CallDirMultParameter());
	}
	
	public DirMultRobustCompoundErrorStatFactory(final CallDirMultParameter dirMultParameter) {
		this(
				dirMultParameter,
				new ProcessCommandLine(
						new DefaultParser(),
						Arrays.asList(
								new EpsilonOptions(dirMultParameter.getMinkaEstimateParameter()),
								new ShowAlphaOption(dirMultParameter),
								new MaxIterationsOption(dirMultParameter.getMinkaEstimateParameter()),
								new SubsampleRunsOptions(dirMultParameter),
								new CalculatePvalueOption(dirMultParameter))));
	}
	
	public DirMultRobustCompoundErrorStatFactory(final CallDirMultParameter dirMultParameter, final ProcessCommandLine processCommandLine) {
		super(Option.builder("DirMult")
				.desc(DirMultCompoundErrorStatFactory.DESC + "\n"+
						"Adjusts variant condition")
				.build(),
				processCommandLine);
		
		this.dirMultParameter 	= dirMultParameter;
	}

	@Override
	public AbstractStat newInstance(double threshold, final int conditions) {
		ConditionEstimateProvider dirMultPileupCountProvider;
		switch (conditions) {
		case 1:
			dirMultPileupCountProvider = new InSilicoEstimationPileupProvider(
					dirMultParameter.calcPValue(),
					dirMultParameter.getMinkaEstimateParameter().getMaxIterations(),
					dirMultParameter.getEstimatedError());
			break;
			
		case 2:
			dirMultPileupCountProvider = new RobustEstimationPileupProvider(
					dirMultParameter.calcPValue(),
					dirMultParameter.getMinkaEstimateParameter().getMaxIterations(),
					dirMultParameter.getEstimatedError()); 
			break;

		default:
			throw new IllegalStateException("Number of conditions not supported: " + conditions);
		}
		
		return new CallStat(threshold, dirMultPileupCountProvider, dirMultParameter);
	}
	
}