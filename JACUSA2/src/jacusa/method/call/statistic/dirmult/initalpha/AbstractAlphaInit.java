package jacusa.method.call.statistic.dirmult.initalpha;

/**
 * 
 * @author Michael Piechotta
 */
public abstract class AbstractAlphaInit {

	private String name;
	private String desc;
	
	public AbstractAlphaInit(final String name, final String desc) {
		this.name	= name;
		this.desc	= desc;
	}
	
	/**
	 * Return the short name.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Return a short description.
	 * 
	 * @return
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * Calculate initial estimates for alpha when > 1 replicates are available.
	 * 
	 * @param baseIs
	 * @param data
	 * @param dataMatrix

	 * 			final T[] data,
	 * @return
	 */
	public abstract double[] init(
			final int[] baseIndexs,
			final double[][] dataMatrix);

	/**
	 * Create a new instance.
	 * 
	 * @param line
	 * @return
	 */
	public abstract AbstractAlphaInit newInstance(final String line);

	/**
	 * Calculate the coverage per pileup/replicate taking pseudocounts into account
	 * 
	 * Helper method.
	 * 
	 * @param dataMatrix
	 * @return
	 */
	protected double[] getCoverages(final int[] baseIndexs, final double[][] dataMatrix) {
		int replicates = dataMatrix.length;
		double[] coverages = new double[replicates];

		for (int replicateIndex = 0; replicateIndex < replicates; ++replicateIndex) {
			double rowSum = 0.0;
			for (int baseIndex : baseIndexs) {
				rowSum += dataMatrix[replicateIndex][baseIndex];
			}
			coverages[replicateIndex] = rowSum;
		}

		return coverages;
	}
	
}