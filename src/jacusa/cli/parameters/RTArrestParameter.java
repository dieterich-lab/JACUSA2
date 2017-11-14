package jacusa.cli.parameters;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.cli.parameters.JACUSAConditionParameter;
import lib.data.AbstractData;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReadInfoCount;

public class RTArrestParameter<T extends AbstractData & hasBaseCallCount & hasReadInfoCount>
extends AbstractParameter<T> implements hasStatisticCalculator<T> {

	private StatisticParameters<T> statisticParameters;

	public RTArrestParameter(final int conditions) {
		super(conditions);
		
		statisticParameters = new StatisticParameters<T>();
		statisticParameters.setStatisticCalculator(new BetaBinomial<T>());
	}

	@Override
	public AbstractConditionParameter<T> createConditionParameter() {
		return new JACUSAConditionParameter<T>();
	}
	
	@Override
	public StatisticParameters<T> getStatisticParameters() {
		return statisticParameters;
	}

}
