package jacusa.filter.factory;

import lib.data.AbstractData;
import lib.data.builder.ConditionContainer;
import lib.util.coordinate.CoordinateController;

public abstract class AbstractFilterFactory<T extends AbstractData> {

	public final static char SEP = ':';

	private final char c;
	protected String desc;

	public AbstractFilterFactory(final char c, final String desc) {
		this.c 		= c;
		this.desc	= desc;
	}

	public char getC() {
		return c;
	}

	public String getDesc() {
		return desc;
	}

	public void processCLI(final String line) throws IllegalArgumentException {
		// implement to change behavior via CLI
	}

	public abstract void registerFilter(final CoordinateController coordinateController, final ConditionContainer<T> conditionContainer);

} 