package test.lib.data.count.basecall;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.MapBaseCallCount;

@DisplayName("Test Map based implementation of BaseCallCount")
class MapBaseCallCountTest extends AbstractBaseCallCountTest {

	public MapBaseCallCountTest() {
		super(new MapBaseCallCount.Parser());
	}
	
	@Override
	BaseCallCount create() {
		return new MapBaseCallCount(); 
	}
	
	static public class ToMapBaseCallCountArgumentConverter extends SimpleArgumentConverter {

		@Override
		protected Object convert(Object src, Class<?> target) throws ArgumentConversionException {
			assertEquals(MapBaseCallCount.class, target, "Can only convert to MapBaseCallCount");
			final String s = String.valueOf(src);
			return new MapBaseCallCount.Parser().parse(s);
		}

	}
	
}
