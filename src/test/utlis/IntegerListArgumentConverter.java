package test.utlis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

// JUNIT: A
public class IntegerListArgumentConverter extends SimpleArgumentConverter {

	@Override
	protected Object convert(Object arg0, Class<?> arg1) throws ArgumentConversionException {
		assertEquals(List.class, arg1, "Can only convert to List");
		final String s = String.valueOf(arg0);
		if (s.equals("*")) {
			return new ArrayList<Integer>(0);
		}
		return Stream.of(s.split(","))
				.map(Integer::parseInt)
				.collect(Collectors.toList());
	}

}
