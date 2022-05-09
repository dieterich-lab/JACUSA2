package lib.data.filter;

import lib.data.Data;

public class BooleanData implements Data<BooleanData> {

	private static final long serialVersionUID = 1L;

	private boolean b;

	public BooleanData() {
		this(false);
	}
	
	public BooleanData(final boolean b) {
		this.b = b;
	}

	private BooleanData(final BooleanData booleanData) {
		b = booleanData.b;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BooleanData)) {
			return false;
		}
		BooleanData bw = (BooleanData) obj;
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

	public void setValue(boolean value) {
		b = value;
	}

	@Override
	public BooleanData copy() {
		return new BooleanData(this);
	}

	@Override
	public void merge(BooleanData booleanData) {
		b |= booleanData.b;
	}

}