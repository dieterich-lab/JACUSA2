package lib.util;

public abstract class MathUtil {

	public static double sum(double[] values) {
		double sum = 0.0;
		for (double value : values) {
			sum += value;
		}
		
		return sum;
	}

	public static double[] columnWiseSum(final int categories, final double[][] dataMatrix) {
		final int replicates = dataMatrix.length;
		final double[] sums = new double[replicates];
		for (int replicateIndex = 0; replicateIndex < replicates; ++replicateIndex) {
			for (int i = 0; i < categories; ++i) {
				sums[replicateIndex] += dataMatrix[replicateIndex][i];
			}
		}
		return sums;
	}

}
