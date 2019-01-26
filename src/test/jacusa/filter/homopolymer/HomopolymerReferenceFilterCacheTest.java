package test.jacusa.filter.homopolymer;

import jacusa.filter.homopolymer.AbstractHomopolymerFilterCache;
import jacusa.filter.homopolymer.HomopolymerReferenceFilterCache;
import lib.data.cache.container.SharedCache;


/**
 * Test @see test.jacusa.filter.homopolymer.HomopolymerReferenceFilterCache
 */
class HomopolymerReferenceFilterCacheTest 
extends AbstractHomopolymerFilterCacheTest {

	AbstractHomopolymerFilterCache createTestInstance(
			final int minHomopolymerLength,
			final SharedCache sharedCache) {
		
		return new HomopolymerReferenceFilterCache(
				getC(), 
				getFetcher(), 
				minHomopolymerLength, 
				sharedCache );
	}
	
}
