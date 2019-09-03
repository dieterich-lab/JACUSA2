package test.lib.util.position;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import lib.record.Record;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.coordinate.DefaultCoordinateTranslator;
import lib.util.position.AllAlignmentBlocksPosProvider;
import lib.util.position.Position;
import test.utlis.SAMRecordBuilder;

@TestInstance(Lifecycle.PER_CLASS)
class AllAlignmentBlocksPositionProviderTest implements PositionProviderTest {

	private static final String CONTIG = "PositionProviderTest";
	
	@ParameterizedTest(name = "{3}")
	@MethodSource("testIterator")
	void testIterator(
			Record record,
			CoordinateTranslator translator,
			List<Position> expected,
			String info) {
		
		final AllAlignmentBlocksPosProvider testInstance = createTestInstance(
				record, translator);
		final List<Position> actual = testInstance.flat();
		assertEquals(expected, actual);
	}

	// TODO Qi add more complicated tests, e.g.: INDEL + spliced + partially outside window
	Stream<Arguments> testIterator() {
		// Reference Sequence
		//ACGAACGT
		//12345678
		return Stream.of(
				
				createArguments(
						1, "2M", new String(), 
						1, 8, 
						new String[] { "1,0,0", "2,1,1"}),

				createArguments(
						1, "2M2N2M", new String(), 
						1, 8, 
						new String[] { "1,0,0", "2,1,1", "5,2,4", "6,3,5"}),

				createArguments(
						1, "2M2I2M", new String(), 
						1, 9, 
						new String[] { "1,0,0", "2,1,1", "3,4,2", "4,5,3"}),

				createArguments(
						1, "2M2D2M", new String(), 
						1, 9, 
						new String[] { "1,0,0", "2,1,1", "5,2,4", "6,3,5"}),

				//ACGAACGT
				//12345678
				//-MNMNM-- cigar
				//--WWW--- window
				createArguments(
						2, "1M1N1M1N1M", new String(), 
						3, 3, 
						new String[] { "4,1,1"})
				
				);
	}
	
	AllAlignmentBlocksPosProvider createTestInstance(
			final Record record, 
			final CoordinateTranslator translator) {
		
		return new AllAlignmentBlocksPosProvider(record, translator);
	}

	Arguments createArguments(
			final int refStart, final String cigarStr, final String readSeq, 
			final int refPosWinStart, final int winLength,
			String[] expectedStrs) {
		
		final Record record = new Record(
				SAMRecordBuilder.createSERead(CONTIG, refStart, cigarStr, readSeq));
		
		final CoordinateTranslator translator = 
				new DefaultCoordinateTranslator(refPosWinStart, winLength); 
		final List<Position> expectedPositions = parseExpected(expectedStrs, record);
		final String info = String.format(
				"read %d-%d, cigar: %s, win: %d-%d",
				record.getSAMRecord().getAlignmentStart(),
				record.getSAMRecord().getAlignmentEnd(),
				record.getSAMRecord().getCigar(),
				translator.getRefPosStart(), translator.getRefPosEnd());
		
		return Arguments.of(
				record,
				translator,
				expectedPositions,
				info);
	}
	
}
