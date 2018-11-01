package test.lib.cli.options;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.HomopolymerFilterFactory;
import jacusa.filter.factory.MaxAlleleCountFilterFactory;
import jacusa.filter.factory.basecall.CombinedFilterFactory;
import jacusa.filter.factory.basecall.INDEL_FilterFactory;
import jacusa.filter.factory.basecall.ReadPositionDistanceFilterFactory;
import jacusa.filter.factory.basecall.SpliceSiteFilterFactory;
import lib.cli.options.AbstractACOption;
import lib.cli.options.FilterConfigOption;
import lib.cli.parameter.AbstractParameter;
import test.utlis.CLIUtils;
import test.utlis.TestUtils;

@DisplayName("Test CLI processing of FilterConfigOption")
class FilterConfigOptionTest extends AbstractACOptionTest<Set<Character>> {

	@DisplayName("Check FilterConfigOption are parsed correctly")
	@ParameterizedTest(name = "List of filters: {arguments}")
	@MethodSource("testProcess")
	@Override
	void testProcess(Set<Character> expected) throws Exception {
		super.testProcess(expected);
	}

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
		
		final String value = TestUtils.collapseSet(falseConfigOption, lib.util.Util.VALUE_SEP);
		getParserWrapper().myAssertThrows(IllegalArgumentException.class, getACOption(), value);
	}

	/*
	 * Method Source
	 */

	static Stream<Arguments> testProcess() {
		final List<Set<Character>> data = new ArrayList<>();
		// 1 
		data.add(new HashSet<>(Arrays.asList('B')));
		// 3
		data.add(new HashSet<>(Arrays.asList('B', 'I', 'S')));
		return data.stream().map(set -> {
			if (! getFilterFactories().keySet().containsAll(set)) {
				throw new IllegalStateException("Unknown filter!");
			} else {
				return Arguments.of(set);
			}});
	}
	
	/*
	 * Helper
	 */
	
	static Map<Character, AbstractFilterFactory> getFilterFactories() {
		final Map<Character, AbstractFilterFactory> abstractPileupFilters = 
				new HashMap<Character, AbstractFilterFactory>();

		final List<AbstractFilterFactory> filterFactories = 
				new ArrayList<AbstractFilterFactory>(10);
		
		filterFactories.add(new CombinedFilterFactory(null, null));
		filterFactories.add(new INDEL_FilterFactory(null, null));
		filterFactories.add(new ReadPositionDistanceFilterFactory(null, null));
		filterFactories.add(new SpliceSiteFilterFactory(null, null));
		
		filterFactories.add(new MaxAlleleCountFilterFactory(null));
		
		filterFactories.add(new HomopolymerFilterFactory(null));

		for (final AbstractFilterFactory filterFactory : filterFactories) {
			abstractPileupFilters.put(filterFactory.getC(), filterFactory);
		}

		return abstractPileupFilters;
	}
	
	@Override
	protected AbstractACOption create(AbstractParameter parameter) {
		return new FilterConfigOption(parameter, getFilterFactories());
	}
	
	@Override
	protected Set<Character> getActualValue(AbstractParameter parameter) {
		return getParameter().getFilterConfig().getFilterFactories().stream()
				.map(f -> f.getC())
				.collect(Collectors.toSet());
	}
	
	@Override
	protected String createLine(Set<Character> v) {
		return CLIUtils.assignValue(getOption(), TestUtils.collapseSet(v, lib.util.Util.VALUE_SEP));
	}
	
}
