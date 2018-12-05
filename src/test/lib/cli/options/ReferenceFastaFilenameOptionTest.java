package test.lib.cli.options;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import lib.cli.options.AbstractACOption;
import lib.cli.options.ReferenceFastaFilenameOption;
import lib.cli.parameter.GeneralParameter;
import test.utlis.CLIUtils;

@DisplayName("Test CLI processing of ReferenceFastaFilenameOption")
class ReferenceFastaFilenameOptionTest extends AbstractACOptionTest<String> {

	/*
	 * Tests
	 */
	
	@DisplayName("Check ReferenceFastaFilenameOption are parsed correctly")
	@ParameterizedTest(name = "Reference fasta fileName: {0}")
	@ValueSource( strings = { "ReferenceFastaFilenameOptionTest.fasta" } )
	@Override
	void testProcess(String expected) throws Exception {
		expected = PATH + expected;
		super.testProcess(expected);
	}

	@Test
	@DisplayName("Check ReferenceFastaFilenameOption fails on wrong input")
	void testProcessFail() throws Exception {
		final String value = PATH + "missingReferenceFastaFilenameOptionTest.fasta";
		getParserWrapper().myAssertThrows(FileNotFoundException.class, getACOption(), value);
	}
	
	/*
	 * Helper
	 */

	@Override
	protected AbstractACOption create(GeneralParameter parameter) {
		return new ReferenceFastaFilenameOption(parameter);
	}

	@Override
	protected String getActualValue(GeneralParameter parameter) {
		return getParameter().getReferenceFilename();
	}
	
	@Override
	protected String createLine(String v) {
		return CLIUtils.assignValue(getOption(), v);
	}
	
}
