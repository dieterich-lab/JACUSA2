package test.lib.data.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;

import lib.data.count.basecall.ArrayBCC;
import lib.data.count.basecall.BaseCallCount;
import lib.data.filter.BaseCallCountFilteredData;

public class BaseCallFilteredDataTest extends AbstractFilteredDataTest<BaseCallCountFilteredData, BaseCallCount> {

	public BaseCallFilteredDataTest() {
		super(new BaseCallCountFilteredData.Parser(new ArrayBCC.Parser()));
	}

	/*
	 * Test
	 */
	
	@Test
	@Override
	void testParserParseFail() {
		for (final String s : new String[] { "C=wrong", "WRONG=1;1;1;1", "A=1;1;1;1;B=2;2;2;2" }) {
			Executable executable = () -> { parser.parse(s); };
			assertThrows(IllegalArgumentException.class, executable, "Should fail in input: " + s);
		}		
	}

	@CsvSource(delimiter = ' ', value = {
			"* * *",
			"A=1;2;3;4 B=5;6;7;8 A=1;2;3;4,B=5;6;7;8",
			"A=1;2;3;4 A=5;6;7;8 A=6;8;10;12" } )
	@Override
	void testMerge(
			@ConvertWith(ToBaseCallFilteredDataArgumentConverter.class) BaseCallCountFilteredData data1, 
			@ConvertWith(ToBaseCallFilteredDataArgumentConverter.class) BaseCallCountFilteredData data2,
			@ConvertWith(ToBaseCallFilteredDataArgumentConverter.class) BaseCallCountFilteredData expected) {

		super.testMerge(data1, data2, expected);
	}

	@CsvSource(delimiter = ' ', value = {
			"*",
			"A=1;2;3;4",
			"A=1;2;3;4,B=5;6;7;8" } )	
	@Override
	void testCopy(@ConvertWith(ToBaseCallFilteredDataArgumentConverter.class) BaseCallCountFilteredData data) {
		super.testCopy(data);
	}
	
	/*
	 * Method source
	 */


	@Override
	Stream<Arguments> testParserParse() {
		return Stream.of(
				Arguments.of("*", new BaseCallCountFilteredData()),
				Arguments.of(
						"A=0;0;0;0", 
						new BaseCallCountFilteredData().add('A', new ArrayBCC())),
				Arguments.of(
						"A=0;0;0;0,B=1;1;1;1", 
						new BaseCallCountFilteredData()
							.add('A', new ArrayBCC())
							.add('B', new ArrayBCC(new int [] { 1, 1, 1, 1 } ))) );
	}
	
	@Override
	Stream<Arguments> testParserWrap() {
		return Stream.of(
				Arguments.of(new BaseCallCountFilteredData(), "*"),
				Arguments.of(
						new BaseCallCountFilteredData().add('A', new ArrayBCC()),
						"A=*"),
				Arguments.of(
						new BaseCallCountFilteredData()
							.add('A', new ArrayBCC())
							.add('B', new ArrayBCC(new int [] { 1, 1, 1, 1 } )),
						"A=*,B=1;1;1;1") );
	}
	
	/*
	 * Helper
	 */
	
	@Override
	protected boolean myEquals(BaseCallCount data1, BaseCallCount data2) {
		return data1.equals(data2);
	}

	public static class ToBaseCallFilteredDataArgumentConverter extends SimpleArgumentConverter {

		@Override
		protected Object convert(Object src, Class<?> target) throws ArgumentConversionException {
			assertEquals(BaseCallCountFilteredData.class, target, "Can only convert to BaseCallFilteredData");
			final String s = String.valueOf(src);
			return new BaseCallCountFilteredData.Parser(new ArrayBCC.Parser()).parse(s);
		}
		
	}
	
}
