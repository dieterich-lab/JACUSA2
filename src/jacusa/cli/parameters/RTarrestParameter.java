package jacusa.cli.parameters;

import jacusa.io.writer.BED6rtArrestResultFormat;
import jacusa.method.rtarrest.BetaBinomial;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.cli.parameter.JACUSAConditionParameter;
import lib.data.AbstractData;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasRTarrestCount;
import lib.data.has.hasReferenceBase;
import lib.data.result.StatisticResult;

/**
 * Class defines parameters and default values that are need for Reverse Transcription arrest (rt-arrest).
 */
public class RTarrestParameter<T extends AbstractData & hasBaseCallCount & hasReferenceBase & hasRTarrestCount>
extends AbstractParameter<T, StatisticResult<T>> 
implements HasStatisticParameters<T> {

	private StatisticParameter<T> statisticParameters;

	public RTarrestParameter(final int conditions) {
		super(conditions);
	}

	@Override
	public AbstractConditionParameter<T> createConditionParameter(final int conditionIndex) {
		return new JACUSAConditionParameter<T>(conditionIndex);
	}
	
	@Override
	public void setDefaultValues() {
		// default result format
		setResultFormat(new BED6rtArrestResultFormat<T, StatisticResult<T>>(this));
		// related to test-statistic
		statisticParameters.setStatisticCalculator(new BetaBinomial<T>());
		statisticParameters.setThreshold(1.0);
	}
	
	@Override
	public StatisticParameter<T> getStatisticParameters() {
		return statisticParameters;
	}

}
