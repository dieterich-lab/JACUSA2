package lib.data.filter;

import lib.util.Data;

public class BooleanWrapper implements Data<BooleanWrapper> {
	
	private static final long serialVersionUID = 1L;
	
	private boolean b;
	
	public BooleanWrapper(final boolean b) {
		this.b = b;
	}
	
	private BooleanWrapper(final BooleanWrapper booleanWrapper) {
		b = booleanWrapper.b;
	}
	
	public boolean getValue() {
		return b;
	}
	
	@Override
	public BooleanWrapper copy() {
		return new BooleanWrapper(this);
	}
	
	@Override
	public void merge(BooleanWrapper booleanWrapper) {
		b |= booleanWrapper.b;
	}
	
}