package lib.estimate;

import lib.stat.estimation.ConditionEstimate;
import lib.util.ExtendedInfo;
import lib.util.Util;

final public class Utils {

	static public void addAlphaValues(final ConditionEstimate conditionEstimate, final ExtendedInfo info, final String prefix) {
		final String id 			= conditionEstimate.getID();
		final int iteration			= conditionEstimate.getNextIteration() - 1;
		final double[] initAlpha 	= conditionEstimate.getAlpha(0);
		final double[] alpha 		= conditionEstimate.getAlpha(iteration);
		final double logLikelihood	= conditionEstimate.getLogLikelihood(iteration);
		
		info.add(prefix + "initAlpha" + id, Util.format(initAlpha[0]));
		for (int i = 1; i < initAlpha.length; ++i) {
			info.add(prefix + "initAlpha" + id, Util.format(initAlpha[i]));
		}
		info.add(prefix + "alpha" + id, Util.format(alpha[0]));			
		for (int i = 1; i < alpha.length; ++i) {
			info.add(prefix + "alpha" + id, Util.format(alpha[i]));
		}
		info.add("prefix + iteration" + id, Integer.toString(iteration));
		info.add("prefix + logLikelihood" + id, Double.toString(logLikelihood));
	}
	
}