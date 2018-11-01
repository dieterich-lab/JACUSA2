package test.lib.data.count.basecall;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

import lib.data.count.basecall.ArrayBaseCallCount;

@DisplayName("Test Array based implementation of BaseCallCount")
public class ArrayBaseCallCountTest extends AbstractBaseCallCountTest {

	public ArrayBaseCallCountTest() {
		super(new ArrayBaseCallCount.Factory(),
				new ArrayBaseCallCount.Parser());
	}
	
	static public class ToArrayBaseCallCountArgumentConverter extends SimpleArgumentConverter {

		@Override
		protected Object convert(Object src, Class<?> target) throws ArgumentConversionException {
			assertEquals(ArrayBaseCallCount.class, target, "Can only convert to ArrayBaseCallCount");
			final String s = String.valueOf(src);
			return new ArrayBaseCallCount.Parser().parse(s);
		}
		
	}
	
}
