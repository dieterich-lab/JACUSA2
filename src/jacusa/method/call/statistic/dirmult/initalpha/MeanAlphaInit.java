package jacusa.method.call.statistic.dirmult.initalpha;

import jacusa.data.BaseCallConfig;

public class MeanAlphaInit extends AbstractAlphaInit {

	public MeanAlphaInit() {
		super("mean", "alpha = mean * n * p * q");
	}

	@Override
	public AbstractAlphaInit newInstance(String line) {
		return new MeanAlphaInit();
	}
	
	@Override
	public double[] init(
			final int[] baseIndexs,
			final double[][] dataMatrix) {
		final double[] alpha = new double[BaseCallConfig.BASES.length];
		final double[] mean = new double[BaseCallConfig.BASES.length];

		double total = 0.0;
		for (int pileupI = 0; pileupI < dataMatrix.length; ++pileupI) {
			for (int baseI : baseIndexs) {
				mean[baseI] += dataMatrix[pileupI][baseI];
				total += dataMatrix[pileupI][baseI];
			}
		}

		for (int baseI : baseIndexs) {
			mean[baseI] /= total;
			alpha[baseI] = mean[baseI];
		}

		return alpha;
	}

}
