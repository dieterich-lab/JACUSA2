package jacusa.cli.parameters;

import jacusa.io.format.rtarrest.BED6rtArrestResultFormat;
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
		// related to test-statistic
		statisticParameters = new StatisticParameter<RTarrestData>(
				new BetaBinomial<RTarrestData>(), Double.NaN);
		// default result format
		setResultFormat(new BED6rtArrestResultFormat<RTarrestData, StatisticResult<RTarrestData>>(this));
	}

	@Override
	public AbstractConditionParameter<RTarrestData> createConditionParameter(final int conditionIndex) {
		final AbstractConditionParameter<RTarrestData> p = new JACUSAConditionParameter<RTarrestData>(conditionIndex);
		p.setMinBASQ((byte)0);
		return p;
	}
	
	@Override
	public StatisticParameter<RTarrestData> getStatisticParameters() {
		return statisticParameters;
	}

}
