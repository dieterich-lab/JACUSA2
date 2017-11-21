package lib.data.cache.container;

import java.util.Arrays;

import htsjdk.samtools.AlignmentBlock;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.tmp.CoordinateController;
import lib.tmp.CoordinateController.WindowPositionGuard;

// TODO optimize
public class MDReferenceCache {

	private final CoordinateController coordinateController;
	
	private byte[] reference;
	
	public MDReferenceCache(final CoordinateController coordinateController) {
		this.coordinateController = coordinateController;

		reference = new byte[coordinateController.getActiveWindowSize()];
		clear();
	}
	
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		for (final AlignmentBlock block : recordWrapper.getSAMRecord().getAlignmentBlocks()) {
			final int referencePosition = block.getReferenceStart() - 1;
			final int readPosition = block.getReadStart() - 1;
			final int length = block.getLength();
			final WindowPositionGuard windowPositionGuard = 
					coordinateController.convert(referencePosition, readPosition, length);
			setReference(windowPositionGuard, recordWrapper);
		}
	}
	
	private void setReference(final WindowPositionGuard windowPositionGuard, final SAMRecordWrapper recordWrapper) {
		final int windowPosition = windowPositionGuard.getWindowPosition();
		final int readPosition = windowPositionGuard.getReadPosition();
		final int length = windowPositionGuard.getLength();
		setReference(windowPosition, readPosition, length, recordWrapper);
	}
	
	private void setReference(final int windowPosition, final int readPosition, final int length, final SAMRecordWrapper recordWrapper) {
		System.arraycopy(recordWrapper.getReference(), readPosition, 
				reference, 
				windowPosition, length);
	}
	
	public byte getReferenceBase(final int windowPosition) {
		return reference[windowPosition];
	}
	
	public void clear() {
		Arrays.fill(reference, (byte)'N');
	}
	
}
