package jacusa.cli.parameters;

import jacusa.io.writer.BED6rtArrestResultFormat;
import jacusa.method.rtarrest.BetaBinomial;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.cli.parameter.JACUSAConditionParameter;
import lib.data.RTarrestData;
import lib.data.result.StatisticResult;

/**
 * Class defines parameters and default values that are need for Reverse Transcription arrest (rt-arrest).
 */
public class RTarrestParameter
extends AbstractParameter<RTarrestData, StatisticResult<RTarrestData>> 
implements HasStatisticParameters<RTarrestData> {

	private StatisticParameter<RTarrestData> statisticParameters;

	public RTarrestParameter(final int conditions) {
		super(conditions);
		statisticParameters = new StatisticParameter<RTarrestData>();
		// default result format
		setResultFormat(new BED6rtArrestResultFormat<RTarrestData, StatisticResult<RTarrestData>>(this));
		// related to test-statistic
		statisticParameters.setStatisticCalculator(new BetaBinomial<RTarrestData>());
		statisticParameters.setThreshold(1.0);
	}

	@Override
	public AbstractConditionParameter<RTarrestData> createConditionParameter(final int conditionIndex) {
		return new JACUSAConditionParameter<RTarrestData>(conditionIndex);
	}
	
	@Override
	public StatisticParameter<RTarrestData> getStatisticParameters() {
		return statisticParameters;
	}

}
