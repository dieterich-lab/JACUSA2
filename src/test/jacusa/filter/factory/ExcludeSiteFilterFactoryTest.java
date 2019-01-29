package test.jacusa.filter.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import htsjdk.samtools.SAMException;
import jacusa.filter.factory.ExcludeSiteFilterFactory;
import jacusa.io.FileType;
import test.utlis.CLIUtils;

/**
 * Tests @see test.jacusa.filter.factory.ExcludeSiteFilterFactory
 */
// TODO add test for codec
@TestInstance(Lifecycle.PER_CLASS)
public class ExcludeSiteFilterFactoryTest {

	public static final String PATH = "src/test/jacusa/filter/factory/excludesitefilterfactory";
	
	private final String fileName_LONG_OPT;
	private final String fileType_LONG_OPT;
	
	private ExcludeSiteFilterFactory testInstance;
	
	public ExcludeSiteFilterFactoryTest() {
		fileName_LONG_OPT = ExcludeSiteFilterFactory.getFileNameOptionBuilder().build().getLongOpt();
		fileType_LONG_OPT = ExcludeSiteFilterFactory.getFileTypeOptionBuilder().build().getLongOpt();
	}
	
	@BeforeEach
	void beforeEach() {
		testInstance = new ExcludeSiteFilterFactory();
	}
	
	@ParameterizedTest(name = "Process line: {0}")
	@MethodSource("testProcessCLI")
	void testProcessCLI(String line, String expectedFileName, FileType expectedFileType) throws ParseException {
		testInstance.processCLI(line);
		
		final String actualFileName = testInstance.getFileName();
		assertEquals(expectedFileName, actualFileName);
		
		final FileType actualFiletype = testInstance.getFileType();
		assertEquals(expectedFileType, actualFiletype);
	}
	
	@DisplayName("Check processCLI fails on wrong input")
	@Test
	void testProcessCLIFails() {
		// file does not exist
		assertThrows(SAMException.class,
				() -> {
					final String line = createLine( PATH + "/" + "wrong.bed", FileType.BED);
					testInstance.processCLI(line);								
				});
		// unknown file type
		assertThrows(IllegalArgumentException.class,
				() -> {
					final String line = createLine( PATH + "/" + "test.bed", "wrong");
					testInstance.processCLI(line);
				});
	}
	
	Stream<Arguments> testProcessCLI() {
		return Stream.of(
				createArguments("test.bed", FileType.BED),
				createArguments("test.vcf", FileType.VCF),
				createArguments("test.vcf4", FileType.VCF) );
	}
	
	Arguments createArguments(final String fileName, final FileType fileType) {
		final String fullPath = PATH + "/" + fileName;
		return Arguments.of(
				createLine(fullPath, fileType),
				fullPath,
				fileType);
	}
	
	String createLine(final String fileName, final String fileType) {
		return new StringBuilder()
				.append(CLIUtils.pr(fileName_LONG_OPT, fileName))
				.append(' ')
				.append(CLIUtils.pr(fileType_LONG_OPT, fileType))
				.toString();
	}
	
	String createLine(final String fileName, final FileType fileType) {
		return createLine(fileName, fileType.getName());
	}
	
}
