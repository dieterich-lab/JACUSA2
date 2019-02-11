package test.lib.stat.dirmult;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

import lib.stat.nominal.NominalData;

// JUNIT A
public class NominalDataArgumentConverter extends SimpleArgumentConverter {

	@Override
	protected Object convert(Object arg0, Class<?> arg1) throws ArgumentConversionException {
		if (arg0.getClass() == NominalData.class) {
			return arg0;
		}
		assertEquals(NominalData.class, arg1, "Can only convert to DirMultData");
		final String s = String.valueOf(arg0);
		return new NominalData.Parser().parse(s);
	}

}
