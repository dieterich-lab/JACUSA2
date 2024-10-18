package test.lib.cli.options;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import htsjdk.samtools.util.StringUtil;
import lib.cli.options.AbstractACOption;
import lib.cli.options.filter.has.BaseSub;
import lib.util.Base;

/**
 * Tests @see lib.cli.options.CollectReadTagOption#process(org.apache.commons.cli.CommandLine)
 */

class CollectReadTagOptionTest 
extends AbstractGeneralParameterProvider
implements ACOptionTest<SortedSet<BaseSub>> {

	@Override
	public Stream<Arguments> testProcess() {
		return Stream.of(
				createArguments(BaseSub.A2G),
				createArguments(BaseSub.A2G, BaseSub.T2C),
				createArguments(
						BaseSub.A2G, BaseSub.G2A, 
						BaseSub.C2T, BaseSub.T2C) );
	}

	Arguments createArguments(final BaseSub... baseSubs) {
		final SortedSet<BaseSub> expected = new TreeSet<>(Arrays.asList(baseSubs));
		final String value = StringUtil.join(
				Character.toString(','), //FIXME use StratifyByReadTagOption.SEP), 
				Arrays.asList(baseSubs) );
		return Arguments.of(
				createOptLine(value),
				expected);
	}
	
	@Test
	void testProcessFails() throws Exception {
		// test A2A -> no substitution
		for (Base base : Base.validValues()) {
			String value = base.toString() + BaseSub.SEP + base.toString();
			myAssertOptThrows(IllegalArgumentException.class, value);
		}
		// wrong
		myAssertOptThrows(IllegalArgumentException.class, "wrong");
	}
	
	@Override
	public AbstractACOption createTestInstance() {
		return null; // FIXME return new StratifyByReadTagOption(getGeneralParamter());
	}

	@Override
	public SortedSet<BaseSub> getActualValue() {
		return null; // FIXME return getGeneralParamter().getReadTags();
	}
	
}
