package test.lib.util;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

import lib.util.Base;

// JUNIT: A
public class BaseSetArgumentConverter extends SimpleArgumentConverter {

	@Override
	protected Object convert(Object arg0, Class<?> arg1) throws ArgumentConversionException {
		final String s = String.valueOf(arg0);
		return Stream.of(s.split(""))
				.map(c -> Base.valueOf(c))
				.collect(Collectors.toSet());
	}

}
