package lib.stat.betabin;

import java.util.Arrays;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;

import lib.cli.parameter.GeneralParameter;
import lib.stat.AbstractStatFactory;
import lib.stat.dirmult.ProcessCommandLine;
import lib.stat.dirmult.options.CalculatePvalueOption;
import lib.stat.dirmult.options.EpsilonOptions;
import lib.stat.dirmult.options.MaxIterationsOption;
import lib.stat.dirmult.options.ShowAlphaOption;
import lib.stat.dirmult.options.SubsampleRunsOptions;
import lib.stat.estimation.provider.arrest.AbstractRTarrestEstimationCountProvider;
import lib.stat.estimation.provider.arrest.RobustRTarrestEstimationCountProvider;

public class RTarrestStatFactory extends AbstractStatFactory {

	private static final String NAME 	= "BetaBin";
	public static final String DESC 	= "Minka Newton iteration method";
	
	private final RTarrestBetaBinParameter dirMultParameter;
	
	public RTarrestStatFactory(final GeneralParameter parameters) {
		this(parameters, new RTarrestBetaBinParameter(parameters));
	}
	
	public RTarrestStatFactory(
			final GeneralParameter parameter,
			final RTarrestBetaBinParameter dirMultParameter) {
		this(
				parameter,
				dirMultParameter,
				new ProcessCommandLine(
						new DefaultParser(),
						Arrays.asList(
								new EpsilonOptions(dirMultParameter.getMinkaEstimateParameter()),
								new ShowAlphaOption(parameter, dirMultParameter),
								new MaxIterationsOption(dirMultParameter.getMinkaEstimateParameter()),
								new SubsampleRunsOptions(parameter, dirMultParameter),
								new CalculatePvalueOption(parameter, dirMultParameter))));
	}
	
	public RTarrestStatFactory(
			final GeneralParameter parameters,
			final RTarrestBetaBinParameter dirMultParameter,
			final ProcessCommandLine processCommandLine) {
		super(parameters,
				Option.builder(NAME)
				.desc(DESC)
				.build(),
				processCommandLine);
		
		this.dirMultParameter = dirMultParameter;
	}

	@Override
	public RTarrestStat newInstance(double threshold, final int conditions) {
		AbstractRTarrestEstimationCountProvider arrestCountProvider;
		
		if (conditions == 2) {
			arrestCountProvider = 
					new RobustRTarrestEstimationCountProvider(dirMultParameter.getMinkaEstimateParameter().getMaxIterations());
		} else {
			throw new IllegalStateException("Number of conditions not supported: " + conditions);
		}

		return new RTarrestStat(threshold, arrestCountProvider, dirMultParameter);
	}
	
}