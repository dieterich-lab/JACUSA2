package lib.data.filter;

import lib.data.Data;

public class BooleanWrapper implements Data<BooleanWrapper> {
	
	private static final long serialVersionUID = 1L;
	
	private boolean b;
	
	public BooleanWrapper(final boolean b) {
		this.b = b;
	}
	
	private BooleanWrapper(final BooleanWrapper booleanWrapper) {
		b = booleanWrapper.b;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof BooleanWrapper)) {
			return false;
		}
		BooleanWrapper bw = (BooleanWrapper)obj;
		return getValue() == bw.getValue();
	}
	
	@Override
	public int hashCode() {
		if (b) {
			return 1;
		}
		return 2;
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