package test.jacusa.filter.cache.processrecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import jacusa.filter.cache.processrecord.ProcessInsertionOperator;
import jacusa.filter.cache.processrecord.ProcessRecord;
import lib.data.cache.region.RegionDataCache;
import lib.data.has.LibraryType;

/**
 * Tests jacusa.filter.cache.processrecord.ProcessInsertionOperator
 */
class ProcessInsertionOperatorTest extends AbstractProcessRecordTest {
	
	@Override
	List<ProcessRecord> createTestInstances(int distance, RegionDataCache regionDataCache) {
		return Arrays.asList(new ProcessInsertionOperator(distance, regionDataCache));
	}

	@Override
	Stream<Arguments> testAddRecordWrapper() {
		final List<LibraryType> libraryTypes = 
				Arrays.asList(
						LibraryType.UNSTRANDED, 
						LibraryType.RF_FIRSTSTRAND, 
						LibraryType.FR_SECONDSTRAND);
		
		final List<Arguments> arguments = new ArrayList<Arguments>();
		
		final int refPosStart 	= 3;
		
		for (final int activeWindowSize : IntStream.range(8, 9).toArray()) {
			for (final LibraryType libraryType : libraryTypes) { 
				for (final boolean negativeStrand : Arrays.asList(true, false)) {
				
					arguments.add(createArguments(
							activeWindowSize, libraryType, 
							2, 
							refPosStart, negativeStrand, "2M2N2M", "", 
							tokern(activeWindowSize, "**GA**GT"), 
							new StringBuilder().append("Auto, ")) );
					
					/* TODO
					createArguments(5, addFrag(10, true, "5M10N2M2I10M"), Arrays.asList(6, 10), Arrays.asList(2, 5)), // TODO
					createArguments(5, addFrag(10, true, "5M2D2M2I10M"), Arrays.asList(6, 10), Arrays.asList(2, 5)), // TODO
					
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
		}
		
		return arguments.stream();
	}

}
