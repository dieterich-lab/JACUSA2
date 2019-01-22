package lib.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import htsjdk.samtools.util.SequenceUtil;
import lib.data.count.basecall.BaseCallCount;
import lib.data.has.HasCoordinate;
import lib.data.has.HasLibraryType;
import lib.data.has.LibraryType;
import lib.util.Base;
import lib.util.Copyable;
import lib.util.coordinate.Coordinate;

public class ParallelData implements HasCoordinate, HasLibraryType, Copyable<ParallelData>, Serializable {

	private static final long serialVersionUID = 1L;

	private final List<List<DataTypeContainer>> data;
	private List<DataTypeContainer> cachedCombinedData;

	private List<DataTypeContainer> cachedPooledData;
	private DataTypeContainer cachedCombinedPooledData;

	private Coordinate cachedCommonCoordinates;
	private LibraryType cachedCommonLibraryType;

	private final List<Integer> replicates;
	private final int totalReplicates;

	private ParallelData(final Builder parallelDataBuilder) {
		data = parallelDataBuilder.data;
		replicates = parallelDataBuilder.replicates;
		totalReplicates = parallelDataBuilder.totalReplicates;
	}

	public int getReplicates(int conditionIndex) {
		return data.get(conditionIndex).size();
	}

	public List<Integer> getReplicates() {
		return replicates;
	}

	public int getTotalReplicates() {
		return totalReplicates;
	}

	public List<DataTypeContainer> getPooledData() {
		if (cachedPooledData == null) {
			for (int conditionIndex = 0; conditionIndex < getConditions(); ++conditionIndex) {
				getPooledData(conditionIndex);
			}
		}
		return Collections.unmodifiableList(cachedPooledData);
	}

	public DataTypeContainer getPooledData(int conditionIndex) {
		if (cachedPooledData == null) {
			cachedPooledData = new ArrayList<>(getConditions());
			cachedPooledData.addAll(Collections.nCopies(getConditions(), null));
		}

		if (cachedPooledData.get(conditionIndex) == null && getReplicates(conditionIndex) > 0) {

			// coordinates and library type
			// should be all the same for all replicates from one condition
			cachedPooledData.set(conditionIndex, merge(getData(conditionIndex)));
		}

		return cachedPooledData.get(conditionIndex);
	}

	public DataTypeContainer getCombinedPooledData() {
		if (cachedCombinedPooledData == null && getConditions() > 0) {
			cachedCombinedPooledData = merge(getPooledData());
		}

		return cachedCombinedPooledData;
	}

	public List<DataTypeContainer> getCombinedData() {
		if (cachedCombinedData == null) {
			cachedCombinedData = new ArrayList<>(getTotalReplicates());

			for (final List<DataTypeContainer> replicateData : data) {
				cachedCombinedData.addAll(replicateData);
			}
		}

		return Collections.unmodifiableList(cachedCombinedData);
	}

	@Override
	public Coordinate getCoordinate() {
		if (cachedCommonCoordinates == null) {
			cachedCommonCoordinates = getCommonCoordinate(getCombinedData());
		}
		return cachedCommonCoordinates;
	}

	@Override
	public LibraryType getLibraryType() {
		if (cachedCommonLibraryType == null) {
			cachedCommonLibraryType = getCommonLibraryType(getCombinedData());
		}
		return cachedCommonLibraryType;
	}

	public DataTypeContainer getDataContainer(int conditionIndex, int replicateIndex) {
		return data.get(conditionIndex).get(replicateIndex);
	}

	public int getConditions() {
		return data.size();
	}

	public List<DataTypeContainer> getData(int conditionIndex) {
		return data.get(conditionIndex);
	}

	@Override
	public ParallelData copy() {
		return new Builder(this).build();
	}

	public DataTypeContainer merge(final List<DataTypeContainer> dataList) {
		final DataTypeContainer copy = dataList.get(0).copy();
		for (int i = 1; i < dataList.size(); ++i) {
			copy.merge(dataList.get(i));
		}
		return copy;
	}

	public static class Builder implements lib.util.Builder<ParallelData> {

		private final List<List<DataTypeContainer>> data;
		private final List<Integer> replicates;
		private final int totalReplicates;

		public Builder(final ParallelData parallelData) {
			this(parallelData.getConditions(), parallelData.getReplicates());
			final int conditions = parallelData.getConditions();
			final List<Integer> replicates = parallelData.getReplicates();

			for (int conditionIndex = 0; conditionIndex < conditions; ++conditionIndex) {
				for (int replicateIndex = 0; replicateIndex < replicates.get(replicateIndex); ++replicateIndex) {
					DataTypeContainer replicate = parallelData.getDataContainer(conditionIndex, replicateIndex).copy();
					withReplicate(conditionIndex, replicateIndex, replicate);
				}
			}
		}

		public Builder(final int conditions, final List<Integer> replicates) {
			data = createEmptyContainer(conditions, replicates);
			this.replicates = new ArrayList<>(replicates);
			totalReplicates = replicates.stream().mapToInt(i -> i).sum();
		}

		public Builder withReplicate(final int conditionIndex, final int replicateIndex,
				final DataTypeContainer dataContainer) {
			data.get(conditionIndex).set(replicateIndex, dataContainer);
			return this;
		}

		public ParallelData build() {
			if (data == null) {
				throw new IllegalStateException("data cannot be null");
			}
			for (int conditionIndex = 0; conditionIndex < data.size(); ++conditionIndex) {
				final List<DataTypeContainer> replicateData = data.get(conditionIndex);
				if (replicateData == null) {
					throw new IllegalStateException(
							"replicateData for conditionIndex: " + conditionIndex + " cannot be null");
				}
				for (int replicateIndex = 0; replicateIndex < replicateData.size(); ++replicateIndex) {
					if (replicateData.get(replicateIndex) == null) {
						throw new IllegalStateException("replicate for conditionIndex: " + conditionIndex
								+ " and replicateIndex: " + replicateIndex + " cannot be null");
					}
				}
			}
			return new ParallelData(this);
		}

		/*
		 * Static methods
		 */

		public static List<DataTypeContainer> createEmptyContainer(final int n) {
			return new ArrayList<>(Collections.nCopies(n, null));
		}

		public static List<List<DataTypeContainer>> createEmptyContainer(final int conditions,
				List<Integer> replicates) {
			if (conditions != replicates.size()) {
				throw new IllegalStateException("conditions != replicates.size()");
			}
			final List<List<DataTypeContainer>> l = new ArrayList<>(conditions);
			for (int conditionIndex = 0; conditionIndex < conditions; ++conditionIndex) {
				l.add(createEmptyContainer(replicates.get(conditionIndex)));
			}
			return l;
		}

	}

	// this will be according to STRAND
	public static Set<Base> getNonReferenceBases(final Coordinate coordinate, final LibraryType libraryType,
			Base referenceBase) {

		if (!SequenceUtil.isValidBase(referenceBase.getByte())) {
			return new HashSet<Base>(0);
		}

		switch (coordinate.getStrand()) {
		case REVERSE:
			referenceBase = referenceBase.getComplement();
			break;

		default:
			break;
		}

		return Base.getNonRefBases(referenceBase);
	}

	public static Set<Base> getVariantBases(final Set<Base> observedBases, final List<BaseCallCount> bccs) {

		if (bccs.size() == 1) {
			throw new IllegalArgumentException();
		}

		final Set<Base> variantBases = new HashSet<Base>(observedBases.size());
		for (final Base base : observedBases) {
			int n = 0;
			for (final BaseCallCount bcc : bccs) {
				if (bcc.getBaseCall(base) > 0) {
					n++;
				}
			}
			if (n < bccs.size()) {
				variantBases.add(base);
			}
		}

		return variantBases;
	}

	@Override
	public String toString() {
		return String.format("conditions: %s", getConditions());
	}

	// for RRDs RNA RNA differences
	/*
	 * @depracted public static <S extends AbstractData & hasBaseCallCount &
	 * hasReferenceBase> int[] getNonRefBaseIndexs(final ParallelData<S>
	 * parallelData) { final int conditions = parallelData.getConditions(); final
	 * byte referenceBase = parallelData.getCombinedPooledData().getReferenceBase();
	 * if (referenceBase == 'N') { throw new
	 * IllegalStateException("Missing reference information"); }
	 * 
	 * final int[] alleles =
	 * parallelData.getCombinedPooledData().getBaseCallCount().getAlleles();
	 * 
	 * int[] observedAlleleCount = new int[BaseCallConfig.BASES.length]; for (int
	 * conditionIndex = 0; conditionIndex < conditions; conditionIndex++) { for (int
	 * baseIndex :
	 * parallelData.getPooledData(conditionIndex).getBaseCallCount().getAlleles()) {
	 * observedAlleleCount[baseIndex]++; } }
	 * 
	 * // A | G // define all non-reference base as potential variants if
	 * (alleles.length == 2 && observedAlleleCount[alleles[0]] +
	 * observedAlleleCount[alleles[1]] == conditions) { // define non-reference base
	 * as potential variants if (referenceBase == 'N') { return new int[0]; }
	 * 
	 * final int referenceBaseIndex =
	 * BaseCallConfig.getInstance().getBaseIndex((byte)referenceBase); for (final
	 * int baseIndex : alleles) { if (baseIndex != referenceBaseIndex) { return new
	 * int[] {baseIndex}; } } }
	 * 
	 * // A | AG if (alleles.length == 2 && observedAlleleCount[alleles[0]] +
	 * observedAlleleCount[alleles[1]] > conditions) { return
	 * ParallelData.getVariantBaseIndexs(parallelData); }
	 * 
	 * // condition1: AG | AG AND condition2: AGC |AGC // return allelesIs; return
	 * new int[0]; }
	 */

	public static Coordinate getCommonCoordinate(final List<DataTypeContainer> containers) {
		Coordinate commonCoordinate = null;
		for (final DataTypeContainer container : containers) {
			final Coordinate specificCoordinate = container.getCoordinate();
			if (commonCoordinate == null) {
				commonCoordinate = specificCoordinate;
			} else if (!commonCoordinate.equals(specificCoordinate)) {
				throw new IllegalStateException("Replicate data has different coordinates: "
						+ commonCoordinate.toString() + " != " + specificCoordinate.toString());
			}
		}

		return commonCoordinate;
	}

	public static LibraryType getCommonLibraryType(final List<DataTypeContainer> containers) {
		LibraryType commonLibraryType = null;
		for (final DataTypeContainer container : containers) {
			final LibraryType specificLibraryType = container.getLibraryType();
			if (commonLibraryType == null) {
				commonLibraryType = specificLibraryType;
			} else if (commonLibraryType != specificLibraryType) {
				return LibraryType.MIXED;
			}
		}
		return commonLibraryType;
	}
}
