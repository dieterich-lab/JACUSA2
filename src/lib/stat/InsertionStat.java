package lib.stat;

import lib.estimate.MinkaParameter;
import lib.stat.estimation.provider.INDELestimateProvider;

public class InsertionStat extends INDELstat {

	public static final String PREFIX = "insertion_";
	public static final String SCORE = PREFIX + "score";
	public static final String PVALUE = PREFIX + "pvalue";
	
	public InsertionStat(
			final MinkaParameter minkaParameter,
			final INDELestimateProvider indelEstimateProvider) {
		super(minkaParameter, indelEstimateProvider, SCORE, PVALUE);
	}
	
}
