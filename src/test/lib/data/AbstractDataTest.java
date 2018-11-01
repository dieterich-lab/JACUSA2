package test.lib.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import lib.data.DataTypeContainer;
import lib.data.DataTypeContainer.AbstractBuilderFactory;
import lib.data.DataTypeContainer.AbstractParser;
import test.utlis.MyAssert;

@TestInstance(Lifecycle.PER_CLASS)
public abstract class AbstractDataTest {

	private final AbstractBuilderFactory builderFactory;
	private final AbstractParser parser;
	
	public AbstractDataTest(
			final AbstractBuilderFactory builderFactory,
			final AbstractParser parser) {
		
		this.builderFactory = builderFactory;
		this.parser = parser;
	}

	protected AbstractBuilderFactory getBuilderFactory() {
		return builderFactory;
	}
	
	/*
	 * Test
	 */
 
	@ParameterizedTest(name = "Parse String {0} and build data")
	@MethodSource("testParserParse")
	void testParserParse(String s, DataTypeContainer expected) {
		assertEquals(expected, parser.parse(s));
	}

	@Test
	abstract void testParserParseFail();
	
	@ParameterizedTest(name = "Wrap data and create String: {1}")
	@MethodSource("testParserWrap")
	void testParserWrap(DataTypeContainer data, String expected) {
		assertEquals(expected, parser.wrap(data));
	}

	@DisplayName("Test copy")
	@ParameterizedTest(name = "Copy data")
	@MethodSource("testCopy")
	void testCopy(DataTypeContainer data) {
		final DataTypeContainer copy = data.copy();
		assertEquals(data.getReferenceBase(), copy.getReferenceBase());
		assertEquals(data.getLibraryType(), copy.getLibraryType());
		MyAssert.assertCopy(data.getCoordinate(), copy.getCoordinate(), "Coordinates");
		testCopySpecific(data, copy);
	}
	abstract void testCopySpecific(DataTypeContainer data, DataTypeContainer copy);
	
	@DisplayName("Test merge")
	@ParameterizedTest(name = "Merge data1 and data2")
	@MethodSource("testMerge")
	void testMerge(DataTypeContainer data1, DataTypeContainer data2, DataTypeContainer expected) {
		data1.merge(data2);
		assertEquals(expected, data1);
		assertSame(expected, data1);
	}

	/*
	 * Abstract
	 */
	
	abstract Stream<Arguments> testParserParse();
	abstract Stream<Arguments> testParserWrap();
	
	abstract Stream<Arguments> testCopy();
	abstract Stream<Arguments> testMerge();

}