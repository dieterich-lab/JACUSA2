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
		for (int replicateI = 0; replicateI < nominalData.getReplicates(); ++replicateI) {
			for (int i = 0; i < categories; ++i) {
				dataProportionMatrix[replicateI][i] = 
						nominalData.getReplicate(replicateI,i) / sums[replicateI];
				alpha[i] = Math.min(alpha[i], dataProportionMatrix[replicateI][i]);
			}
		}

		return alpha;
	}
	
}
