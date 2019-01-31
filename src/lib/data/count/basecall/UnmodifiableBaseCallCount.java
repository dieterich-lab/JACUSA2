package lib.data.count.basecall;

import java.util.Collections;
import java.util.Set;

import lib.util.Base;

public class UnmodifiableBaseCallCount extends AbstractBaseCallCount {

	private static final long serialVersionUID = 1L;

	private final BaseCallCount bcc;
	
	public UnmodifiableBaseCallCount(final BaseCallCount bcc) {
		this.bcc = bcc;
	}

	@Override
	public int getCoverage() {
		return bcc.getCoverage();
	}

	@Override
	public BaseCallCount copy() {
		return new UnmodifiableBaseCallCount(bcc.copy());
	}

	@Override
	public int getBaseCall(Base base) {
		return bcc.getBaseCall(base);
	}

	@Override
	public String toString() {
		return bcc.toString();
	}
	
	
	@Override
	public Set<Base> getAlleles() {
		return Collections.unmodifiableSet(bcc.getAlleles()); 
	}

	@Override
	public UnmodifiableBaseCallCount increment(Base base) {
		throw new UnsupportedOperationException();
	}

	@Override
	public UnmodifiableBaseCallCount clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public UnmodifiableBaseCallCount set(Base base, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	public UnmodifiableBaseCallCount add(Base base, BaseCallCount baseCallCount) {
		throw new UnsupportedOperationException();
	}

	@Override
	public UnmodifiableBaseCallCount add(BaseCallCount baseCallCount) {
		throw new UnsupportedOperationException();
	}

	@Override
	public UnmodifiableBaseCallCount add(Base dest, Base src, BaseCallCount baseCallCount) {
		throw new UnsupportedOperationException();
	}

	@Override
	public UnmodifiableBaseCallCount subtract(Base base, BaseCallCount baseCallCount) {
		throw new UnsupportedOperationException();
	}

	@Override
	public UnmodifiableBaseCallCount subtract(BaseCallCount baseCallCount) {
		throw new UnsupportedOperationException();
	}

	@Override
	public UnmodifiableBaseCallCount subtract(Base dest, Base src, BaseCallCount baseCallCount) {
		throw new UnsupportedOperationException();
	}

	@Override
	public UnmodifiableBaseCallCount invert() {
		throw new UnsupportedOperationException();
	}
	
}
