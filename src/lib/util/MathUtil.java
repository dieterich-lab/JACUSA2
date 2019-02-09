package lib.util;

public abstract class MathUtil {

	public static double sum(double[] values) {
		double sum = 0.0;
		for (double value : values) {
			sum += value;
		}
		
		return sum;
	}

}
