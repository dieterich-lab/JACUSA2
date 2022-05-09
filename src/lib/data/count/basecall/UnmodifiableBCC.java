package lib.data.count.basecall;

import java.util.Collections;
import java.util.Set;

import lib.util.Base;

public class UnmodifiableBCC extends AbstractBaseCallCount {

	private static final long serialVersionUID = 1L;

	private final BaseCallCount bcc;

	public UnmodifiableBCC(final BaseCallCount bcc) {
		this.bcc = bcc;
	}

	@Override
	public int getCoverage() {
		return bcc.getCoverage();
	}

	@Override
	public BaseCallCount copy() {
		return new UnmodifiableBCC(bcc.copy());
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
	public UnmodifiableBCC increment(Base base) {
		throw new UnsupportedOperationException();
	}

	@Override
	public UnmodifiableBCC clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public UnmodifiableBCC set(Base base, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	public UnmodifiableBCC add(Base base, BaseCallCount bcc) {
		throw new UnsupportedOperationException();
	}

	@Override
	public UnmodifiableBCC add(BaseCallCount bcc) {
		throw new UnsupportedOperationException();
	}

	@Override
	public UnmodifiableBCC add(Base dest, Base src, BaseCallCount bcc) {
		throw new UnsupportedOperationException();
	}

	@Override
	public UnmodifiableBCC subtract(Base base, BaseCallCount bcc) {
		throw new UnsupportedOperationException();
	}

	@Override
	public UnmodifiableBCC subtract(BaseCallCount bcc) {
		throw new UnsupportedOperationException();
	}

	@Override
	public UnmodifiableBCC subtract(Base dest, Base src, BaseCallCount bcc) {
		throw new UnsupportedOperationException();
	}

	@Override
	public UnmodifiableBCC invert() {
		throw new UnsupportedOperationException();
	}

}
