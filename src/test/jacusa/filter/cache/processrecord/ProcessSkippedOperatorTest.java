package test.jacusa.filter.cache.processrecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import htsjdk.samtools.util.StringUtil;
import jacusa.filter.cache.processrecord.ProcessRecord;
import jacusa.filter.cache.processrecord.ProcessSkippedOperator;
import lib.data.cache.region.RegionDataCache;
import lib.data.has.LibraryType;
import test.utlis.ReferenceSequence;

/**
 * Tests @see jacusa.filter.cache.processrecord.ProcessSkippedOperator
 */
class ProcessSkippedOperatorTest extends AbstractProcessRecordTest {
	
	@Override
	List<ProcessRecord> createTestInstances(int distance, RegionDataCache regionDataCache) {
		return Arrays.asList(new ProcessSkippedOperator(distance, regionDataCache));
	}

	// ACGAACGT
	// 12345678
	// --**^^**
	@Override
	Stream<Arguments> testAddRecordWrapper() {
		final List<LibraryType> libraryTypes = 
				Arrays.asList(LibraryType.UNSTRANDED, LibraryType.RF_FIRSTSTRAND, LibraryType.FR_SECONDSTRAND);
		
		final List<Arguments> arguments = new ArrayList<Arguments>();
		
		final int refSeqLength 	= ReferenceSequence.getReferenceSequence(CONTIG).length();
		final int refPosStart 	= 3;
		
		for (final int activeWindowSize : IntStream.range(8, 9).toArray()) {
			for (final LibraryType libraryType : libraryTypes) { 
				for (final boolean negativeStrand : Arrays.asList(true, false)) {
					
					/*
					arguments.add(createArguments(
							activeWindowSize, libraryType, 
							2, 
							refPosStart, negativeStrand, "4M", "", 
							tokern(activeWindowSize, StringUtil.repeatCharNTimes('*', refSeqLength)), 
							new StringBuilder().append("Auto, ")) );
					 */

					arguments.add(createArguments(
							activeWindowSize, libraryType, 
							2, 
							refPosStart, negativeStrand, "2M2N2M", "", 
							tokern(activeWindowSize, "**GA**GT"), 
							new StringBuilder().append("Auto, ")) );
					
					/*
					arguments.add(createArguments(
							activeWindowSize, libraryType, 
							3, 
							refPosStart, negativeStrand, "2M2N2M", "", 
							tokern(activeWindowSize, "***A**G*"), 
							new StringBuilder().append("Auto, ")) );
							*/
				}
			}
		}
		
		return arguments.stream();
	}
	
	List<String> tokern(final int activeWindowSize, final String expected) {
		final List<String> token = new ArrayList<String>();
		
		String tmp = expected;
		while (tmp.length() > 0) {
			final int length = Math.min(activeWindowSize, tmp.length());
			token.add(tmp.substring(0, length));
			if (length == tmp.length()) {
				tmp = "";
			} else {
				tmp = tmp.substring(length);
			}
		}
		
		return token;
	}
	
}
