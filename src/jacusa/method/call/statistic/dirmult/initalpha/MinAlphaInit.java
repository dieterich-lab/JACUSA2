package jacusa.method.call.statistic.dirmult.initalpha;

import java.util.Arrays;

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
			final int[] baseIndexs,
			final double[][] dataMatrix) {
		final double[] alpha = new double[dataMatrix[0].length];
		Arrays.fill(alpha, Double.MAX_VALUE);

		double[] dataCoverages = getCoverages(baseIndexs, dataMatrix);

		double[][] dataProportionMatrix = new double[dataMatrix.length][alpha.length];
		for (int replicateIndex = 0; replicateIndex < dataMatrix.length; ++replicateIndex) {
			for (int baseIndex : baseIndexs) {
				dataProportionMatrix[replicateIndex][baseIndex] = 
						dataMatrix[replicateIndex][baseIndex] / dataCoverages[replicateIndex];
				alpha[baseIndex] = Math.min(alpha[baseIndex], dataProportionMatrix[replicateIndex][baseIndex]);
			}
		}

		return alpha;
	}
	
}
