package lib.data.cache.region;

import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.CoordinateController.WindowPositionGuard;

import htsjdk.samtools.SAMRecord;

import lib.cli.options.BaseCallConfig;
import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.AbstractDataCache;

public abstract class AbstractRestrictedRegionDataCache<X extends AbstractData>
extends AbstractDataCache<X> 
implements RestrictedRegionDataCache<X> {

	private final BaseCallConfig baseCallConfig;

	public AbstractRestrictedRegionDataCache(final BaseCallConfig baseCallConfig, 
			final CoordinateController coordinateController) {

		super(coordinateController);
		this.baseCallConfig = baseCallConfig;
	}

	@Override
	public void addRegion(final int referencePosition, final int readPosition, final int length, final SAMRecordWrapper recordWrapper) {
		if (referencePosition < 0) {
			throw new IllegalArgumentException("Reference Position cannot be < 0! -> outside of alignmentBlock");
		}
		
		final WindowPositionGuard windowPositionGuard = getCoordinateController().convert(referencePosition, readPosition, length);
		
		if (windowPositionGuard.getWindowPosition() < 0 && windowPositionGuard.getLength() > 0) {
			throw new IllegalArgumentException("Window position cannot be < 0! -> outside of alignmentBlock");
		}

		final SAMRecord record = recordWrapper.getSAMRecord();
		for (int offset = 0; offset < windowPositionGuard.getLength(); ++offset) {
			final int baseIndex 	= baseCallConfig.getBaseIndex(record.getReadBases()[windowPositionGuard.getReadPosition() + offset]);
			final byte baseQuality 	= record.getBaseQualities()[windowPositionGuard.getReadPosition() + offset];
			if (isValid(windowPositionGuard.getWindowPosition() + offset, windowPositionGuard.getReadPosition() + offset , 
					baseIndex, baseQuality)) {

				increment(windowPositionGuard.getWindowPosition() + offset,
						windowPositionGuard.getReadPosition() + offset,
						baseIndex, baseQuality);	
			}
		}
	}
	
	public BaseCallConfig getBaseCallConfig() {
		return baseCallConfig;
	}
	
}
