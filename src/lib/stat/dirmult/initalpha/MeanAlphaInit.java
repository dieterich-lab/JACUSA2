package lib.stat.dirmult.initalpha;

import lib.stat.dirmult.DirMultData;

public class MeanAlphaInit extends AbstractAlphaInit {

	public MeanAlphaInit() {
		super("mean", "alpha = mean * n * p * q");
	}

	@Override
	public AbstractAlphaInit newInstance(String line) {
		return new MeanAlphaInit();
	}
	
	@Override
	public double[] init(final DirMultData dirMultData) {
		final int categories 	= dirMultData.getCategories();
		final double[] alpha 	= new double[categories];
		final double[] mean 	= new double[categories];

		double total 			= 0.0;
		for (int replicateIndex = 0; replicateIndex < dirMultData.getReplicates(); ++replicateIndex) {
			for (int i = 0; i < categories; i++) { 
				final double tmp = dirMultData.getReplicate(replicateIndex)[i];
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
