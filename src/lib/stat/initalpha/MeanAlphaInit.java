package lib.stat.initalpha;

import lib.stat.nominal.NominalData;

public class MeanAlphaInit extends AbstractAlphaInit {

	public MeanAlphaInit() {
		super("mean", "alpha = mean * n * p * q");
	}

	@Override
	public AbstractAlphaInit newInstance(String line) {
		return new MeanAlphaInit();
	}
	
	@Override
	public double[] init(final NominalData nominalData) {
		final int categories 	= nominalData.getCategories();
		final double[] alpha 	= new double[categories];
		final double[] mean 	= new double[categories];

		double total 			= 0.0;
		for (int replicateIndex = 0; replicateIndex < nominalData.getReplicates(); ++replicateIndex) {
			for (int i = 0; i < categories; i++) { 
				final double tmp = nominalData.getReplicate(replicateIndex)[i];
				mean[i] += tmp;
				total 	+= tmp;
			}
		}

		for (int i = 0; i < categories; i++) {
			mean[i] /= total;
			alpha[i] = mean[i];
		}

		return alpha;
	}

}
