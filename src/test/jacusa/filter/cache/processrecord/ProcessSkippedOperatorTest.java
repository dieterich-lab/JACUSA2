package test.jacusa.filter.cache.processrecord;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import jacusa.filter.cache.processrecord.AbstractProcessRecord;
import jacusa.filter.cache.processrecord.ProcessSkippedOperator;

class ProcessSkippedOperatorTest extends AbstractProcessRecordTest {

	@Override
	protected AbstractProcessRecord createTestInstance(int distance, InspectRegioDataCache regionDataCache) {
		return new ProcessSkippedOperator(distance, regionDataCache);
	}

	@Override
	public Stream<Arguments> testProcessRecord() {
		return Stream.of(
				// TODO add more test
				createArguments(5, addFrag(10, true, "20M"), Arrays.asList(), Arrays.asList()),
				createArguments(2, addFrag(10, true, "10M100N10M"), Arrays.asList(9, 11), Arrays.asList(2, 2)) );

	}
	
}
