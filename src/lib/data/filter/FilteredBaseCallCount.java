package lib.data.filter;

import lib.data.count.basecall.BaseCallCount;

public class FilteredBaseCallCount extends AbstractFilteredData<FilteredBaseCallCount, BaseCallCount> {

	private static final long serialVersionUID = 1L;

	public FilteredBaseCallCount() {
		super(BaseCallCount.class);
	}
	
	public FilteredBaseCallCount(FilteredBaseCallCount o) {
		super(o);
	}
	
	@Override
	public FilteredBaseCallCount copy() {
		return new FilteredBaseCallCount(this);
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
