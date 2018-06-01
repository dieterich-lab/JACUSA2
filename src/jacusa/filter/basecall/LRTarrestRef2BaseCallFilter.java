package jacusa.filter.basecall;

import htsjdk.samtools.util.StringUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import jacusa.filter.AbstractFilter;
import jacusa.filter.FilterRatio;
import lib.cli.options.Base;
import lib.data.AbstractData;
import lib.data.BaseCallData;
import lib.data.ParallelData;
import lib.data.basecall.array.ArrayBaseCallCount;
import lib.data.cache.extractor.lrtarrest.RefPos2BaseCallCountExtractor;
import lib.data.count.BaseCallCount;
import lib.data.has.HasReferenceBase;
import lib.data.result.Result;
import lib.util.coordinate.Coordinate;

/**
 * 
 * @param <T>
 */
public class LRTarrestRef2BaseCallFilter<T extends AbstractData & HasReferenceBase> 
extends AbstractFilter<T> {

	public static final char SEP = ',';

	private final RefPos2BaseCallCountExtractor<T> observed;
	private final RefPos2BaseCallCountExtractor<T> filtered;
	
	private final BaseCallCountFilter baseCallCountFilter;
	
	// container for artefacts
	private final Set<Integer> filteredRefPositions;

	public LRTarrestRef2BaseCallFilter(final char c, 
			final RefPos2BaseCallCountExtractor<T> observed,
			final RefPos2BaseCallCountExtractor<T> filtered,
			final int overhang, 
			final FilterRatio filterRatio) {

		super(c, overhang);

		this.observed = observed;
		this.filtered = filtered;
		baseCallCountFilter = new BaseCallCountFilter(filterRatio);
		filteredRefPositions = new HashSet<Integer>(10);
	}

	@Override
	protected boolean filter(final ParallelData<T> parallelData) {
		// clear buffer
		filteredRefPositions.clear();
		
		// merged/combined condition and replicate data
		final T combinedPooled = parallelData.getCombinedPooledData();

		// get all reference positions of base substitutions 
		// that are linked to the same read arrest site
		final Set<Integer> refPositions = 
				new TreeSet<Integer>(observed.getRefPos2BaseCallCountExtractor(combinedPooled).getRefPos());
		
		final boolean[] artefact = new boolean[refPositions.size()];
		// result of method: all linked positions need to be artefacts
		boolean filter = false;
		int refPositionIndex = 0;
		
		for (int refPos : refPositions) {
			final byte refBase = observed.getRefPos2BaseCallCountExtractor(combinedPooled).getRefBase(refPos);
			
			// create base call counts
			final BaseCallCount[][] observedBaseCallCount = createBaseCallCount(refPos, refBase, observed, parallelData);
			final BaseCallCount[][] filteredBaseCallCount = createBaseCallCount(refPos, refBase, filtered, parallelData);
			
			// create new data - to infer variant bases
			final Coordinate coordinate = new Coordinate(parallelData.getCoordinate());
			coordinate.setPosition(refPos);;
			ParallelData<BaseCallData> tmpParallelData = BaseCallFilter.createBaseCallData(
					parallelData.getLibraryType(), coordinate, refBase, observedBaseCallCount);
			final Set<Base> variantBases = ParallelData.getVariantBases(tmpParallelData);
			
			if (baseCallCountFilter.filter(variantBases, observedBaseCallCount, filteredBaseCallCount)) {
				artefact[refPositionIndex] = true;
				// add to buffer
				filteredRefPositions.add(refPos);
				filter = true;
			}

			refPositionIndex++;
		}
		
		return filter;
	}

	private BaseCallCount[][] createBaseCallCount(final int refPos, final byte refBase, 
			final RefPos2BaseCallCountExtractor<T> extractor,
			final ParallelData<T> parallelData) {

		final int conditions = parallelData.getConditions();
		final BaseCallCount[][] baseCallCount = new BaseCallCount[conditions][];
		
		for (int conditionIndex = 0; conditionIndex < conditions; ++conditionIndex) {
			// number of replicates for this condition
			final int replicates = parallelData.getReplicates(conditionIndex);
			baseCallCount[conditionIndex] = new BaseCallCount[replicates];
			for (int replicateIndex = 0; replicateIndex < replicates; replicateIndex++) {
				baseCallCount[conditionIndex][replicateIndex] = new ArrayBaseCallCount();
				// get base call count from linked position
				BaseCallCount tmpBC = extractor.getRefPos2BaseCallCountExtractor(parallelData.getData(conditionIndex, replicateIndex))
						.getBaseCallCount(refPos);
				if (tmpBC != null) {
					// add to new data
					baseCallCount[conditionIndex][replicateIndex].add(tmpBC);
				}
			}
		}
		
		return baseCallCount;
	}

	@Override
	public void addInfo(Result<T> result) {
		final String value = StringUtil.join(Character.toString(SEP), filteredRefPositions);
		// add position of artefact(s) to unique char id
		result.getFilterInfo().add(Character.toString(getC()), value);
	}

}
