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

	public final static String NAME = "DirMult";
	
	private final CallEstimationParameter dirMultParameter;
	
	public DirMultRobustCompoundErrorStatFactory() {
		this(new CallEstimationParameter());
	}
	
	public DirMultRobustCompoundErrorStatFactory(
			final CallEstimationParameter dirMultParameter) {
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
	
	public DirMultRobustCompoundErrorStatFactory(
			final CallEstimationParameter dirMultParameter,
			final ProcessCommandLine processCommandLine) {
		super(
				Option.builder(NAME)
					.desc(DirMultCompoundErrorStatFactory.DESC + "\n" + "Adjusts variant condition")
					.build(),
				processCommandLine);
		
		this.dirMultParameter 	= dirMultParameter;
	}

	@Override
	public AbstractStat newInstance(double threshold, int conditions) {
		ConditionEstimateProvider dirMultPileupCountProvider;
		switch (conditions) {
		case 1:
			dirMultPileupCountProvider = new InSilicoEstimationPileupProvider(
					dirMultParameter.calcPValue(),
					dirMultParameter.getMinkaParameter().getMaxIterations(),
					dirMultParameter.getEstimatedError());
			break;
			
		case 2:
			dirMultPileupCountProvider = new RobustEstimationPileupProvider(
					dirMultParameter.calcPValue(),
					dirMultParameter.getMinkaParameter().getMaxIterations(),
					dirMultParameter.getEstimatedError()); 
			break;

		default:
			throw new IllegalStateException("Number of conditions not supported: " + conditions);
		}
		
		return new CallStat(threshold, dirMultPileupCountProvider, dirMultParameter);
	}
	
}