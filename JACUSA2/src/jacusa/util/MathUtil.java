package jacusa.util;

public abstract class MathUtil {

	public static double sum(double[] values) {
		double sum = 0.0;
		for (double value : values) {
			sum += value;
		}
		
		return sum;
	}

	public static double Prob2Phred(final double prob) {
		double q = -10.0 * Math.log10(prob);
		q = Math.min(q, 40.0);
		return (double)((int)(q * 100)) / 100d; 
	}

}
