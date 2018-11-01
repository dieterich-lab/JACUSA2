package lib.stat.dirmult.initalpha;

import java.util.Arrays;

import lib.stat.dirmult.DirMultData;

public class MinAlphaInit extends AbstractAlphaInit {

	public MinAlphaInit() {
		super("min", "alpha = min_k mean(p)");
	}

	@Override
	public AbstractAlphaInit newInstance(String line) {
		return new MinAlphaInit();
	}

	@Override
	public double[] init(final DirMultData dirMultData) {
		final int categories = dirMultData.getCategories();
		final double[] alpha = new double[categories];
		Arrays.fill(alpha, Double.MAX_VALUE);

		final double[] sums = dirMultData.getRowWiseSums();

		double[][] dataProportionMatrix = new double[dirMultData.getReplicates()][alpha.length];
		for (int replicateIndex = 0; replicateIndex < dirMultData.getReplicates(); ++replicateIndex) {
			for (int i = 0; i < categories; ++i) {
				dataProportionMatrix[replicateIndex][i] = 
						dirMultData.getReplicate(replicateIndex,i) / sums[replicateIndex];
				alpha[i] = Math.min(alpha[i], dataProportionMatrix[replicateIndex][i]);
			}
		}

		return alpha;
	}
	
}
