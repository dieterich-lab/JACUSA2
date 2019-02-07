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
import lib.util.position.CigarElementExtendedPositionProviderBuilder;
import lib.util.position.Position;
import lib.util.position.PositionProvider;
import test.utlis.SAMRecordBuilder;

@TestInstance(Lifecycle.PER_CLASS)
class CigarElementExtendedPositionProviderBuilderTest implements PositionProviderTest {

	private static final String CONTIG = "PositionProviderTest";
	
	@ParameterizedTest(name = "{5}")
	@MethodSource("testBuilder")
	void testBuilder(
			int index,
			int upDownStream,
			SAMRecordExtended recordExtended,
			CoordinateTranslator translator,
			List<Position> expected,
			String info) {
		
		final CigarElementExtendedPositionProviderBuilder testInstance = createTestInstance(
				index, upDownStream, recordExtended, translator);
		final PositionProvider positionProvider = testInstance.build();
		final List<Position> actual = positionProvider.flat();
		assertEquals(expected, actual);
	}

	// TODO Qi add more complicated tests, e.g.: INDEL + spliced + partially outside window
	Stream<Arguments> testBuilder() {
		// Reference Sequence
		//ACGAACGT
		//12345678
		return Stream.of(
				
				createArguments(
						1, 1, 
						2, "2M2N2M", new String(), 
						1, 8, 
						//ACGAACGT
						//12345678
						//-**--**- aligned
						//--+--+-- expected
						new String[] { "3,1,2", "6,2,5"}),

				createArguments(
						1, 2, 
						2, "2M2N2M", new String(), 
						1, 8, 
						//ACGAACGT
						//12345678
						//-**--**- aligned
						//--+--+-- expected
						new String[] { "2,0,1", "3,1,2", "6,2,5", "7,3,6"}),

				createArguments(
						1, 3, 
						2, "2M2N2M", new String(), 
						1, 8, 
						//ACGAACGT
						//12345678
						//-**--**- aligned
						//--+--+-- expected
						new String[] { "2,0,1", "3,1,2", "6,2,5", "7,3,6"}),
				
				createArguments(
						1, 1, 
						2, "2M2D2M", new String(), 
						1, 8, 
						//ACGAACGT
						//12345678
						//-**--**- aligned
						//--+--+-- expected
						new String[] { "3,1,2", "6,2,5"}),
				
				createArguments(
						1, 1, 
						2, "2M2I2M", "CGTTCG", 
						1, 8, 
						//ACGCGT
						//123678
						//-****- aligned
						//--+--+-- expected
						new String[] { "3,1,2", "4,4,3"}),
				
				createArguments(
						1, 1, 
						2, "2M2I2M", "CGTTCG", 
						1, 3, 
						//ACGCGT
						//123678
						//-****- aligned
						//--+--+-- expected
						new String[] { "3,1,2" })
				
				);
	}
	
	Arguments createArguments(
			final int cigarEEIndex, final int upDownStream,
			final int refStart, final String cigarStr, final String readSeq, 
			final int refPosWinStart, final int winLength,
			String[] expectedStrs) {
		
		final SAMRecordExtended recordExtended = new SAMRecordExtended(
				SAMRecordBuilder.createSERead(CONTIG, refStart, cigarStr, readSeq));
		
		final CoordinateTranslator translator = 
				new DefaultCoordinateTranslator(refPosWinStart, winLength); 
		final List<Position> expectedPositions = parseExpected(expectedStrs, recordExtended);
		final String info = String.format(
				"cigarEEIndex: %d, upDownStream: %d, read %d-%d, win: %d-%d",
				cigarEEIndex,
				upDownStream,
				recordExtended.getSAMRecord().getAlignmentStart(),
				recordExtended.getSAMRecord().getAlignmentEnd(),
				translator.getRefPosStart(), translator.getRefPosEnd());
		
		return Arguments.of(
				cigarEEIndex,
				upDownStream,
				recordExtended,
				translator,
				expectedPositions,
				info);
	}
	
	CigarElementExtendedPositionProviderBuilder createTestInstance(
			final int cigarElementExtendedIndex,
			final int upDownStream,
			final SAMRecordExtended recordExtended,
			final CoordinateTranslator translator) {
		
		return new CigarElementExtendedPositionProviderBuilder(
				cigarElementExtendedIndex, upDownStream, recordExtended, 
				translator);
	}
	
}
