package test.jacusa.filter.cache.processrecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import htsjdk.samtools.util.StringUtil;
import jacusa.filter.cache.processrecord.ProcessDeletionOperator;
import jacusa.filter.cache.processrecord.ProcessRecord;
import lib.data.cache.region.RegionDataCache;
import lib.data.has.LibraryType;
import test.utlis.ReferenceSequence;

/**
 * Tests test.jacusa.filter.cache.processrecord.ProcessDeletionOperator
 */
class ProcessDeletionOperatorTest extends AbstractProcessRecordTest {

	@Override
	List<ProcessRecord> createTestInstances(int distance, RegionDataCache regionDataCache) {
		return Arrays.asList(new ProcessDeletionOperator(distance, regionDataCache));
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
		final int refSeqLength 			= ReferenceSequence.getReferenceSequence(CONTIG).length();
		
		for (final int activeWindowSize : IntStream.range(8, 9).toArray()) {
			for (final LibraryType libraryType : libraryTypes) { 
				for (final boolean negativeStrand : Arrays.asList(true, false)) {
					
					arguments.add(createArguments(
							activeWindowSize, libraryType, 
							2, 
							2, negativeStrand, "4M", "", 
							tokern(activeWindowSize, StringUtil.repeatCharNTimes('*', refSeqLength)), 
							new StringBuilder().append("Auto, ")) );
					
					// ACGAACGT
					// 12345678
					// *CG**CG*
					arguments.add(createArguments(
							activeWindowSize, libraryType, 
							1, 
							2, false, "2M2D2M", "", 
							Arrays.asList("**G**C**"), 
							new StringBuilder()) );
				}
			}
		}
		
		return arguments.stream();
		// TODO more tests
					/*
					createArguments(
							8, LibraryType.UNSTRANDED, 
							1, 
							2, false, "5M10N2M2D10M", "", 
							Arrays.asList(StringUtil.repeatCharNTimes('*', 8)), 
							new StringBuilder()),
					*/
				
				
				/*
				createArguments(5, addFrag(10, true, "5M2I2M2D10M"), Arrays.asList(8, 10), Arrays.asList(2, 5)), // TODO
				
				createArguments(2, addFrag(10, true, "10M2D10M"), Arrays.asList(9, 11), Arrays.asList(2, 2)),
				createArguments(5, addFrag(10, true, "10M2D10M"), Arrays.asList(6, 11), Arrays.asList(5, 5)),
				createArguments(5, addFrag(10, true, "10S10M2D10M"), Arrays.asList(16, 21), Arrays.asList(5, 5)),
				createArguments(5, addFrag(10, true, "10H10M2D10M"), Arrays.asList(6, 11), Arrays.asList(5, 5)),
				createArguments(5, addFrag(10, false, "10M2D10M"), Arrays.asList(6, 11), Arrays.asList(5, 5)),
				createArguments(5, addFrag(10, true, "1M2D10M"), Arrays.asList(1, 2), Arrays.asList(1, 5)),
				*/
				

	}
	
}
