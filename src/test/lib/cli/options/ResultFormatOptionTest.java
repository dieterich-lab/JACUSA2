package test.lib.cli.options;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.cli.MissingArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import jacusa.io.format.call.BED6callResultFormat;
import jacusa.io.format.call.VCFcallFormat;
import lib.cli.options.AbstractACOption;
import lib.cli.options.ResultFormatOption;
import lib.cli.parameter.GeneralParameter;
import lib.io.ResultFormat;
import test.utlis.TestUtils;

/**
 * Tests @see lib.cli.options.ResultFormatOption#process(org.apache.commons.cli.CommandLine)
 */
class ResultFormatOptionTest 
extends AbstractGeneralParameterProvider
implements ACOptionTest<Character> {
	
	@Test
	@DisplayName("Test ResultFormatOption fails on wrong input")
	void testProcessFail() throws Exception {
		// test required
		myAssertOptThrows(MissingArgumentException.class, "");
		
		// get all available chars for filters
		final Set<Character> available = getResultFormats(getGeneralParamter()).keySet();
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
		
		final String value = TestUtils.collapseSet(falseConfigOption, lib.io.InputOutput.VALUE_SEP);
		myAssertOptThrows(IllegalArgumentException.class, value);
	}

	@Override
	public Stream<Arguments> testProcess() {
		return getResultFormats(getGeneralParamter()).keySet().stream()
				.map(c -> createArguments(c));
	}
	
	Arguments createArguments(final char c) {
		return Arguments.of(
				createOptLine(Character.toString(c)),
				c);
	}
	
	static Map<Character, ResultFormat> getResultFormats(GeneralParameter parameter) {
		return Arrays.asList(
				new BED6callResultFormat("test", parameter),
				new VCFcallFormat(parameter))
				.stream()
				.collect(Collectors.toMap(ResultFormat::getC, Function.identity()) );
	}

	@Override
	public AbstractACOption createTestInstance() {
		final GeneralParameter parameter = getGeneralParamter();
		return new ResultFormatOption(parameter, getResultFormats(parameter));
	}

	@Override
	public Character getActualValue() {
		return getGeneralParamter().getResultFormat().getC();
	}

}
