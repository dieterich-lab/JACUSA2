package test.lib.data.count.basecall;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.DefaultBCC;

// JUNIT: A
public class BaseCallCountListArgumentConverter extends SimpleArgumentConverter {

	@Override
	protected Object convert(Object arg0, Class<?> arg1) throws ArgumentConversionException {
		assertEquals(List.class, arg1, "Can only convert to List");
		final BaseCallCount.AbstractParser parser = 
				new DefaultBCC.Parser(',', BaseCallCount.AbstractParser.BASE_CALL_SEP);
		final String s = String.valueOf(arg0);
		return Stream.of(s.split(";"))
			.map(c -> parser.parse(c))
			.collect(Collectors.toList());
	}

}
