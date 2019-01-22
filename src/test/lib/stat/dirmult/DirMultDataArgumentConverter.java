package test.lib.stat.dirmult;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

import lib.stat.dirmult.DirMultData;

// JUNIT A
public class DirMultDataArgumentConverter extends SimpleArgumentConverter {

	@Override
	protected Object convert(Object arg0, Class<?> arg1) throws ArgumentConversionException {
		if (arg0.getClass() == DirMultData.class) {
			return arg0;
		}
		assertEquals(DirMultData.class, arg1, "Can only convert to DirMultData");
		final String s = String.valueOf(arg0);
		return new DirMultData.Parser().parse(s);
	}

}
