package lib.data.validator.paralleldata;

import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Fetcher;

public class RTarrestParallelPileup
implements ParallelDataValidator {
	
	private final ExtendedVariantSiteValidator variantSite;
	private final Fetcher<BaseCallCount> arrestBccFetcher;
	private final Fetcher<BaseCallCount> throughBccFetcher;

	public RTarrestParallelPileup(
			final Fetcher<BaseCallCount> totalBccFetcher,
			final Fetcher<BaseCallCount> arrestBccFetcher,
			final Fetcher<BaseCallCount> throughBccFetche) {

		this.variantSite = new ExtendedVariantSiteValidator(totalBccFetcher);
		this.arrestBccFetcher 	= arrestBccFetcher;
		this.throughBccFetcher 	= throughBccFetche;
		
	}

	@Override
	public boolean isValid(final ParallelData parallelData) {
		final DataContainer combinedPooledContainer = parallelData.getCombPooledData();
		return variantSite.isValid(parallelData) || 
				arrestBccFetcher.fetch(combinedPooledContainer).getCoverage() > 0 &&
				throughBccFetcher.fetch(combinedPooledContainer).getCoverage() > 0;
	}

}