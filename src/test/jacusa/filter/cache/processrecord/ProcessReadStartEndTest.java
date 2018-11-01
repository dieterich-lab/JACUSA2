package test.jacusa.filter.cache.processrecord;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import jacusa.filter.cache.processrecord.AbstractProcessRecord;
import jacusa.filter.cache.processrecord.ProcessReadStartEnd;

class ProcessReadStartEndTest extends AbstractProcessRecordTest {

	@Override
	protected AbstractProcessRecord createTestInstance(int distance, InspectRegioDataCache regionDataCache) {
		return new ProcessReadStartEnd(distance, regionDataCache);
	}

	@Override
	public Stream<Arguments> testProcessRecord() {
		return Stream.of(
				createArguments(5, addFrag(10, true, "20M"), Arrays.asList(1, 16), Arrays.asList(5, 5)),
				createArguments(2, addFrag(10, true, "20M"), Arrays.asList(1, 19), Arrays.asList(2, 2)) );

				/* FIXME
			createArguments(5, addFrag(10, true, "5M10N2M2I10M"), Arrays.asList(6, 10), Arrays.asList(2, 5)),
			createArguments(5, addFrag(10, true, "5M2D2M2I10M"), Arrays.asList(6, 10), Arrays.asList(2, 5)),
			
			createArguments(5, addFrag(10, true, "20M"), Arrays.asList(), Arrays.asList()),
			createArguments(2, addFrag(10, true, "10M2I10M"), Arrays.asList(9, 13), Arrays.asList(2, 2)),
			createArguments(5, addFrag(10, true, "10M2I10M"), Arrays.asList(6, 13), Arrays.asList(5, 5)),
			createArguments(5, addFrag(10, true, "10S10M2I10M"), Arrays.asList(16, 23), Arrays.asList(5, 5)),
			createArguments(5, addFrag(10, true, "10H10M2I10M"), Arrays.asList(6, 13), Arrays.asList(5, 5)),
			createArguments(5, addFrag(10, false, "10M2I10M"), Arrays.asList(6, 13), Arrays.asList(5, 5)),
			createArguments(5, addFrag(10, true, "1M2I10M"), Arrays.asList(1, 4), Arrays.asList(1, 5)),
			createArguments(5, addFrag(10, true, "10M2I1M"), Arrays.asList(6, 13), Arrays.asList(5, 1)) );
			*/
	}
	
}
