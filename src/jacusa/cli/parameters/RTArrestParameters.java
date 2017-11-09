package jacusa.cli.parameters;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.BaseQualReadInfoData;
import lib.data.builder.factory.AbstractDataBuilderFactory;

public class RTArrestParameters<T extends BaseQualReadInfoData>
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
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public StatisticParameters<T> getStatisticParameters() {
		return statisticParameters;
	}

}
