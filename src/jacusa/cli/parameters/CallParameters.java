package jacusa.cli.parameters;

import jacusa.data.BaseQualData;
import jacusa.method.call.statistic.dirmult.DirichletMultinomialRobustCompoundError;
import jacusa.pileup.builder.AbstractDataBuilderFactory;

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