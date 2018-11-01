package test.lib.data.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;

import lib.data.filter.BooleanWrapperFilteredData;
import lib.data.filter.BooleanWrapper;

public class BooleanFilteredDataTest extends AbstractFilteredDataTest<BooleanWrapperFilteredData, BooleanWrapper> {

	public BooleanFilteredDataTest() {
		super(new BooleanWrapperFilteredData.Parser());
	}
	
	/*
	 * Test
	 */
	
	@Override
	void testParserParseFail() {
		for (final String s : new String[] { "C=wrong", "WRONG=true", "WRONG=false", "A=true,B=true" }) {
			Executable executable = () -> { parser.parse(s); };
			assertThrows(IllegalArgumentException.class, executable);
		}
	}

	@CsvSource(delimiter = ' ', value = {
		"* * *",
		"A=true B=false A=true,B=false",
		"A=true A=false A=true",
		"A=true,B=false C=false A=true,B=false,C=true"
	} )
	@Override
	void testMerge(
			@ConvertWith(ToBooleanFilteredDataArgumentConverter.class) BooleanWrapperFilteredData data1, 
			@ConvertWith(ToBooleanFilteredDataArgumentConverter.class) BooleanWrapperFilteredData data2,
			@ConvertWith(ToBooleanFilteredDataArgumentConverter.class) BooleanWrapperFilteredData expected) {

		super.testMerge(data1, data2, expected);
	}
	
	@CsvSource(delimiter = ' ', value = {
			"*",
			"A=true",
			"A=true,B=false"
		} )
	@Override
	void testCopy(BooleanWrapperFilteredData data) {
		super.testCopy(data);
	}
	
	/*
	 * Method source
	 */	

	@Override
	Stream<Arguments> testParserParse() {
		return Stream.of(
				Arguments.of("*", new BooleanWrapperFilteredData()),
				Arguments.of("A=true", new BooleanWrapperFilteredData().add('A', new BooleanWrapper(true))),
				Arguments.of(
						"A=true,B=false", 
						new BooleanWrapperFilteredData()
							.add('A', new BooleanWrapper(true))
							.add('B', new BooleanWrapper(false))) );
	}
	
	@Override
	Stream<Arguments> testParserWrap() {
		return Stream.of(
				Arguments.of(new BooleanWrapperFilteredData(), "*"),
				Arguments.of(
						new BooleanWrapperFilteredData()
							.add('A', new BooleanWrapper(true)), 
						"A=true"),
				Arguments.of(
						new BooleanWrapperFilteredData()
							.add('A', new BooleanWrapper(true))
							.add('B', new BooleanWrapper(false)), 
						"A=true,B=false") );
	}

	/*
	 * Helper
	 */

	public static class ToBooleanFilteredDataArgumentConverter extends SimpleArgumentConverter {

		@Override
		protected Object convert(Object src, Class<?> target) throws ArgumentConversionException {
			assertEquals(BooleanWrapperFilteredData.class, target, "Can only convert to BooleanFilteredData");
			final String s = String.valueOf(src);
			return new BooleanWrapperFilteredData.Parser().parse(s);
		}
		
	}

	@Override
	protected boolean myEquals(BooleanWrapper data1, BooleanWrapper data2) {
		return data1.equals(data2);
	}
	
}