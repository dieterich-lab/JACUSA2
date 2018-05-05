package jacusa.cli.parameters;

import jacusa.io.format.call.BED6callResultFormat;
import jacusa.method.call.statistic.dirmult.DirichletMultinomialRobustCompoundError;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.cli.parameter.JACUSAConditionParameter;
import lib.data.CallData;
import lib.data.result.StatisticResult;

/**
 * Parameters specific to call method(s).
 */
public class CallParameter 
extends AbstractParameter<CallData, StatisticResult<CallData>> 
implements HasStatisticParameters<CallData> {

	private final StatisticParameter<CallData> statisticParameter;
	
	public CallParameter(final int conditionSize) {
		super(conditionSize);
		// set default DirMul and threshold to 1.0
		statisticParameter = new StatisticParameter<CallData>(
				new DirichletMultinomialRobustCompoundError<CallData>(this), 
				1.0);
		// set default result
		setResultFormat(new BED6callResultFormat<CallData, StatisticResult<CallData>>(this));
	}
	
	@Override
	public AbstractConditionParameter<CallData> createConditionParameter(final int conditionIndex) {
		return new JACUSAConditionParameter<CallData>(conditionIndex);
	}
	
	@Override
	public StatisticParameter<CallData> getStatisticParameters() {
		return statisticParameter;
	}

}
