package jacusa.cli.parameters;

import jacusa.pileup.builder.AbstractDataBuilderFactory;
import lib.cli.parameters.AbstractParameters;
import lib.data.BaseQualReadInfoData;

public class RTArrestParameters<T extends BaseQualReadInfoData>
extends AbstractParameters<T> 
implements hasStatisticCalculator<T> {

	private StatisticParameters<T> statisticParameters;

	public RTArrestParameters(final int conditions, final AbstractDataBuilderFactory<T> dataBuilderFactory) {
		super(conditions, dataBuilderFactory);
		
		statisticParameters = new StatisticParameters<T>();
		statisticParameters.setStatisticCalculator(new BetaBinomial<T>());
	}
	
	@Override
	public StatisticParameters<T> getStatisticParameters() {
		return statisticParameters;
	}

}
