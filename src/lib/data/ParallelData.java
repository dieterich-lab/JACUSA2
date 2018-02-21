package lib.data;

import lib.cli.options.BaseCallConfig;
import lib.data.generator.DataGenerator;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasCoordinate;
import lib.data.has.hasLibraryType;
import lib.data.has.hasPileupCount;
import lib.data.has.hasReferenceBase;
import lib.util.coordinate.Coordinate;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class ParallelData<T extends AbstractData> 
implements hasCoordinate, hasLibraryType {
	
	private DataGenerator<T> dataGenerator;
	
	private LIBRARY_TYPE libraryType;
	private Coordinate coordinate;

	private T[][] data;
	private T[] cachedCombinedData;
	
	private T[] cachedPooledData;
	private T cachedCombinedPooledData;

	private int cachedTotalReplicates;
	
	public ParallelData(final DataGenerator<T> dataGenerator) {
		this.dataGenerator = dataGenerator;
		reset();
	}

	public ParallelData(final DataGenerator<T> dataGenerator, 
			final Coordinate coordinate, final T[][] data) {
		this.dataGenerator 	= dataGenerator;
		this.coordinate 	= new Coordinate(coordinate);
		
		this.libraryType	= getCommonLibraryType(data);
		
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
		coordinate = new Coordinate(parallelData.getCoordinate());

		// copy data
		data = dataGenerator.copyContainerData(parallelData.data);
		if (parallelData.cachedCombinedData != null) {
			cachedCombinedData = dataGenerator.copyReplicateData(parallelData.cachedCombinedData);
		}
		cachedTotalReplicates = parallelData.cachedTotalReplicates;
	}
	
	public Coordinate getCoordinate() {
		return coordinate;
	}

	public LIBRARY_TYPE getLibraryType() {
		return libraryType;
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
		coordinate 	= new Coordinate();

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
			
			T tmpData = dataGenerator.createData(null, getCoordinate());
			
			for (int replicateIndex = 0; replicateIndex < getReplicates(conditionIndex); replicateIndex++) {
				tmpData.add(getData(conditionIndex, replicateIndex));
			}
			cachedPooledData[conditionIndex] = tmpData;
		}

		return cachedPooledData[conditionIndex];
	}

	public T getCombinedPooledData() {
		if (cachedCombinedPooledData == null && getPooledData(0) != null) {

			cachedCombinedPooledData = dataGenerator.createData(getLibraryType(), getCoordinate());
			for (int conditionIndex = 0; conditionIndex < getConditions(); conditionIndex++) {
				cachedCombinedPooledData.add(getPooledData(conditionIndex));
			}
		}

		return cachedCombinedPooledData;
	}
	
	public T[] getCombinedData() {
		if (cachedCombinedData == null) {
			cachedCombinedData = dataGenerator.createReplicateData(cachedTotalReplicates);

			int dest = 0;;
			for (int conditionIndex = 0; conditionIndex < getConditions(); conditionIndex++) {
				System.arraycopy(
						data[conditionIndex], 
						0, 
						cachedCombinedData[conditionIndex], 
						dest, 
						getReplicates(conditionIndex));
				dest += getReplicates(conditionIndex);
			}
		}
		
		return cachedCombinedData;
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

	public static <S extends PileupData> int[] getNonReferenceBaseIndexs(ParallelData<S> parallelData) {
		final byte referenceBase = parallelData.getCombinedPooledData().getPileupCount().getReferenceBase();
		if (referenceBase == 'N') {
			return new int[0];
		}
	
		final int[] allelesIndexs = parallelData
				.getCombinedPooledData()
				.getPileupCount()
				.getBaseCallCount()
				.getAlleles();
		
		final int referenceBaseIndex = BaseCallConfig.getInstance().getBaseIndex((byte)referenceBase);
	
		// find non-reference base(s)
		int i = 0;
		final int[] tmp = new int[allelesIndexs.length];
		for (final int baseIndex : allelesIndexs) {
			if (baseIndex != referenceBaseIndex) {
				tmp[i] = baseIndex;
				++i;
			}
		}
		final int[] ret = new int[i];
		System.arraycopy(tmp, 0, ret, 0, i);
		return ret;
	}

	// FIXME
	// suffices that one replicate contains replicate
	public static <S extends AbstractData & hasBaseCallCount> int[] getVariantBaseIndexs(ParallelData<S> parallelData) {
		int n = 0;
		int[] alleles = parallelData.getCombinedPooledData().getBaseCallCount().getAlleles();
		int[] baseCount = new int[BaseCallConfig.BASES.length];
		
		for (int baseIndex : alleles) {
			for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); conditionIndex++) {
				if (parallelData.getPooledData(conditionIndex).getBaseCallCount().getBaseCallCount(baseIndex) > 0) {
					baseCount[baseIndex]++;
				}
			}
			if (baseCount[baseIndex] > 0 && baseCount[baseIndex] < parallelData.getConditions()) {
				++n;
			}
		}

		int[] variantBaseIs = new int[n];
		int j = 0;
		for (int baseIndex : alleles) {
			if (baseCount[baseIndex] > 0 && baseCount[baseIndex] < parallelData.getConditions()) {
				variantBaseIs[j] = baseIndex;
				++j;
			}
		}

		return variantBaseIs;
	}
	
	// FIXME
	// ORDER RESULTS [0] SHOULD BE THE VARIANTs TO TEST
	public static <S extends AbstractData & hasBaseCallCount & hasReferenceBase> int[] getVariantBaseIndexs2(final ParallelData<S> parallelData) {
			final int conditions = parallelData.getConditions();
			final byte referenceBase = parallelData.getCombinedPooledData().getReferenceBase();
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
	
	public static <S extends AbstractData & hasPileupCount> S[] flat(final S[] data, 
			final S[]ret, 
			final int[] variantBaseIndexs, final int commonBaseIndex) {
		for (int i = 0; i < data.length; ++i) {
			ret[i] = data[i];

			for (int variantBaseIndex : variantBaseIndexs) {
				ret[i].getPileupCount().add(commonBaseIndex, variantBaseIndex, data[i].getPileupCount());
				ret[i].getPileupCount().substract(variantBaseIndex, variantBaseIndex, data[i].getPileupCount());
			}
			
		}
		return ret;
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
	
	public static <S extends AbstractData> void prettyPrint(final ParallelData<S> parallelPileupData) {
		final StringBuilder sb = new StringBuilder();

		// coordinate
		sb.append("Container Coordinate: ");
		sb.append(parallelPileupData.getCoordinate().toString());
		sb.append('\n');

		// pooled
		sb.append("Container combined pooled: \n");
		sb.append(parallelPileupData.getCombinedPooledData().toString());
		sb.append('\n');

		System.out.print(sb.toString());
	}
	
}
