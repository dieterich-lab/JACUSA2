package test.lib.cli.options;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.cli.MissingArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jacusa.io.format.call.BED6callResultFormat;
import jacusa.io.format.call.VCFcallFormat;
import lib.cli.options.AbstractACOption;
import lib.cli.options.ResultFormatOption;
import lib.cli.parameter.GeneralParameter;
import lib.io.ResultFormat;
import test.utlis.CLIUtils;
import test.utlis.TestUtils;

@DisplayName("Test CLI processing of ResultFormatOption")
class ResultFormatOptionTest extends AbstractACOptionTest<Character> {

	/*
	 * Tests
	 */
	
	@DisplayName("Check ResultFormatOption are parsed correctly")
	@ParameterizedTest(name = "List of filters: {arguments}")
	@MethodSource("testProcess")
	@Override
	void testProcess(Character expected) throws Exception {
		super.testProcess(expected);
	}

	@Test
	@DisplayName("Test ResultFormatOption fails on wrong input")
	void testProcessFail() throws Exception {
		// test required
		getParserWrapper().myAssertThrows(MissingArgumentException.class, getACOption(), "");
		
		// get all available chars for filters
		final Set<Character> available = getResultFormats(getParameter()).keySet();
		// get all REMAINING/NOT USED chars
		final Set<Character> notUsedChars = IntStream.rangeClosed(65, 90)
				.mapToObj(i -> (char)i)
				.filter(c -> ! available.contains(c))
				.collect(Collectors.toSet());

		// create container for correct and false filter(s)
		final Set<Character> falseConfigOption = new HashSet<>();
		// add correct
		falseConfigOption.addAll(available);
		// add 2 false chars
		falseConfigOption.addAll(notUsedChars.stream().limit(2).collect(Collectors.toSet()));
		
		final String value = TestUtils.collapseSet(falseConfigOption, lib.util.Util.VALUE_SEP);
		getParserWrapper().myAssertThrows(IllegalArgumentException.class, getACOption(), value);
	}
	
	/*
	 * Helper
	 */

	static Stream<Arguments> testProcess() {
		return getResultFormats(null).keySet().stream()
				.map(Arguments::of);
	}
	
	static Map<Character, ResultFormat> getResultFormats(GeneralParameter parameter) {
		final Map<Character, ResultFormat> resultFormats = 
				new HashMap<Character, ResultFormat>();

		ResultFormat resultFormat = null;

		// BED like output
		resultFormat = new BED6callResultFormat("test", parameter);
		resultFormats.put(resultFormat.getC(), resultFormat);

		resultFormat = new VCFcallFormat(parameter);
		resultFormats.put(resultFormat.getC(), resultFormat);

		return resultFormats;
	}

	
	@Override
	protected AbstractACOption create(GeneralParameter parameter) {
		return new ResultFormatOption(parameter, getResultFormats(parameter));
	}

	@Override
	protected Character getActualValue(GeneralParameter parameter) {
		return parameter.getResultFormat().getC();
	}

	@Override
	protected String createLine(Character v) {
		return CLIUtils.assignValue(getOption(), Character.toString(v));
	}
}
