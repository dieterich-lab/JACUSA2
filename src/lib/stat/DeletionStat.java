package lib.stat;

import lib.stat.dirmult.EstimationParameter;
import lib.stat.estimation.provider.INDELestimateProvider;

public class DeletionStat extends INDELstat {

	public static final String PREFIX = "deletion_";
	public static final String SCORE = PREFIX + "score";
	public static final String PVALUE = PREFIX + "pvalue";
	
	public DeletionStat(
			final EstimationParameter estimationParameter,
			final INDELestimateProvider indelEstimateProvider) {
		super(estimationParameter, indelEstimateProvider, PREFIX);
	}
	
}
