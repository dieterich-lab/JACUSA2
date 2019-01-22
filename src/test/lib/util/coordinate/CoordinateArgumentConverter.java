package test.lib.util.coordinate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

import lib.util.coordinate.Coordinate;

// JUNIT: A
public class CoordinateArgumentConverter extends SimpleArgumentConverter {

	@Override
	protected Object convert(Object arg0, Class<?> arg1) throws ArgumentConversionException {
		if (arg0.getClass() == Coordinate.class) {
			return arg0;
		}
		assertEquals(Coordinate.class, arg1, "Can only convert to Coordinate");
		final String s = String.valueOf(arg0);		
		return new Coordinate.Parser().parse(s);
	}

}
