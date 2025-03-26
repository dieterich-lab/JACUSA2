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
import lib.stat.estimation.provider.arrest.LRTarrestEstimationCountProvider;

public class LRTarrestStatFactory extends AbstractStatFactory {

	private static final String NAME = "BetaBin"; 
	private static final String DESC = "Minka Newton iteration method";
	
	private final LRTarrestBetaBinParameter dirMultParameter;
	
	public LRTarrestStatFactory(final GeneralParameter parameters) {
		this(parameters, new LRTarrestBetaBinParameter(parameters));
	}
	
	public LRTarrestStatFactory(
			final GeneralParameter parameter,
			final LRTarrestBetaBinParameter dirMultParameter) {
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
	
	public LRTarrestStatFactory(
			final GeneralParameter parameters,
			final LRTarrestBetaBinParameter dirMultParameter,
			final ProcessCommandLine processCommandLine) {
		super(
				parameters,
				Option.builder(NAME)
					.desc(DESC)
					.build(),
					processCommandLine);
		
		this.dirMultParameter 	= dirMultParameter;
	}

	@Override
	public LRTarrestStat newInstance(double threshold, final int conditions) {
		LRTarrestEstimationCountProvider arrestCountProvider;
		
		if (conditions == 2) {
			arrestCountProvider = 
				new LRTarrestEstimationCountProvider(dirMultParameter.getMinkaEstimateParameter().getMaxIterations());
		} else {
			throw new IllegalStateException("Number of conditions not supported: " + conditions);
		}
		
		return new LRTarrestStat(threshold, arrestCountProvider, dirMultParameter);
	}

}
