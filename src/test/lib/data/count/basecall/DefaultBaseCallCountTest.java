package test.lib.data.count.basecall;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.DefaultBCC;

class DefaultBaseCallCountTest extends AbstractBaseCallCountTest {

	public DefaultBaseCallCountTest() {
		super(new DefaultBCC.Parser());
	}
	
	@Override
	BaseCallCount create() {
		return new DefaultBCC();
	}
	
	static public class ToDefaultBaseCallCountArgumentConverter extends SimpleArgumentConverter {

		@Override
		protected Object convert(Object src, Class<?> target) throws ArgumentConversionException {
			assertEquals(DefaultBCC.class, target, "Can only convert to DefaultBaseCallCount");
			final String s = String.valueOf(src);
			return new DefaultBCC.Parser().parse(s);
		}

	}
	
}
