package jacusa.cli.parameters;

import jacusa.io.writer.BED6callResultFormat;
import jacusa.method.call.statistic.dirmult.DirichletMultinomialRobustCompoundError;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.cli.parameter.JACUSAConditionParameter;
import lib.data.CallData;
import lib.data.result.StatisticResult;

/**
 * Parameters specific to call method(s)
 * @author Michael Piechotta
 */
public class CallParameter 
extends AbstractParameter<CallData, StatisticResult<CallData>> 
implements hasStatisticCalculator<CallData> {

	private final StatisticParameter<CallData> statisticParameter;
	
	public CallParameter(final int conditionSize) {
		super(conditionSize);
		statisticParameter = new StatisticParameter<CallData>();
		
	}
	
	@Override
	public void setDefaultValues() {
		// set default result
		setResultFormat(new BED6callResultFormat<CallData, StatisticResult<CallData>>(this));

		// set default DirMul and threshold to 1.0
		statisticParameter.setStatisticCalculator(
				new DirichletMultinomialRobustCompoundError<CallData>(this));
		statisticParameter.setThreshold(1.0);
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
