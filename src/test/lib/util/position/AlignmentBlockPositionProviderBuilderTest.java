package test.lib.util.position;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import lib.recordextended.SAMRecordExtended;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.coordinate.DefaultCoordinateTranslator;
import lib.util.position.AlignmentBlockPositionProviderBuilder;
import lib.util.position.Position;
import lib.util.position.PositionProvider;
import test.utlis.SAMRecordBuilder;

// This class exemplifies how traverse of an alignment block is tested
// the aligned position contains 3 positional fields: refPos, readPos, and winPos
// winPos is guaranteed to be correct ONLY if adjustWindowPos() has been called!
// The expected field is an String array where each string is of the following format: 
// "refPos,readPos,winPos"
// You can add more tests, e.g.: spliced reads (modify cigarStr), 
// INDELSs (modify readSeq and cigarStr)
// Furthermore, virtually all tests are implemented with SE reads - you could implement PE reads.
// You need to duplicate the interface in SAMRecordBuilder to support PE reads - set correct flags etc.
// check https://github.com/samtools/htsjdk/blob/master/src/main/java/htsjdk/samtools/SAMRecordSetBuilder.java 
@TestInstance(Lifecycle.PER_CLASS)
class AlignmentBlockPositionProviderBuilderTest implements PositionProviderTest {

	private static final String CONTIG = "PositionProviderTest"; 
	
	@ParameterizedTest(name = "{6}")
	@MethodSource("testTryFirst")
	void testTryFirst(
			int blockIndex,
			SAMRecordExtended recordExtended,
			CoordinateTranslator translator,
			int length,
			boolean adjustWinPos,
			List<Position> expected,
			String info) {
		
		final AlignmentBlockPositionProviderBuilder testInstance = createTestInstace(
				blockIndex, recordExtended, translator);
		testInstance.tryFirst(length);
		if (adjustWinPos) {
			testInstance.adjustWindowPos();
		}
		final PositionProvider positionProvider = testInstance.build();
		final List<Position> actual = positionProvider.flat();
		assertEquals(expected, actual);
	}
	
	Stream<Arguments> testTryFirst() {
		// Reference Sequence
		//ACGAACGT
		//12345678
		return Stream.of(
				
				createArguments(
						0, 3, 
						1, "3M", "", 
						1, 3, false, 
						new String[] { "1,0,0", "2,1,1", "3,2,2"}), // position format: "ref,read,win"
				
				createArguments(
						0, 3, 
						1, "3M", "", 
						1, 3, true, 
						new String[] { "1,0,0", "2,1,1", "3,2,2"}),

				// test length < blockLength
				createArguments(
						0, 2, 
						1, "3M", "", 
						1, 3, false, 
						new String[] { "1,0,0", "2,1,1"}),

				// test length > blockLength
				createArguments(
						0, 4, 
						1, "3M", "", 
						1, 3, false, 
						new String[] { "1,0,0", "2,1,1", "3,2,2"}) );
	}
	
	@ParameterizedTest(name = "{6}")
	@MethodSource("testTryLast")
	void testTryLast(int blockIndex,
			SAMRecordExtended recordExtended,
			CoordinateTranslator translator,
			int length,
			boolean adjustWinPos,
			List<Position> expected,
			String info) {
		
		final AlignmentBlockPositionProviderBuilder testInstance = createTestInstace(
				blockIndex, recordExtended, translator);
		testInstance.tryLast(length);
		if (adjustWinPos) {
			testInstance.adjustWindowPos();
		}
		final PositionProvider positionProvider = testInstance.build();
		final List<Position> actual = positionProvider.flat();
		assertEquals(expected, actual);
	}
	
	Stream<Arguments> testTryLast() {
		// Reference Sequence
		//ACGAACGT
		//12345678
		return Stream.of(
				
				createArguments(
						0, 3, 
						1, "3M", "", 
						1, 3, false, 
						new String[] { "1,0,0", "2,1,1", "3,2,2"}), // position format: "ref,read,win"
				
				createArguments(
						0, 3, 
						1, "3M", "", 
						1, 3, true, 
						new String[] { "1,0,0", "2,1,1", "3,2,2"}),

				// test length < blockLength
				createArguments(
						0, 2, 
						1, "3M", "", 
						1, 3, false, 
						new String[] { "2,1,1", "3,2,2"}),

				// test length > blockLength
				createArguments(
						0, 4, 
						1, "3M", "", 
						1, 3, false, 
						new String[] { "1,0,0", "2,1,1", "3,2,2"}) );
	}

	@ParameterizedTest(name = "{6}")
	@MethodSource("testIgnoreFirst")
	void testIgnoreFirst(
			int blockIndex,
			SAMRecordExtended recordExtended,
			CoordinateTranslator translator,
			int length,
			boolean adjustWinPos,
			List<Position> expected,
			String info) {
		
		final AlignmentBlockPositionProviderBuilder testInstance = createTestInstace(
				blockIndex, recordExtended, translator);
		testInstance.ignoreFirst(length);
		if (adjustWinPos) {
			testInstance.adjustWindowPos();
		}
		final PositionProvider positionProvider = testInstance.build();
		final List<Position> actual = positionProvider.flat();
		assertEquals(expected, actual);
	}
	
	Stream<Arguments> testIgnoreFirst() {
		// Reference Sequence
		//ACGAACGT
		//12345678
		return Stream.of(
				
				createArguments(
						0, 3, 
						1, "3M", "", 
						1, 3, false, 
						new String[] { }), // position format: "ref,read,win"
				
				createArguments(
						0, 3, 
						1, "3M", "", 
						1, 3, true, 
						new String[] { }),

				// test length < blockLength
				createArguments(
						0, 1, 
						1, "3M", "", 
						1, 3, false, 
						new String[] { "2,1,1", "3,2,2"}),

				// test length > blockLength
				createArguments(
						0, 4, 
						1, "3M", "", 
						1, 3, false, 
						new String[] { }) );
	}

	@ParameterizedTest(name = "{5}")
	@MethodSource("testAdjustForWindow")
	void testAdjustForWindow(int blockIndex,
			SAMRecordExtended recordExtended,
			CoordinateTranslator translator,
			boolean adjustWinPos,
			List<Position> expected,
			String info) {
		
		final AlignmentBlockPositionProviderBuilder testInstance = createTestInstace(
				blockIndex, recordExtended, translator);
		if (adjustWinPos) {
			testInstance.adjustWindowPos();
		}
		final PositionProvider positionProvider = testInstance.build();
		final List<Position> actual = positionProvider.flat();
		assertEquals(expected, actual);
	}
	
	Stream<Arguments> testAdjustForWindow() {
		// Reference Sequence
		//ACGAACGT
		//12345678
		return Stream.of(
				
				createArguments(
						0, 
						3, "3M", "", 
						1, 3, true, 
						new String[] { "3,0,2" }), // position format: "ref,read,win"
				
				createArguments(
						0, 
						3, "3M", "", 
						1, 3, false, 
						new String[] { "3,0,2", "4,1,3", "5,2,4"}),

				createArguments(
						0, 
						3, "3M", "", 
						3, 3, false, 
						new String[] { "3,0,0", "4,1,1", "5,2,2"}),

				createArguments(
						0, 
						3, "3M", "", 
						3, 3, true, 
						new String[] { "3,0,0", "4,1,1", "5,2,2"}), 
				
				createArguments(
						0, 
						5, "3M", "", 
						3, 3, false, 
						new String[] { "5,0,2", "6,1,3", "7,2,4"}),

				createArguments(
						0, 
						5, "3M", "", 
						3, 3, true, 
						new String[] { "5,0,2"} )
				
				);

	}

	AlignmentBlockPositionProviderBuilder createTestInstace(
			final int blockIndex, 
			final SAMRecordExtended recordExtended, 
			final CoordinateTranslator translator) {
		
		return new AlignmentBlockPositionProviderBuilder(blockIndex, recordExtended, translator);
	}

	Arguments createArguments(
			final int blockIndex, 
			final int refStart, final String cigarStr, final String readSeq, 
			final int refPosWinStart, final int winLength, final boolean adjustWinPos,
			String[] expectedStrs) {
		
		final SAMRecordExtended recordExtended = new SAMRecordExtended(
				SAMRecordBuilder.createSERead(CONTIG, refStart, cigarStr, readSeq));
		
		final CoordinateTranslator translator = 
				new DefaultCoordinateTranslator(refPosWinStart, winLength); 
		final List<Position> expectedPositions = parseExpected(expectedStrs, recordExtended);
		final String info = String.format(
				"blockIndex: %d, read %d-%d, win: %d-%d, adjustWinPos: %s",
				blockIndex,
				recordExtended.getSAMRecord().getAlignmentStart(),
				recordExtended.getSAMRecord().getAlignmentEnd(),
				translator.getRefPosStart(), translator.getRefPosEnd(), 
				adjustWinPos);
		
		return Arguments.of(
				blockIndex,
				recordExtended,
				translator,
				adjustWinPos,
				expectedPositions,
				info);
	}
	
	Arguments createArguments(
			final int blockIndex, final int length,
			final int refStart, final String cigarStr, final String readSeq, 
			final int refPosWinStart, final int winLength, final boolean adjustWinPos,
			String[] expectedStrs) {
		
		final SAMRecordExtended recordExtended = new SAMRecordExtended(
				SAMRecordBuilder.createSERead(CONTIG, refStart, cigarStr, readSeq));
		
		final CoordinateTranslator translator = 
				new DefaultCoordinateTranslator(refPosWinStart, winLength); 
		final List<Position> expectedPositions = parseExpected(expectedStrs, recordExtended);
		final String info = String.format(
				"blockIndex: %d, length: %d, adjustWinPos: %s",
				blockIndex, length, adjustWinPos);
		
		return Arguments.of(
				blockIndex,
				recordExtended,
				translator,
				length, 
				adjustWinPos,
				expectedPositions,
				info);
	}

}
