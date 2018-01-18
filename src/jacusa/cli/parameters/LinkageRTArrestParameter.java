package jacusa.cli.parameters;

import jacusa.io.writer.BED6lrtArrestResultFormat2;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.cli.parameter.JACUSAConditionParameter;
import lib.data.AbstractData;
import lib.data.has.hasReadInfoExtendedCount;
import lib.data.has.hasReferenceBase;
import lib.data.result.StatisticResult;

public class LinkageRTArrestParameter<T extends AbstractData & hasReferenceBase & hasReadInfoExtendedCount>
extends AbstractParameter<T, StatisticResult<T>> 
implements hasStatisticCalculator<T> {

	private StatisticFactory<T> statisticParameters;

	public LinkageRTArrestParameter(final int conditions) {
		super(conditions);
		
		statisticParameters = new StatisticFactory<T>(new BetaBinomial<T>(), 1.0);
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
	public StatisticFactory<T> getStatisticParameters() {
		return statisticParameters;
	}

}
