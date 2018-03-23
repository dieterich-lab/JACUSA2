package lib.data.cache.container;

import htsjdk.samtools.AlignmentBlock;

import java.util.Arrays;

import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateController.WindowPositionGuard;
import lib.util.coordinate.Coordinate;

public class SimpleMDReferenceProvider implements ReferenceProvider {

	private final CoordinateController coordinateController;

	private final byte[] reference;
	private Coordinate window;

	public SimpleMDReferenceProvider(final CoordinateController coordinateController) {
		this.coordinateController = coordinateController;
		
		reference = new byte[coordinateController.getActiveWindowSize()];
		Arrays.fill(reference, (byte)'N');
	}

	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		int srcPos = 0;
		for (final AlignmentBlock block : recordWrapper.getSAMRecord().getAlignmentBlocks()) {
			final int referencePosition = block.getReferenceStart();
			final int readPosition = block.getReadStart() - 1;
			final int length = block.getLength();
			
			final WindowPositionGuard windowPositionGuard = coordinateController.convert(referencePosition, readPosition, length);
			if (windowPositionGuard.isValid()) {
				// hack - see method recordWrapper.getReference()
				// adjust srcPos by offset from diff. of read position(s)
				final int offset = windowPositionGuard.getReadPosition() - readPosition;
				System.arraycopy(
						recordWrapper.getReference(), 
						srcPos + offset, 
						reference, 
						windowPositionGuard.getWindowPosition(), 
						windowPositionGuard.getLength());
			}
			srcPos += length;
		}
	}

	@Override
	public void update() {
		if (window == null || window != coordinateController.getActive()) {
			window = coordinateController.getActive();
			Arrays.fill(reference, (byte)'N');
		}
	}
	
	@Override
	public byte getReference(final Coordinate coordinate) {
		final int windowPosition = coordinateController.convert2windowPosition(coordinate);
		return getReference(windowPosition);
	}
	
	@Override
	public byte getReference(final int windowPosition) {
		return reference[windowPosition];
	}
	
}
