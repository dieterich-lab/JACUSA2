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
import lib.stat.estimation.provider.arrest.LRTarrestEstimationCountProvider;

public class LRTarrestStatFactory extends AbstractStatFactory {

	public static final String NAME = "BetaBin"; 
	private static final String DESC = "Minka Newton iteration method";
	
	private final LRTarrestBetaBinParameter dirMultParameter;
	
	public LRTarrestStatFactory() {
		this(new LRTarrestBetaBinParameter());
	}
	
	public LRTarrestStatFactory(final LRTarrestBetaBinParameter dirMultParameter) {
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
	
	public LRTarrestStatFactory(
			final LRTarrestBetaBinParameter dirMultParameter,
			final ProcessCommandLine processCommandLine) {
		super(
				Option.builder(NAME)
					.desc(DESC)
					.build(),
					processCommandLine);
		
		this.dirMultParameter 	= dirMultParameter;
	}

	@Override
	public LRTarrestStat newInstance(double threshold, final int conditions) {
		if (conditions != 2) {
			throw new IllegalStateException("Number of conditions not supported: " + conditions);
		}
		
		final LRTarrestEstimationCountProvider arrestCountProvider = 
				new LRTarrestEstimationCountProvider(dirMultParameter.getMinkaEstimateParameter().getMaxIterations());
		return new LRTarrestStat(threshold, arrestCountProvider, dirMultParameter);
	}

}
