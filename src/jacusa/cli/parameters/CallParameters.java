package jacusa.cli.parameters;

import jacusa.method.call.statistic.dirmult.DirichletMultinomialRobustCompoundError;
import jacusa.pileup.builder.AbstractDataBuilderFactory;
import lib.cli.parameters.AbstractParameters;
import lib.data.BaseQualData;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class CallParameters<T extends BaseQualData> 
extends AbstractParameters<T> 
implements hasStatisticCalculator<T> {

	private StatisticParameters<T> statisticParameters;
	
	public CallParameters(final int conditions, final AbstractDataBuilderFactory<T> dataBuilderFactory) {
		super(conditions, dataBuilderFactory);
		
		statisticParameters = new StatisticParameters<T>();
		statisticParameters.setStatisticCalculator(
				new DirichletMultinomialRobustCompoundError<T>(this));
	}

	@Override
	public StatisticParameters<T> getStatisticParameters() {
		return statisticParameters;
	}

}