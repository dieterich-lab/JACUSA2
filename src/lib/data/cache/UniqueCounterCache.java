package lib.data.cache;

import htsjdk.samtools.AlignmentBlock;

import java.util.Arrays;

import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.util.Coordinate;

public abstract class UniqueCounterCache<T extends AbstractData> 
implements Cache<T> {

	private Cache<T> cache;
	
	private boolean[] visited;
	private SAMRecordWrapper recordWrapper;
	
	public UniqueCounterCache(final int activeWindowSize, final Cache<T> cache) {
		this.cache = cache;
		visited = new boolean[activeWindowSize];
	}
	
	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		checkRecordWrapper(recordWrapper);
		for (final AlignmentBlock block : recordWrapper.getSAMRecord().getAlignmentBlocks()) {
			final int readPosition = block.getReadStart() - 1;
			addRecordWrapperRegion(readPosition, block.getLength(), recordWrapper);
		}
	}
	
	@Override
	public void addRecordWrapperPosition(final int readPosition, final SAMRecordWrapper recordWrapper) {
		checkRecordWrapper(recordWrapper);
		_addRecordWrapperPosition(readPosition, recordWrapper);
	}
	
	@Override
	public void addRecordWrapperRegion(final int readPosition, final int length, final SAMRecordWrapper recordWrapper) {
		checkRecordWrapper(recordWrapper);
		for (int i = 0; i < length; ++i) {
			_addRecordWrapperPosition(readPosition, recordWrapper);
		}
	}
	private void _addRecordWrapperPosition(final int readPosition, final SAMRecordWrapper recordWrapper) {
		if (! visited[readPosition]) {
			cache.addRecordWrapperPosition(readPosition, recordWrapper);
			visited[readPosition] = true;
		}
	}
	
	/*
	public abstract void addRecordWrapperRegion(int windowPosition, int length, int readPosition, final SAMRecordWrapper recordWrapper); {
		if (this.recordWrapper != recordWrapper) {
			this.recordWrapper = recordWrapper;
			Arrays.fill(visited, false);
		}
		final SAMRecord record = recordWrapper.getSAMRecord();

		int offset = 0;

		if (readPosition < 0) {
			offset += Math.abs(readPosition);
			
			windowPosition += offset;
			readPosition += offset;
			length -= offset;
		}

		if (windowPosition < 0) {
			offset += Math.abs(windowPosition);
			
			windowPosition += offset;
			readPosition += offset;
			length -= offset;
		}

		for (int i = 0; i < length && windowPosition + i < baseCallCache.getWindowSize() && readPosition + i < record.getReadLength(); ++i) {
			if (! visited[windowPosition + i]) {
				final int baseIndex = baseCallCache.getBaseCallConfig().getBaseIndex(record.getReadBases()[readPosition + i]);

				// corresponds to N -> ignore
				if (baseIndex < 0) {
					continue;
				}

				byte qual = record.getBaseQualities()[readPosition + i];
				if (qual >= conditionParameter.getMinBASQ()) {
					baseCallCache. addHighQualityBaseCall(windowPosition + i, baseIndex, qual);
					visited[windowPosition + i] = true;
				}
			}
		}
	}
	*/

	private void checkRecordWrapper(final SAMRecordWrapper recordWrapper) {
		if (this.recordWrapper != recordWrapper) {
			visited = new boolean[recordWrapper.getSAMRecord().getReadLength()];
			Arrays.fill(visited, false);
			this.recordWrapper = recordWrapper;
		}
	}
	
	@Override
	public T getData(final Coordinate coordinate) {
		return cache.getData(coordinate);
	}

	public boolean[] getVisited() {
		return visited;
	}

	@Override
	public void clear() {
		visited = null;
		recordWrapper = null;
	}

}