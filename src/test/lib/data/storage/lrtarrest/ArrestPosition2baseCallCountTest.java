package test.lib.data.storage.lrtarrest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import htsjdk.samtools.util.StringUtil;
import lib.data.count.basecall.ArrayBaseCallCount;
import lib.data.count.basecall.BaseCallCount;
import lib.data.storage.lrtarrest.ArrestPosition2baseCallCount;
import lib.util.Base;
import test.lib.data.count.basecall.ArrayBaseCallCountTest.ToArrayBaseCallCountArgumentConverter;

@TestInstance(Lifecycle.PER_CLASS)
class ArrestPosition2baseCallCountTest {

	private final ArrestPosition2baseCallCount.Parser parser;
	
	public ArrestPosition2baseCallCountTest() {
		parser = new ArrestPosition2baseCallCount.Parser();
	}

	/*
	 * Test
	 */
	
	@ParameterizedTest(name = "Wrap Object and expect String {1}")
	@MethodSource("testParserWrap")
	void testParserWrap(ArrestPosition2baseCallCount o, String expected) {
		final String actual = parser.wrap(o);
		assertEquals(expected, actual);
	}

	@ParameterizedTest(name = "Parse String {0} and create Object")
	@MethodSource("testParserParse")
	void testParserParse(String s, ArrestPosition2baseCallCount expected) {
		final ArrestPosition2baseCallCount actual = parser.parse(s);
		assertEquals(expected, actual);
	}

	@ParameterizedTest(name = "Add Arrest Pos. {0} Base {1} to Object {2} and expect {3}")
	@CsvSource( delimiter = '\t', value = {
			"1	A	*	1:1;0;0;0",
			"2	A	1:1;0;0;0	1:1;0;0;0,2:1;0;0;0"
	})
	void testAddBase(
			int arrestPos, 
			Base base,
			@ConvertWith(ToPosition2BaseCallCountArgumentConverter.class) ArrestPosition2baseCallCount o,
			@ConvertWith(ToPosition2BaseCallCountArgumentConverter.class) ArrestPosition2baseCallCount expected) {
		
		o.addBaseCall(arrestPos, base);
		assertEquals(expected, o);
	}
	
	@ParameterizedTest(name = "ArrestPos {0} is contained in Object {1}: {2}")
	@CsvSource( delimiter = '\t', value = {
			"10	*	false",
			"10	10:1;0;0;0	true",
			"10	12:1;0;0;0	false",
			"10	11:1;1;1;1,12:1;1;1;1	false",
			} )
	void testContains(
			int arrestPos, 
			@ConvertWith(ToPosition2BaseCallCountArgumentConverter.class) ArrestPosition2baseCallCount o, 
			boolean expected) {

		final boolean actual = o.contains(arrestPos);
		assertEquals(expected, actual);
	}

	@ParameterizedTest(name = "For Arrest Position {0} from Object {1} get arrest base call count: {2}")
	@CsvSource( delimiter = '\t', value = {
			"1	1:1;0;0;0,2:0;2;0;0,3:0;0;3;0,4:0;0;0;4	1;0;0;0",
			"2	1:1;0;0;0,2:0;2;0;0,3:0;0;3;0,4:0;0;0;4	0;2;0;0" } )	
	void testGetArrestBaseCallCount(
		int arrestPos,
		@ConvertWith(ToPosition2BaseCallCountArgumentConverter.class) ArrestPosition2baseCallCount o,
		@ConvertWith(ToArrayBaseCallCountArgumentConverter.class) ArrayBaseCallCount expected) {

	final BaseCallCount actual = o.getArrestBaseCallCount(arrestPos); 
	assertEquals(expected, actual);
	}

	@Disabled
	@ParameterizedTest(name = "For Position {0} from Object {1} get through base call count: {2}")
	@CsvSource( delimiter = '\t', value = {
			"1	*	*",
			"1	1:1;0;0;0,2:0;2;0;0,3:0;0;3;0,4:0;0;0;4	0;2;3;4",
			"4	1:1;0;0;0,2:0;2;0;0,3:0;0;3;0,4:0;0;0;4	1;2;3;0" } )	
	void testGetThroughBaseCallCount(
			int pos,
			@ConvertWith(ToPosition2BaseCallCountArgumentConverter.class) ArrestPosition2baseCallCount o,
			@ConvertWith(ToArrayBaseCallCountArgumentConverter.class) ArrayBaseCallCount expected) {
		final BaseCallCount actual = o.getThroughBaseCallCount(pos); 
		assertEquals(expected, actual);
	}

	@ParameterizedTest(name = "From Object {0} get total base call count: {1}")
	@CsvSource( delimiter = '\t', value = {
			"*	*",
			"1:1;0;0;0,2:0;2;0;0,3:0;0;3;0,4:0;0;0;4	1;2;3;4" } )
	void testGetTotalBaseCallCount(
			@ConvertWith(ToPosition2BaseCallCountArgumentConverter.class) ArrestPosition2baseCallCount o,
			@ConvertWith(ToArrayBaseCallCountArgumentConverter.class) ArrayBaseCallCount expected) {

		final BaseCallCount actual = o.getTotalBaseCallCount(); 
		assertEquals(expected, actual);
	}

	@ParameterizedTest(name = "Merge {0} and {1} resulting in {2}")
	@CsvSource( delimiter = '\t', value = { 
			"1:0;0;0;1	2:0;0;0;1	1:0;0;0;1,2:0;0;0;1",
			"1:0;0;0;1	1:0;0;0;1	1:0;0;0;2",
			"1:0;0;0;1	1:1;0;0;0	1:1;0;0;1" } )
	void testMerge(
			@ConvertWith(ToPosition2BaseCallCountArgumentConverter.class) ArrestPosition2baseCallCount o1,
			@ConvertWith(ToPosition2BaseCallCountArgumentConverter.class) ArrestPosition2baseCallCount o2,
			@ConvertWith(ToPosition2BaseCallCountArgumentConverter.class) ArrestPosition2baseCallCount expected) {

		o1.merge(o2);
		assertEquals(expected, o1);
	}

	@ParameterizedTest(name = "Reset {0}")
	@CsvSource( delimiter = '\t', value = { 
		"1:0;0;0;1",
		"1:0;0;0;1,2:1;0;0;1" } )
	void testReset(@ConvertWith(ToPosition2BaseCallCountArgumentConverter.class) ArrestPosition2baseCallCount o) {
		o.clear();
		assertEquals(0, o.getPositions().size());
	}

	/*
	 * Method source
	 */

	Stream<Arguments> testParserParse() {
		//ArrestPos2BaseCallCount o, String expected
		return Stream.of(
				Arguments.of(
						"*",
						new ArrestPosition2baseCallCount(-1)),
				Arguments.of(
						StringUtil.join(Character.toString(','),
								"9:1;0;0;0",
								"10:0;1;0;0",
								"50:0;0;1;0",
								"101:0;0;0;1"),
						new ArrestPosition2baseCallCount(-1)
							.addBaseCall(9, Base.A)
							.addBaseCall(10, Base.C)
							.addBaseCall(50, Base.G)
							.addBaseCall(101, Base.T)) );
	}
	
	Stream<Arguments> testParserWrap() {
		//ArrestPos2BaseCallCount o, String expected
		return Stream.of(
				Arguments.of(
						new ArrestPosition2baseCallCount(-1)
						, "*"),
				Arguments.of(						
						new ArrestPosition2baseCallCount(-1)
							.addBaseCall(9, Base.A)
							.addBaseCall(10, Base.C)
							.addBaseCall(50, Base.G)
							.addBaseCall(101, Base.T),
							StringUtil.join(Character.toString(','),
									"9:1;0;0;0",
									"10:0;1;0;0",
									"50:0;0;1;0",
									"101:0;0;0;1")) );
	}
	
	/*
	 * Helper
	 */
	
	public static class ToPosition2BaseCallCountArgumentConverter extends SimpleArgumentConverter {

		@Override
		protected Object convert(Object src, Class<?> target) throws ArgumentConversionException {
			assertEquals(ArrestPosition2baseCallCount.class, target, "Can only convert to ArrayPos2BaseCallCount");
			final String s = String.valueOf(src);
			return new ArrestPosition2baseCallCount.Parser().parse(s);
		}

	}
	
}
