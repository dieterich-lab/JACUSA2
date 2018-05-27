package jacusa.method.call.statistic.dirmult.initalpha;

import htsjdk.samtools.util.SequenceUtil;
import lib.cli.options.Base;

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
			final Base[] bases,
			final double[][] dataMatrix) {
		final int n = SequenceUtil.VALID_BASES_UPPER.length;
		final double[] alpha = new double[n];
		final double[] mean = new double[n];

		double total = 0.0;
		for (int pileupI = 0; pileupI < dataMatrix.length; ++pileupI) {
			for (final Base base : bases) {
				mean[base.getIndex()] += dataMatrix[pileupI][base.getIndex()];
				total += dataMatrix[pileupI][base.getIndex()];
			}
		}

		for (final Base base : bases) {
			mean[base.getIndex()] /= total;
			alpha[base.getIndex()] = mean[base.getIndex()];
		}

		return alpha;
	}

}
