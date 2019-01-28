package test.jacusa.filter.factory.rtarrest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.rtarrest.RTarrestHomozygousFilterFactory;
import jacusa.method.rtarrest.RTarrestMethod;
import jacusa.method.rtarrest.RTarrestMethod.RT_READS;
import lib.data.cache.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.util.Util;
import test.utlis.CLIUtils;
import test.utlis.TestUtils;
import test.jacusa.filter.factory.AbstractHomozygousFilterFactoryTest;

@DisplayName("Test CLI processing of RTarrestHomozygousFilterFactory")
public class RTarrestHomozygousFilterFactoryTest extends AbstractHomozygousFilterFactoryTest {
	
	private RTarrestHomozygousFilterFactory testInstance;
	
	@DisplayName("Test processCLI sets apply2reads correctly")
	@ParameterizedTest(name = "Parse line: {0} and expect apply2reads to be: {1}")
	@MethodSource("testProcessCLIApply2Reads")
	void testProcessCLIApply2Reads(String line, Set<RT_READS> expected) throws ParseException {
		createTestInstance(2);
		getTestInstance().processCLI(line);
		final Set<RT_READS> actual = testInstance.getApply2Reads();
		TestUtils.equalSets(expected, actual);
	}
	
	@Test
	@DisplayName("Test processCLI fails on wrong input")
	void testProcessCLIApply2ReadsFails() {
		createTestInstance(2);
		// not RT_READS
		assertThrows(IllegalArgumentException.class,
				() -> {
					final String line1 = createLine(1);
					final String line2 = setApply2Reads("wrong");
					getTestInstance().processCLI(line1 + line2);								
				});
		// required missing
		assertThrows(MissingOptionException.class,
				() -> {
					final String line1 = createLine(1);
					final String line2 = setApply2Reads("");
					getTestInstance().processCLI(line1 + line2);								
				});
	}
	
	/*
	 * Method Source
	 */
	
	public Stream<Arguments> testProcessCLIApply2Reads() {
		final List<Set<RT_READS>> data = new ArrayList<>();
		data.add(new HashSet<>(Arrays.asList(RT_READS.ARREST)));
		data.add(new HashSet<>(Arrays.asList(RT_READS.THROUGH)));
		data.add(new HashSet<>(Arrays.asList(RT_READS.ARREST, RT_READS.THROUGH)));

		return data.stream().map(e -> {
			// add conditionIndex
			final String line1 = createLine(1);
			// add apply2reads
			final String line2 = setApply2Reads(e);
			return Arguments.of(line1 + line2, e);
			});
	}
	
	/*
	 * Helper
	 */
	
	protected void createTestInstance(final int conditionSize) {
		testInstance = 
				new RTarrestHomozygousFilterFactory(
						conditionSize, 
						new Apply2readsBaseCallCountSwitch(
								new HashSet<>(Arrays.asList(RT_READS.ARREST)), 
								null, 
								null, 
								null) );
	}
	
	protected AbstractFilterFactory getTestInstance() {
		return testInstance;
	}
	
	protected int getHomozygousConditionIndex() {
		return testInstance.getHomozygousConditionIndex();
	}
	
	protected String setApply2Reads(String value) {
		return CLIUtils.pr(getApply2ReadsLongOpt(), value);
	}

	protected String setApply2Reads(Set<RT_READS> value) {
		return setApply2Reads(TestUtils.collapseSet(value, Character.toString(Util.AND)));
	}
	
	protected static String getApply2ReadsLongOpt() {
		return RTarrestMethod.getReadsOptionBuilder().build().getLongOpt();
	}

}
