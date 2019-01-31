package test.lib.util.coordinate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

import lib.util.coordinate.Coordinate;
import lib.util.coordinate.OneCoordinate;

// JUNIT: A
public class OneCoordinateArgumentConverter extends SimpleArgumentConverter {

	@Override
	protected Object convert(Object arg0, Class<?> arg1) throws ArgumentConversionException {
		if (arg0.getClass() == Coordinate.class) {
			return arg0;
		}
		assertEquals(Coordinate.class, arg1, "Can only convert to Coordinate");
		final String s = String.valueOf(arg0);		
		return new OneCoordinate.Parser().parse(s);
	}

}
