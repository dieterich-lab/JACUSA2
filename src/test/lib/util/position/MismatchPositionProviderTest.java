package test.lib.util.position;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import htsjdk.samtools.SAMTag;
import lib.data.validator.Validator;
import lib.record.Record;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.coordinate.DefaultCoordinateTranslator;
import lib.util.position.MismatchPosProvider;
import lib.util.position.Position;
import test.utlis.SAMRecordBuilder;

@TestInstance(Lifecycle.PER_CLASS)
class MismatchPositionProviderTest implements PositionProviderTest {

	private static final String CONTIG = "PositionProviderTest";

	@ParameterizedTest(name = "{3}")
	@MethodSource("testIterator")
	void testIterator(
			Record record,
			CoordinateTranslator translator,
			List<Position> expected,
			String info) {
		
		final MismatchPosProvider testInstance = createTestInstance(
				record, translator);
		final List<Position> actual = testInstance.flat();
		assertEquals(expected, actual);
	}

	Stream<Arguments> testIterator() {
		// Reference Sequence
		//ACGAACGT
		//12345678
		return Stream.of(
				createArguments(
						1, "2M", new String(), 
						1, 8, 
						new String[] { }),

				// outside of window
				createArguments(
						1, "2M", "AT", 
						1, 9, 
						new String[] { "2,1,1" }),
				// deletion in query
				createArguments(
						1, "2M3D2M", "ACCT", 
						1, 8, 
						new String[] { "7,3,6" }),
				// insertion in query
				createArguments(
						1, "2M3I2M", "ACCCCGA", 
						1, 8, 
						new String[] { }),
				// gap in query
				createArguments(
						1, "2M3N2M", "ACCG", 
						1, 8, 
						new String[] { }),

				createArguments(
						1, "2M", "AT", 
						1, 8, 
						new String[] { "2,1,1" })
				
				);
	}
	
	MismatchPosProvider createTestInstance(
			final Record record, 
			final CoordinateTranslator translator) {
		
		return new MismatchPosProvider(record, translator, new ArrayList<Validator>());
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
		
		final int nm 	= record.getSAMRecord().getIntegerAttribute(SAMTag.NM.name());
		final String md = record.getSAMRecord().getStringAttribute(SAMTag.MD.name());
				
		final String info = String.format(
				"read %d-%d, cigar: %s, readSeq: %s, NM: %d, MD: %s, win: %d-%d",
				record.getSAMRecord().getAlignmentStart(),
				record.getSAMRecord().getAlignmentEnd(),
				record.getSAMRecord().getCigar(),
				readSeq.isEmpty() ? "*" : readSeq,
				nm,
				md,
				translator.getRefPosStart(), translator.getRefPosEnd());
		
		return Arguments.of(
				record,
				translator,
				expectedPositions,
				info);
	}
	
}
