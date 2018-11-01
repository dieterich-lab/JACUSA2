package test.lib.cli.options;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lib.cli.options.SAMPathnameArg;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.JACUSAConditionParameter;
import lib.util.Util;

@DisplayName("Test CLI processing of SAMPathnameArg")
class SAMPathnameArgTest {

	private AbstractConditionParameter conditionParameter;
	private SAMPathnameArg pathnameArg;
	
	@BeforeEach
	void setUp() {
		final int conditionIndex = 2;
		conditionParameter = new JACUSAConditionParameter(conditionIndex);
		pathnameArg = new SAMPathnameArg(conditionIndex, conditionParameter);
	}

	/*
	 * Test
	 */

	private String toFilename(int i) {
		final StringBuilder sb = new StringBuilder();
		sb.append(AbstractACOptionTest.PATH);
		sb.append("SAMPathnameArg");
		sb.append(i);
		sb.append(".bam");
		return sb.toString();
	}
	
	@Test
	@DisplayName("Check SAMPathnameArg are parsed correctly")
	void testProcessArg() throws FileNotFoundException {
		final String[] expected = IntStream.rangeClosed(1, 3)
				.mapToObj(i -> toFilename(i))
				.toArray(String[]::new);

		final String arg = String.join(new StringBuilder(1).append(Util.FIELD_SEP), 
				expected);

		pathnameArg.processArg(arg);

		// check
		final String[] actual = conditionParameter.getRecordFilenames();
		assertEquals(expected, actual);
	}

	@Test
	@DisplayName("Check SAMPathnameArg fails on wrong input")
	void testProcessArgFail() {
		// no bam
		assertThrows(FileNotFoundException.class,
				() -> {
					final String[] fileNames = new String[] { "SAMPathnameArg4.bam" };
					final String arg = String.join(new StringBuilder(1).append(Util.FIELD_SEP), fileNames);
					pathnameArg.processArg(arg);				
				});

		// no bai
		assertThrows(FileNotFoundException.class,
				() -> {
					final String[] fileNames = new String[] { "SAMPathnameArg5.bam" };
					final String arg = String.join(new StringBuilder(1).append(Util.FIELD_SEP), fileNames);
					pathnameArg.processArg(arg);				
				});
	}
	
}
