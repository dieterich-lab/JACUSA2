package test.lib.data.cache.lrtarrest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

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
import lib.data.cache.lrtarrest.ArrestPos2BaseCallCount;
import lib.data.count.basecall.ArrayBaseCallCount;
import lib.data.count.basecall.BaseCallCount;
import lib.util.Base;
import lib.util.coordinate.Coordinate;
import test.lib.data.count.basecall.ArrayBaseCallCountTest.ToArrayBaseCallCountArgumentConverter;
import test.lib.util.coordinate.CoordinateUtilTest.ToCoordinateArgumentConverter;

@TestInstance(Lifecycle.PER_CLASS)
class ArrestPos2BaseCallCountTest {

	private final ArrestPos2BaseCallCount.Parser parser;
	
	public ArrestPos2BaseCallCountTest() {
		parser = new ArrestPos2BaseCallCount.Parser();
	}

	/*
	 * Test
	 */
	
	@ParameterizedTest(name = "Wrap Object and expect String {1}")
	@MethodSource("testParserWrap")
	void testParserWrap(ArrestPos2BaseCallCount o, String expected) {
		final String actual = parser.wrap(o);
		assertEquals(expected, actual);
	}

	@ParameterizedTest(name = "Parse String {0} and create Object")
	@MethodSource("testParserParse")
	void testParserParse(String s, ArrestPos2BaseCallCount expected) {
		final ArrestPos2BaseCallCount actual = parser.parse(s);
		assertEquals(expected, actual);
		
	}

	@ParameterizedTest(name = "Add Arrest Pos. {0} Base {1} within Window {2} to Object {3} and expect {4}")
	@CsvSource( delimiter = '\t', value = {
			"1	A	1:1-10:.	1,*	1,1:1;0;0;0",
			"2	A	1:1-10:.	1,1:1;0;0;0	1,1:1;0;0;0,2:1;0;0;0"
	})
	void testAddBase(
			int arrestPos, 
			Base base,
			@ConvertWith(ToCoordinateArgumentConverter.class) Coordinate window,
			@ConvertWith(ToArrestPos2BaseCallCountArgumentConverter.class) ArrestPos2BaseCallCount o,
			@ConvertWith(ToArrestPos2BaseCallCountArgumentConverter.class) ArrestPos2BaseCallCount expected) {
		
		o.addBaseCall(arrestPos, base, window);
		assertEquals(expected, o);
	}
	
	@ParameterizedTest(name = "ArrestPos {0} is contained in Object {1}: {2}")
	@CsvSource( delimiter = '\t', value = {
			"10	10,*	false",
			"10	10,10:1;0;0;0	true",
			"10	12,12:1;0;0;0	false",
			"10	11,11:1;1;1;1,12:1;1;1;1	false",
			} )
	void testContains(
			int arrestPos, 
			@ConvertWith(ToArrestPos2BaseCallCountArgumentConverter.class) ArrestPos2BaseCallCount o, 
			boolean expected) {

		final boolean actual = o.contains(arrestPos);
		assertEquals(expected, actual);
	}

	@ParameterizedTest(name = "For Arrest Position {0} from Object {1} get arrest base call count: {2}")
	@CsvSource( delimiter = '\t', value = {
			"1	1,1:1;0;0;0,2:0;2;0;0,3:0;0;3;0,4:0;0;0;4	1;0;0;0",
			"2	2,1:1;0;0;0,2:0;2;0;0,3:0;0;3;0,4:0;0;0;4	0;2;0;0" } )	
	void testGetArrestBaseCallCount(
		int arrestPos,
		@ConvertWith(ToArrestPos2BaseCallCountArgumentConverter.class) ArrestPos2BaseCallCount o,
		@ConvertWith(ToArrayBaseCallCountArgumentConverter.class) ArrayBaseCallCount expected) {

	final BaseCallCount actual = o.getArrestBaseCallCount(arrestPos); 
	assertEquals(expected, actual);
	}

	@ParameterizedTest(name = "For Position {0} from Object {1} get through base call count: {2}")
	@CsvSource( delimiter = '\t', value = {
			"1	1,*	*",
			"1	1,1:1;0;0;0,2:0;2;0;0,3:0;0;3;0,4:0;0;0;4	0;2;3;4",
			"4	1,1:1;0;0;0,2:0;2;0;0,3:0;0;3;0,4:0;0;0;4	1;2;3;0" } )	
	void testGetThroughBaseCallCount(
			int pos,
			@ConvertWith(ToArrestPos2BaseCallCountArgumentConverter.class) ArrestPos2BaseCallCount o,
			@ConvertWith(ToArrayBaseCallCountArgumentConverter.class) ArrayBaseCallCount expected) {

		final BaseCallCount actual = o.getThroughBaseCallCount(pos); 
		assertEquals(expected, actual);
	}

	@ParameterizedTest(name = "From Object {0} get total base call count: {1}")
	@CsvSource( delimiter = '\t', value = {
			"1,*	*",
			"1,1:1;0;0;0,2:0;2;0;0,3:0;0;3;0,4:0;0;0;4	1;2;3;4" } )
	void testGetTotalBaseCallCount(
			@ConvertWith(ToArrestPos2BaseCallCountArgumentConverter.class) ArrestPos2BaseCallCount o,
			@ConvertWith(ToArrayBaseCallCountArgumentConverter.class) ArrayBaseCallCount expected) {

		final BaseCallCount actual = o.getTotalBaseCallCount(); 
		assertEquals(expected, actual);
	}

	@ParameterizedTest(name = "Merge {0} and {1} resulting in {2}")
	@CsvSource( delimiter = '\t', value = { 
			"1,1:0;0;0;1	1,2:0;0;0;1	1,1:0;0;0;1,2:0;0;0;1",
			"1,1:0;0;0;1	1,1:0;0;0;1	1,1:0;0;0;2",
			"1,1:0;0;0;1	1,1:1;0;0;0	1,1:1;0;0;1" } )
	void testMerge(
			@ConvertWith(ToArrestPos2BaseCallCountArgumentConverter.class) ArrestPos2BaseCallCount o1,
			@ConvertWith(ToArrestPos2BaseCallCountArgumentConverter.class) ArrestPos2BaseCallCount o2,
			@ConvertWith(ToArrestPos2BaseCallCountArgumentConverter.class) ArrestPos2BaseCallCount expected) {

		o1.merge(o2);
		assertEquals(expected, o1);
	}

	@ParameterizedTest(name = "Reset {0}")
	@CsvSource( delimiter = '\t', value = { 
		"1,1:0;0;0;1",
		"1,1:0;0;0;1,2:1;0;0;1" } )
	void testReset(@ConvertWith(ToArrestPos2BaseCallCountArgumentConverter.class) ArrestPos2BaseCallCount o) {
		o.clear();
		assertEquals(0, o.getArrestPos().size());
	}

	/*
	 * Method source
	 */

	Stream<Arguments> testParserParse() {
		//ArrestPos2BaseCallCount o, String expected
		final Coordinate window = new Coordinate("chr1", 10, 100);
		return Stream.of(
				Arguments.of(
						"*",
						new ArrestPos2BaseCallCount()),
				Arguments.of(
						StringUtil.join(Character.toString(','),
								"9:1;0;0;0",
								"10:0;1;0;0",
								"50:0;0;1;0",
								"101:0;0;0;1"),
						new ArrestPos2BaseCallCount()
							.addBaseCall(9, Base.A, window)
							.addBaseCall(10, Base.C, window)
							.addBaseCall(50, Base.G, window)
							.addBaseCall(101, Base.T, window)) );
	}
	
	Stream<Arguments> testParserWrap() {
		//ArrestPos2BaseCallCount o, String expected
		final Coordinate window = new Coordinate("chr1", 10, 100);
		return Stream.of(
				Arguments.of(
						new ArrestPos2BaseCallCount()
						, "*"),
				Arguments.of(						
						new ArrestPos2BaseCallCount()
							.addBaseCall(9, Base.A, window)
							.addBaseCall(10, Base.C, window)
							.addBaseCall(50, Base.G, window)
							.addBaseCall(101, Base.T, window),
							StringUtil.join(Character.toString(','),
									"9:1;0;0;0",
									"10:0;1;0;0",
									"50:0;0;1;0",
									"101:0;0;0;1")) );
	}
	
	/*
	 * Helper
	 */
	
	public static class ToArrestPos2BaseCallCountArgumentConverter extends SimpleArgumentConverter {

		@Override
		protected Object convert(Object src, Class<?> target) throws ArgumentConversionException {
			assertEquals(ArrestPos2BaseCallCount.class, target, "Can only convert to ArrayPos2BaseCallCount");
			final String s = String.valueOf(src);
			return new ArrestPos2BaseCallCount.Parser().parse(s);
		}

	}
	
}
