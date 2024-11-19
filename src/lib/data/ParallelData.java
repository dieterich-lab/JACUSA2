package lib.data;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import htsjdk.samtools.util.SequenceUtil;
import lib.data.count.PileupCount;
import lib.data.count.basecall.BaseCallCount;
import lib.data.downsample.SamplePileupCount;
import lib.data.has.HasCoordinate;
import lib.data.has.HasLibraryType;
import lib.util.Base;
import lib.util.Copyable;
import lib.util.LibraryType;
import lib.util.coordinate.Coordinate;

public class ParallelData implements HasCoordinate, HasLibraryType, Copyable<ParallelData>, Serializable {

	private static final long serialVersionUID = 1L;

	private final List<List<DataContainer>> data;
	private List<DataContainer> cachedCombinedData;

	private List<DataContainer> cachedPooledData;
	private DataContainer cachedCombinedPooledData;

	private Coordinate cachedCommonCoordinates;
	private LibraryType cachedCommonLibraryType;

	private final List<Integer> replicates;
	private final int totalReplicates;

	private ParallelData(final Builder parallelDataBuilder) {
		data 			= parallelDataBuilder.data;
		replicates 		= parallelDataBuilder.replicates;
		totalReplicates = parallelDataBuilder.totalReplicates;
	}

	public int getReplicates(int condI) {
		return data.get(condI).size();
	}

	public List<Integer> getReplicates() {
		return replicates;
	}

	public int getTotalReplicates() {
		return totalReplicates;
	}

	public List<DataContainer> getPooledData() {
		if (cachedPooledData == null) {
			for (int condI = 0; condI < getConditions(); ++condI) {
				getPooledData(condI);
			}
		}
		return Collections.unmodifiableList(cachedPooledData);
	}

	public DataContainer getPooledData(int condI) {
		if (cachedPooledData == null) {
			cachedPooledData = new ArrayList<>(getConditions());
			cachedPooledData.addAll(Collections.nCopies(getConditions(), null));
		}

		if (cachedPooledData.get(condI) == null && getReplicates(condI) > 0) {

			// coordinates and library type
			// should be all the same for all replicates from one condition
			cachedPooledData.set(condI, merge(getData(condI)));
		}

		return cachedPooledData.get(condI);
	}

	public DataContainer getCombPooledData() {
		if (cachedCombinedPooledData == null && getConditions() > 0) {
			cachedCombinedPooledData = merge(getPooledData());
		}

		return cachedCombinedPooledData;
	}

	public List<DataContainer> getCombinedData() {
		if (cachedCombinedData == null) {
			cachedCombinedData = new ArrayList<>(getTotalReplicates());

			for (final List<DataContainer> replicateData : data) {
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

	public DataContainer getDataContainer(int condI, int replicateI) {
		return data.get(condI).get(replicateI);
	}

	public int getConditions() {
		return data.size();
	}

	public List<DataContainer> getData(int condI) {
		return data.get(condI);
	}

	@Override
	public ParallelData copy() {
		return new Builder(this).build();
	}

	public DataContainer merge(final List<DataContainer> dataList) {
		final DataContainer copy = dataList.get(0).copy();
		for (int i = 1; i < dataList.size(); ++i) {
			copy.merge(dataList.get(i));
		}
		return copy;
	}

	public static class Builder implements lib.util.Builder<ParallelData> {

		private final List<List<DataContainer>> data;
		private final List<Integer> replicates;
		private final int totalReplicates;

		public Builder(final ParallelData parallelData) {
			this(parallelData.getConditions(), parallelData.getReplicates());
			final int conditions = parallelData.getConditions();
			final List<Integer> tmpReplicates = parallelData.getReplicates();

			for (int condI = 0; condI < conditions; ++condI) {
				for (int replicateI = 0; replicateI < tmpReplicates.get(replicateI); ++replicateI) {
					DataContainer replicate = parallelData.getDataContainer(condI, replicateI).copy();
					withReplicate(condI, replicateI, replicate);
				}
			}
		}

		public Builder(final int conditions, final List<Integer> replicates) {
			data = createEmptyContainer(conditions, replicates);
			this.replicates = new ArrayList<>(replicates);
			totalReplicates = replicates.stream().mapToInt(i -> i).sum();
		}

		public Builder withReplicate(final int condI, final int replicateI,
				final DataContainer dataContainer) {
			data.get(condI).set(replicateI, dataContainer);
			return this;
		}

		public Builder sample(final int condI, final DataContainer data, final int[] coverages) {
			final SamplePileupCount sample = new SamplePileupCount(data.getPileupCount());

			for (int replicateI = 0; replicateI < coverages.length; replicateI++) {
				final int coverage = coverages[replicateI];
				final PileupCount newPileupCount = sample.sample(coverage);
				this.data.get(condI).get(replicateI).getPileupCount().merge(newPileupCount);
			}

			return this;
		}

		public ParallelData build() {
			if (data == null) {
				throw new IllegalStateException("data cannot be null");
			}
			for (int condI = 0; condI < data.size(); ++condI) {
				final List<DataContainer> replicateData = data.get(condI);
				if (replicateData == null) {
					throw new IllegalStateException(
							"replicateData for condI: " + condI + " cannot be null");
				}
				for (int replicateI = 0; replicateI < replicateData.size(); ++replicateI) {
					if (replicateData.get(replicateI) == null) {
						throw new IllegalStateException("replicate for condI: " + condI
								+ " and replicateI: " + replicateI + " cannot be null");
					}
				}
			}
			return new ParallelData(this);
		}

		/*
		 * Static methods
		 */

		public static List<DataContainer> createEmptyContainer(final int n) {
			return new ArrayList<>(Collections.nCopies(n, null));
		}

		public static List<List<DataContainer>> createEmptyContainer(final int conditions,
				List<Integer> replicates) {
			if (conditions != replicates.size()) {
				throw new IllegalStateException("conditions != replicates.size()");
			}
			final List<List<DataContainer>> l = new ArrayList<>(conditions);
			for (int condI = 0; condI < conditions; ++condI) {
				l.add(createEmptyContainer(replicates.get(condI)));
			}
			return l;
		}

	}

	public static Set<Base> getNonReferenceBases(Base referenceBase) {
		if (!SequenceUtil.isValidBase(referenceBase.getByte())) {
			return new HashSet<>(0);
		}
		
		return Base.getNonRefBases(referenceBase);
	}

	public static Set<Base> getVariantBases(final BaseCallCount bcc1, final BaseCallCount bcc2) {
		final Set<Base> alleles1 = new HashSet<>(bcc1.getAlleles());
		final Set<Base> alleles2 = new HashSet<>(bcc2.getAlleles());
		
		final Set<Base> variants = new HashSet<>(4);
		for (final Base base : Base.validValues()) {
			if (alleles1.contains(base) && ! alleles2.contains(base) || 
					alleles2.contains(base) && ! alleles1.contains(base)) {
				variants.add(base);
			}
		}
		return variants;
	}
	
	public static Set<Base> getVariantBases(final Set<Base> observedBases, final List<BaseCallCount> bccs) {

		if (bccs.size() == 1) {
			throw new IllegalArgumentException();
		}

		final Set<Base> variantBases = new HashSet<>(observedBases.size());
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
		return String.format("cond.: %s", getConditions());
	}
	
	public static Coordinate getCommonCoordinate(final List<DataContainer> containers) {
		Coordinate commonCoordinate = null;
		for (final DataContainer container : containers) {
			final Coordinate specificCoordinate = container.getCoordinate();
			if (commonCoordinate == null) {
				commonCoordinate = specificCoordinate;
			} else if (!commonCoordinate.equals(specificCoordinate)) {
				throw new IllegalStateException("Replicate data have different coordinates: "
						+ commonCoordinate.toString() + " != " + specificCoordinate.toString());
			}
		}
		
		return commonCoordinate;
	}
	
	public static LibraryType getCommonLibraryType(final List<DataContainer> containers) {
		LibraryType commonLibraryType = null;
		for (final DataContainer container : containers) {
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
