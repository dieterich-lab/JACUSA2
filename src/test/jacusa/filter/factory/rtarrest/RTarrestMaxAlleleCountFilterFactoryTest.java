package test.jacusa.filter.factory.rtarrest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.rtarrest.RTarrestMaxAlleleCountFilterFactory;
import jacusa.method.rtarrest.RTarrestMethod;
import jacusa.method.rtarrest.RTarrestMethod.RT_READS;
import lib.data.cache.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.util.Util;
import test.jacusa.filter.factory.AbstractMaxAlleleCountFilterFactoryTest;
import test.utlis.CLIUtils;
import test.utlis.TestUtils;

@DisplayName("Test CLI processing of RTarrestMaxAlleleCountFilterFactory")
class RTarrestMaxAlleleCountFilterFactoryTest extends AbstractMaxAlleleCountFilterFactoryTest {

	private RTarrestMaxAlleleCountFilterFactory testInstance;
	
	@BeforeEach
	public void setUp() {
		testInstance = 
				new RTarrestMaxAlleleCountFilterFactory(
						new Apply2readsBaseCallCountSwitch(
								new HashSet<>(Arrays.asList(RT_READS.ARREST)), 
								null, 
								null, 
								null) );
	}

	/*
	 * Tests
	 */

	@DisplayName("Test processCLI sets apply2reads correctly")
	@ParameterizedTest(name = "Parse line: {0} and expect apply2reads to be: {1}")
	@MethodSource("testProcessCLIApply2Reads")
	void testProcessCLIApply2Reads(String line, Set<RT_READS> expected) throws ParseException {
		testInstance.processCLI(line);
		TestUtils.equalSets(expected, testInstance.getApply2Reads());
	}
	
	@Test
	@DisplayName("Test processCLI fails on wrong input")
	void testProcessCLIApply2ReadsFails() {
		// not RT_READS
		assertThrows(IllegalArgumentException.class,
				() -> {
					final String line = setApply2Reads("wrong");
					testInstance.processCLI(line);								
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
			// add apply2reads
			final String line = setApply2Reads(e);
			return Arguments.of(line, e);
			});
	}
	
	/*
	 * Helper
	 */
	
	protected String getApply2ReadsLongOpt() {
		return RTarrestMethod.getReadsOptionBuilder().build().getLongOpt();
	}

	protected String setApply2Reads(String value) {
		return CLIUtils.pr(getApply2ReadsLongOpt(), value);
	}

	protected String setApply2Reads(Set<RT_READS> value) {
		return setApply2Reads(TestUtils.collapseSet(value, Character.toString(Util.AND)));
	}
	
	@Override
	protected int getMaxAlleles() {
		return testInstance.getMaxAlleles();
	}
	
	@Override
	protected AbstractFilterFactory getTestInstance() {
		return testInstance;
	}
	
}
