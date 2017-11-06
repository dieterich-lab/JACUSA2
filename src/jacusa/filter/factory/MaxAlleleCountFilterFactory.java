package jacusa.filter.factory;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.BaseQualData;
import jacusa.data.ParallelPileupData;
import jacusa.data.Result;
import jacusa.filter.AbstractFilter;
import jacusa.filter.FilterContainer;
import jacusa.pileup.iterator.WindowedIterator;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class MaxAlleleCountFilterFactory<T extends BaseQualData> 
extends AbstractFilterFactory<T> {

	//
	private static final int MAX_ALLELES = 2;
	//
	private int alleles;
	//
	private AbstractParameters<T> parameters;
	//
	private boolean strict;
	
	public MaxAlleleCountFilterFactory(AbstractParameters<T> parameters) {
		super('M', "Max allowed alleles per parallel pileup. Default: "+ MAX_ALLELES);
		alleles = MAX_ALLELES;
		this.parameters = parameters;
		strict = parameters.collectLowQualityBaseCalls();
	}

	@Override
	public AbstractFilter<T> getFilter() {
		if (strict) {
			return new MaxAlleleStrictFilter(getC());
		}

		return new MaxAlleleFilter(getC());
	}

	@Override
	public void registerFilter(FilterContainer<T> filterContainer) {
		filterContainer.add(getFilter());
	}
	
	@Override
	public void processCLI(String line) throws IllegalArgumentException {
		if (line.length() == 1) {
			return;
		}

		final String[] s = line.split(Character.toString(AbstractFilterFactory.SEP));

		for (int i = 1; i < s.length; ++i) {
			switch(i) {
			case 1:
				final int alleleCount = Integer.valueOf(s[i]);
				if (alleleCount < 0) {
					throw new IllegalArgumentException("Invalid allele count " + line);
				}
				this.alleles = alleleCount;
				break;
		
			case 2:
				if (! s[i].equals("strict")) {
					throw new IllegalArgumentException("Did you mean strict? " + line);
				}
				parameters.collectLowQualityBaseCalls(true);
				strict = true;
				break;
			default:
				throw new IllegalArgumentException("Invalid argument: " + line);
			}
		}
	}
	
	private class MaxAlleleFilter extends AbstractFilter<T> {
		public MaxAlleleFilter(final char c) {
			super(c);
		}
		
		@Override
		public boolean filter(final Result<T> result, final WindowedIterator<T> windowIterator) {
			final ParallelPileupData<T> parallelData = result.getParellelData();
			return parallelData.getCombinedPooledData()
					.getBaseQualCount().getAlleles().length > alleles;
		}
		
		@Override
		public int getOverhang() { 
			return 0;
		}

	}
	
	private class MaxAlleleStrictFilter extends AbstractFilter<T> {
		
		public MaxAlleleStrictFilter(final char c) {
			super(c);
		}
		
		@Override
		public boolean filter(final Result<T> result, final WindowedIterator<T> windowIterator) {
			return windowIterator.getConditionContainer().getAlleleCount(result.getParellelData().getCoordinate()) > alleles;
		}
		
		@Override
		public int getOverhang() { 
			return 0;
		}
		
	}
	
}
