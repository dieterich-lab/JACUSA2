package test.lib.data.storage.readsubstitution;

import lib.data.storage.readsubstitution.BaseCallInterpreter;
import lib.record.ProcessedRecord;
import lib.util.Base;
import lib.util.position.Position;
import lib.util.position.UnmodifiablePosition;
import test.utlis.ReferenceSequence;
import test.utlis.SAMRecordBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.util.StringUtil;

@TestInstance(Lifecycle.PER_CLASS)
abstract class AbstractBaseCallInterpreterTest {

	static final String CONTIG = "BaseCallInterpreterTest";
	
	private BaseCallInterpreter testInstance;

	public AbstractBaseCallInterpreterTest(final BaseCallInterpreter testInstance) {
		this.testInstance = testInstance;
	}
	
	abstract Stream<Arguments> testGetReadBase();
	abstract Stream<Arguments> testGetRefBase();
	
	@ParameterizedTest(name = "{3}")
	@MethodSource("testGetReadBase")
	void testGetReadBase(SAMRecord samRecord, int readPos, Base expected, String info) {
		final ProcessedRecord record = new ProcessedRecord(samRecord);
		final Base actual = testInstance.getReadBase(record, readPos);
		assertEquals(expected, actual);
	}

	@ParameterizedTest(name = "{3}")
	@MethodSource("testGetRefBase")
	void testGetRefBase(SAMRecord samRecord, Position pos, Base expected, String info) {
		final ProcessedRecord record = new ProcessedRecord(samRecord);
		final Base actual = testInstance.getRefBase(record, pos);
		assertEquals(expected, actual);
	}
	
	
	Arguments createRefArgs(
			final int refStart, final boolean negativeStrand, 
			final String cigarStr,
			final int refPos,
			final Base expected) {
		
		return createRefArgs(refStart, negativeStrand, cigarStr, "", refPos, expected);
	}
	
	Arguments createRefArgs(
			final int refStart, final boolean negativeStrand, 
			final String cigarStr,
			final String readSeq,
			final int refPos,
			final Base expected) {

		final String contig = CONTIG;
		
		final SAMRecord record 	= SAMRecordBuilder.createSERead(
				contig, refStart, negativeStrand, cigarStr, readSeq);
		final String coord		= contig + ":" + 
				Integer.toString(refStart) + ":" + 
				(negativeStrand ? "-" : "+");
		final String simReadSeq	= StringUtil.bytesToString(record.getReadBases());
		final int readPos		= record.getReadPositionAtReferencePosition(refPos) - 1;
		final Base readBase		= Base.valueOf(record.getReadBases()[readPos]);
		final char refBase		= ReferenceSequence.getReferenceSequence(contig).charAt(refPos - 1);
		
		return Arguments.of(
				record,
				new UnmodifiablePosition(refPos, readPos, -1, new ProcessedRecord(record)),
				expected,
				String.format("Read: %s %s %s; readPos: %d %s; refPos: %d %c", 
						coord, simReadSeq, cigarStr, readPos, readBase, refPos, refBase) );
	}
	

	Arguments createReadArgs(
			final int refStart, final boolean negativeStrand, 
			final String cigarStr,
			final int readPos,
			final Base expected) {
		
		return createReadArgs(refStart, negativeStrand, cigarStr, "", readPos, expected);
	}
	
	Arguments createReadArgs(
			final int refStart, final boolean negativeStrand, 
			final String cigarStr,
			final String readSeq,
			final int readPos,
			final Base expected) {

		final String contig = CONTIG;
		
		final SAMRecord record 	= SAMRecordBuilder.createSERead(
				contig, refStart, negativeStrand, cigarStr, readSeq);
		final String coord		= contig + ":" + 
				Integer.toString(refStart) + ":" + 
				(negativeStrand ? "-" : "+");
		final String simReadSeq	= StringUtil.bytesToString(record.getReadBases());
		final Base readBase		= Base.valueOf(record.getReadBases()[readPos]);
		
		final int refPos		= record.getReferencePositionAtReadPosition(readPos + 1);
		final char refBase		= ReferenceSequence.getReferenceSequence(contig).charAt(refPos - 1);
		
		return Arguments.of(
				record,
				readPos,
				expected,
				String.format("Read: %s %s %s; readPos: %d %s; refPos: %d %c", 
						coord, simReadSeq, cigarStr, readPos + 1, readBase, refPos, refBase) );
	}
	
}
