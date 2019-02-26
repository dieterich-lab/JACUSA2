package test.lib.data.storage.readsubstitution;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import htsjdk.samtools.SAMRecord;
import lib.cli.options.filter.has.HasReadSubstitution.BaseSubstitution;
import lib.data.storage.PositionProcessor;
import lib.data.storage.Storage;
import lib.data.storage.basecall.AbstractBaseCallCountStorage;
import lib.data.storage.basecall.DefaultBaseCallCountStorage;
import lib.data.storage.container.SharedStorage;
import lib.data.storage.readsubstitution.BaseCallInterpreter;
import lib.data.storage.readsubstitution.BaseSubstitutionRecordProcessor;
import lib.data.validator.CombinedValidator;
import lib.data.validator.Validator;
import lib.recordextended.SAMRecordExtended;
import lib.util.Base;
import lib.util.LibraryType;
import lib.util.coordinate.OneCoordinate;
import lib.util.position.Position;
import test.utlis.ReferenceSequence;
import test.utlis.SAMRecordBuilder;
import test.utlis.SharedStorageBuilder;

@TestInstance(Lifecycle.PER_CLASS)
class BaseSubstitutionRecordProcessorTest {

	// use this contig from ReferenceSequence to generate reads from
	private static final String CONTIG = "BaseSubstitutionTest";
	
	@ParameterizedTest(name = "{4}")
	@MethodSource("testProcess")
	void testProcess(
			BaseSubstitutionRecordProcessor testInstance,
			List<SAMRecordExtended> records, 			// currently this is only Single End
			Map<BaseSubstitution, Storage> actual,	// reference to internal map of testInstance
			Map<BaseSubstitution, Storage> expected,	// this is what we expect
			String info 								// JUNIT related; gives more informative test message
			) {
		
		testInstance.preProcess();
		// let testInstance process reads
		for (final SAMRecordExtended recordExtended : records) {
			testInstance.process(recordExtended);
		}
		testInstance.postProcess();
		assertEquals(expected.keySet(), actual.keySet());
		
		for (final BaseSubstitution baseSub : expected.keySet()) {
			final AbstractBaseCallCountStorage expectedBccStorage = 
					(AbstractBaseCallCountStorage)expected.get(baseSub);
			final int winSize = expectedBccStorage.getCoordinateController().getActiveWindowSize();
			final AbstractBaseCallCountStorage actualBccStorage = 
					(AbstractBaseCallCountStorage)actual.get(baseSub);
			
			for (int winPos = 0; winPos < winSize; ++winPos) {
				for (final Base base : Base.validValues()) {
					assertEquals(
							expectedBccStorage.getCount(winPos, base), 
							actualBccStorage.getCount(winPos, base),
							String.format("baseSub: %s winPos: <%d> base: <%s>",
									baseSub, winPos, base) );
				}
			}
		}
	}
	
	Stream<Arguments> testProcess() {
		// 012345678901
		// ACGAACGTACGT ref.
		// 123456789012
		final List<Arguments> args = new ArrayList<Arguments>();
		final LibraryType[] libs = 
				new LibraryType[] { 
						LibraryType.UNSTRANDED, 
						LibraryType.RF_FIRSTSTRAND, 
						LibraryType.FR_SECONDSTRAND };
		
		// for stranded libs and negativeStrand base calls need to be inverted 
		// this is done in AbstractBaseCallCountStorage.populate()
		/*for (final LibraryType lib : libs) {
			for (final boolean negativeStrand : new boolean[] { true, false } ) {
				args.add(cSE(
						lib, 
						1, 7,
						1, negativeStrand, "3M", new String(),
						new String[] {} ) );
			}
			args.add(cSE(
					lib, 
					1, 7,
					1, false, "3M", "ATG", // ref:ACG
					new String[] { "C2T,0,A", "C2T,1,T", "C2T,2,G" } ));
		}
		args.add(cSE(
				LibraryType.UNSTRANDED, 
				1, 7,
				1, true, "3M", "ATG", // ref:ACG
				new String[] { "C2T,0,A", "C2T,1,T", "C2T,2,G" } ));*/
		/*args.add(cSE(
				LibraryType.RF_FIRSTSTRAND, 
				1, 7,
				1, true, "3M", "ATG", // ref:ACG
				new String[] { "G2A,0,A", "G2A,1,T", "G2A,2,G" } ));
		args.add(cSE(
				LibraryType.FR_SECONDSTRAND, 
				1, 7,
				1, true, "3M", "ATG", // ref:ACG
				new String[] { "G2A,0,A", "G2A,1,T", "G2A,2,G" } )); */
		// 012345678901
		// ACGAACGTACGT ref.
		// TGCTTGCATGCA reverese.
		// 123456789012
		//     [  ]
		//     >><<
		//     <<  >>
		//  <<   >>
		//  <<  >>
		//      <<  >>
		// ACGAACGTACGT
		// 123456789012
		//      <<
		//       >>
		args.add(cPE(
				LibraryType.UNSTRANDED, 
				5, 8,
				5, true, "2M", "AT", // ref:AC
				9, true, "2M", "AC",
				new HashSet<BaseSubstitution>(Arrays.asList(BaseSubstitution.C2T)),
				new String[] {"C2T,0,A","C2T,1,T" } ));
		args.add(cPE(
				LibraryType.UNSTRANDED, 
				5, 8,
				5, true, "2M", "TC", // ref:AC
				8, true, "2M", "AC", // ref:TA
				new HashSet<BaseSubstitution>(Arrays.asList(BaseSubstitution.A2T)),
				new String[] { "A2T,0,T" ,"A2T,1,C", "A2T,3,A"} ));
		args.add(cPE(
				LibraryType.UNSTRANDED, 
				5, 8,
				4, true, "2M", "AT", // ref:AA
				9, true, "2M", "AC",
				new HashSet<BaseSubstitution>(Arrays.asList(BaseSubstitution.A2T)),
				new String[] {"A2T,0,T" } ));
		args.add(cPE(
				LibraryType.UNSTRANDED, 
				5, 8,
				4, true, "2M", "AT", // ref:AA
				1, true, "2M", "AC",
				new HashSet<BaseSubstitution>(Arrays.asList(BaseSubstitution.A2T)),
				new String[] {"A2T,0,T" } ));
		// ACGAACGTACGT
		// 123456789012
		//     [  ]
		//    AT
		//     AC
		args.add(cPE(
				LibraryType.UNSTRANDED, 
				5, 8,
				4, true, "2M", "AT", // ref:AA
				5, true, "2M", "AC",
				new HashSet<BaseSubstitution>(Arrays.asList(BaseSubstitution.A2T)),
				new String[] {"A2T,0,A", "A2T,0,T", "A2T,1,C" } ));
		return args.stream();
	}

	// creates arguments for the test
	// this is a convenience method to have an empty validator 
	Arguments cSE(
			final LibraryType libraryType,
			final int refWinStart, final int refWinEnd, 
			final int refStart, final boolean negativeStrand, final String cigarStr, final String readSeq,
			final Set<BaseSubstitution> queryBaseSub,
			final String[] expectedStr) {
		
		return cSE(
				libraryType, 
				refWinStart, refWinEnd, 
				refStart, negativeStrand, cigarStr, readSeq, 
				new CombinedValidator(new ArrayList<Validator>()),
				queryBaseSub,
				expectedStr);
	}

	Arguments cPE(
			final LibraryType libraryType,
			final int refWinStart, final int refWinEnd,
			final int refStart, final boolean negativeStrand, final String cigarStr, final String readSeq, 
			final int refStart2, final boolean negativeStrand2, final String cigarStr2, final String readSeq2,
			final Set<BaseSubstitution> queryBaseSub,
			final String[] expectedStr) {
		final Validator validator = new CombinedValidator(new ArrayList<Validator>());
		// size of window  
		final int activeWindowSize = refWinEnd - refWinStart + 1;
		
		// simulate Paired End Reads
		final List<SAMRecordExtended> records = new SAMRecordBuilder()
				.withPERead(CONTIG, refStart, negativeStrand, cigarStr, readSeq, 
						refStart2, negativeStrand2, cigarStr2, readSeq2)
				.getRecords().stream()
				.map(r -> new SAMRecordExtended(r))
				.collect(Collectors.toList());
		// make nice informative message to output along the test 
		final String info = String.format(
				"lib.: %s, read %d-%d:%s cigar: %s, readSeq: %s", 
				libraryType,
					records.get(0).getSAMRecord().getAlignmentStart(), records.get(0).getSAMRecord().getAlignmentEnd(), (negativeStrand ? '-' : '+'),
					cigarStr,
					(readSeq.isEmpty() ? '*' : readSeq) );

		// holds some important data, e.g.: current window, reference info, stuff should be shared
		final SharedStorage sharedStorage 	= new SharedStorageBuilder(
				activeWindowSize, libraryType, CONTIG, ReferenceSequence.get())
				.withActive(new OneCoordinate(CONTIG, refWinStart, refWinEnd))
				.build();

		// create the expected storage
		final Map<BaseSubstitution, Storage> expected 	= parseExpected(expectedStr, sharedStorage);
		if (! expected.keySet().equals(queryBaseSub)) {
			throw new IllegalStateException("Query and expected string do not match!");
		}
		// need to be able to check against expected, this is otherwise buried/private in the testInstance 
		final Map<BaseSubstitution, Storage> actual  	= new HashMap<>(expected.size());
		for (final BaseSubstitution baseSub : queryBaseSub) {
			actual.put(baseSub, new DefaultBaseCallCountStorage(sharedStorage, null));
		}
		
		return Arguments.of(
				createTestInstance(sharedStorage, libraryType, validator, actual),
				records,
				actual,
				expected,
				info);
	}
	
	Arguments cSE(
			final LibraryType libraryType,
			final int refWinStart, final int refWinEnd,
			final int refStart, final boolean negativeStrand, final String cigarStr, final String readSeq, 
			final Validator validator,
			final Set<BaseSubstitution> queryBaseSub,
			final String[] expectedStr) {
		
		// size of window  
		final int activeWindowSize = refWinEnd - refWinStart + 1;
		
		// simulate Single End Read
		final SAMRecord record = SAMRecordBuilder.createSERead(
						CONTIG, refStart, negativeStrand, cigarStr, readSeq);
		// for future use this is a list -> Paired End
		final List<SAMRecordExtended> records = Arrays.asList(new SAMRecordExtended(record));
		// make nice informative message to output along the test 
		final String info = String.format(
				"lib.: %s, read %d-%d:%s cigar: %s, readSeq: %s", 
				libraryType,
					record.getAlignmentStart(), record.getAlignmentEnd(), (negativeStrand ? '-' : '+'),
					cigarStr,
					(readSeq.isEmpty() ? '*' : readSeq) );

		// holds some important data, e.g.: current window, reference info, stuff should be shared
		final SharedStorage sharedStorage 	= new SharedStorageBuilder(
				activeWindowSize, libraryType, CONTIG, ReferenceSequence.get())
				.withActive(new OneCoordinate(CONTIG, refWinStart, refWinEnd))
				.build();

		// create the expected storage
		final Map<BaseSubstitution, Storage> expected 	= parseExpected(expectedStr, sharedStorage);
		// need to be able to check against expected, this is otherwise buried/private in the testInstance 
		
		final Map<BaseSubstitution, Storage> actual  	= new HashMap<>(expected.size());
		for (final BaseSubstitution baseSub : expected.keySet()) {
			actual.put(baseSub, new DefaultBaseCallCountStorage(sharedStorage, null));
		}
		
		return Arguments.of(
				createTestInstance(sharedStorage, libraryType, validator, true, actual),
				records,
				actual,
				expected,
				info);
	}
	
	BaseSubstitutionRecordProcessor createTestInstance(
			final SharedStorage sharedStorage,
			final LibraryType libraryType,
			final Validator validator,
			final boolean stratifyOnlyBcc,
			final Map<BaseSubstitution, Storage> baseSub2storage) {

		final List<PositionProcessor> positionProcessors = new ArrayList<>(2);
		positionProcessors.add(
				new PositionProcessor(
						Arrays.asList(validator), 
						new ArrayList<>(baseSub2storage.values())));
		
		// how to interpret strand and libraryType to infer 
		final BaseCallInterpreter bci = BaseCallInterpreter.build(libraryType);
		return new BaseSubstitutionRecordProcessor(
				sharedStorage,
				bci,
				validator,
				stratifyOnlyBcc,
				baseSub2storage.keySet(),
				positionProcessors);
	}
	
	// ',' separated array of strings of the following form: "x2y,winPos,{A|C|G|T}" 
	// where x,y in {A,C,G,T}
	Map<BaseSubstitution, Storage> parseExpected(final String[] str, final SharedStorage sharedStorage) {
		final Map<BaseSubstitution, Storage> expected = new HashMap<>(str.length);
		for (final String tmpStr : str) {
			final String[] cols 			= tmpStr.split(",");
			final BaseSubstitution baseSub 	= BaseSubstitution.valueOf(cols[0]);
			final int winPos 				= Integer.parseInt(cols[1]);
			final Base base					= Base.valueOf(cols[2]);
			if (! expected.containsKey(baseSub)) {
				expected.put(baseSub, new DefaultBaseCallCountStorage(sharedStorage, null));
			}
			final Storage tmp = expected.get(baseSub);
			final Position toyPos = new ToyPosition(winPos, base);
			tmp.increment(toyPos);
		}
		return expected;
	}
	
	private class ToyPosition implements Position {

		private final int winPos;
		private final Base base;
		
		public ToyPosition(final int winPos, final Base base) {
			this.winPos = winPos;
			this.base 	= base;
		}
		
		@Override
		public Position copy() {
			return new ToyPosition(winPos, base);
		}
		
		@Override
		public int getReadPosition() {
			return -1;
		}
		
		@Override
		public SAMRecordExtended getRecordExtended() {
			return null;
		}
		
		@Override
		public int getReferencePosition() {
			return -1;
		}
		
		@Override
		public int getWindowPosition() {
			return winPos;
		}
		
		@Override
		public boolean isValidReferencePosition() {
			return true;
		}
		
		@Override
		public Base getReadBaseCall() {
			return base;
		}
		
		@Override
		public SAMRecord getRecord() {
			return null;
		}
		
	}
	
}
