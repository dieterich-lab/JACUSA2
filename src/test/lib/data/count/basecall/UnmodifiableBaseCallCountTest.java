package test.lib.data.count.basecall;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

import lib.data.count.basecall.DefaultBaseCallCount;

// TODO Qi
// This should test once craeted the values CANNOT be changed
// check AbstractBaseCallCountTest.java#void testParserParseFail() for how Exceptions are expected...
class UnmodifiableBaseCallCountTest extends AbstractBaseCallCountTest {

	public UnmodifiableBaseCallCountTest() {
		super(new DefaultBaseCallCount.Factory(), new DefaultBaseCallCount.Parser());
	}
	
	static public class ToDefaultBaseCallCountArgumentConverter extends SimpleArgumentConverter {

		@Override
		protected Object convert(Object src, Class<?> target) throws ArgumentConversionException {
			assertEquals(DefaultBaseCallCount.class, target, "Can only convert to DefaultBaseCallCount");
			final String s = String.valueOf(src);
			return new DefaultBaseCallCount.Parser().parse(s);
		}

	}
	
}
