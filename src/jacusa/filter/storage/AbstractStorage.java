package jacusa.filter.storage;

import lib.cli.parameters.JACUSAConditionParameters;
import lib.data.AbstractData;

public abstract class AbstractStorage<T extends AbstractData> {

	private JACUSAConditionParameters<T> condition;
	
	// corresponds to CLI option 
	private final char c;

	public AbstractStorage(final char c) {
		this.c = c;
	}
	

	public final char getC() {
		return c;
	}

	public void setCondition(final JACUSAConditionParameters<T> condition) {
		this.condition = condition;
	}
	
	public JACUSAConditionParameters<T> getCondition() {
		return condition;
	}

	public abstract int getOverhang();
	public abstract void clear();

}
