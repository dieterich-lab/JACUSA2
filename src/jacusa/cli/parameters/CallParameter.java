package jacusa.cli.parameters;

import jacusa.method.call.statistic.dirmult.DirichletMultinomialRobustCompoundError;
import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.cli.parameters.JACUSAConditionParameter;
import lib.data.AbstractData;
import lib.data.builder.factory.AbstractDataBuilderFactory;
import lib.data.has.hasPileupCount;

public class CallParameter<T extends AbstractData & hasPileupCount> 
extends AbstractParameter<T> implements hasStatisticCalculator<T> {

	private StatisticParameters<T> statisticParameter;
	
	public CallParameter(final int conditionSize, final AbstractDataBuilderFactory<T> dataBuilderFactory) {
		super(conditionSize, dataBuilderFactory);
		
		statisticParameter = new StatisticParameters<T>();
		statisticParameter.setStatisticCalculator(
				new DirichletMultinomialRobustCompoundError<T>(this));
	}
	
	@Override
	public AbstractConditionParameter<T> createConditionParameter(
			final AbstractDataBuilderFactory<T> dataBuilderFactory) {
		
		return new JACUSAConditionParameter<T>(dataBuilderFactory);
	}
	
	@Override
	public StatisticParameters<T> getStatisticParameters() {
		return statisticParameter;
	}

}