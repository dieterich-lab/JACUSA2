package lib.stat.initalpha;

import java.util.Arrays;

import lib.stat.nominal.NominalData;

public class MinAlphaInit extends AbstractAlphaInit {

	public MinAlphaInit() {
		super("min", "alpha = min_k mean(p)");
	}

	@Override
	public AbstractAlphaInit newInstance(String line) {
		return new MinAlphaInit();
	}

	@Override
	public double[] init(final NominalData nominalData) {
		final int categories = nominalData.getCategories();
		final double[] alpha = new double[categories];
		Arrays.fill(alpha, Double.MAX_VALUE);

		final double[] sums = nominalData.getRowWiseSums();

		double[][] dataProportionMatrix = new double[nominalData.getReplicates()][alpha.length];
		for (int replicateIndex = 0; replicateIndex < nominalData.getReplicates(); ++replicateIndex) {
			for (int i = 0; i < categories; ++i) {
				dataProportionMatrix[replicateIndex][i] = 
						nominalData.getReplicate(replicateIndex,i) / sums[replicateIndex];
				alpha[i] = Math.min(alpha[i], dataProportionMatrix[replicateIndex][i]);
			}
		}

		return alpha;
	}
	
}
