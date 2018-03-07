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

public class LinkageRTArrestParameter<T extends AbstractData & hasReferenceBase & hasLRTarrestCount>
extends AbstractParameter<T, StatisticResult<T>> 
implements hasStatisticCalculator<T> {

	private StatisticParameter<T> statisticParameters;

	public LinkageRTArrestParameter(final int conditions) {
		super(conditions);
		
		statisticParameters = new StatisticParameter<T>(new BetaBinomial<T>(), 1.0);
	}

	@Override
	public AbstractConditionParameter<T> createConditionParameter(final int conditionIndex) {
		return new JACUSAConditionParameter<T>(conditionIndex);
	}
	
	@Override
	public void setDefaultValues() {
		setResultFormat(new BED6lrtArrestResultFormat2<T, StatisticResult<T>>(this));
	}
	
	@Override
	public StatisticParameter<T> getStatisticParameters() {
		return statisticParameters;
	}

}
