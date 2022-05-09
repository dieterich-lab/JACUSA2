package lib.data.filter;

public class FilteredBoolean extends AbstractFilteredData<FilteredBoolean, BooleanData> {

	private static final long serialVersionUID = 1L;

	public FilteredBoolean() {
		super(BooleanData.class);
	}

	public FilteredBoolean(final FilteredBoolean template) {
		super(template);
	}

	@Override
	public FilteredBoolean copy() {
		return new FilteredBoolean(this);
	}

}
