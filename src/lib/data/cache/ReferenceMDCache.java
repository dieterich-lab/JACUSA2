package lib.data.cache;

import java.util.Arrays;

import lib.util.coordinate.Coordinate;
import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.util.SequenceUtil;

import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.has.hasReferenceBase;

public class ReferenceMDCache<T extends AbstractData & hasReferenceBase> 
extends AbstractCache<T> {

	private byte[] referenceBases;

	public ReferenceMDCache(final int activeWindowSize) {
		super(activeWindowSize);
		referenceBases = new byte[getActiveWindowSize()];
	}

	// TODO make more efficient
	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		byte[] reference = recordWrapper.getReference();
		if (reference.length == 0) {
			return;
		}
		for (final AlignmentBlock alignmentBlock : recordWrapper.getSAMRecord().getAlignmentBlocks()) {
			addReferenceBase(alignmentBlock.getReferenceStart(), 
					alignmentBlock.getReadStart() - 1, 
					alignmentBlock.getLength(), 
					recordWrapper);
		}
	}
	
	@Override
	public void addRecordWrapperPosition(final int readPosition, final SAMRecordWrapper recordWrapper) {
		final int referencePosition = recordWrapper.getSAMRecord().getReferencePositionAtReadPosition(readPosition);
		addReferenceBase(referencePosition, readPosition, 1, recordWrapper);
	}
	
	@Override
	public void addRecordWrapperRegion(final int readPosition, final int length, final SAMRecordWrapper recordWrapper) {
		final int referencePosition = recordWrapper.getSAMRecord().getReferencePositionAtReadPosition(readPosition);
		addReferenceBase(referencePosition, readPosition, length, recordWrapper);
	}
	
	@Override
	public void addData(final T data, final Coordinate coordinate) {
		final int windowPosition = Coordinate.makeRelativePosition(getActiveWindowCoordinate(), coordinate.getPosition());
		data.setReferenceBase(referenceBases[windowPosition]);
	}

	protected void addReferenceBase(final int referencePosition, final int readPosition, int length, 
			final SAMRecordWrapper recordWrapper) {

		if (referencePosition < 0) {
			throw new IllegalArgumentException("Reference Position cannot be < 0! -> outside of alignmentBlock");
		}
		
		final WindowPosition windowPosition = WindowPosition.convert(
				getActiveWindowCoordinate(), referencePosition, readPosition, length);
		
		if (windowPosition.getWindowPosition() < 0 && windowPosition.getLength() > 0) {
			throw new IllegalArgumentException("Window position cannot be < 0! -> outside of alignmentBlock");
		}
		
		for (int j = 0; j < windowPosition.getLength(); ++j) {
			referenceBases[windowPosition.getWindowPosition() + j] = 
					recordWrapper.getReference()[windowPosition.getRead() + j];
		}
	}
	
	@Override
	public void clear() {
		Arrays.fill(referenceBases, SequenceUtil.N);
	}

}
