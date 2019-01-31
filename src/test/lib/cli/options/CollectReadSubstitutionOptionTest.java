package test.lib.cli.options;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import htsjdk.samtools.util.StringUtil;
import lib.cli.options.AbstractACOption;
import lib.cli.options.CollectReadSubstituionOption;
import lib.cli.options.filter.has.HasReadSubstitution.BaseSubstitution;
import lib.util.Base;

/**
 * Tests @see lib.cli.options.CollectReadSubstitutionOption#process(org.apache.commons.cli.CommandLine)
 */
class CollectReadSubstitutionOptionTest 
extends AbstractGeneralParameterProvider
implements ACOptionTest<SortedSet<BaseSubstitution>> {

	@Override
	public Stream<Arguments> testProcess() {
		return Stream.of(
				createArguments(BaseSubstitution.AtoG),
				createArguments(BaseSubstitution.AtoG, BaseSubstitution.TtoC),
				createArguments(
						BaseSubstitution.AtoG, BaseSubstitution.GtoA, 
						BaseSubstitution.CtoT, BaseSubstitution.TtoC) );
	}

	Arguments createArguments(final BaseSubstitution... baseSubs) {
		final SortedSet<BaseSubstitution> expected = new TreeSet<>(Arrays.asList(baseSubs));
		final String value = StringUtil.join(
				Character.toString(CollectReadSubstituionOption.SEP), 
				Arrays.asList(baseSubs) );
		return Arguments.of(
				createOptLine(value),
				expected);
	}
	
	@Test
	void testProcessFails() throws Exception {
		// test A2A -> no substitution
		for (Base base : Base.validValues()) {
			String value = base.toString() + BaseSubstitution.SEP + base.toString();
			myAssertOptThrows(IllegalArgumentException.class, value);
		}
		// wrong
		myAssertOptThrows(IllegalArgumentException.class, "wrong");
		
	}
	
	@Override
	public AbstractACOption createTestInstance() {
		return new CollectReadSubstituionOption(getGeneralParamter());
	}

	@Override
	public SortedSet<BaseSubstitution> getActualValue() {
		return getGeneralParamter().getReadSubstitutions();
	}
	
}
