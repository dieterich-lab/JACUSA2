package test.jacusa.filter.cache.processrecord;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordSetBuilder;
import htsjdk.samtools.TextCigarCodec;
import htsjdk.samtools.Cigar;
import htsjdk.samtools.SAMFileHeader.SortOrder;
import jacusa.filter.cache.processrecord.AbstractProcessRecord;
import lib.data.DataTypeContainer;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.region.RegionDataCache;
import lib.data.count.basecall.ArrayBaseCallCount;
import lib.data.count.basecall.BaseCallCount;
import lib.util.Base;
import lib.util.coordinate.Coordinate;
import test.utlis.TestUtils;

@TestInstance(Lifecycle.PER_CLASS)
public abstract class AbstractProcessRecordTest {

	private int records;
	private SAMRecordSetBuilder recordBuilder;
	private AbstractProcessRecord testInstance;
	private InspectRegioDataCache regionDataCache;

	public AbstractProcessRecordTest() {
		records = 1;
		recordBuilder = new SAMRecordSetBuilder(true, SortOrder.coordinate);
		recordBuilder.setRandomSeed(1);
		recordBuilder.setUseNmFlag(true);
	}
	
	@BeforeEach
	void beforeEach() {
		regionDataCache = new InspectRegioDataCache();
	}
	
	@AfterEach
	void afterEach() {
		recordBuilder = null;
	}

	@DisplayName("Test processRecord")
	@ParameterizedTest(name = "{3}")
	@MethodSource("testProcessRecord")
	void testProcessRecord(int distance, SAMRecord record, Map<Integer, BaseCallCount> expected, String msg, TestReporter testReporter) {
		testInstance = createTestInstance(distance, regionDataCache);
		SAMRecordWrapper recordWrapper = new SAMRecordWrapper(record);
		testInstance.processRecord(recordWrapper);

		// same reference positions
		TestUtils.equalSets(expected.keySet(), regionDataCache.getRef2BaseCallCount().keySet());
		
		Set<Integer> mergedReferencePositions = new TreeSet<>(expected.keySet());
		mergedReferencePositions.addAll(regionDataCache.getRef2BaseCallCount().keySet());
		// same base call counts

		for (final int referencePosition : mergedReferencePositions) {
			final BaseCallCount expectedBaseCallCount = expected.get(referencePosition);
			final BaseCallCount actualBaseCallCount = regionDataCache.getRef2BaseCallCount().get(referencePosition);

			assertEquals(expectedBaseCallCount, actualBaseCallCount, "Mismatch at reference position: " + referencePosition);
		}
	}

	/*
	 * Abstract
	 */

	protected abstract AbstractProcessRecord createTestInstance(int distance, InspectRegioDataCache regionDataCache);
	public abstract Stream<Arguments> testProcessRecord();

	/*
	 * Helper
	 */

	protected SAMRecordSetBuilder getRecordBuilder() {
		return recordBuilder;
	}

	protected SAMRecord addFrag(final int start, final boolean negativeStrand, final String cigarString) {
		final Cigar cigar = TextCigarCodec.decode(cigarString);
		final int readLength = cigar.getReadLength();
		recordBuilder.setReadLength(readLength);
		SAMRecord record = recordBuilder.addFrag("read" + records, 1, start, negativeStrand, false, cigarString, null, 40, false, false);
		return record;
	}

	protected Arguments createArguments(final int distance, final SAMRecord record,
			List<Integer> readPositions, List<Integer> lengths) {
		return createArguments(distance, record, readPositions, lengths, "");
	}

	protected Arguments createArguments(final int distance, final SAMRecord record, 
			List<Integer> readPositions, List<Integer> lengths, 
			final String tmpMsg) {

		if (readPositions.size() != lengths.size()) {
			throw new IllegalStateException("Size of readPositions != lengths");
		}
		
		final List<Integer> referencePositions = new ArrayList<>(); 
		final List<BaseCallCount> baseCallCounts = new ArrayList<>(); 

		for (int i = 0; i < readPositions.size(); ++i) {
			final int readPosition = readPositions.get(i);
			final int length = lengths.get(i);

			for (int offset = 0; offset < length; ++offset) {
				final int referencePosition = record.getReferencePositionAtReadPosition(readPosition + offset);
				referencePositions.add(referencePosition);
				final BaseCallCount baseCallCount = new ArrayBaseCallCount();
				final Base base = Base.valueOf(record.getReadBases()[readPosition + offset - 1]);
				baseCallCount.increment(base);
				baseCallCounts.add(baseCallCount);
			}
		}
		
		return Arguments.of(distance, record, 
				createExpected(referencePositions, baseCallCounts), 
				tmpMsg + record.getCigarString());
	}
	
	protected Map<Integer, BaseCallCount> createExpected(List<Integer> referencePositions, List<BaseCallCount> baseCallCounts) {
		if (referencePositions.size() != baseCallCounts.size()) {
			throw new IllegalStateException("Size of referencePositions != baseCalls");
		}
		final Map<Integer, BaseCallCount> ref2baseCallCount = new HashMap<>();
		for (int i = 0; i < referencePositions.size(); ++i) {
			ref2baseCallCount.put(referencePositions.get(i), baseCallCounts.get(i));
		}
		return ref2baseCallCount;
	}
	
	protected class InspectRegioDataCache implements RegionDataCache {

		private Map<Integer, BaseCallCount> ref2baseCallCount;

		public InspectRegioDataCache() {
			ref2baseCallCount = new HashMap<>(100);
		}

		@Override
		public void populate(DataTypeContainer container, Coordinate coordinate) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			ref2baseCallCount.clear();
		}

		@Override
		public void addRegion(int referencePosition, int readPosition, int length, SAMRecordWrapper recordWrapper) {
			final SAMRecord record = recordWrapper.getSAMRecord();
			for (int offset = 0; offset < length; ++offset) {
				final Base base = Base.valueOf(record.getReadBases()[readPosition + offset]);
				add(referencePosition + offset, base);
			}			
		}

		private void add(final int referencePosition, final Base base) {
			if (! ref2baseCallCount.containsKey(referencePosition)) {
				ref2baseCallCount.put(referencePosition, new ArrayBaseCallCount());
			}
			final BaseCallCount baseCallCount = ref2baseCallCount.get(referencePosition);
			baseCallCount.increment(base);
		}

		public Map<Integer, BaseCallCount> getRef2BaseCallCount() {
			return ref2baseCallCount;
		}

	}
	
}
