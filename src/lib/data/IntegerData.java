package lib.data;

public class IntegerData implements Data<IntegerData> {

	private static final long serialVersionUID = 1L;

	private int i;

	public IntegerData() {
		this(0); // TODO set it null
	}

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
		IntegerData bw = (IntegerData) obj;
		return getValue() == bw.getValue();
	}

	@Override
	public int hashCode() {
		return i;
	}

	public int getValue() {
		return i;
	}

	public void setValue(final int i) {
		this.i = i;
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