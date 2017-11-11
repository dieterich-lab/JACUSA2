package jacusa.filter.storage;

import lib.cli.parameters.AbstractConditionParameter;
import lib.data.AbstractData;

public abstract class AbstractStorage<T extends AbstractData> {

	private AbstractConditionParameter<T> conditionParameter;
	
	// corresponds to CLI option 
	private final char c;

	public AbstractStorage(final char c) {
		this.c = c;
	}

	public final char getC() {
		return c;
	}

	public void setCondition(final AbstractConditionParameter<T> conditionParameter) {
		this.conditionParameter = conditionParameter;
	}
	
	public AbstractConditionParameter<T> getConditionParameter() {
		return conditionParameter;
	}

	public abstract int getOverhang();
	public abstract void clear();

}
