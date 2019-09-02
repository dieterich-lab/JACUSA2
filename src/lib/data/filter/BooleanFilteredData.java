package lib.data.filter;

import java.util.Map;

public class BooleanFilteredData extends AbstractFilteredData<BooleanFilteredData, BooleanData> {

	private static final long serialVersionUID = 1L;
	
	public BooleanFilteredData() {
		super();
	}
	
	protected BooleanFilteredData(Map<Character, BooleanData> map) {
		super(map);
	}
	
	@Override
	public final boolean equals(Object obj) {
		if (obj == null || ! getClass().isInstance(obj)) {
			return false;
		}
		if (obj == this) {
			return true;
		}

		return specificEquals(getClass().cast(obj));
	}
	
	@Override
	protected BooleanFilteredData newInstance(Map<Character, BooleanData> map) {
		return new BooleanFilteredData(map);
	}
	
	public static class Parser extends AbstractFilteredData.AbstractParser<BooleanFilteredData, BooleanData> {

		public Parser(final char filterSep, final char filterDataSep) {
			super(filterSep, filterDataSep);
		}
		
		public Parser() {
			super();
		}
		
		@Override
		public BooleanFilteredData parse(String s) {
			final BooleanFilteredData bfd = new BooleanFilteredData();
			parse(s, bfd);
			return bfd;
		}

		@Override
		protected BooleanData parseFilteredData(String s) {
			return new BooleanData(Boolean.valueOf(s));
		}

		@Override
		public String wrap(BooleanFilteredData o) {
			return wrap(o);
		}

		@Override
		protected String wrapFilteredElement(BooleanData filteredElement) {
			return Boolean.toString(filteredElement.getValue());
		}
	
	}
	
}
