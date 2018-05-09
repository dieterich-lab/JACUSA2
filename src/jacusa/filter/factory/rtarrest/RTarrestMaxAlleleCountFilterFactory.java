package jacusa.filter.factory.rtarrest;

import java.util.HashSet;
import java.util.Set;

import jacusa.filter.AbstractFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.io.format.BEDlikeWriter;
import jacusa.method.rtarrest.RTArrestFactory.RT_READS;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.builder.ConditionContainer;
import lib.data.count.BaseCallCount;
import lib.data.has.HasArrestBaseCallCount;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasThroughBaseCallCount;
import lib.util.coordinate.CoordinateController;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class RTarrestMaxAlleleCountFilterFactory<T extends AbstractData & HasBaseCallCount & HasArrestBaseCallCount & HasThroughBaseCallCount> 
extends AbstractFilterFactory<T> {

	// default value for max alleles
	private static final int MAX_ALLELES = 2;
	// chosen value
	private int alleles;
	private final Set<RT_READS> apply2reads;

	public RTarrestMaxAlleleCountFilterFactory() {
		super('M', 
				"Max allowed alleles per parallel pileup.\n" +
				"Apply filter to read arrest OR read through reads OR both.\n" +
				"(M:<MAX_ALLELS>:[arrest|through|arrest&through])" +
				"Default: "+ MAX_ALLELES + ":arrest");
		alleles 	= MAX_ALLELES;
		apply2reads = new HashSet<RT_READS>(2);
		apply2reads.add(RT_READS.ARREST);
	}

	@Override
	public void registerFilter(final CoordinateController coordinateController, ConditionContainer<T> conditionContainer) {
		conditionContainer.getFilterContainer().addFilter(new MaxAlleleFilter(getC()));
	}

	@Override
	public void processCLI(String line) throws IllegalArgumentException {
		if (line.length() == 1) {
			return;
		}

		// format: M:2
		final String[] s = line.split(Character.toString(AbstractFilterFactory.OPTION_SEP));
		for (int i = 1; i < s.length; ++i) {
			switch(i) {

			case 1:
				final int alleleCount = Integer.valueOf(s[i]);
				if (alleleCount < 0) {
					throw new IllegalArgumentException("Invalid allele count " + line);
				}
				this.alleles = alleleCount;
				break;

			case 2: // choose arrest, through or arrest&through
				final String[] options = s[2].split("&");
				apply2reads.clear();
				for (final String option : options) {
					final RT_READS tmp = RT_READS.valueOf(option);
					if (tmp == null) {
						throw new IllegalArgumentException("Invalid argument: " + line);						
					}
					apply2reads.add(tmp);
				}

				break;				
				
			default:
				throw new IllegalArgumentException("Invalid argument: " + line);
			}
		}
	}
	
	/**
	 * TODO add comments.
	 */
	private class MaxAlleleFilter extends AbstractFilter<T> {
		
		public MaxAlleleFilter(final char c) {
			super(c);
		}
		
		@Override
		public boolean filter(final ParallelData<T> parallelData) {
			BaseCallCount baseCallCount = null;
			if (apply2reads.size() == 2) {
				baseCallCount = parallelData.getCombinedPooledData().getBaseCallCount();
			} else {
				if (apply2reads.contains(RT_READS.ARREST)) {
					baseCallCount = parallelData.getCombinedPooledData().getArrestBaseCallCount();	
				} else if (apply2reads.contains(RT_READS.THROUGH)) {
					baseCallCount = parallelData.getCombinedPooledData().getThroughBaseCallCount();	
				}
			}

			return baseCallCount.getAlleles().size() > alleles;
		}

		@Override
		public int getOverhang() { 
			return 0;
		}

	}

	@Override
	public void addFilteredData(StringBuilder sb, T data) {
		sb.append(BEDlikeWriter.EMPTY);
	}
	
}
