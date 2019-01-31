package test.jacusa.io.format;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import jacusa.JACUSA;
import lib.cli.parameter.GeneralParameter;
import lib.data.ParallelData;
import lib.data.DataContainer.AbstractBuilderFactory;
import lib.data.DataContainer.DefaultBuilderFactory;
import lib.data.result.Result;
import lib.io.BEDlikeResultFileWriter;
import lib.util.AbstractTool;

//TODO add filter
@TestInstance(Lifecycle.PER_CLASS)
public abstract class AbstractResultWriterTest {

	public static final String PATH = "src/test/jacusa/io/format/";
	
	private AbstractBuilderFactory builderFactory;
	
	// needed to fake header output - how JACUSA was called
	@SuppressWarnings("unused")
	private AbstractTool tool;

	private BEDlikeResultFileWriter testInstance;
	// stores current file handle for output
	private File actualFile;

	public AbstractResultWriterTest() {
		builderFactory = new DefaultBuilderFactory();
	}
	
	@BeforeAll
	void beforeAll() {
		tool = new JACUSA(new String[] {});
	}

	@BeforeEach
	void beforeEach() {
		closeTestInstance();
	}
	
	@AfterEach
	void AfterEach() {
		closeTestInstance();
		closeActualFile();
	}

	/*
	 * Test
	 */
	
	@DisplayName("Test the correct header is created")
	@ParameterizedTest(name = ("Create correct header for conditions/replicates: {0}"))
	@MethodSource("testWriteHeader")
	void testWriteHeader(List<Integer> condition2replicateSize, String actualFileName, String expectedFileName) throws IOException {
		final int conditionSize = condition2replicateSize.size();
		// create actual file
		actualFile = new File(actualFileName);
		// create parameter dependent on conditionSize
		final GeneralParameter parameter = createParameter(conditionSize);
		// populate filename(s) to simulate replicates
		for (int conditionIndex = 0; conditionIndex < conditionSize; ++conditionIndex) {
			final int replicateSizes = condition2replicateSize.get(conditionIndex);
			parameter.getConditionParameter(conditionIndex).setRecordFilenames(new String[replicateSizes]);
		}
		// create test instance
		testInstance = createTestInstance(actualFileName, parameter);
		// write header
		testInstance.writeHeader(parameter.getConditionParameters());
		// close test instance
		testInstance.close();
		// create expected file
		final File expectedFile = new File(expectedFileName);
		// check it is not empty
		if (expectedFile.length() == 0) {
			throw new IllegalStateException("Size of expected is zero: " + expectedFile.getPath());
		}
		// check if there are the same
		// ignore first line of actual output
		ResultWriterTestUtils.assertEqualFiles(expectedFile, actualFile, 1);
	}

	@DisplayName("Test the correct output is created")
	@ParameterizedTest(name = ("TODO"))
	@MethodSource("testWriteResult")
	@Disabled("TODO")
	void testWriteResult(List<Result> results, String actualFileName, String expectedFileName) throws IOException {
		fail("Not yet implemented");
		// create actual file
		actualFile = new File(actualFileName);
		// use first result to infer conditionSize and replicateSize
		Result tmpStatResult = results.get(0);
		final ParallelData tmpParallelelData = tmpStatResult.getParellelData(); 
		final int conditionSize = tmpParallelelData.getConditions();
		// create parameter dependent on conditionSize
		final GeneralParameter parameter = createParameter(conditionSize);
		// populate filename(s) to simulate replicates
		for (int conditionIndex = 0; conditionIndex < conditionSize; ++conditionIndex) {
			final int replicateSizes = tmpParallelelData.getReplicates(conditionIndex);
			parameter.getConditionParameter(conditionIndex).setRecordFilenames(new String[replicateSizes]);
		}
		// create test instance
		testInstance = createTestInstance(actualFileName, parameter);
		// write header
		testInstance.writeHeader(parameter.getConditionParameters());
		// write results
		results.stream()
			.forEach(r -> testInstance.writeResult(r));
		// close
		testInstance.close();
		// create expected file
		final File expectedFile = new File(expectedFileName);
		if (expectedFile.length() == 0) {
			throw new IllegalStateException("Size of expected is zero: " + expectedFile.getPath());
		}
		// check if there are the same
		// ignore first line of actual output
		ResultWriterTestUtils.assertEqualFiles(expectedFile, actualFile, 1);
	}

	
	/*
	 * Abstract
	 */
	
	public abstract Stream<Arguments> testWriteResult();
	public abstract Stream<Arguments> testWriteHeader();
	public abstract GeneralParameter createParameter(int conditionSize);
	public abstract BEDlikeResultFileWriter createTestInstance(String fileName, GeneralParameter parameter);
	
	/*
	 * Helper
	 */

	protected AbstractBuilderFactory getBuilderFactory() {
		return builderFactory;
	}
	
	private void closeTestInstance() {
		if (testInstance != null) {
			try {
				testInstance.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			testInstance = null;
		}
	}
	
	private void closeActualFile() {
		if (actualFile != null && actualFile.exists()) {
			actualFile.delete();
			actualFile = null;
		}
	}
	
}
