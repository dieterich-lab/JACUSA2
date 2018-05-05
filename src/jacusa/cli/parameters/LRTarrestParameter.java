package jacusa.cli.parameters;

import jacusa.io.format.lrtarrest.BED6lrtArrestResultFormat2;
import jacusa.method.rtarrest.BetaBinomial;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.cli.parameter.JACUSAConditionParameter;
import lib.data.LRTarrestData;
import lib.data.result.StatisticResult;

/**
 * Class defines parameters and default values that are need for Linked Reverse Transcription arrest (lrt-arrest).
 */
public class LRTarrestParameter
extends AbstractParameter<LRTarrestData, StatisticResult<LRTarrestData>> 
implements HasStatisticParameters<LRTarrestData> {

	private StatisticParameter<LRTarrestData> statisticParameters;

	public LRTarrestParameter(final int conditions) {
		super(conditions);
		// test-statistic related
		statisticParameters = new StatisticParameter<LRTarrestData>(
				new BetaBinomial<LRTarrestData>(), 1.0);
		// default output format
		setResultFormat(new BED6lrtArrestResultFormat2<LRTarrestData, StatisticResult<LRTarrestData>>(this));

		setActiveWindowSize(1000);
	}

	@Override
	public AbstractConditionParameter<LRTarrestData> createConditionParameter(final int conditionIndex) {
		return new JACUSAConditionParameter<LRTarrestData>(conditionIndex);
	}
	
	@Override
	public StatisticParameter<LRTarrestData> getStatisticParameters() {
		return statisticParameters;
	}

}
