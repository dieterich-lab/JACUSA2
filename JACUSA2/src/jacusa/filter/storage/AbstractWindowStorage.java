package jacusa.filter.storage;

import java.util.Arrays;

import jacusa.data.AbstractData;
import jacusa.data.BaseCallConfig;

import jacusa.pileup.builder.WindowCache;
import jacusa.util.WindowCoordinate;

import net.sf.samtools.SAMRecord;

public abstract class AbstractWindowStorage<T extends AbstractData> 
extends AbstractStorage<T> {

	// count indel, read start/end, splice site as only 1!!!
	// this ensure that a base-call will only be counted once...
	private boolean[] visited;
	private final BaseCallConfig baseConfig;

	private WindowCache windowCache;
	
	// container for current SAMrecord
	protected SAMRecord record;

	public AbstractWindowStorage(final char c, final BaseCallConfig baseConfig) {
		super(c);
		this.baseConfig = baseConfig;
	}
	
	// visited = new boolean[windowSize];
	
	protected void addRegion(int windowPosition, int length, int readPosition, SAMRecord record) {
		if (this.record != record) {
			this.record = record;
			Arrays.fill(visited, false);
		}

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

		for (int i = 0; i < length && windowPosition + i < windowCache.getWindowSize() && readPosition + i < record.getReadLength(); ++i) {
			if (! visited[windowPosition + i]) {
				final int baseIndex = baseConfig.getBaseIndex(record.getReadBases()[readPosition + i]);

				// corresponds to N -> ignore
				if (baseIndex < 0) {
					continue;
				}

				byte qual = record.getBaseQualities()[readPosition + i];
				if (qual >= getCondition().getMinBASQ()) {
					windowCache.addHighQualityBaseCall(windowPosition + i, baseIndex, qual);
					visited[windowPosition + i] = true;
				}
			}
		}
	}
	
	public void setWindowCoordinates(final WindowCoordinate windowCoordinates) {
		windowCache = new WindowCache(windowCoordinates);
	}

	public WindowCache getWindowCache() {
		return windowCache;
	}

	@Override
	public void clear() {
		windowCache.clear();		
	}

}