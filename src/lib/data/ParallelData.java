package lib.data;

import lib.method.AbstractMethodFactory;
import lib.util.Coordinate;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class ParallelData<T extends AbstractData> 
implements hasCoordinate {
	
	private AbstractMethodFactory<T> methodFactory;
	
	private Coordinate coordinate;

	private T[][] data;
	private T[] cachedCombinedData;
	
	private T[] cachedPooledData;
	private T cachedCombinedPooledData;

	private int cachedTotalReplicates;
	
	public ParallelData(final AbstractMethodFactory<T> methodFactory) {
		this.methodFactory 		= methodFactory;
		reset();
	}

	public ParallelData(final AbstractMethodFactory<T> methodFactory, 
			final Coordinate coordinate, final T[][] data) {
		this.methodFactory 	= methodFactory;
		this.coordinate 	= new Coordinate(coordinate);
		
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
		methodFactory = parallelData.methodFactory;
		coordinate = new Coordinate(parallelData.getCoordinate());

		// copy data
		data = methodFactory.copyContainer(parallelData.data);
		cachedCombinedData = methodFactory.copyReplicateData(parallelData.cachedCombinedData);
		cachedTotalReplicates = parallelData.cachedTotalReplicates;
	}
	
	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(final Coordinate coordinate) {
		this.coordinate = coordinate;
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
			data = methodFactory.createContainer(data.length);
		} else {
			final int conditions = methodFactory.getParameters().getConditionsSize();
			data = methodFactory.createContainer(conditions); 
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
			cachedPooledData = methodFactory.createReplicateData(getConditions());
		}
		
		if (cachedPooledData[conditionIndex] == null && 
				getReplicates(conditionIndex) > 0) {
			
			T tmpData = methodFactory.createData();
			tmpData.setCoordinate(getCoordinate()); // TODO check
			
			for (int replicateIndex = 0; replicateIndex < getReplicates(conditionIndex); replicateIndex++) {
				tmpData.add(getData(conditionIndex, replicateIndex));
			}
			cachedPooledData[conditionIndex] = tmpData;
		}

		return cachedPooledData[conditionIndex];
	}

	public T getCombinedPooledData() {
		if (cachedCombinedPooledData == null && getPooledData(0) != null) {

			cachedCombinedPooledData = methodFactory.createData();
			for (int conditionIndex = 0; conditionIndex < getConditions(); conditionIndex++) {
				cachedCombinedPooledData.add(getPooledData(conditionIndex));
			}
		}

		return cachedCombinedPooledData;
	}
	
	public T[] getCombinedData() {
		if (cachedCombinedData == null) {
			cachedCombinedData = methodFactory.createReplicateData(cachedTotalReplicates);

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

	public static <S extends BaseQualData> int[] getNonReferenceBaseIndexs(ParallelData<S> parallelData) {
		final char referenceBase = parallelData.getCombinedPooledData().getReferenceBase();
		if (referenceBase == 'N') {
			return new int[0];
		}
	
		final int[] allelesIndexs = parallelData
				.getCombinedPooledData()
				.getBaseQualCount()
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

	// suffices that one replicate contains replicate
	public static <S extends BaseQualData> int[] getVariantBaseIndexs(ParallelData<S> parallelData) {
		int n = 0;
		int[] alleles = parallelData.getCombinedPooledData().getBaseQualCount().getAlleles();
		
		for (int baseIndex : alleles) {
			for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); conditionIndex++) {
				if (parallelData.getPooledData(conditionIndex).getBaseQualCount().getBaseCount(baseIndex) > 0) {
					alleles[baseIndex]++;
				}
			}
			if (alleles[baseIndex] > 0 && alleles[baseIndex] < parallelData.getConditions()) {
				++n;
			}
		}

		int[] variantBaseIs = new int[n];
		int j = 0;
		for (int baseIndex : alleles) {
			if (alleles[baseIndex] > 0 && alleles[baseIndex] < parallelData.getConditions()) {
				variantBaseIs[j] = baseIndex;
				++j;
			}
		}

		return variantBaseIs;
	}
	
	public static <S extends BaseQualData> S[] flat(final S[] data, 
			final S[]ret, 
			final int[] variantBaseIndexs, final int commonBaseIndex) {
		for (int i = 0; i < data.length; ++i) {
			ret[i] = data[i];

			for (int variantBaseIndex : variantBaseIndexs) {
				ret[i].getBaseQualCount().add(commonBaseIndex, variantBaseIndex, data[i].getBaseQualCount());
				ret[i].getBaseQualCount().substract(variantBaseIndex, variantBaseIndex, data[i].getBaseQualCount());
			}
			
		}
		return ret;
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
