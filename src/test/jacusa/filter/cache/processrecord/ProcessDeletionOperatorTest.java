package test.jacusa.filter.cache.processrecord;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import jacusa.filter.cache.processrecord.AbstractProcessRecord;
import jacusa.filter.cache.processrecord.ProcessDeletionOperator;

class ProcessDeletionOperatorTest extends AbstractProcessRecordTest {
	
	@Override
	protected AbstractProcessRecord createTestInstance(int distance, InspectRegioDataCache regionDataCache) {
		return new ProcessDeletionOperator(distance, regionDataCache);
	}

	/*
	 * distance
	 * record(s)
	 * map
	 * msg
	 */
	@Override
	public Stream<Arguments> testProcessRecord() {
		return Stream.of(
				createArguments(5, addFrag(10, true, "5M10N2M2D10M"), Arrays.asList(6, 8), Arrays.asList(2, 5)), // TODO
				createArguments(5, addFrag(10, true, "5M2I2M2D10M"), Arrays.asList(8, 10), Arrays.asList(2, 5)), // TODO
				
				createArguments(5, addFrag(10, true, "20M"), Arrays.asList(), Arrays.asList()),
				createArguments(2, addFrag(10, true, "10M2D10M"), Arrays.asList(9, 11), Arrays.asList(2, 2)),
				createArguments(5, addFrag(10, true, "10M2D10M"), Arrays.asList(6, 11), Arrays.asList(5, 5)),
				createArguments(5, addFrag(10, true, "10S10M2D10M"), Arrays.asList(16, 21), Arrays.asList(5, 5)),
				createArguments(5, addFrag(10, true, "10H10M2D10M"), Arrays.asList(6, 11), Arrays.asList(5, 5)),
				createArguments(5, addFrag(10, false, "10M2D10M"), Arrays.asList(6, 11), Arrays.asList(5, 5)),
				createArguments(5, addFrag(10, true, "1M2D10M"), Arrays.asList(1, 2), Arrays.asList(1, 5)),
				createArguments(5, addFrag(10, true, "10M2D1M"), Arrays.asList(6, 11), Arrays.asList(5, 1)) );
	}
	
}
