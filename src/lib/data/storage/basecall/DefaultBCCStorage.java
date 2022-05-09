package lib.data.storage.basecall;

import java.util.Arrays;

import lib.util.Base;
import lib.data.DataType;
import lib.data.count.basecall.BaseCallCount;
import lib.data.storage.container.SharedStorage;

public class DefaultBCCStorage
extends AbstractBaseCallCountStorage {

	private final int[] bcA;
	private final int[] bcC;
	private final int[] bcG;
	private final int[] bcT;
	
	public DefaultBCCStorage(
			final SharedStorage sharedStorage, final DataType<BaseCallCount> dataType) {

		super(sharedStorage, dataType);

		bcA = new int[getCoordinateController().getActiveWindowSize()];
		bcC = new int[getCoordinateController().getActiveWindowSize()];
		bcG = new int[getCoordinateController().getActiveWindowSize()];
		bcT = new int[getCoordinateController().getActiveWindowSize()];
	}

	@Override
	void increment(int winPos, Base base) {
		getBaseCallStorage(base)[winPos] += 1;
	}
	
	private int[] getBaseCallStorage(final Base base) {
		switch (base) {
		
		case A:
			return bcA;
			
		case C:
			return bcC;
			
		case G:
			return bcG;
			
		case T:
			return bcT;
		
		default:
			throw new IllegalStateException();
		}
	}
	
	@Override
	public int getCount(int winPos, Base base) {
		return getBaseCallStorage(base)[winPos];
	}
	
	@Override
	protected void clearSpecific() {
		for (final Base base : Base.validValues()) {
			Arrays.fill(getBaseCallStorage(base), 0);	
		}
	}

}
