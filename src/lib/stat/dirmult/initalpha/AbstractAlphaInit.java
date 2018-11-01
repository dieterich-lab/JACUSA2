package lib.stat.dirmult.initalpha;

import lib.stat.dirmult.DirMultData;

/**
 * 
 * @author Michael Piechotta
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
	public abstract double[] init(DirMultData dirMultData);
	
	/**
	 * Create a new instance.
	 * 
	 * @param line
	 * @return
	 */
	public abstract AbstractAlphaInit newInstance(String line);
	
}