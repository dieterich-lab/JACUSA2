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
		for (int replicateI = 0; replicateI < nominalData.getReplicates(); ++replicateI) {
			for (int i = 0; i < categories; i++) { 
				final double tmp = nominalData.getReplicate(replicateI)[i];
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
