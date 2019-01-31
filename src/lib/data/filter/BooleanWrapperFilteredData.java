package lib.data.filter;

import java.util.Map;

public class BooleanWrapperFilteredData extends AbstractFilteredData<BooleanWrapperFilteredData, BooleanWrapper> {

	private static final long serialVersionUID = 1L;
	
	public BooleanWrapperFilteredData() {
		super();
	}
	
	protected BooleanWrapperFilteredData(Map<Character, BooleanWrapper> map) {
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

		return equals(getClass().cast(obj));
	}
	
	@Override
	protected BooleanWrapperFilteredData newInstance(Map<Character, BooleanWrapper> map) {
		return new BooleanWrapperFilteredData(map);
	}
	
	public static class Parser extends AbstractFilteredData.AbstractParser<BooleanWrapperFilteredData, BooleanWrapper> {

		public Parser(final char filterSep, final char filterDataSep) {
			super(filterSep, filterDataSep);
		}
		
		public Parser() {
			super();
		}
		
		@Override
		public BooleanWrapperFilteredData parse(String s) {
			final BooleanWrapperFilteredData bfd = new BooleanWrapperFilteredData();
			parse(s, bfd);
			return bfd;
		}

		@Override
		protected BooleanWrapper parseFilteredData(String s) {
			return new BooleanWrapper(Boolean.valueOf(s));
		}

		@Override
		public String wrap(BooleanWrapperFilteredData o) {
			return wrap(o);
		}

		@Override
		protected String wrapFilteredElement(BooleanWrapper filteredElement) {
			return Boolean.toString(filteredElement.getValue());
		}
	
	}
	
}
