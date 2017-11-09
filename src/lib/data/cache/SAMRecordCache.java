package lib.data.cache;

import java.util.ArrayList;
import java.util.List;

import lib.util.Coordinate;

import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.builder.SAMRecordWrapper;

public class SAMRecordCache extends AbstractCache {

	private BaseCallConfig baseCallConfig;
	private List<List<SAMRecordWrapper>> records;

	public SAMRecordCache(final BaseCallConfig baseCallConfig, final int activeWindowSize) {
		super(activeWindowSize);
		this.baseCallConfig = baseCallConfig;

		records = new ArrayList<List<SAMRecordWrapper>>(activeWindowSize);
		for (int i = 0; i < activeWindowSize; ++i) {
			records.add(new ArrayList<SAMRecordWrapper>(50));
		}
	}

	// TODO
	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();

		for (final AlignmentBlock block : record.getAlignmentBlocks()) {
			int referencePosition = block.getReferenceStart();
			int windowPosition = Coordinate.makeRelativePosition(getActiveWindowCoordinates(), referencePosition);
			int readPosition = block.getReadStart() - 1;

			// alignment length
			int length = block.getLength();
			
			if (windowPosition == -1) {
				windowPosition = referencePosition - getActiveWindowCoordinates().getStart();
				if (windowPosition > getActiveWindowSize()) { // downtstream of window -> ignore TODO distance
					continue;
				}
				// alignment outside of window - upstream TODO distance
				if (windowPosition + length < 0) { 
					continue;
				}
				final int offset = Math.abs(windowPosition); 
				windowPosition += offset;
				readPosition += offset;
				length -= offset;
			}

			int lengthOffset = getActiveWindowSize() - (windowPosition + length);
			if (lengthOffset <= 0) {
				incrementBaseCalls(windowPosition, readPosition, length + lengthOffset, recordWrapper);
				return;
			}
			incrementBaseCalls(windowPosition, readPosition, length, recordWrapper);
		}
	}
	
	@Override
	public AbstractData getData(Coordinate coordinate) {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected void incrementBaseCalls(final int windowPosition, 
			final int readPosition, final int length, final SAMRecordWrapper recordWrapper) {
		final SAMRecord record = recordWrapper.getSAMRecord();
		for (int i = 0; i < length; ++i) {
			// ensure minimal base call quality score
			final byte baseCallQuality = record.getBaseQualities()[readPosition + i];
			if (baseCallQuality < baseCallConfig.getMinBQ()) {
				continue;
			}
			
			// consider only chosen bases
			final int baseIndex = baseCallConfig.getBaseIndex(record.getReadBases()[readPosition + i]);
			if (baseIndex < 0) {
				continue;
			}

			_incrementBaseCalls(windowPosition + i, baseIndex, baseCallQuality, recordWrapper);
		}
	}

	protected void _incrementBaseCalls(final int windowPosition, final int baseIndex, final byte baseCallQuality, 
			final SAMRecordWrapper recordWrapper) {
		records.get(windowPosition).add(recordWrapper);
	}

	@Override
	public void clear() {
		for (int i = 0; i < getActiveWindowSize(); ++i) {
			records.get(i).clear();
		}
	}

	public List<SAMRecordWrapper> getRecordWrapper(final int windowPosition) {
		return records.get(windowPosition);
	}

}
