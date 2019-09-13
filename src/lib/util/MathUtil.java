package lib.util;

public final class MathUtil {

	private MathUtil() {
		// nothing to be done
	}
	
	public static double sum(double[] values) {
		double sum = 0.0;
		for (double value : values) {
			sum += value;
		}
		
		return sum;
	}

}
