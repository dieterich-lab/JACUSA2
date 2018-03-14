package jacusa.cli.parameters;

import jacusa.io.writer.BED6lrtArrestResultFormat2;
import jacusa.method.rtarrest.BetaBinomial;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.cli.parameter.JACUSAConditionParameter;
import lib.data.AbstractData;
import lib.data.has.hasLRTarrestCount;
import lib.data.has.hasReferenceBase;
import lib.data.result.StatisticResult;

/**
 * Class defines parameters and default values that are need for Linked Reverse Transcription arrest (lrt-arrest).
 */
public class LRTarrestParameter<T extends AbstractData & hasReferenceBase & hasLRTarrestCount>
extends AbstractParameter<T, StatisticResult<T>> 
implements HasStatisticParameters<T> {

	private StatisticParameter<T> statisticParameters;

	public LRTarrestParameter(final int conditions) {
		super(conditions);
		statisticParameters = new StatisticParameter<T>();
	}

	@Override
	public AbstractConditionParameter<T> createConditionParameter(final int conditionIndex) {
		return new JACUSAConditionParameter<T>(conditionIndex);
	}
	
	@Override
	public void setDefaultValues() {
		// default output format
		setResultFormat(new BED6lrtArrestResultFormat2<T, StatisticResult<T>>(this));
		// test-statistic related
		statisticParameters.setStatisticCalculator(new BetaBinomial<T>());
		statisticParameters.setThreshold(1.0);
	}
	
	@Override
	public StatisticParameter<T> getStatisticParameters() {
		return statisticParameters;
	}

}
