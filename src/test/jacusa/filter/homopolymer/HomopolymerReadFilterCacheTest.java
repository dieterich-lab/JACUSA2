package test.jacusa.filter.homopolymer;

import jacusa.filter.homopolymer.AbstractHomopolymerFilterCache;
import jacusa.filter.homopolymer.HomopolymerReadFilterCache;
import lib.data.cache.container.SharedCache;

/**
 * Test @see test.jacusa.filter.homopolymer.HomopolymerReadFilterCache
 */
class HomopolymerReadFilterCacheTest 
extends AbstractHomopolymerFilterCacheTest {
	
	AbstractHomopolymerFilterCache createTestInstance(
			final int minHomopolymerLength,
			final SharedCache sharedCache) {
		return new HomopolymerReadFilterCache(
				getC(), 
				getFetcher(), 
				minHomopolymerLength, 
				sharedCache );
	}

	/*
	public int getWindowPosition(final int windowIndex, final Coordinate current) {
		int windowPosition = coordinateController.getCoordinateTranslator().convert2windowPosition(current);
		if (LibraryType.isStranded(libraryType)) {
			windowPosition = 2 * windowPosition + windowIndex * 2 * activeWindowSize;
			if (current.getStrand() == STRAND.REVERSE) {
				windowPosition++;
			}
		} else {
			windowPosition += windowIndex * activeWindowSize;
		}

		return windowPosition;
	}
	*/
	
}
