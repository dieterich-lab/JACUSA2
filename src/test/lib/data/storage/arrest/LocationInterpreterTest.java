package test.lib.data.storage.arrest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import htsjdk.samtools.SAMRecord;
import lib.data.storage.arrest.LocationInterpreter;
import lib.record.Record;
import lib.util.LibraryType;
import lib.util.coordinate.CoordinateTranslator;
import lib.util.coordinate.DefaultCoordinateTranslator;
import lib.util.position.Position;
import lib.util.position.PositionProvider;
import lib.util.position.UnmodifiablePosition;
import test.utlis.SAMRecordBuilder;

@TestInstance(Lifecycle.PER_CLASS)
interface LocationInterpreterTest {

	@ParameterizedTest(name = "{3}")
	@MethodSource("testGetThroughPositionProvider")
	default void testGetThroughPositionProvider(
			CoordinateTranslator translator,
			Record record,
			List<Position> expected,
			String info) {

		final PositionProvider throughPositionProvider = 
				createTestInstance().getThroughPositionProvider(record, translator);
	
		final List<Position> actual = new ArrayList<>(expected.size());
		while (throughPositionProvider.hasNext())
			actual.add(throughPositionProvider.next());
		assertEquals(expected, actual);
	}
	
	@ParameterizedTest(name = "{3}")
	@MethodSource("testGetArrestPosition")
	default void testGetArrestPosition(
			CoordinateTranslator translator,
			Record record,
			Position expected,
			String info) {
		
		final LocationInterpreter testInstance 	= createTestInstance();
		final Position actual = testInstance.getArrestPosition(record, translator);
		assertEquals(expected, actual);
	}
	
	Stream<Arguments> testGetThroughPositionProvider();
	Stream<Arguments> testGetArrestPosition();
	
	LocationInterpreter createTestInstance();

	String getContig();
	LibraryType getLibraryType();
	
	// ',' separated array of strings of the following form: "ref,winPos,read,{A|C|G|T}" 
	default List<Position> parseExpected(final Record record, final String[] str) {
		final List<Position> expected = new ArrayList<Position>(str.length);
		for (final String tmpStr : str) {
			final String[] cols = tmpStr.split(",");
			final int refPos	= Integer.parseInt(cols[0]);
			final int readPos 	= Integer.parseInt(cols[1]);
			final int winPos 	= Integer.parseInt(cols[2]);
			expected.add(new UnmodifiablePosition(refPos, readPos, winPos, record));
		}
		return expected;
	}

	default CoordinateTranslator cT(final int refPosWinStart, final int refPosWinEnd) {
		final int length = refPosWinEnd - refPosWinStart + 1;
		return new DefaultCoordinateTranslator(refPosWinStart, length);
	}

	default void addReadInfo(final Record record, final StringBuilder infoBuilder) {
		final SAMRecord samRecord = record.getSAMRecord();
		infoBuilder.append("Read: ");
		if (samRecord.getReadPairedFlag()) {
			infoBuilder.append("PE ");
			if (samRecord.getFirstOfPairFlag()) {
				addReadDetailsInfo(1, record, infoBuilder);
				addReadDetailsInfo(2, record.getMate(), infoBuilder);
			} else {
				addReadDetailsInfo(1, record.getMate(), infoBuilder);
				addReadDetailsInfo(2, record, infoBuilder);
			}
		} else {
			infoBuilder.append("SE ");
			addReadDetailsInfo(record, infoBuilder);
		}
	}
	
	default void addReadDetailsInfo(final int readNumber, final Record record, final StringBuilder infoBuilder) {
		infoBuilder.append("Mate: ").append(readNumber).append(" ");
		addReadDetailsInfo(record, infoBuilder);
	}
	
	default void addReadDetailsInfo(final Record record, final StringBuilder infoBuilder) {
		final SAMRecord samRecord = record.getSAMRecord();
		infoBuilder
		.append(samRecord.getAlignmentStart())
		.append('-')
		.append(samRecord.getAlignmentEnd()).append(':')
		.append(samRecord.getReadNegativeStrandFlag() ? '-' : '+')
		.append(' ');
	}

	default Arguments cArrestSE(
			final int refWinStart, final int refWinEnd,
			final int refStart, final boolean negativeStrand, final String cigarStr,
			final int refExpected, final int readExpected, final int winExpected) {

		
		final SAMRecord samRecord = new SAMRecordBuilder().createSERecord(getContig(), refStart, negativeStrand, cigarStr, "");
		final Record record = new Record(samRecord);
		
		final StringBuilder infoBuilder = new StringBuilder()
				.append("Lib.: ").append(getLibraryType()).append("; ");
		
		return cArrestArgs(
				cT(refWinStart, refWinEnd),
				record,
				new UnmodifiablePosition(refExpected, readExpected, winExpected, record),
				infoBuilder);
	}

	default Arguments cArrestPE(
			final int refWinStart, final int refWinEnd,
			final int refStart1, final boolean negativeStrand1, final String cigarStr1,
			final int refStart2, final boolean negativeStrand2, final String cigarStr2,
			final int refExpected, final int readExpected, final int winExpected) {

		final StringBuilder infoBuilder = new StringBuilder()
				.append("Lib.: ").append(getLibraryType()).append("; ");
		
		final Iterator<SAMRecord> it = new SAMRecordBuilder().withPERead(
				getContig(), 
				refStart1, negativeStrand1, cigarStr1, "", 
				refStart2, negativeStrand2, cigarStr2, "").getRecords().iterator();
		final SAMRecord samRecord = it.next();
		final Record record = new Record(samRecord, samRecord.getFileSource().getReader());
		
		return cArrestArgs(
				cT(refWinStart, refWinEnd),
				record,
				new UnmodifiablePosition(refExpected, readExpected, winExpected, null),
				infoBuilder);
	}
	
	default Arguments cArrestArgs(
			final CoordinateTranslator translator, 
			final Record record,
			final Position expected, 
			final StringBuilder infoBuilder) {
		
		addReadInfo(record, infoBuilder);
		
		return Arguments.of(
				translator, record, 
				expected, infoBuilder.toString());
	}
	
	default Arguments cThroughSE(
			final int refWinStart, final int refWinEnd,
			final int refStart, final boolean negativeStrand, final String cigarStr,
			final String[] expectedStr) {

		final StringBuilder infoBuilder = new StringBuilder()
				.append("Lib.: ").append(getLibraryType()).append("; ");
		
		final SAMRecord samRecord = new SAMRecordBuilder().createSERecord(
				getContig(), refStart, negativeStrand, cigarStr, "");
		final Record record = new Record(samRecord); 
		
		return cThroughArgs(
				cT(refWinStart, refWinEnd),
				record,
				parseExpected(record, expectedStr),
				infoBuilder);
	}
	
	default Arguments cThroughPE(
			final int refWinStart, final int refWinEnd,
			final int refStart1, final boolean negativeStrand1, final String cigarStr1,
			final int refStart2, final boolean negativeStrand2, final String cigarStr2,
			final String[] expectedStr) {

		final StringBuilder infoBuilder = new StringBuilder()
				.append("Lib.: ").append(getLibraryType()).append("; ");
		
		final Iterator<SAMRecord> it = new SAMRecordBuilder().withPERead(
						getContig(), 
						refStart1, negativeStrand1, cigarStr1, "", 
						refStart2, negativeStrand2, cigarStr2, "").getRecords().iterator();
		final SAMRecord samRecord = it.next();
		final Record record = new Record(samRecord, samRecord.getFileSource().getReader());
		
		return cThroughArgs(
				cT(refWinStart, refWinEnd),
				record,
				parseExpected(record, expectedStr),
				infoBuilder);
	}
	
	default Arguments cThroughArgs(
			final CoordinateTranslator translator, 
			final Record record,
			final List<Position> expected, 
			final StringBuilder infoBuilder) {

		addReadInfo(record, infoBuilder);
		
		return Arguments.of(
				translator, record, 
				expected, infoBuilder.toString());
	}

	
}
