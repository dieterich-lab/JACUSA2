package jacusa.cli.parameters;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.cli.parameters.JACUSAConditionParameter;
import lib.data.AbstractData;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReadInfoCount;
import lib.data.result.StatisticResult;

public class RTArrestParameter<T extends AbstractData & hasBaseCallCount & hasReadInfoCount>
extends AbstractParameter<T, StatisticResult<T>> 
implements hasStatisticCalculator<T> {

	private StatisticFactory<T> statisticParameters;

	public RTArrestParameter(final int conditions) {
		super(conditions);
		
		statisticParameters = new StatisticFactory<T>(new BetaBinomial<T>(), 1.0);
	}

	@Override
	public AbstractConditionParameter<T> createConditionParameter() {
		return new JACUSAConditionParameter<T>();
	}
	
	@Override
	public StatisticFactory<T> getStatisticParameters() {
		return statisticParameters;
	}

}
