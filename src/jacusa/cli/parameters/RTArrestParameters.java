package jacusa.cli.parameters;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.cli.parameters.JACUSAConditionParameter;
import lib.data.AbstractData;
import lib.data.builder.factory.AbstractDataBuilderFactory;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReadInfoCount;

public class RTArrestParameters<T extends AbstractData & hasBaseCallCount & hasReadInfoCount>
extends AbstractParameter<T> implements hasStatisticCalculator<T> {

	private StatisticParameters<T> statisticParameters;

	public RTArrestParameters(final int conditions, final AbstractDataBuilderFactory<T> dataBuilderFactory) {
		super(conditions, dataBuilderFactory);
		
		statisticParameters = new StatisticParameters<T>();
		statisticParameters.setStatisticCalculator(new BetaBinomial<T>());
	}

	@Override
	public AbstractConditionParameter<T> createConditionParameter(
			AbstractDataBuilderFactory<T> dataBuilderFactory) {

		return new JACUSAConditionParameter<T>(dataBuilderFactory);
	}
	
	@Override
	public StatisticParameters<T> getStatisticParameters() {
		return statisticParameters;
	}

}
