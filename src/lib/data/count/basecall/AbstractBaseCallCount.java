package lib.data.count.basecall;


import lib.util.Base;

abstract class AbstractBaseCallCount implements BaseCallCount {
	
	private static final long serialVersionUID = 1L;

	@Override
	public boolean equals(Object obj) {
		if (obj == null || ! (obj instanceof BaseCallCount)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		
		final BaseCallCount bcc = (BaseCallCount) obj;
		for (final Base base : Base.validValues()) {
			if (getBaseCall(base) != bcc.getBaseCall(base)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 1;
		for (final Base base : Base.validValues()) {
			hash = 31 * hash + getBaseCall(base);
		}
		return hash;
	}
}
