package jacusa.cli.parameters;

import jacusa.io.writer.BED6callResultFormat;
import jacusa.method.call.statistic.dirmult.DirichletMultinomialRobustCompoundError;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.cli.parameter.JACUSAConditionParameter;
import lib.data.CallData;
import lib.data.result.StatisticResult;

public class CallParameter 
extends AbstractParameter<CallData, StatisticResult<CallData>> implements hasStatisticCalculator<CallData> {

	private StatisticFactory<CallData> statisticFactory;
	
	public CallParameter(final int conditionSize) {
		super(conditionSize);
		
		statisticFactory = new StatisticFactory<CallData>(new DirichletMultinomialRobustCompoundError<CallData>(this), 1.0);
	}
	
	@Override
	public void setDefaultValues() {
		setResultFormat(new BED6callResultFormat<CallData, StatisticResult<CallData>>(this));
	}
	
	@Override
	public AbstractConditionParameter<CallData> createConditionParameter(final int conditionIndex) {
		return new JACUSAConditionParameter<CallData>(conditionIndex);
	}
	
	@Override
	public StatisticFactory<CallData> getStatisticParameters() {
		return statisticFactory;
	}

}