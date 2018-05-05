package jacusa.filter.factory;

import htsjdk.samtools.util.StringUtil;

import java.util.HashSet;
import java.util.Set;

import jacusa.filter.AbstractFilter;
import jacusa.io.format.BEDlikeWriter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.builder.ConditionContainer;
import lib.data.has.HasLRTarrestCount;
import lib.data.result.Result;
import lib.util.coordinate.CoordinateController;

/**
 * 
 *
 * @param <T>
 */
public class LRTarrestHomozygousFilterFactory<T extends AbstractData & HasLRTarrestCount>
extends AbstractFilterFactory<T> {
	
	// which condition is required to be homozygous
	private int homozygousConditionIndex;
	private final AbstractParameter<T, ?> parameters;
	
	
	public LRTarrestHomozygousFilterFactory(final AbstractParameter<T, ?> parameters) {
		super('H', 
				"Filter non-homozygous pileup/BAM in condition 1 or 2 " +
				"(MUST be set to H:1 or H:2). Default: none");
		homozygousConditionIndex 	= -1;
		this.parameters 			= parameters;
	}

	@Override
	public void processCLI(final String line) throws IllegalArgumentException {
		if (line.length() == 1) {
			throw new IllegalArgumentException("Invalid argument " + line + ". MUST be set to H:1 or H:2)");
		}

		// format of s: 	H:<condition>[:strict]
		// array content:	0:1			  :2
		final String[] s = line.split(Character.toString(AbstractFilterFactory.OPTION_SEP));
		for (int i = 1; i < s.length; ++i) {
			switch(i) {

			case 1: // set homozygous conditionIndex
				final int conditionIndex = Integer.parseInt(s[1]);
				// make sure conditionIndex is within provided conditions
				if (conditionIndex >= 1 && conditionIndex <= parameters.getConditionsSize()) {
					this.homozygousConditionIndex = conditionIndex;
				} else {
					throw new IllegalArgumentException("Invalid argument: " + line);
				}
				break;

			default:
				throw new IllegalArgumentException("Invalid argument: " + line);
			}
		}
	}

	@Override
	public void registerFilter(final CoordinateController coordinateController, final ConditionContainer<T> conditionContainer) {
		// no caches are need
		// parallelData suffices to filter
		conditionContainer.getFilterContainer().addFilter(new HomozygousFilter(getC()));
	}
	
	/**
	 * TODO add comments. 
	 */
	private class HomozygousFilter 
	extends AbstractFilter<T> {

		private final Set<Integer> filteredRefPositions;
		
		public HomozygousFilter(final char c) {
			super(c);
			filteredRefPositions 		= new HashSet<Integer>(5);
		}

		@Override
		public boolean filter(final ParallelData<T> parallelData) {
			final T data = parallelData.getCombinedPooledData();

			if (data.getLRTarrestCount().getRefPos2bc4arrest().size() > 0) {
				filteredRefPositions.clear();
			}
			boolean filter = false;
			for (final int refPos : data.getLRTarrestCount().getRefPos2bc4arrest().keySet()) {
				if (parallelData.getPooledData(homozygousConditionIndex - 1).getLRTarrestCount().getRefPos2bc4arrest().containsKey(refPos) &&
						parallelData.getPooledData(homozygousConditionIndex - 1).getLRTarrestCount().getRefPos2bc4arrest().get(refPos).getAlleles().length > 1) {

					filter = true;
					filteredRefPositions.add(refPos);
				}
			}
			return filter;
		}

		@Override
		public void addInfo(Result<T> result) {
			final String value = StringUtil.join(Character.toString(BEDlikeWriter.SEP2), filteredRefPositions);
			// add position of artefact(s) to unique char id
			result.getFilterInfo().add(Character.toString(getC()), value);
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
