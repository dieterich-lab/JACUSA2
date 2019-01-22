package test.utlis;

import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

// JUNIT: A
public class DoubleArrayArgumentConverter extends SimpleArgumentConverter {

	@Override
	protected Object convert(Object arg0, Class<?> arg1) throws ArgumentConversionException {
		final String s = String.valueOf(arg0);
		final String[] e = s.split(",");
		final double[] d = new double[e.length];
		for (int i = 0; i < e.length; i++) {
			d[i] = Double.parseDouble(e[i]); 
		}
		return d;
	}

}
