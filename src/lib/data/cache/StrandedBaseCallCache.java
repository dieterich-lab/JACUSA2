package lib.data.cache;

import java.util.ArrayList;
import java.util.List;

import lib.util.Coordinate.STRAND;

import htsjdk.samtools.SAMRecord;

import lib.data.BaseCallConfig;
import lib.data.builder.SAMRecordWrapper;

public class StrandedBaseCallCache extends AbstractCache {

	private BaseCallCache forward; 
	private BaseCallCache reverse;
		
	public StrandedBaseCallCache(final BaseCallConfig baseCallConfig, final int activeWindowSize) {
		super(activeWindowSize);
		forward = new BaseCallCache(baseCallConfig, activeWindowSize);
		reverse = new BaseCallCache(baseCallConfig, activeWindowSize);
	}

	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();
		if (record.getReadNegativeStrandFlag()) {
			reverse.addRecordWrapper(recordWrapper);
		} else {
			forward.addRecordWrapper(recordWrapper);
		}
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

	public List<SAMRecordWrapper> getRecordWrapper(final int windowPosition, final STRAND strand) {
		switch (strand) {
		case FORWARD:
			return forward.getRecordWrapper(windowPosition);

		case REVERSE:
			return reverse.getRecordWrapper(windowPosition);
			
		case UNKNOWN:
			List<SAMRecordWrapper> combinedList = 
				new ArrayList<SAMRecordWrapper>(forward.getRecordWrapper(windowPosition));
			combinedList.addAll(reverse.getRecordWrapper(windowPosition));
			return combinedList; 
		}
		return new ArrayList<SAMRecordWrapper>(0);
	}

}
