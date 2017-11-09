package lib.data.cache;

import lib.util.Coordinate;
import lib.util.Coordinate.STRAND;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;

public abstract class AbstractStrandedBaseCallCache extends AbstractCache {

	private BaseCallCache forward; 
	private BaseCallCache reverse;

	public AbstractStrandedBaseCallCache(final BaseCallConfig baseCallConfig, final int activeWindowSize) {
		super(activeWindowSize);
		forward = new BaseCallCache(baseCallConfig, activeWindowSize);
		reverse = new BaseCallCache(baseCallConfig, activeWindowSize);
	}
	
	@Override
	public void clear() {
		forward.clear();
		reverse.clear();
	}

	public int getCoverage(final int windowPosition, final STRAND strand) {
		switch (strand) {
		case FORWARD:
			return forward.getCoverage(windowPosition);

		case REVERSE:
			return reverse.getCoverage(windowPosition);
			
		case UNKNOWN:
			return forward.getCoverage(windowPosition) + reverse.getCoverage(windowPosition); 
		}
		return 0;
	}
	
	public int getBaseCalls(final int baseIndex, final int windowPosition, final STRAND strand) {
		switch (strand) {
		case FORWARD:
			return forward.getBaseCalls(baseIndex, windowPosition);

		case REVERSE:
			return reverse.getBaseCalls(baseIndex, windowPosition);
			
		case UNKNOWN:
			return forward.getBaseCalls(baseIndex, windowPosition) + 
					reverse.getBaseCalls(baseIndex, windowPosition); 
		}
		return 0;
	}

	public int getBaseCallQualities(final int baseIndex, final int baseQualIndex, 
			final int windowPosition, final STRAND strand) {

		switch (strand) {
		case FORWARD:
			return forward.getBaseCallQualities(baseIndex, baseQualIndex, 
					windowPosition);

		case REVERSE:
			return reverse.getBaseCallQualities(baseIndex, baseQualIndex, 
					windowPosition);
			
		case UNKNOWN:
			return forward.getBaseCallQualities(baseIndex, baseQualIndex, windowPosition) + 
					reverse.getBaseCallQualities(baseIndex, baseQualIndex, windowPosition); 
		}

		return 0;		
	}

	@Override
	public AbstractData getData(Coordinate coordinate) {
		switch (coordinate.getStrand()) {
		case FORWARD:
			return forward.getData(coordinate);

		case REVERSE:
			return reverse.getData(coordinate);

		case UNKNOWN:
			return null; // TODO
		}
		
		return null;
	}
	
	protected BaseCallCache getForward() {
		return forward;
	}

	protected BaseCallCache getReverse() {
		return reverse;
	}
	
}
