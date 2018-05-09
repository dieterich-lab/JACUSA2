package lib.data.filter;

import lib.data.AbstractFilteredData;

// TODO add and, or
public class BooleanFilteredData extends AbstractFilteredData<Boolean> {

	public BooleanFilteredData() {
		super();
	}
	
	public BooleanFilteredData(final BooleanFilteredData src) {
		super(src);
	}
	
	@Override
	public void merge(final AbstractFilteredData<Boolean> src) {
		for (final char c : src.getFilters()) {
			boolean newValue = get(c) || src.get(c);
			add(c, newValue);
		}
	}
	
	@Override
	public AbstractFilteredData<Boolean> copy() {
		return new BooleanFilteredData(this);
	}
	
	@Override
	protected Boolean copy(Boolean data) {
		return data.booleanValue();
	}
	
}
