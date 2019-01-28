package test.jacusa.filter.cache.processrecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import jacusa.filter.cache.processrecord.ProcessReadStartEnd;
import jacusa.filter.cache.processrecord.ProcessRecord;
import lib.data.cache.region.RegionDataCache;
import lib.data.has.LibraryType;

/**
 * Tests @see jacusa.filter.cache.processrecord.ProcessReadStartEnd
 */
class ProcessReadStartEndTest extends AbstractProcessRecordTest {

	@Override
	List<ProcessRecord> createTestInstances(int distance, RegionDataCache regionDataCache) {
		return Arrays.asList(new ProcessReadStartEnd(distance, regionDataCache));
	}

	// ACGAACGT
	// 12345678
	@Override
	Stream<Arguments> testAddRecordWrapper() {
		final List<LibraryType> libraryTypes = 
				Arrays.asList(
						LibraryType.UNSTRANDED, 
						LibraryType.RF_FIRSTSTRAND, 
						LibraryType.FR_SECONDSTRAND);
		
		final List<Arguments> arguments = new ArrayList<Arguments>();

		for (final int activeWindowSize : IntStream.range(8, 9).toArray()) {
			for (final LibraryType libraryType : libraryTypes) { 
				for (final boolean negativeStrand : Arrays.asList(true, false)) {
					
					// TODO more tests
					arguments.add(createArguments(
							activeWindowSize, libraryType, 
							1, 
							2, negativeStrand, "4M", "", 
							tokern(activeWindowSize, "*C**A***"), 
							new StringBuilder().append("Auto, ")) );
					
				}
			}
		}
		
		return arguments.stream();
	}
	
}
