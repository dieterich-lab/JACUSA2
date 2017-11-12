package lib.data.cache;

import java.util.Arrays;

import lib.method.AbstractMethodFactory;
import lib.util.Coordinate;

import htsjdk.samtools.SAMRecord;

import lib.data.AbstractData;
import lib.data.builder.SAMRecordWrapper;
import lib.data.has.hasBaseCallCount;

public class BaseCallCache<T extends AbstractData & hasBaseCallCount> 
extends AbstractCache<T> {

	private final int[] coverage;
	private final int[][] baseCalls;

	public BaseCallCache(final AbstractMethodFactory<T> methodFactory) {
		super(methodFactory);

		coverage = new int[getActiveWindowSize()];
		baseCalls = new int[getBaseSize()][getActiveWindowSize()];
	}

	@Override
	public void addRecordWrapper(SAMRecordWrapper recordWrapper) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void addRecordWrapperPosition(final int readPosition, final SAMRecordWrapper recordWrapper) {
		
	}
	
	@Override
	public void addRecordWrapperRegion(final int readPosition, final int length, final SAMRecordWrapper recordWrapper) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public T getData(final Coordinate coordinate) {
		final T data = getDataGenerator().createData();

		final int windowPosition = getWindowPosition(coordinate);
		if (coverage[windowPosition] == 0) {
			return data;
		}

		for (int baseIndex = 0; baseIndex < getBaseSize(); baseIndex++) {
			data.getBaseCallCount().set(baseIndex, baseCalls[windowPosition][baseIndex]);
		}
		
		return data;
	}

	protected void incrementBaseCalls(final int windowPosition, final int readPosition, final int length, final SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();
		for (int i = 0; i < length; ++i) {
			// consider only chosen bases
			final int baseIndex = getBaseCallConfig().getBaseIndex(record.getReadBases()[readPosition + i]);
			if (baseIndex < 0) {
				continue;
			}

			coverage[windowPosition] += 1;
			baseCalls[baseIndex][windowPosition] += 1;
		}
	}
	
	@Override
	public void clear() {
		Arrays.fill(coverage, 0);
		for (int[] b : baseCalls) {
			Arrays.fill(b, 0);	
		}
	}
	
	public int getCoverage(final int windowPosition) {
		return coverage[windowPosition];
	}
	
	public int getBaseCalls(final int baseIndex, final int windowPosition) {
		return baseCalls[baseIndex][windowPosition];
	}

	private int getBaseSize() {
		return getBaseCallConfig().getBases().length; 
	}
	
}
