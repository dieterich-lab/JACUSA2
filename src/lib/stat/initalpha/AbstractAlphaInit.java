package lib.stat.initalpha;

import lib.stat.nominal.NominalData;

/**
 * TODO add documentation
 */
public abstract class AbstractAlphaInit {

	private final String name;
	private final String desc;
	
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
	public abstract double[] init(NominalData dirMultData);
	
	/**
	 * Create a new instance.
	 * 
	 * @param line
	 * @return
	 */
	public abstract AbstractAlphaInit newInstance(String line);
	
}