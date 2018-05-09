package lib.util;

import java.text.DecimalFormat;

public abstract class Util {

	public static String printAlpha(double a[]) {
		DecimalFormat df = new DecimalFormat("0.0000"); 
		StringBuilder sb = new StringBuilder();

		sb.append(df.format(a[0]));
		for (int i = 1; i < a.length; ++i) {
			sb.append("  ");
			sb.append(df.format(a[i]));
		}
		return sb.toString();
	}
	
}