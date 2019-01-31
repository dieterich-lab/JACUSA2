package test.lib.cli.options;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import jacusa.filter.factory.FilterFactory;
import jacusa.filter.factory.HomopolymerFilterFactory;
import jacusa.filter.factory.MaxAlleleCountFilterFactory;
import jacusa.filter.factory.basecall.CombinedFilterFactory;
import jacusa.filter.factory.basecall.INDEL_FilterFactory;
import jacusa.filter.factory.basecall.ReadPositionFilterFactory;
import jacusa.filter.factory.basecall.SpliceSiteFilterFactory;
import lib.cli.options.AbstractACOption;
import lib.cli.options.FilterConfigOption;
import lib.data.DataType;
import lib.data.fetcher.DefaultFilteredDataFetcher;
import test.utlis.TestUtils;

/**
 * Tests @see lib.cli.options.FilterConfigOption#process(org.apache.commons.cli.CommandLine)
 */
class FilterConfigOptionTest 
extends AbstractGeneralParameterProvider
implements ACOptionTest<Set<Character>> {

	@Disabled
	@Test
	@DisplayName("Check FilterConfigOption fails on wrong input")
	void testProcessFail() throws Exception {
		// get all available chars for filters
		final Set<Character> available = getFilterFactories().keySet();
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
		return Stream.of(
				createArguments("B"),
				createArguments("B,I,S") );
	}
	
	Arguments createArguments(final String filters) {
		return Arguments.of(
				createOptLine(filters), 
				extractC(filters) );
	}
	
	Map<Character, FilterFactory> getFilterFactories() {
		return Arrays.asList(
				new CombinedFilterFactory(
						DataType.BCC.getFetcher(), 
						new DefaultFilteredDataFetcher<>(DataType.F_BCC)),
				
				new INDEL_FilterFactory(
						DataType.BCC.getFetcher(), 
						new DefaultFilteredDataFetcher<>(DataType.F_BCC)),
				
				new ReadPositionFilterFactory(
						DataType.BCC.getFetcher(), 
						new DefaultFilteredDataFetcher<>(DataType.F_BCC)),
				
				new SpliceSiteFilterFactory(
						DataType.BCC.getFetcher(), 
						new DefaultFilteredDataFetcher<>(DataType.F_BCC)),
				new MaxAlleleCountFilterFactory(DataType.BCC.getFetcher()),
				new HomopolymerFilterFactory(new DefaultFilteredDataFetcher<>(DataType.F_BOOLEAN)))
				.stream()
				.collect(Collectors.toMap(FilterFactory::getC, Function.identity()));
	}
	
	@Override
	public AbstractACOption createTestInstance() {
		return new FilterConfigOption(getGeneralParamter(), getFilterFactories());
	}
	
	@Override
	public Set<Character> getActualValue() {
		return getGeneralParamter().getFilterConfig().getFilterFactories().stream()
				.map(FilterFactory::getC)
				.collect(Collectors.toSet() );
	}
	
	private Set<Character> extractC(String filters) {
		return Arrays.asList(filters.split(",")).stream()
			.map(s -> s.charAt(0))
			.collect(Collectors.toSet());
	}
	
}
