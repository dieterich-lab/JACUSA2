package lib.data.filter;

import java.util.Map;

import lib.data.count.basecall.BaseCallCount;

public class BaseCallCountFilteredData extends AbstractFilteredData<BaseCallCountFilteredData, BaseCallCount> {

	private static final long serialVersionUID = 1L;

	public BaseCallCountFilteredData() {
		super();
	}
	
	protected BaseCallCountFilteredData(Map<Character, BaseCallCount> map) {
		super(map);
	}
	
	@Override
	protected BaseCallCountFilteredData newInstance(Map<Character, BaseCallCount> map) {
		return new BaseCallCountFilteredData(map);
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
	
	public static class Parser extends AbstractFilteredData.AbstractParser<BaseCallCountFilteredData, BaseCallCount> {

		private final BaseCallCount.AbstractParser bccParser;
		
		public Parser(final BaseCallCount.AbstractParser bccParser) {
			this.bccParser = bccParser;
		}
		
		@Override
		public BaseCallCountFilteredData parse(String s) {
			final BaseCallCountFilteredData bccFd = new BaseCallCountFilteredData();
			parse(s, bccFd);
			return bccFd;
		}

		@Override
		public String wrap(BaseCallCountFilteredData o) {
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
	
}
