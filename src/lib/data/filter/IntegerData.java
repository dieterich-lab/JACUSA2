package lib.data.filter;

import lib.data.Data;

public class IntegerData implements Data<IntegerData> {
	
	private static final long serialVersionUID = 1L;
	
	private int i;
	
	public IntegerData(final int i) {
		this.i = i;
	}
	
	private IntegerData(final IntegerData integerData) {
		i = integerData.i;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof IntegerData)) {
			return false;
		}
		IntegerData bw = (IntegerData)obj;
		return getValue() == bw.getValue();
	}
	
	@Override
	public int hashCode() {
		return i;
	}
	
	public int getValue() {
		return i;
	}
	
	@Override
	public IntegerData copy() {
		return new IntegerData(this);
	}
	
	public void add(final int count) {
		i += count;
	}
	
	@Override
	public void merge(IntegerData integerData) {
		i += integerData.i;
	}
	
}