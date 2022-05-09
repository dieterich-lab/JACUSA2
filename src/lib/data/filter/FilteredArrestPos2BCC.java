package lib.data.filter;

import lib.data.storage.lrtarrest.ArrestPosition2BaseCallCount;

public class FilteredArrestPos2BCC extends AbstractFilteredData<FilteredArrestPos2BCC, ArrestPosition2BaseCallCount> {

	private static final long serialVersionUID = 1L;

	public FilteredArrestPos2BCC() {
		super(ArrestPosition2BaseCallCount.class);
	}
	
	public FilteredArrestPos2BCC(FilteredArrestPos2BCC o) {
		super(o);
	}
	
	@Override
	public FilteredArrestPos2BCC copy() {
		return new FilteredArrestPos2BCC(this);
	}
	
	/* TODO
	public static class Parser extends AbstractFilteredData.AbstractParser<FilteredBaseCallCount, BaseCallCount> {

		private final BaseCallCount.AbstractParser bccParser;
		
		public Parser(final BaseCallCount.AbstractParser bccParser) {
			this.bccParser = bccParser;
		}
		
		@Override
		public FilteredBaseCallCount parse(String s) {
			final FilteredBaseCallCount bccFd = new FilteredBaseCallCount();
			parse(s, bccFd);
			return bccFd;
		}

		@Override
		public String wrap(FilteredBaseCallCount o) {
			return wrap(o);
		}

		@Override
		protected String wrapFilteredElement(BaseCallCount filteredElement) {
			return bccParser.wrap(filteredElement);
		}

		@Override
		protected BaseCallCount parseFilteredData(String s) {
			return bccParser.parse(s);
		}
		
	}
	*/
}
