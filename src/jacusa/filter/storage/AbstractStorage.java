package jacusa.filter.storage;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;

public abstract class AbstractStorage<T extends AbstractData> {

	private ConditionParameters<T> condition;
	
	// corresponds to CLI option 
	private final char c;

	public AbstractStorage(final char c) {
		this.c = c;
	}
	

	public final char getC() {
		return c;
	}

	public void setCondition(final ConditionParameters<T> condition) {
		this.condition = condition;
	}
	
	public ConditionParameters<T> getCondition() {
		return condition;
	}

	public abstract int getOverhang();
	public abstract void clear();

}
