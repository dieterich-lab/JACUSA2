package test.lib.cli.options;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lib.cli.options.SAMPathnameArg;
import lib.cli.parameter.ConditionParameter;
import lib.io.InputOutput;

/**
 * Tests @see lib.cli.options.SAMPathnameArg#processArg(String)
 */
class SAMPathnameArgTest {

	private ConditionParameter conditionParameter;
	private SAMPathnameArg testInstance;
	
	@BeforeEach
	void beforeEach() {
		final int conditionIndex 	= 1;
		conditionParameter 			= new ConditionParameter(conditionIndex);
		testInstance 				= new SAMPathnameArg(conditionIndex, conditionParameter);
	}

	private String toFilename(int i) {
		return new StringBuilder()
				.append(OptionTest.PATH).append("SAMPathnameArgTest")
				.append("_File").append(i).append(".bam").toString();
	}
	
	@Test
	@DisplayName("Check SAMPathnameArg are parsed correctly")
	void testProcessArg() throws FileNotFoundException {
		final String[] expected = IntStream.rangeClosed(1, 3)
				.mapToObj(i -> toFilename(i))
				.toArray(String[]::new);

		final String arg = String.join(new StringBuilder(1).append(SAMPathnameArg.SEP), 
				expected);

		testInstance.processArg(arg);

		// check
		final String[] actual = conditionParameter.getRecordFilenames();
		assertArrayEquals(expected, actual);
	}

	@Test
	void testProcessArgFail() {
		// no bam
		assertThrows(FileNotFoundException.class,
				() -> {
					final String[] fileNames = new String[] { "wrong.bam" };
					final String arg = String.join(new StringBuilder(1).append(InputOutput.FIELD_SEP), fileNames);
					testInstance.processArg(arg);				
				});

		// no bai
		assertThrows(FileNotFoundException.class,
				() -> {
					final String[] fileNames = new String[] { "SAMPathnameArg4.bam" };
					final String arg = String.join(new StringBuilder(1).append(InputOutput.FIELD_SEP), fileNames);
					testInstance.processArg(arg);				
				});
	}
	
}
