package jacusa.method.call.statistic.dirmult.initalpha;

import java.util.Arrays;

import lib.util.Base;

public class MinAlphaInit extends AbstractAlphaInit {

	public MinAlphaInit() {
		super("min", "alpha = min_k mean(p)");
	}

	@Override
	public AbstractAlphaInit newInstance(String line) {
		return new MinAlphaInit();
	}

	@Override
	public double[] init(
			final Base[] bases,
			final double[][] dataMatrix) {
		final double[] alpha = new double[dataMatrix[0].length];
		Arrays.fill(alpha, Double.MAX_VALUE);

		double[] dataCoverages = getCoverages(bases, dataMatrix);

		double[][] dataProportionMatrix = new double[dataMatrix.length][alpha.length];
		for (int replicateIndex = 0; replicateIndex < dataMatrix.length; ++replicateIndex) {
			for (final Base base: bases) {
				dataProportionMatrix[replicateIndex][base.getIndex()] = 
						dataMatrix[replicateIndex][base.getIndex()] / dataCoverages[replicateIndex];
				alpha[base.getIndex()] = Math.min(alpha[base.getIndex()], dataProportionMatrix[replicateIndex][base.getIndex()]);
			}
		}

		return alpha;
	}
	
}
