package lib.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import htsjdk.samtools.util.SequenceUtil;
import lib.cli.options.Base;
import lib.data.generator.DataGenerator;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasCoordinate;
import lib.data.has.HasLibraryType;
import lib.data.has.HasPileupCount;
import lib.data.has.HasReferenceBase;
import lib.util.coordinate.Coordinate;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class ParallelData<T extends AbstractData> 
implements HasCoordinate, HasLibraryType {

	private final int REPLICATE_INDEX = 0;
	
	private DataGenerator<T> dataGenerator;

	private T[][] data;
	private T[] cachedCombinedData;
	
	private T[] cachedPooledData;
	private T cachedCombinedPooledData;

	private int cachedTotalReplicates;
	
	public ParallelData(final DataGenerator<T> dataGenerator) {
		this.dataGenerator = dataGenerator;
		reset();
	}

	public ParallelData(final DataGenerator<T> dataGenerator, final T[][] data) {
		this.dataGenerator 	= dataGenerator;
		
		int conditions 		= data.length;

		this.data = data;
		cachedTotalReplicates = 0;
		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			cachedTotalReplicates += data[conditionIndex].length;
		}
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param parallelData
	 */
	public ParallelData(final ParallelData<T> parallelData) {
		dataGenerator = parallelData.dataGenerator;

		// copy data
		data = dataGenerator.copyContainerData(parallelData.data);
		if (parallelData.cachedCombinedData != null) {
			cachedCombinedData = dataGenerator.copyReplicateData(parallelData.cachedCombinedData);
		}
		cachedTotalReplicates = parallelData.cachedTotalReplicates;
	}
	
	public void setData(T[][] data) {
		this.data = data;
		resetCache();
	}

	// make this faster remove data and add new
	public void setData(int conditionIndex, T[] data) {
		this.data[conditionIndex] = data;

		if (cachedCombinedData != null) {
			cachedCombinedData[conditionIndex] = null;
		}

		cachedCombinedPooledData = null;
		
		if (cachedPooledData != null) {
			cachedPooledData[conditionIndex] = null;
		}
		
		cachedTotalReplicates = -1;
	}

	public void reset() {
		if (data != null) {
			data = dataGenerator.createContainerData(data.length);
		}
		resetCache();
	}
	
	protected void resetCache() {
		cachedCombinedData 			= null;
		cachedCombinedPooledData 	= null;
		cachedPooledData 			= null;
		cachedTotalReplicates		= -1;
	}

	public int getReplicates(int conditionIndex) {
		return data[conditionIndex].length;
	}

	public int getTotalReplicates() {
		if (cachedTotalReplicates == -1) {
			cachedTotalReplicates = 0;
			for (int conditionIndex = 0; conditionIndex < getConditions(); conditionIndex++) {
				cachedTotalReplicates += data[conditionIndex].length;
			}
		}

		return cachedTotalReplicates;
	}

	public boolean isValid() {
		for (int conditionIndex = 0; conditionIndex < getConditions(); conditionIndex++) {
			if (data[conditionIndex].length <= 0) {
				return false;
			}
		}

		return true;
	}

	public T getPooledData(int conditionIndex) {
		if (cachedPooledData == null) {
			cachedPooledData = dataGenerator.createReplicateData(getConditions());
		}
		
		if (cachedPooledData[conditionIndex] == null && 
				getReplicates(conditionIndex) > 0) {
			
			// use first replicate to infer coordinates and library type
			// should be all the same for all replicates from one condition
			final T data = getData(conditionIndex, REPLICATE_INDEX);
			final LIBRARY_TYPE libraryType = data.getLibraryType();
			final Coordinate coordinate = data.getCoordinate();

			final T tmpData = dataGenerator.createData(libraryType, new Coordinate(coordinate));

			for (int replicateIndex = 0; replicateIndex < getReplicates(conditionIndex); replicateIndex++) {
				getDataGenerator().merge(tmpData, getData(conditionIndex, replicateIndex));
			}
			cachedPooledData[conditionIndex] = tmpData;
		}

		return cachedPooledData[conditionIndex];
	}

	public T getCombinedPooledData() {
		if (cachedCombinedPooledData == null && getPooledData(0) != null) {

			cachedCombinedPooledData = dataGenerator.createData(getCommonLibraryType(data), getCommonCoordinate(data));
			for (int conditionIndex = 0; conditionIndex < getConditions(); conditionIndex++) {
				getDataGenerator().merge(cachedCombinedPooledData, getPooledData(conditionIndex));
			}
		}

		return cachedCombinedPooledData;
	}
	
	public T[] getCombinedData() {
		if (cachedCombinedData == null) {
			cachedCombinedData = dataGenerator.createReplicateData(getTotalReplicates());

			int dest = 0;;
			for (int conditionIndex = 0; conditionIndex < getConditions(); conditionIndex++) {
				System.arraycopy(
						data[conditionIndex], 
						0, 
						cachedCombinedData, 
						dest, 
						getReplicates(conditionIndex));
				dest += getReplicates(conditionIndex);
			}
		}
		
		return cachedCombinedData;
	}

	public DataGenerator<T> getDataGenerator() {
		return dataGenerator;
	}
	
	@Override
	public Coordinate getCoordinate() {
		return getCommonCoordinate(data);
	}

	@Override
	public LIBRARY_TYPE getLibraryType() {
		return getCommonLibraryType(data);
	}
	
	public T getData(int conditionIndex, int replicateIndex) {
		return data[conditionIndex][replicateIndex];
	}

	public int getConditions() {
		return data.length;
	}

	public T[] getData(int conditionIndex) {
		return data[conditionIndex];
	}

	public ParallelData<T> copy() {
		return new ParallelData<T>(this);
	}

	// this will be according to STRAND
	public static <S extends AbstractData & HasReferenceBase & HasBaseCallCount> Set<Base> getNonReferenceBaseIndexs(ParallelData<S> parallelData) {
		Base referenceBase = Base.valueOf(parallelData.getCombinedPooledData().getReferenceBase());
		if (SequenceUtil.isValidBase(referenceBase.getC())) {
			return new HashSet<Base>(0);
		}

		switch (parallelData.getCoordinate().getStrand()) {
		case REVERSE:
			referenceBase = referenceBase.getComplement();
			break;

		default:
			break;
		}

		return Base.getNonRefBases(referenceBase);
	}

	public static <S extends AbstractData & HasReferenceBase & HasBaseCallCount> Set<Base> getVariantBaseIndexs(ParallelData<S> parallelData) {
		if (parallelData.getConditions() == 1) {
			return getNonReferenceBaseIndexs(parallelData);
		}

		Set<Base> alleles = parallelData.getCombinedPooledData().getBaseCallCount().getAlleles();
		final List<Base> bases = new ArrayList<Base>(alleles.size());

		for (final Base base : alleles) {
			int n = 0;
			for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); conditionIndex++) {
				if (parallelData.getPooledData(conditionIndex).getBaseCallCount().getBaseCall(base) > 0) {
					n++;
				}
			}
			if (n < parallelData.getConditions()) {
				bases.add(base);
			}
		}

		final Set<Base> variantBaseIs = new HashSet<Base>(bases.size());
		for (int i = 0; i < bases.size(); ++i) {
			variantBaseIs.add(bases.get(i));
		}

		return variantBaseIs;
	}

	@Override
	public String toString() {
		return prettyPrint(this);
	}

	// for RRDs RNA RNA differences
	/* @depracted
	public static <S extends AbstractData & hasBaseCallCount & hasReferenceBase> int[] getNonRefBaseIndexs(final ParallelData<S> parallelData) {
		final int conditions = parallelData.getConditions();
		final byte referenceBase = parallelData.getCombinedPooledData().getReferenceBase();
		if (referenceBase == 'N') {
			throw new IllegalStateException("Missing reference information");
		}
		
		final int[] alleles = parallelData.getCombinedPooledData().getBaseCallCount().getAlleles();

		int[] observedAlleleCount = new int[BaseCallConfig.BASES.length];
		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			for (int baseIndex : parallelData.getPooledData(conditionIndex).getBaseCallCount().getAlleles()) {
				observedAlleleCount[baseIndex]++;
			}
		}

		// A | G
		// define all non-reference base as potential variants
		if (alleles.length == 2 && 
				observedAlleleCount[alleles[0]] + observedAlleleCount[alleles[1]] == conditions) {
			// define non-reference base as potential variants
			if (referenceBase == 'N') {
				return new int[0];
			}
			
			final int referenceBaseIndex = BaseCallConfig.getInstance().getBaseIndex((byte)referenceBase);
			for (final int baseIndex : alleles) {
				if (baseIndex != referenceBaseIndex) {
					return new int[] {baseIndex};
				}
			}
		}
		
		// A | AG
		if (alleles.length == 2 && 
				observedAlleleCount[alleles[0]] + observedAlleleCount[alleles[1]] > conditions) {
			return ParallelData.getVariantBaseIndexs(parallelData);
		}

		// condition1: AG | AG AND condition2: AGC |AGC
		// return allelesIs;
		return new int[0];
	}
	*/
	
	public static <S extends AbstractData & HasPileupCount> S[] flat(final S[] data, 
			final S[]ret, 
			final Set<Base> variantBases, final Base commonBase) {

		for (int i = 0; i < data.length; ++i) {
			ret[i] = data[i];

			for (final Base variantBase : variantBases) {
				ret[i].getPileupCount().add(commonBase, variantBase, data[i].getPileupCount());
				ret[i].getPileupCount().substract(variantBase, variantBase, data[i].getPileupCount());
			}
			
		}

		return ret;
	}

	public static <S extends AbstractData> Coordinate getCommonCoordinate(final S[][] data) {
		Coordinate coordinate = null;
		for (final S[] conditionData : data) {
			coordinate = getCommonCoordinate(conditionData, coordinate);
		}

		return coordinate;
	}
	
	private static <S extends AbstractData> Coordinate getCommonCoordinate(final S[] data, Coordinate coordinate) {
		for (final S replicateData : data) {
			if (coordinate == null) {
				coordinate = replicateData.getCoordinate();
			} else if (! coordinate.equal(replicateData.getCoordinate())) {
				throw new IllegalStateException("Replicate data has different coordinates: " + coordinate.toString() + " != " + replicateData.getCoordinate().toString());
			}
		}

		return coordinate;
	}
	
	public static <S extends AbstractData> LIBRARY_TYPE getCommonLibraryType(final S[][] data) {
		LIBRARY_TYPE tmp = null;
		for (final S[] conditionData : data) {
			for (final S replicateData : conditionData) {
				if (tmp == null) {
					tmp = replicateData.getLibraryType();
				} else if (tmp != replicateData.getLibraryType()) {
					return LIBRARY_TYPE.MIXED;
				}
			}
		}
		
		
		return tmp;
	}
	
	public static <S extends AbstractData> String prettyPrint(final ParallelData<S> parallelPileupData) {
		final StringBuilder sb = new StringBuilder();

		// coordinate
		sb.append("Container Coordinate: ");
		sb.append(parallelPileupData.getCombinedPooledData().getCoordinate().toString());
		sb.append('\n');

		// pooled
		sb.append("Container combined pooled: \n");
		sb.append(parallelPileupData.getCombinedPooledData().toString());
		sb.append('\n');

		return sb.toString();
	}
	
}
