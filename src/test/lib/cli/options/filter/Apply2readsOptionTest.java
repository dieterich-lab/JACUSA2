package test.lib.cli.options.filter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import htsjdk.samtools.util.StringUtil;
import jacusa.method.rtarrest.RTarrestMethod.RT_READS;
import lib.cli.options.AbstractOption;
import lib.cli.options.filter.Apply2readsOption;
import lib.cli.options.filter.has.HasApply2reads;
import lib.io.InputOutput;
import test.lib.cli.options.OptionTest;

/**
 * Tests @see lib.cli.options.filter.Apply2readsOption#process(org.apache.commons.cli.CommandLine)
 */
class Apply2readsOptionTest 
implements OptionTest<Set<RT_READS>> {
	
	private HasApply2reads hasApply2reads;
	
	@BeforeEach
	void beforeEach() {
		hasApply2reads = new DefaultHasApply2reads(new HashSet<>());
	}
	
	@Test
	void testProcessCLIFails() throws Exception {
		// not a RT_READS
		myAssertLongOptThrows(IllegalArgumentException.class, "wrong");
	}
	
	@Override
	public AbstractOption createTestInstance() {
		return new Apply2readsOption(hasApply2reads);
	}
	
	@Override
	public Set<RT_READS> getActualValue() {
		return hasApply2reads.getApply2Reads();
	}
	
	@Override
	public Stream<Arguments> testProcess() {
		return Stream.of(
				createArguments(RT_READS.ARREST),
				createArguments(RT_READS.THROUGH),
				createArguments(RT_READS.ARREST, RT_READS.THROUGH) );
	}
	
	Arguments createArguments(final RT_READS... apply2Reads) {
		return Arguments.of(
				createLongOptLine(StringUtil.join(
						Character.toString(InputOutput.AND), 
						apply2Reads)),
				new HashSet<RT_READS>(Arrays.asList(apply2Reads)) );
	}

	private class DefaultHasApply2reads implements HasApply2reads {
		
		private final Set<RT_READS> apply2reads;
		
		public DefaultHasApply2reads(final Set<RT_READS> apply2reads) {
			this.apply2reads = apply2reads;
		}
		
		@Override
		public Set<RT_READS> getApply2Reads() {
			return apply2reads;
		}

	}
	
}
