package jacusa.filter.factory;

import lib.data.AbstractData;
import lib.data.builder.ConditionContainer;
import lib.util.coordinate.CoordinateController;

/**
 * This filter factory 
 * 
 * @param <T>
 */
public abstract class AbstractFilterFactory<T extends AbstractData> {

	// TODO add comment - what does this stand for?
	public final static char SEP = ':';

	// unique char id - corresponds CLI
	private final char c;
	// description of filter - shown in help
	private final String desc;

	public AbstractFilterFactory(final char c, final String desc) {
		this.c 		= c;
		this.desc	= desc;
	}

	/**
	 * Gets unique char id of this filter.
	 * 
	 * @return unique char id 
	 */
	public char getC() {
		return c;
	}

	/**
	 * Gets the description for this filter.
	 * 
	 * @return string that describes this filter
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * TODO add comments.
	 * 
	 * @param line
	 * @throws IllegalArgumentException
	 */
	public void processCLI(final String line) throws IllegalArgumentException {
		// implement to change behavior via CLI
	}

	/**
	 * TODO add comments.
	 * 
	 * @param coordinateController
	 * @param conditionContainer
	 */
	public abstract void registerFilter(final CoordinateController coordinateController, final ConditionContainer<T> conditionContainer);

} 