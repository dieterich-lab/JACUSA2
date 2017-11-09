package jacusa.cli.parameters;

import jacusa.method.call.statistic.dirmult.DirichletMultinomialRobustCompoundError;
import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;
import lib.data.builder.factory.AbstractDataBuilderFactory;
import lib.data.has.hasPileupCount;

public class CallParameters<T extends AbstractData & hasPileupCount> 
extends AbstractParameter<T> implements hasStatisticCalculator<T> {

	private StatisticParameters<T> statisticParameter;
	
	public CallParameters(final int conditionSize, final AbstractDataBuilderFactory<T> dataBuilderFactory) {
		super(conditionSize, dataBuilderFactory);
		
		statisticParameter = new StatisticParameters<T>();
		statisticParameter.setStatisticCalculator(
				new DirichletMultinomialRobustCompoundError<T>(this));
	}
	
	@Override
	public AbstractConditionParameter<T> createConditionParameter(
			final AbstractDataBuilderFactory<T> dataBuilderFactory) {
		// TODO 
		return null;
	}
	
	@Override
	public StatisticParameters<T> getStatisticParameters() {
		return statisticParameter;
	}

}