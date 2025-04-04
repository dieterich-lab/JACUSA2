package lib.stat.betabin;

import java.util.Arrays;


import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;

import lib.stat.AbstractStatFactory;
import lib.stat.dirmult.ProcessCommandLine;
import lib.stat.dirmult.options.CalculatePvalueOption;
import lib.stat.dirmult.options.EpsilonOptions;
import lib.stat.dirmult.options.MaxIterationsOption;
import lib.stat.dirmult.options.ShowAlphaOption;
import lib.stat.dirmult.options.SubsampleRunsOptions;
import lib.stat.estimation.provider.arrest.RobustRTarrestEstimationCountProvider;

public class RTarrestStatFactory extends AbstractStatFactory {

	public static final String NAME 	= "BetaBin";
	public static final String DESC 	= "Minka Newton iteration method";
	
	private final RTarrestBetaBinParameter dirMultParameter;
	
	public RTarrestStatFactory() {
		this(new RTarrestBetaBinParameter());
	}
	
	public RTarrestStatFactory(final RTarrestBetaBinParameter dirMultParameter) {
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
	
	public RTarrestStatFactory(
			final RTarrestBetaBinParameter dirMultParameter,
			final ProcessCommandLine processCommandLine) {
		super(
				Option.builder(NAME)
					.desc(DESC)
					.build(),
					processCommandLine);
		
		this.dirMultParameter = dirMultParameter;
	}

	@Override
	public RTarrestStat newInstance(double threshold, final int conditions) {
		if (conditions != 2) {
			throw new IllegalStateException("Number of conditions not supported: " + conditions);
		}

		return new RTarrestStat(
				threshold, 
				new RobustRTarrestEstimationCountProvider(
						dirMultParameter.getMinkaEstimateParameter().getMaxIterations()),
						dirMultParameter);
	}
	
}